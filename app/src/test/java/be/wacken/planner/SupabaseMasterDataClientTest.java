package be.wacken.planner;

import org.json.JSONObject;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalLineupEntry;
import be.wacken.planner.domain.FestivalStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class SupabaseMasterDataClientTest {
    @Test
    public void mapsBandRowsToDomainBandsWithSpotifyArtistUrl() throws Exception {
        JSONObject row = new JSONObject()
                .put("name", "5th Avenue")
                .put("biography_html", "Heavy metal from Hamburg")
                .put("image_url", "https://example.test/band.jpg")
                .put("youtube_url", "https://youtube.test/watch")
                .put("spotify_artist_id", "artist-123");

        Band band = SupabaseMasterDataClient.toBand(row);

        assertEquals("5th Avenue", band.name());
        assertEquals("Heavy metal from Hamburg", band.biography().orElseThrow());
        assertEquals("https://example.test/band.jpg", band.imageUrl().orElseThrow());
        assertEquals("https://youtube.test/watch", band.youtubeUrl().orElseThrow());
        assertEquals("https://open.spotify.com/artist/artist-123", band.spotifyUrl().orElseThrow());
    }

    @Test
    public void mapsBlankOptionalBandFieldsToEmptyOptionals() throws Exception {
        JSONObject row = new JSONObject()
                .put("name", "No Links")
                .put("biography_html", "")
                .put("image_url", "")
                .put("youtube_url", "")
                .put("spotify_artist_id", "");

        Band band = SupabaseMasterDataClient.toBand(row);

        assertTrue(band.biography().isEmpty());
        assertTrue(band.imageUrl().isEmpty());
        assertTrue(band.youtubeUrl().isEmpty());
        assertTrue(band.spotifyUrl().isEmpty());
    }

    @Test
    public void mapsSupabaseTimestampToLocalDateTime() {
        assertEquals(
                LocalDateTime.of(2026, 7, 30, 18, 15),
                SupabaseMasterDataClient.toLocalDateTime("2026-07-30T18:15:00")
        );
    }

    @Test
    public void mapsFestivalRowsToDomainFestivals() throws Exception {
        JSONObject row = new JSONObject()
                .put("id", "summer-breeze-2027")
                .put("name", "Summer Breeze 2027")
                .put("status", "ACTIVE");

        Festival festival = SupabaseMasterDataClient.toFestival(row);

        assertEquals(new Festival("summer-breeze-2027", "Summer Breeze 2027", FestivalStatus.ACTIVE), festival);
    }

    @Test
    public void mapsFestivalLineupRowsToDomainEntries() throws Exception {
        org.json.JSONArray rows = new org.json.JSONArray()
                .put(new JSONObject()
                        .put("festival_id", "summer-breeze-2027")
                        .put("band_id", "any-given-day")
                        .put("uploaded_display_name", "Any Given Day"));

        List<FestivalLineupEntry> entries = SupabaseMasterDataClient.parseFestivalLineupEntries(
                rows,
                Map.of("any-given-day", "Any given Day")
        );

        assertEquals(List.of(new FestivalLineupEntry("summer-breeze-2027", new Band("Any given Day"), "Any Given Day")), entries);
    }

    @Test
    public void derivesStableBandIdsAndSpotifyArtistIdsForWrites() {
        assertEquals("any-given-day", SupabaseMasterDataClient.bandIdFor(new Band("Any Given Day")));
        assertEquals(
                Optional.of("spotify-artist"),
                SupabaseMasterDataClient.spotifyArtistId(Optional.of("https://open.spotify.com/artist/spotify-artist?si=abc"))
        );
    }

    @Test
    public void missingMetadataPatchDoesNotOverwriteExistingValues() throws Exception {
        JSONObject existing = new JSONObject()
                .put("biography_html", "Existing bio")
                .put("image_url", "")
                .put("youtube_url", "https://youtube.example/existing")
                .put("spotify_artist_id", "");
        Band candidate = new Band(
                "Any Given Day",
                Optional.of("New bio"),
                Optional.of("https://images.example/agd.jpg"),
                Optional.of("https://youtube.example/new"),
                Optional.of("https://open.spotify.com/artist/spotify-artist")
        );

        JSONObject patch = SupabaseMasterDataClient.missingMetadataPatch(existing, candidate);

        assertEquals("https://images.example/agd.jpg", patch.getString("image_url"));
        assertEquals("spotify-artist", patch.getString("spotify_artist_id"));
        assertTrue(patch.isNull("biography_html") || !patch.has("biography_html"));
        assertTrue(patch.isNull("youtube_url") || !patch.has("youtube_url"));
    }
}
