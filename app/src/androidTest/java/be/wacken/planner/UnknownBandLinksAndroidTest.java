package be.wacken.planner;

import static org.junit.Assert.*;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;
import be.wacken.planner.domain.Band;
import be.wacken.planner.persistence.RoomBandRepository;
import be.wacken.planner.persistence.WackenDatabase;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** Uses Android's org.json implementation, whose optString turns JSON null into "null". */
public final class UnknownBandLinksAndroidTest {
    private Instrumentation instrumentation;
    private WackenDatabase database;
    private AuthSession previousSession;
    private Activity activity;
    private Optional<Band> previousBand;

    @Before public void setup() {
        instrumentation = InstrumentationRegistry.getInstrumentation();
        database = Room.inMemoryDatabaseBuilder(instrumentation.getTargetContext(), WackenDatabase.class)
                .allowMainThreadQueries().build();
        previousSession = new AuthSessionStore(instrumentation.getTargetContext()).load();
    }

    @After public void cleanup() {
        if (activity != null) instrumentation.runOnMainSync(activity::finish);
        if (previousBand != null) {
            WackenDatabase persistent = WackenDatabase.get(instrumentation.getTargetContext());
            if (previousBand.isPresent()) new RoomBandRepository(persistent).save(previousBand.get());
            else persistent.getOpenHelper().getWritableDatabase().execSQL(
                    "DELETE FROM bands WHERE name = ?", new Object[]{"Abbie Falls"});
        }
        new AuthSessionStore(instrumentation.getTargetContext()).save(previousSession);
        database.close();
    }

    @Test public void nullMetadataFromCreationRemainsAbsentInRoom() throws Exception {
        String result = "{\"group_id\":\"group\",\"user_id\":\"user\","
                + "\"festival\":{\"id\":\"next\",\"name\":\"Next\",\"status\":\"ACTIVE\"},"
                + "\"festivals\":[{\"id\":\"next\",\"name\":\"Next\",\"status\":\"ACTIVE\"}],"
                + "\"bands\":[" + unknownRow() + "],"
                + "\"lineup\":[{\"band_name\":\"Abbie Falls\",\"uploaded_display_name\":\"Abbie Falls\"}],"
                + "\"planning_ratings\":[],\"reused_bands\":0,\"created_bands\":1,\"prefilled_ratings\":0}";
        new RoomFestivalCreationCache(database).accept(
                SupabaseFestivalCreationGateway.parse(result, "group", "user"));

        assertNoMetadata(new RoomBandRepository(database).findByName("Abbie Falls").orElseThrow());
    }

    @Test public void normalSyncClearsBogusCachedLinksAndDetailOffersNoUnknownMusicAction() throws Exception {
        RoomBandRepository cache = new RoomBandRepository(database);
        cache.save(new Band("Abbie Falls", Optional.of("null"),
                Optional.of("https://open.spotify.com/artist/null")));
        SupabaseMasterDataClient client = new SupabaseMasterDataClient((method, path, body, upsert) ->
                "[" + unknownRow() + "]");
        cache.replaceAll(client.bands());
        Band band = cache.findByName("Abbie Falls").orElseThrow();
        openDetail(band);

        instrumentation.runOnMainSync(() -> {
            assertNull(findAction(activity.getWindow().getDecorView(), "Open YouTube"));
            assertNull(findAction(activity.getWindow().getDecorView(), "Open Spotify"));
            assertNull(findAction(activity.getWindow().getDecorView(), "Band image"));
        });
        assertNoMetadata(band);
    }

    @Test public void knownLinksSurviveSyncAndRemainAvailableOnDetail() throws Exception {
        String youtube = "https://www.youtube.com/@AbbieFalls";
        JSONObject row = new JSONObject().put("name", "Abbie Falls")
                .put("youtube_url", youtube).put("spotify_artist_id", "known-artist");
        SupabaseMasterDataClient client = new SupabaseMasterDataClient((method, path, body, upsert) ->
                "[" + row + "]");
        RoomBandRepository cache = new RoomBandRepository(database);
        cache.replaceAll(client.bands());
        Band band = cache.findByName("Abbie Falls").orElseThrow();
        assertEquals(Optional.of(youtube), band.youtubeUrl());
        assertEquals(Optional.of("https://open.spotify.com/artist/known-artist"), band.spotifyUrl());
        openDetail(band);
        instrumentation.runOnMainSync(() -> {
            assertNotNull(findAction(activity.getWindow().getDecorView(), "Open YouTube"));
            assertNotNull(findAction(activity.getWindow().getDecorView(), "Open Spotify"));
        });
    }

    @Test public void missingAndBlankMetadataAlsoRemainAbsent() throws Exception {
        for (String row : List.of("{\"name\":\"Abbie Falls\"}",
                "{\"name\":\"Abbie Falls\",\"youtube_url\":\" \",\"spotify_artist_id\":\"\","
                        + "\"biography_html\":\"\",\"image_url\":\" \"}")) {
            assertNoMetadata(SupabaseMasterDataClient.toBand(new JSONObject(row)));
        }
    }

    @Test public void reviewedMetadataCanFillJsonNullButCannotOverwriteKnownLinks() throws Exception {
        Band proposal = new Band("Abbie Falls", Optional.of("Reviewed biography"),
                Optional.of("https://example.test/image.jpg"),
                Optional.of("https://www.youtube.com/channel/reviewed"),
                Optional.of("https://open.spotify.com/artist/reviewed"));
        JSONObject patch = SupabaseMasterDataClient.missingMetadataPatch(new JSONObject(unknownRow()), proposal);
        assertEquals(4, patch.length());
        assertEquals(proposal.youtubeUrl().orElseThrow(), patch.getString("youtube_url"));
        JSONObject known = new JSONObject().put("youtube_url", "https://www.youtube.com/channel/existing")
                .put("spotify_artist_id", "existing");
        JSONObject preserved = SupabaseMasterDataClient.missingMetadataPatch(known, proposal);
        assertFalse(preserved.has("youtube_url"));
        assertFalse(preserved.has("spotify_artist_id"));
    }

    private void openDetail(Band band) {
        RoomBandRepository persistent = new RoomBandRepository(WackenDatabase.get(instrumentation.getTargetContext()));
        previousBand = persistent.findByName(band.name());
        persistent.save(band);
        new AuthSessionStore(instrumentation.getTargetContext()).save(
                new AuthSession("fixture-token", "fixture-refresh", "user", "fixture@example.test", Long.MAX_VALUE, "group", "member"));
        Intent intent = new Intent(instrumentation.getTargetContext(), BandDetailActivity.class)
                .putExtra(BandDetailActivity.EXTRA_BAND_NAME, band.name())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity = instrumentation.startActivitySync(intent);
        instrumentation.waitForIdleSync();
    }

    private static String unknownRow() {
        return "{\"name\":\"Abbie Falls\",\"biography_html\":null,\"image_url\":null,"
                + "\"youtube_url\":null,\"spotify_artist_id\":null}";
    }

    private static void assertNoMetadata(Band band) {
        assertEquals("Unknown YouTube must remain absent", Optional.empty(), band.youtubeUrl());
        assertEquals(Optional.empty(), band.spotifyUrl());
        assertEquals(Optional.empty(), band.imageUrl());
        assertEquals(Optional.empty(), band.biography());
    }

    private static View findAction(View view, String description) {
        if (description.contentEquals(view.getContentDescription() == null ? "" : view.getContentDescription())) return view;
        if (view instanceof ViewGroup group) {
            for (int i = 0; i < group.getChildCount(); i++) {
                View found = findAction(group.getChildAt(i), description);
                if (found != null) return found;
            }
        }
        return null;
    }
}
