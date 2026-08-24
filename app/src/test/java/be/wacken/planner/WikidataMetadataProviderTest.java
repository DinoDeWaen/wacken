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

public final class WikidataMetadataProviderTest {
    @Test
    public void mapsEntityClaimsToProviderCandidateMetadata() throws Exception {
        FakeWikidataClient client = new FakeWikidataClient();
        client.respond("?action=wbsearchentities&search=Any+Given+Day&language=en&type=item&limit=5&format=json", """
                {"search":[{"id":"Q123","label":"Any Given Day"}]}
                """);
        client.respond("?action=wbgetentities&ids=Q123&props=labels%7Cdescriptions%7Cclaims&languages=en&format=json", """
                {"entities":{"Q123":{
                  "labels":{"en":{"value":"Any Given Day"}},
                  "claims":{
                    "P18":[{"mainsnak":{"datavalue":{"value":"Any Given Day Rockharz 2016.jpg"}}}],
                    "P1902":[{"mainsnak":{"datavalue":{"value":"spotify-artist-id"}}}],
                    "P2397":[{"mainsnak":{"datavalue":{"value":"UC-any-given-day"}}}]
                  }
                }}}
                """);

        List<BandMetadataProviderCandidate> candidates = new WikidataMetadataProvider(client).search("Any Given Day");

        assertEquals(1, candidates.size());
        BandMetadataProviderCandidate candidate = candidates.get(0);
        assertEquals("Any Given Day", candidate.candidateName());
        assertEquals(Optional.of("https://commons.wikimedia.org/wiki/Special:FilePath/Any%20Given%20Day%20Rockharz%202016.jpg"), candidate.imageUrl());
        assertEquals(Optional.of("https://open.spotify.com/artist/spotify-artist-id"), candidate.spotifyUrl());
        assertEquals(Optional.of("https://www.youtube.com/channel/UC-any-given-day"), candidate.youtubeUrl());
        assertEquals(Optional.of("https://www.wikidata.org/wiki/Q123"), candidate.sourceUrl());
        assertEquals(95, candidate.confidence());
    }

    @Test
    public void returnsMultipleEntitiesForReviewWithoutChoosingAutomatically() throws Exception {
        FakeWikidataClient client = new FakeWikidataClient();
        client.respond("?action=wbsearchentities&search=Ghost&language=en&type=item&limit=5&format=json", """
                {"search":[{"id":"Q1","label":"Ghost"},{"id":"Q2","label":"Ghost"}]}
                """);
        client.respond("?action=wbgetentities&ids=Q1&props=labels%7Cdescriptions%7Cclaims&languages=en&format=json", entity("Q1", "ghost-one"));
        client.respond("?action=wbgetentities&ids=Q2&props=labels%7Cdescriptions%7Cclaims&languages=en&format=json", entity("Q2", "ghost-two"));

        List<BandMetadataProviderCandidate> candidates = new WikidataMetadataProvider(client).search("Ghost");

        assertEquals(2, candidates.size());
        assertEquals(Optional.of("https://open.spotify.com/artist/ghost-one"), candidates.get(0).spotifyUrl());
        assertEquals(Optional.of("https://open.spotify.com/artist/ghost-two"), candidates.get(1).spotifyUrl());
    }

    @Test
    public void directEntityIdLookupSkipsBroadSearchWhenIdentityIsAvailable() throws Exception {
        FakeWikidataClient client = new FakeWikidataClient();
        client.respond("?action=wbgetentities&ids=Q123&props=labels%7Cdescriptions%7Cclaims&languages=en&format=json", entity("Q123", "spotify-artist-id"));

        List<BandMetadataProviderCandidate> candidates = new WikidataMetadataProvider(client).search("Q123");

        assertEquals(1, candidates.size());
        assertEquals(List.of("?action=wbgetentities&ids=Q123&props=labels%7Cdescriptions%7Cclaims&languages=en&format=json"), client.paths());
    }

    @Test
    public void providerIsConfiguredWithoutApiKeys() {
        assertTrue(new WikidataMetadataProvider(path -> "{}").configured());
    }

    @Test
    public void wrapsUnavailableWikidataAsLookupException() {
        WikidataMetadataProvider provider = new WikidataMetadataProvider(path -> {
            throw new IOException("offline");
        });

        assertThrows(BandMetadataLookupException.class, () -> provider.search("Any Given Day"));
    }

    private static String entity(String id, String spotifyId) {
        return """
                {"entities":{"%s":{
                  "labels":{"en":{"value":"Ghost"}},
                  "claims":{"P1902":[{"mainsnak":{"datavalue":{"value":"%s"}}}]}
                }}}
                """.formatted(id, spotifyId);
    }

    private static final class FakeWikidataClient implements WikidataMetadataProvider.WikidataHttpClient {
        private final List<String> paths = new ArrayList<>();
        private final java.util.Map<String, String> responses = new java.util.HashMap<>();

        void respond(String path, String response) {
            responses.put(path, response);
        }

        List<String> paths() {
            return List.copyOf(paths);
        }

        @Override
        public String get(String pathAndQuery) throws IOException {
            paths.add(pathAndQuery);
            if (!responses.containsKey(pathAndQuery)) {
                throw new IOException("No fake response for " + pathAndQuery);
            }
            return responses.get(pathAndQuery);
        }
    }
}
