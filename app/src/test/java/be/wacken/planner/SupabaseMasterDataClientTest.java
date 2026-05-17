package be.wacken.planner;

import org.json.JSONObject;
import org.junit.Test;

import java.time.LocalDateTime;

import be.wacken.planner.domain.Band;

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
}
