package be.wacken.planner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import be.wacken.planner.application.BandMetadataField;
import be.wacken.planner.application.BandMetadataLookupException;
import be.wacken.planner.application.BandMetadataProviderCandidate;
import be.wacken.planner.application.BandMetadataProposal;
import be.wacken.planner.application.BandMetadataSearchResult;
import be.wacken.planner.application.SearchBandMetadataUseCase;
import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;

public final class SpotifyMetadataProviderTest {
    @Test
    public void mapsConfiguredArtistSearchToSpotifyAndImageCandidate() throws Exception {
        FakeSpotifyClient client = new FakeSpotifyClient();
        client.respondPost("https://accounts.spotify.com/api/token", """
                {"access_token":"token-123","token_type":"Bearer","expires_in":3600}
                """);
        client.respondGet("https://api.spotify.com/v1/search?q=artist%3A%22Any+Given+Day%22&type=artist&limit=5", """
                {"artists":{"items":[{
                  "name":"Any Given Day",
                  "popularity":74,
                  "external_urls":{"spotify":"https://open.spotify.com/artist/agd"},
                  "images":[{"url":"https://i.scdn.co/image/agd-large"}]
                }]}}
                """);

        List<BandMetadataProviderCandidate> candidates = new SpotifyMetadataProvider("client-id", "client-secret", client).search("Any Given Day");

        assertEquals(1, candidates.size());
        BandMetadataProviderCandidate candidate = candidates.get(0);
        assertEquals("Any Given Day", candidate.candidateName());
        assertEquals(Optional.of("https://open.spotify.com/artist/agd"), candidate.spotifyUrl());
        assertEquals(Optional.of("https://i.scdn.co/image/agd-large"), candidate.imageUrl());
        assertEquals(Optional.of("https://open.spotify.com/artist/agd"), candidate.sourceUrl());
        assertEquals(95, candidate.confidence());
        assertEquals(List.of(
                "POST https://accounts.spotify.com/api/token",
                "GET https://api.spotify.com/v1/search?q=artist%3A%22Any+Given+Day%22&type=artist&limit=5"
        ), client.calls());
    }

    @Test
    public void reportsNotConfiguredWhenCredentialsAreMissing() {
        assertFalse(new SpotifyMetadataProvider("", "client-secret", new FakeSpotifyClient()).configured());
        assertFalse(new SpotifyMetadataProvider("client-id", "", new FakeSpotifyClient()).configured());
    }

    @Test
    public void returnsMultipleArtistCandidatesForReviewWithoutChoosingAutomatically() throws Exception {
        FakeSpotifyClient client = configuredClient("""
                {"artists":{"items":[
                  {"name":"Ghost","popularity":91,"external_urls":{"spotify":"https://open.spotify.com/artist/ghost-1"},"images":[]},
                  {"name":"Ghost","popularity":63,"external_urls":{"spotify":"https://open.spotify.com/artist/ghost-2"},"images":[]}
                ]}}
                """);

        List<BandMetadataProviderCandidate> candidates = new SpotifyMetadataProvider("client-id", "client-secret", client).search("Ghost");

        assertEquals(2, candidates.size());
        assertEquals(Optional.of("https://open.spotify.com/artist/ghost-1"), candidates.get(0).spotifyUrl());
        assertEquals(Optional.of("https://open.spotify.com/artist/ghost-2"), candidates.get(1).spotifyUrl());
    }

    @Test
    public void wrapsUnavailableSpotifyAsLookupException() {
        SpotifyMetadataProvider provider = new SpotifyMetadataProvider("client-id", "client-secret", new SpotifyMetadataProvider.SpotifyHttpClient() {
            @Override
            public String postForm(String baseUrl, String path, String authorization, String body) throws IOException {
                throw new IOException("offline");
            }

            @Override
            public String get(String baseUrl, String pathAndQuery, String authorization) {
                return "{}";
            }
        });

        assertThrows(BandMetadataLookupException.class, () -> provider.search("Any Given Day"));
    }

    @Test
    public void frameworkDoesNotOfferSpotifyValuesForExistingMetadataFields() {
        FakeBandRepository bands = new FakeBandRepository(List.of(new Band(
                "Any Given Day",
                Optional.empty(),
                Optional.of("https://images.test/existing.jpg"),
                Optional.empty(),
                Optional.of("https://open.spotify.com/artist/existing")
        )));
        SpotifyMetadataProvider provider = new SpotifyMetadataProvider("client-id", "client-secret", new StaticSpotifyClient());

        BandMetadataSearchResult result = new SearchBandMetadataUseCase(bands, List.of(provider)).search("Any Given Day");

        assertTrue(result.proposals().stream().noneMatch(proposal -> proposal.field() == BandMetadataField.IMAGE_URL));
        assertTrue(result.proposals().stream().noneMatch(proposal -> proposal.field() == BandMetadataField.SPOTIFY_URL));
        assertEquals(List.of(), result.proposals().stream().map(BandMetadataProposal::field).toList());
    }

    private static FakeSpotifyClient configuredClient(String searchResponse) {
        FakeSpotifyClient client = new FakeSpotifyClient();
        client.respondPost("https://accounts.spotify.com/api/token", """
                {"access_token":"token-123","token_type":"Bearer","expires_in":3600}
                """);
        client.respondGet("https://api.spotify.com/v1/search?q=artist%3A%22Ghost%22&type=artist&limit=5", searchResponse);
        return client;
    }

    private static final class FakeSpotifyClient implements SpotifyMetadataProvider.SpotifyHttpClient {
        private final List<String> calls = new ArrayList<>();
        private final Map<String, String> postResponses = new HashMap<>();
        private final Map<String, String> getResponses = new HashMap<>();

        void respondPost(String url, String response) {
            postResponses.put(url, response);
        }

        void respondGet(String url, String response) {
            getResponses.put(url, response);
        }

        List<String> calls() {
            return List.copyOf(calls);
        }

        @Override
        public String postForm(String baseUrl, String path, String authorization, String body) throws IOException {
            String url = baseUrl + path;
            calls.add("POST " + url);
            if (!postResponses.containsKey(url)) {
                throw new IOException("No fake POST response for " + url);
            }
            return postResponses.get(url);
        }

        @Override
        public String get(String baseUrl, String pathAndQuery, String authorization) throws IOException {
            String url = baseUrl + pathAndQuery;
            calls.add("GET " + url);
            if (!getResponses.containsKey(url)) {
                throw new IOException("No fake GET response for " + url);
            }
            return getResponses.get(url);
        }
    }

    private static final class StaticSpotifyClient implements SpotifyMetadataProvider.SpotifyHttpClient {
        @Override
        public String postForm(String baseUrl, String path, String authorization, String body) {
            return "{\"access_token\":\"token-123\",\"expires_in\":3600}";
        }

        @Override
        public String get(String baseUrl, String pathAndQuery, String authorization) {
            return """
                    {"artists":{"items":[{
                      "name":"Any Given Day",
                      "external_urls":{"spotify":"https://open.spotify.com/artist/new"},
                      "images":[{"url":"https://images.test/new.jpg"}]
                    }]}}
                    """;
        }
    }

    private static final class FakeBandRepository implements BandRepository {
        private final List<Band> bands = new ArrayList<>();

        FakeBandRepository(List<Band> bands) {
            this.bands.addAll(bands);
        }

        @Override
        public void save(Band band) {
            bands.removeIf(existing -> existing.name().equals(band.name()));
            bands.add(band);
        }

        @Override
        public void replaceAll(List<Band> bands) {
            this.bands.clear();
            this.bands.addAll(bands);
        }

        @Override
        public Optional<Band> findByName(String name) {
            return bands.stream().filter(band -> band.name().equals(name)).findFirst();
        }

        @Override
        public List<Band> findAll() {
            return List.copyOf(bands);
        }
    }
}
