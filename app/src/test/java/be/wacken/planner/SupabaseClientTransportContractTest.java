package be.wacken.planner;

import static org.junit.Assert.*;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.PersonalBandRatingEvent;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.SavedFestivalPlanningRating;
import be.wacken.planner.domain.SavedRating;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;
import org.junit.Test;

public final class SupabaseClientTransportContractTest {
    private static final AuthSession SESSION = new AuthSession("access", "refresh", "member", "member@example.test",
            Long.MAX_VALUE, "group", "member");
    private static final String BAND = "[{\"id\":\"canonical-band\"}]";

    @Test
    public void legacyRatingKeepsCanonicalLookupMemberGroupAndUpsertPayload() throws Exception {
        Fixture f = new Fixture(new Reply(200, BAND), new Reply(201, ""));
        new SupabaseRatingClient(f.sessions, f.transport).pushRating(SESSION, new SavedRating("member", new Band("A & B"), Rating.of(5)));
        assertTrue(f.paths.get(0).contains("name=eq.A+%26+B"));
        assertEquals("/rest/v1/ratings?on_conflict=group_id,user_id,band_id", f.paths.get(1));
        assertPayload(f, "group", "member", 5);
    }

    @Test
    public void planningResetKeepsFestivalMemberGroupAndBandFilters() throws Exception {
        Fixture f = new Fixture(new Reply(200, BAND), new Reply(204, ""));
        new SupabaseFestivalPlanningRatingClient(f.sessions, f.transport).pushRating(SESSION,
                new SavedFestivalPlanningRating("group", "member", "festival", new Band("Band"), Rating.of(0)));
        assertEquals("DELETE", f.connections.get(1).getRequestMethod());
        assertEquals("/rest/v1/festival_planning_ratings?group_id=eq.group&user_id=eq.member&festival_id=eq.festival&band_id=eq.canonical-band", f.paths.get(1));
        assertFalse(f.connections.get(1).outputOpened);
    }

    @Test
    public void planningWriteKeepsFestivalAndUpsertIdentity() throws Exception {
        Fixture f = new Fixture(new Reply(200, BAND), new Reply(201, ""));
        new SupabaseFestivalPlanningRatingClient(f.sessions, f.transport).pushRating(SESSION,
                new SavedFestivalPlanningRating("group", "member", "festival", new Band("Band"), Rating.of(4)));
        assertEquals("/rest/v1/festival_planning_ratings?on_conflict=group_id,user_id,festival_id,band_id", f.paths.get(1));
        assertPayload(f, "group", "member", 4);
        assertEquals("festival", f.body(1).getString("festival_id"));
    }

    @Test
    public void historyKeepsStableEventIdentityDateAndSourceSnapshot() throws Exception {
        Fixture f = new Fixture(new Reply(200, BAND), new Reply(201, ""));
        String operation = "11111111-1111-4111-8111-111111111111";
        new SupabasePersonalBandRatingClient(f.sessions, f.transport).pushEvent(SESSION,
                new PersonalBandRatingEvent(operation, "member", new Band("Band"), Optional.of("festival"),
                        Rating.of(3), Instant.parse("2026-08-01T12:00:00Z"), Optional.of("Festival")));
        assertEquals("/rest/v1/personal_band_rating_events?on_conflict=id", f.paths.get(1));
        assertEquals("GET", f.connections.get(0).getRequestMethod());
        assertEquals("POST", f.connections.get(1).getRequestMethod());
        assertEquals(2, f.paths.size());
        JSONObject body = f.body(1);
        assertEquals(operation, body.getString("id"));
        assertEquals("member", body.getString("user_id"));
        assertEquals("canonical-band", body.getString("band_id"));
        assertEquals("2026-08-01T12:00:00Z", body.getString("created_at"));
        assertEquals("Festival", body.getString("festival_name_snapshot"));
        assertEquals(3, body.getInt("rating"));
    }

    @Test
    public void scheduleWriteKeepsFestivalScopedConflictAndActingMember() throws Exception {
        Fixture f = new Fixture(new Reply(201, ""));
        new SupabaseScheduleLockClient(f.sessions, "festival", f.transport).saveGroupLock("conflict", "selected");
        assertEquals("/rest/v1/group_schedule_locks?on_conflict=group_id,festival_id,conflict_key", f.paths.get(0));
        JSONObject body = f.body(0);
        assertEquals("group", body.getString("group_id"));
        assertEquals("festival", body.getString("festival_id"));
        assertEquals("member", body.getString("updated_by"));
        assertEquals("conflict", body.getString("conflict_key"));
        assertEquals("selected", body.getString("selected_candidate_key"));
    }

    @Test
    public void rejectedJwtStillRefreshesOnceAndRetriesWithNewCredentials() throws Exception {
        Fixture f = new Fixture(new Reply(401, "{\"message\":\"JWT expired\"}"), new Reply(200, "[]"));
        assertTrue(new SupabaseScheduleLockClient(f.sessions, "festival", f.transport).pullGroupLocks().isEmpty());
        assertEquals(1, f.refreshes);
        assertEquals("Bearer access", f.connections.get(0).getRequestProperty("Authorization"));
        assertEquals("Bearer renewed", f.connections.get(1).getRequestProperty("Authorization"));
        assertEquals(f.paths.get(0), f.paths.get(1));
    }

    @Test
    public void anotherMembersRatingOrAnotherGroupsPlanningWriteIsRejectedBeforeTransport() throws Exception {
        Fixture f = new Fixture();
        assertThrows(IOException.class, () -> new SupabaseRatingClient(f.sessions, f.transport)
                .pushRating(SESSION, new SavedRating("other", new Band("Band"), Rating.of(3))));
        assertThrows(IOException.class, () -> new SupabaseFestivalPlanningRatingClient(f.sessions, f.transport)
                .pushRating(SESSION, new SavedFestivalPlanningRating("other", "member", "festival", new Band("Band"), Rating.of(3))));
        assertThrows(IOException.class, () -> new SupabasePersonalBandRatingClient(f.sessions, f.transport)
                .pushEvent(SESSION, new PersonalBandRatingEvent("event", "other", new Band("Band"), Optional.empty(), Rating.of(3), Instant.now())));
        assertTrue(f.paths.isEmpty());
    }

    private static void assertPayload(Fixture f, String group, String user, int rating) throws Exception {
        assertEquals("POST", f.connections.get(1).getRequestMethod());
        assertEquals("resolution=merge-duplicates", f.connections.get(1).getRequestProperty("Prefer"));
        JSONObject body = f.body(1);
        assertEquals(group, body.getString("group_id"));
        assertEquals(user, body.getString("user_id"));
        assertEquals("canonical-band", body.getString("band_id"));
        assertEquals(rating, body.getInt("rating"));
    }

    @Test
    public void allRatingAdaptersKeepExactCaseAndEscapingForCanonicalLookup() throws Exception {
        for (int kind = 0; kind < 3; kind++) {
            for (String name : new String[] {"Band", "BAND", "A & B", "AC/DC"}) {
                Fixture f = new Fixture(new Reply(200, BAND), new Reply(201, ""));
                push(kind, f, new Band(name));
                assertEquals("/rest/v1/bands?select=id&name=eq."
                        + java.net.URLEncoder.encode(name, StandardCharsets.UTF_8) + "&limit=1", f.paths.get(0));
                assertEquals("canonical-band", f.body(1).getString("band_id"));
            }
        }
    }

    @Test
    public void absentOrMalformedCatalogIdentityNeverReachesARatingWrite() throws Exception {
        for (int kind = 0; kind < 3; kind++) {
            for (String response : new String[] {"[]", "{}", "[{ } ]", "[{\"id\":null}]", "[{\"id\":42}]", "not-json"}) {
                Fixture f = new Fixture(new Reply(200, response));
                int adapter = kind;
                assertThrows(IOException.class, () -> push(adapter, f, new Band("Band")));
                assertEquals(1, f.paths.size());
                assertEquals("GET", f.connections.get(0).getRequestMethod());
            }
        }
    }

    @Test
    public void catalogMappingPreservesExactNamesAndSkipsUnknownBandReferences() throws Exception {
        String catalog = "[{\"id\":\"known\",\"name\":\"BAND\"}]";
        Fixture legacy = new Fixture(new Reply(200, catalog), new Reply(200,
                "[{\"user_id\":\"member\",\"band_id\":\"known\",\"rating\":3},{\"user_id\":\"member\",\"band_id\":\"unknown\",\"rating\":5}]"));
        assertEquals(List.of(new SavedRating("member", new Band("BAND"), Rating.of(3))),
                new SupabaseRatingClient(legacy.sessions, legacy.transport).pullGroupRatings(SESSION));
        assertEquals("/rest/v1/bands?select=id,name", legacy.paths.get(0));
        assertTrue(legacy.paths.get(1).endsWith("group_id=eq.group"));

        Fixture planning = new Fixture(new Reply(200, catalog), new Reply(200,
                "[{\"group_id\":\"group\",\"user_id\":\"member\",\"festival_id\":\"festival\",\"band_id\":\"known\",\"rating\":4},{\"band_id\":\"unknown\"}]"));
        assertEquals(List.of(new SavedFestivalPlanningRating("group", "member", "festival", new Band("BAND"), Rating.of(4))),
                new SupabaseFestivalPlanningRatingClient(planning.sessions, planning.transport).pullGroupRatings(SESSION));
        assertTrue(planning.paths.get(1).contains("group_id=eq.group"));

        Fixture history = new Fixture(new Reply(200, catalog), new Reply(200,
                "[{\"id\":\"event\",\"user_id\":\"member\",\"band_id\":\"known\",\"rating\":3,\"created_at\":\"2026-08-01T12:00:00Z\"},{\"band_id\":\"unknown\"}]"));
        List<PersonalBandRatingEvent> events = new SupabasePersonalBandRatingClient(history.sessions, history.transport).pullUserEvents(SESSION);
        assertEquals(1, events.size());
        assertEquals("BAND", events.get(0).band().name());
        assertTrue(history.paths.get(1).endsWith("user_id=eq.member"));
    }

    private static void push(int kind, Fixture f, Band band) throws IOException {
        switch (kind) {
            case 0 -> new SupabaseRatingClient(f.sessions, f.transport).pushRating(SESSION,
                    new SavedRating("member", band, Rating.of(3)));
            case 1 -> new SupabaseFestivalPlanningRatingClient(f.sessions, f.transport).pushRating(SESSION,
                    new SavedFestivalPlanningRating("group", "member", "festival", band, Rating.of(3)));
            case 2 -> new SupabasePersonalBandRatingClient(f.sessions, f.transport).pushEvent(SESSION,
                    new PersonalBandRatingEvent("event", "member", band, Optional.empty(), Rating.of(3), Instant.parse("2026-08-01T12:00:00Z")));
            default -> throw new AssertionError("Unknown fixture adapter");
        }
    }

    @Test
    public void accountChangeAfterLookupRejectsWriteWithoutChangingItsCanonicalIdentity() throws Exception {
        for (int kind = 0; kind < 3; kind++) {
            Fixture f = new Fixture(new Reply(200, BAND));
            f.changeAccountAfterLookup = true;
            int adapter = kind;
            IOException error = assertThrows(IOException.class, () -> push(adapter, f, new Band("Band")));
            assertTrue(error.getMessage().contains("signed-in member changed"));
            assertEquals(1, f.paths.size());
        }
    }

    private record Reply(int status, String body) { }

    private static final class Fixture {
        final List<SupabaseHttpTransportTest.RecordingConnection> connections = new ArrayList<>();
        final List<String> paths = new ArrayList<>();
        final SupabaseSessionManager sessions;
        final SupabaseHttpTransport transport;
        int refreshes;
        AuthSession current = SESSION;
        boolean changeAccountAfterLookup;

        Fixture(Reply... replies) throws Exception {
            for (Reply reply : replies) connections.add(new SupabaseHttpTransportTest.RecordingConnection(reply.status(), reply.body()));
            AuthSessionRepository store = new AuthSessionRepository() {
                public AuthSession load() { return current; }
                public void save(AuthSession value) { current = value; }
                public void clear() { throw new AssertionError("Unexpected session clear"); }
            };
            sessions = new SupabaseSessionManager(store, session -> {
                refreshes++;
                return new AuthSession("renewed", "refresh", "member", "member@example.test", Long.MAX_VALUE, "group", "member");
            }, () -> 1000);
            transport = new SupabaseHttpTransport(url -> {
                int index = paths.size();
                paths.add(url.getFile());
                if (index >= connections.size()) throw new AssertionError("Unexpected request: " + url.getFile());
                if (changeAccountAfterLookup && index == 0) {
                    current = new AuthSession("other-access", "other-refresh", "other", "other@example.test", Long.MAX_VALUE, "group", "member");
                }
                return connections.get(index);
            });
        }

        JSONObject body(int index) throws Exception {
            return new JSONObject(connections.get(index).output.toString(StandardCharsets.UTF_8));
        }
    }
}
