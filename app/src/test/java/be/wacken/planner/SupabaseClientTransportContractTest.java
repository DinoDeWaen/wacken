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

    private record Reply(int status, String body) { }

    private static final class Fixture {
        final List<SupabaseHttpTransportTest.RecordingConnection> connections = new ArrayList<>();
        final List<String> paths = new ArrayList<>();
        final SupabaseSessionManager sessions;
        final SupabaseHttpTransport transport;
        int refreshes;

        Fixture(Reply... replies) throws Exception {
            for (Reply reply : replies) connections.add(new SupabaseHttpTransportTest.RecordingConnection(reply.status(), reply.body()));
            AuthSessionRepository store = new AuthSessionRepository() {
                private AuthSession current = SESSION;
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
                return connections.get(index);
            });
        }

        JSONObject body(int index) throws Exception {
            return new JSONObject(connections.get(index).output.toString(StandardCharsets.UTF_8));
        }
    }
}
