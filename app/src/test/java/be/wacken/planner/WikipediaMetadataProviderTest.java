package be.wacken.planner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import be.wacken.planner.application.BandMetadataLookupException;
import be.wacken.planner.application.BandMetadataProviderCandidate;

public final class WikipediaMetadataProviderTest {
    @Test
    public void mapsPageSummaryToBiographyImageAndSourceCandidate() throws Exception {
        FakeWikimediaClient client = new FakeWikimediaClient();
        client.respond("https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=Any+Given+Day&srlimit=5&format=json", """
                {"query":{"search":[{"title":"Any Given Day"}]}}
                """);
        client.respond("https://en.wikipedia.org/api/rest_v1/page/summary/Any%20Given%20Day", """
                {
                  "title":"Any Given Day",
                  "type":"standard",
                  "extract":"Any Given Day is a German metalcore band.",
                  "originalimage":{"source":"https://upload.wikimedia.org/agd.jpg"},
                  "content_urls":{"desktop":{"page":"https://en.wikipedia.org/wiki/Any_Given_Day"}}
                }
                """);

        List<BandMetadataProviderCandidate> candidates = new WikipediaMetadataProvider(client).search("Any Given Day");

        assertEquals(1, candidates.size());
        BandMetadataProviderCandidate candidate = candidates.get(0);
        assertEquals("Any Given Day", candidate.candidateName());
        assertEquals(Optional.of("Any Given Day is a German metalcore band."), candidate.biography());
        assertEquals(Optional.of("https://upload.wikimedia.org/agd.jpg"), candidate.imageUrl());
        assertEquals(Optional.of("https://en.wikipedia.org/wiki/Any_Given_Day"), candidate.sourceUrl());
        assertEquals(95, candidate.confidence());
    }

    @Test
    public void returnsMultipleSearchResultsForReviewWithoutChoosingAutomatically() throws Exception {
        FakeWikimediaClient client = new FakeWikimediaClient();
        client.respond("https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=Ghost&srlimit=5&format=json", """
                {"query":{"search":[{"title":"Ghost (Swedish band)"},{"title":"Ghost (British band)"}]}}
                """);
        client.respond("https://en.wikipedia.org/api/rest_v1/page/summary/Ghost%20%28Swedish%20band%29", summary("Ghost (Swedish band)", "Swedish band."));
        client.respond("https://en.wikipedia.org/api/rest_v1/page/summary/Ghost%20%28British%20band%29", summary("Ghost (British band)", "British band."));

        List<BandMetadataProviderCandidate> candidates = new WikipediaMetadataProvider(client).search("Ghost");

        assertEquals(2, candidates.size());
        assertEquals("Ghost (Swedish band)", candidates.get(0).candidateName());
        assertEquals("Ghost (British band)", candidates.get(1).candidateName());
        assertEquals(70, candidates.get(0).confidence());
    }

    @Test
    public void directWikidataIdentityLookupPrefersLinkedEnglishWikipediaPage() throws Exception {
        FakeWikimediaClient client = new FakeWikimediaClient();
        client.respond("https://www.wikidata.org/w/api.php?action=wbgetentities&ids=Q123&props=sitelinks&sitefilter=enwiki&format=json", """
                {"entities":{"Q123":{"sitelinks":{"enwiki":{"title":"Any Given Day"}}}}}
                """);
        client.respond("https://en.wikipedia.org/api/rest_v1/page/summary/Any%20Given%20Day", summary("Any Given Day", "German band."));

        List<BandMetadataProviderCandidate> candidates = new WikipediaMetadataProvider(client).search("Q123");

        assertEquals(1, candidates.size());
        assertEquals("Any Given Day", candidates.get(0).candidateName());
        assertEquals(100, candidates.get(0).confidence());
        assertEquals(List.of(
                "https://www.wikidata.org/w/api.php?action=wbgetentities&ids=Q123&props=sitelinks&sitefilter=enwiki&format=json",
                "https://en.wikipedia.org/api/rest_v1/page/summary/Any%20Given%20Day"
        ), client.urls());
    }

    @Test
    public void skipsDisambiguationPagesAsUnsuitableMetadata() throws Exception {
        FakeWikimediaClient client = new FakeWikimediaClient();
        client.respond("https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=Ghost&srlimit=5&format=json", """
                {"query":{"search":[{"title":"Ghost"}]}}
                """);
        client.respond("https://en.wikipedia.org/api/rest_v1/page/summary/Ghost", """
                {"title":"Ghost","type":"disambiguation","extract":"Ghost may refer to..."}
                """);

        assertTrue(new WikipediaMetadataProvider(client).search("Ghost").isEmpty());
    }

    @Test
    public void providerIsConfiguredWithoutApiKeys() {
        assertTrue(new WikipediaMetadataProvider((baseUrl, pathAndQuery) -> "{}").configured());
    }

    @Test
    public void wrapsUnavailableWikipediaAsLookupException() {
        WikipediaMetadataProvider provider = new WikipediaMetadataProvider((baseUrl, pathAndQuery) -> {
            throw new IOException("offline");
        });

        assertThrows(BandMetadataLookupException.class, () -> provider.search("Any Given Day"));
    }

    private static String summary(String title, String extract) {
        return """
                {
                  "title":"%s",
                  "type":"standard",
                  "extract":"%s",
                  "thumbnail":{"source":"https://upload.wikimedia.org/thumb.jpg"},
                  "content_urls":{"desktop":{"page":"https://en.wikipedia.org/wiki/%s"}}
                }
                """.formatted(title, extract, title.replace(" ", "_"));
    }

    private static final class FakeWikimediaClient implements WikipediaMetadataProvider.WikimediaHttpClient {
        private final List<String> urls = new ArrayList<>();
        private final Map<String, String> responses = new HashMap<>();

        void respond(String url, String response) {
            responses.put(url, response);
        }

        List<String> urls() {
            return List.copyOf(urls);
        }

        @Override
        public String get(String baseUrl, String pathAndQuery) throws IOException {
            String url = baseUrl + pathAndQuery;
            urls.add(url);
            if (!responses.containsKey(url)) {
                throw new IOException("No fake response for " + url);
            }
            return responses.get(url);
        }
    }
}
