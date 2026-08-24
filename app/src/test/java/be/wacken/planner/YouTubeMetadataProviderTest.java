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

public final class YouTubeMetadataProviderTest {
    @Test
    public void mapsConfiguredChannelSearchToYouTubeCandidate() throws Exception {
        FakeYouTubeClient client = new FakeYouTubeClient();
        client.respond("/search?part=snippet&q=Any+Given+Day&type=channel&maxResults=5&key=api-key", """
                {"items":[{
                  "id":{"kind":"youtube#channel","channelId":"UC-any-given-day"},
                  "snippet":{"title":"Any Given Day"}
                }]}
                """);

        List<BandMetadataProviderCandidate> candidates = new YouTubeMetadataProvider("api-key", client).search("Any Given Day");

        assertEquals(1, candidates.size());
        BandMetadataProviderCandidate candidate = candidates.get(0);
        assertEquals("Any Given Day", candidate.candidateName());
        assertEquals(Optional.of("https://www.youtube.com/channel/UC-any-given-day"), candidate.youtubeUrl());
        assertEquals(Optional.of("https://www.youtube.com/channel/UC-any-given-day"), candidate.sourceUrl());
        assertEquals(95, candidate.confidence());
    }

    @Test
    public void reportsNotConfiguredWhenApiKeyIsMissing() {
        assertFalse(new YouTubeMetadataProvider("", new FakeYouTubeClient()).configured());
    }

    @Test
    public void returnsMultipleChannelCandidatesForReviewWithoutChoosingAutomatically() throws Exception {
        FakeYouTubeClient client = new FakeYouTubeClient();
        client.respond("/search?part=snippet&q=Ghost&type=channel&maxResults=5&key=api-key", """
                {"items":[
                  {"id":{"channelId":"UC-ghost-1"},"snippet":{"title":"Ghost"}},
                  {"id":{"channelId":"UC-ghost-2"},"snippet":{"title":"Ghost Official"}}
                ]}
                """);

        List<BandMetadataProviderCandidate> candidates = new YouTubeMetadataProvider("api-key", client).search("Ghost");

        assertEquals(2, candidates.size());
        assertEquals(Optional.of("https://www.youtube.com/channel/UC-ghost-1"), candidates.get(0).youtubeUrl());
        assertEquals(Optional.of("https://www.youtube.com/channel/UC-ghost-2"), candidates.get(1).youtubeUrl());
    }

    @Test
    public void wrapsUnavailableYouTubeAsLookupException() {
        YouTubeMetadataProvider provider = new YouTubeMetadataProvider("api-key", pathAndQuery -> {
            throw new IOException("offline");
        });

        assertThrows(BandMetadataLookupException.class, () -> provider.search("Any Given Day"));
    }

    @Test
    public void frameworkDoesNotOfferYouTubeValueForExistingMetadataField() {
        FakeBandRepository bands = new FakeBandRepository(List.of(new Band(
                "Any Given Day",
                Optional.empty(),
                Optional.empty(),
                Optional.of("https://www.youtube.com/channel/existing"),
                Optional.empty()
        )));
        YouTubeMetadataProvider provider = new YouTubeMetadataProvider("api-key", pathAndQuery -> """
                {"items":[{"id":{"channelId":"UC-new"},"snippet":{"title":"Any Given Day"}}]}
                """);

        BandMetadataSearchResult result = new SearchBandMetadataUseCase(bands, List.of(provider)).search("Any Given Day");

        assertTrue(result.proposals().stream().noneMatch(proposal -> proposal.field() == BandMetadataField.YOUTUBE_URL));
        assertEquals(List.of(), result.proposals().stream().map(BandMetadataProposal::field).toList());
    }

    private static final class FakeYouTubeClient implements YouTubeMetadataProvider.YouTubeHttpClient {
        private final Map<String, String> responses = new HashMap<>();
        private final List<String> paths = new ArrayList<>();

        void respond(String pathAndQuery, String response) {
            responses.put(pathAndQuery, response);
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
