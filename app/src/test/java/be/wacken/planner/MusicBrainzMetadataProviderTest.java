package be.wacken.planner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import be.wacken.planner.application.BandMetadataLookupException;
import be.wacken.planner.application.BandMetadataProviderCandidate;

public final class MusicBrainzMetadataProviderTest {
    @Test
    public void mapsArtistRelationshipUrlsToProviderCandidateMetadata() throws Exception {
        FakeMusicBrainzClient client = new FakeMusicBrainzClient();
        client.respond("/artist?query=artist%3A%22Any+Given+Day%22&limit=5&fmt=json", """
                {"artists":[{"id":"artist-1","name":"Any Given Day","score":98}]}
                """);
        client.respond("/artist/artist-1?inc=url-rels+aliases&fmt=json", """
                {"relations":[
                  {"type":"streaming music","url":{"resource":"https://open.spotify.com/artist/agd"}},
                  {"type":"youtube","url":{"resource":"https://www.youtube.com/@anygivenday"}}
                ]}
                """);

        List<BandMetadataProviderCandidate> candidates = new MusicBrainzMetadataProvider(client).search("Any Given Day");

        assertEquals(1, candidates.size());
        assertEquals("Any Given Day", candidates.get(0).candidateName());
        assertEquals(Optional.of("https://open.spotify.com/artist/agd"), candidates.get(0).spotifyUrl());
        assertEquals(Optional.of("https://www.youtube.com/@anygivenday"), candidates.get(0).youtubeUrl());
        assertEquals(Optional.of("https://musicbrainz.org/artist/artist-1"), candidates.get(0).sourceUrl());
        assertEquals(98, candidates.get(0).confidence());
        assertEquals(List.of(
                "/artist?query=artist%3A%22Any+Given+Day%22&limit=5&fmt=json",
                "/artist/artist-1?inc=url-rels+aliases&fmt=json"
        ), client.paths());
    }

    @Test
    public void returnsMultipleArtistCandidatesForReviewWithoutChoosingAutomatically() throws Exception {
        FakeMusicBrainzClient client = new FakeMusicBrainzClient();
        client.respond("/artist?query=artist%3A%22Ghost%22&limit=5&fmt=json", """
                {"artists":[
                  {"id":"artist-1","name":"Ghost","score":95},
                  {"id":"artist-2","name":"Ghost","score":71}
                ]}
                """);
        client.respond("/artist/artist-1?inc=url-rels+aliases&fmt=json", """
                {"relations":[{"url":{"resource":"https://open.spotify.com/artist/ghost-1"}}]}
                """);
        client.respond("/artist/artist-2?inc=url-rels+aliases&fmt=json", """
                {"relations":[{"url":{"resource":"https://open.spotify.com/artist/ghost-2"}}]}
                """);

        List<BandMetadataProviderCandidate> candidates = new MusicBrainzMetadataProvider(client).search("Ghost");

        assertEquals(2, candidates.size());
        assertEquals(Optional.of("https://open.spotify.com/artist/ghost-1"), candidates.get(0).spotifyUrl());
        assertEquals(Optional.of("https://open.spotify.com/artist/ghost-2"), candidates.get(1).spotifyUrl());
    }

    @Test
    public void providerIsConfiguredWithoutApiKeys() {
        assertTrue(new MusicBrainzMetadataProvider(path -> "{}").configured());
    }

    @Test
    public void wrapsUnavailableMusicBrainzAsLookupException() {
        MusicBrainzMetadataProvider provider = new MusicBrainzMetadataProvider(path -> {
            throw new IOException("offline");
        });

        assertThrows(BandMetadataLookupException.class, () -> provider.search("Any Given Day"));
    }

    private static final class FakeMusicBrainzClient implements MusicBrainzMetadataProvider.MusicBrainzHttpClient {
        private final List<String> paths = new ArrayList<>();
        private final java.util.Map<String, String> responses = new java.util.HashMap<>();

        void respond(String path, String response) {
            responses.put(path, response);
        }

        List<String> paths() {
            return List.copyOf(paths);
        }

        @Override
        public String get(String path) throws IOException {
            paths.add(path);
            if (!responses.containsKey(path)) {
                throw new IOException("No fake response for " + path);
            }
            return responses.get(path);
        }
    }
}
