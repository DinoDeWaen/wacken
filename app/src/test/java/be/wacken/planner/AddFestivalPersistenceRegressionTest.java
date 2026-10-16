package be.wacken.planner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import be.wacken.planner.application.AddFestivalResult;
import be.wacken.planner.application.AddFestivalUseCase;
import be.wacken.planner.application.FestivalCsvReader;
import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.FestivalPlanningRatingRepository;
import be.wacken.planner.domain.PersonalBandRatingEvent;
import be.wacken.planner.domain.PersonalBandRatingHistoryRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.SavedFestivalPlanningRating;

/** Exercises the real use case and Supabase adapters, with a stateful API contract double.
 * Real PostgreSQL/RLS verification complements this fast regression suite. */
public final class AddFestivalPersistenceRegressionTest {
    @Test
    public void completeSummerBreezeCsvDoesNotOverwriteOrCollideWithKnownBand() throws Exception {
        DatabaseApi api = new DatabaseApi();
        api.bands.put("any-given-day", new JSONObject()
                .put("id", "any-given-day").put("name", "Any given Day")
                .put("biography_html", "Golden-source biography").put("active", true));
        String csv = new String(Files.readAllBytes(repositoryFile("data/summer-breeze-2027/bands.csv")), StandardCharsets.UTF_8);
        List<Band> uploaded = FestivalCsvReader.bandNames(csv).stream().map(Band::new).toList();

        AddFestivalResult result = useCase(api).addFestival("group", "member", "summer-breeze", "Summer breeze", uploaded);

        assertTrue(result.message(), result.success());
        assertEquals(uploaded.stream().map(Band::name).distinct().count(), api.lineup.size());
        assertEquals("Any given Day", api.bands.get("any-given-day").getString("name"));
        assertEquals("Golden-source biography", api.bands.get("any-given-day").getString("biography_html"));
        assertTrue(api.bands.values().stream().anyMatch(row -> "Any Given Day".equals(row.optString("name"))));
    }

    @Test
    public void failedBandWriteDoesNotLeaveAnActiveFestivalOrPartialCatalog() throws Exception {
        DatabaseApi api = new DatabaseApi();
        api.failBand = "Rejected Act";
        try {
            useCase(api).addFestival("group", "member", "test-festival", "Test festival",
                    List.of(new Band("First Act"), new Band("Rejected Act")));
            fail("Expected the injected persistence failure");
        } catch (SupabaseSyncException expected) {
            assertTrue(expected.getMessage().contains("Injected"));
        }

        assertTrue("A failed creation must not leave an empty active festival", api.festivals.isEmpty());
        assertTrue("A failed creation must not leave newly inserted bands", api.bands.isEmpty());
        assertTrue(api.lineup.isEmpty());
    }

    @Test
    public void duplicateCsvBandRowsProduceOneLineupEntry() throws Exception {
        DatabaseApi api = new DatabaseApi();
        List<Band> uploaded = FestivalCsvReader.bandNames("name\nAirbourne\nAirbourne\n").stream().map(Band::new).toList();

        AddFestivalResult result = useCase(api).addFestival("group", "member", "test-festival", "Test festival", uploaded);

        assertTrue(result.message(), result.success());
        assertEquals(1, api.lineup.size());
    }

    @Test
    public void archivedFestivalIdentityCannotBeReactivatedByCreatingAnotherFestival() throws Exception {
        DatabaseApi api = new DatabaseApi();
        api.festivals.put("summer-breeze", new JSONObject()
                .put("id", "summer-breeze").put("name", "Historical Summer Breeze").put("status", "ARCHIVED"));

        AddFestivalResult result = useCase(api).addFestival("group", "member", "summer-breeze", "New Summer Breeze",
                List.of(new Band("Airbourne")));

        assertFalse("An archived identity belongs to the historical festival", result.success());
        assertEquals("ARCHIVED", api.festivals.get("summer-breeze").getString("status"));
        assertEquals("Historical Summer Breeze", api.festivals.get("summer-breeze").getString("name"));
        assertTrue(api.bands.isEmpty());
    }

    private AddFestivalUseCase useCase(DatabaseApi api) {
        SupabaseMasterDataClient client = new SupabaseMasterDataClient(api);
        return new AddFestivalUseCase(new SupabaseFestivalRepository(client), new SupabaseBandRepository(client),
                new SupabaseFestivalLineupRepository(client), new EmptyPlanningRatings(), new EmptyPersonalRatings());
    }

    private Path repositoryFile(String name) {
        return List.of(Path.of(name), Path.of("..", name)).stream().filter(Files::exists).findFirst().orElseThrow();
    }

    private static final class DatabaseApi implements SupabaseMasterDataClient.RequestExecutor {
        final Map<String, JSONObject> bands = new LinkedHashMap<>();
        final Map<String, JSONObject> festivals = new LinkedHashMap<>();
        final Map<String, JSONObject> lineup = new LinkedHashMap<>();
        String failBand = "";

        @Override
        public String execute(String method, String endpoint, String body, boolean upsert) throws IOException {
            try {
                String table = endpoint.split("\\?")[0].substring("/rest/v1/".length());
                Map<String, JSONObject> rows = switch (table) {
                    case "bands" -> bands;
                    case "festivals" -> festivals;
                    case "festival_lineup_entries" -> lineup;
                    default -> throw new IOException("Unexpected endpoint " + endpoint);
                };
                if ("GET".equals(method)) {
                    JSONArray result = new JSONArray();
                    for (JSONObject row : rows.values()) {
                        if (matches(row, endpoint)) result.put(row);
                    }
                    return result.toString();
                }
                if ("DELETE".equals(method)) {
                    rows.entrySet().removeIf(entry -> matches(entry.getValue(), endpoint));
                } else if ("PATCH".equals(method)) {
                    JSONObject patch = new JSONObject(body);
                    for (JSONObject row : rows.values()) {
                        if (matches(row, endpoint)) {
                            java.util.Iterator<String> keys = patch.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                row.put(key, patch.get(key));
                            }
                        }
                    }
                } else if ("POST".equals(method)) {
                    JSONArray inserted = body.startsWith("[") ? new JSONArray(body) : new JSONArray().put(new JSONObject(body));
                    List<String> insertedKeys = new ArrayList<>();
                    for (int index = 0; index < inserted.length(); index++) {
                        JSONObject row = inserted.getJSONObject(index);
                        if ("bands".equals(table) && failBand.equals(row.optString("name"))) {
                            throw new IOException("Injected band write failure");
                        }
                        String key = "festival_lineup_entries".equals(table)
                                ? row.getString("festival_id") + ":" + row.getString("band_id") : row.getString("id");
                        if (rows.containsKey(key) || insertedKeys.contains(key)) {
                            throw new IOException("duplicate key value violates unique constraint \"" + table + "_pkey\"");
                        }
                        insertedKeys.add(key);
                    }
                    for (int index = 0; index < inserted.length(); index++) rows.put(insertedKeys.get(index), inserted.getJSONObject(index));
                } else {
                    throw new IOException("Unexpected method " + method);
                }
                return "";
            } catch (org.json.JSONException error) {
                throw new IOException(error);
            }
        }

        private boolean matches(JSONObject row, String endpoint) {
            int marker = endpoint.indexOf('?');
            if (marker < 0) return true;
            for (String parameter : endpoint.substring(marker + 1).split("&")) {
                String[] pair = parameter.split("=", 2);
                if (pair.length == 2 && pair[1].startsWith("eq.")) {
                    String expected = URLDecoder.decode(pair[1].substring(3), StandardCharsets.UTF_8);
                    if (!expected.equals(row.optString(pair[0], "true".equals(expected) ? "true" : ""))) return false;
                }
            }
            return true;
        }
    }

    private static final class EmptyPlanningRatings implements FestivalPlanningRatingRepository {
        public void save(String group, String user, String festival, Band band, Rating rating) { }
        public Optional<Rating> findByUserFestivalAndBand(String user, String festival, Band band) { return Optional.empty(); }
        public List<SavedFestivalPlanningRating> findByFestival(String festival) { return List.of(); }
        public List<SavedFestivalPlanningRating> findAll() { return List.of(); }
    }

    private static final class EmptyPersonalRatings implements PersonalBandRatingHistoryRepository {
        public void save(PersonalBandRatingEvent event) { }
        public List<PersonalBandRatingEvent> findByUserAndBand(String user, Band band) { return List.of(); }
    }
}
