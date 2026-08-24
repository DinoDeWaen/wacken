package be.wacken.planner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import be.wacken.planner.application.BandMetadataLookupException;
import be.wacken.planner.application.BandMetadataLookupProvider;
import be.wacken.planner.application.BandMetadataProviderCandidate;

final class WikipediaMetadataProvider implements BandMetadataLookupProvider {
    private static final String PROVIDER_NAME = "Wikipedia";
    private static final String WIKIPEDIA_ACTION_API = "https://en.wikipedia.org/w/api.php";
    private static final String WIKIPEDIA_REST_API = "https://en.wikipedia.org/api/rest_v1";
    private static final String WIKIDATA_ACTION_API = "https://www.wikidata.org/w/api.php";
    private static final Pattern WIKIDATA_ENTITY_ID = Pattern.compile("Q\\d+");
    private static final int LIMIT = 5;

    private final WikimediaHttpClient client;

    WikipediaMetadataProvider() {
        this(new UrlConnectionWikimediaHttpClient());
    }

    WikipediaMetadataProvider(WikimediaHttpClient client) {
        this.client = client;
    }

    @Override
    public String name() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean configured() {
        return true;
    }

    @Override
    public List<BandMetadataProviderCandidate> search(String bandName) throws BandMetadataLookupException {
        try {
            List<PageSearchResult> pages = pageSearchResults(bandName);
            List<BandMetadataProviderCandidate> candidates = new ArrayList<>();
            for (PageSearchResult page : pages) {
                candidateFrom(page).ifPresent(candidates::add);
            }
            return candidates;
        } catch (IOException | JSONException error) {
            throw new BandMetadataLookupException("Wikipedia lookup failed.", error);
        }
    }

    private List<PageSearchResult> pageSearchResults(String searchTerm) throws IOException, JSONException {
        String trimmed = searchTerm == null ? "" : searchTerm.trim();
        if (trimmed.isBlank()) {
            return List.of();
        }
        if (WIKIDATA_ENTITY_ID.matcher(trimmed).matches()) {
            return titleFromWikidata(trimmed)
                    .map(title -> List.of(new PageSearchResult(title, 100)))
                    .orElseGet(List::of);
        }
        String encoded = URLEncoder.encode(trimmed, StandardCharsets.UTF_8);
        JSONObject root = new JSONObject(client.get(WIKIPEDIA_ACTION_API, "?action=query&list=search&srsearch=" + encoded + "&srlimit=" + LIMIT + "&format=json"));
        JSONObject query = root.optJSONObject("query");
        JSONArray search = query == null ? null : query.optJSONArray("search");
        if (search == null) {
            return List.of();
        }
        List<PageSearchResult> results = new ArrayList<>();
        for (int index = 0; index < search.length(); index++) {
            JSONObject item = search.getJSONObject(index);
            String title = item.optString("title", "").trim();
            if (title.isBlank()) {
                continue;
            }
            results.add(new PageSearchResult(title, scoreFor(title, trimmed)));
        }
        return results;
    }

    private Optional<String> titleFromWikidata(String entityId) throws IOException, JSONException {
        JSONObject root = new JSONObject(client.get(WIKIDATA_ACTION_API, "?action=wbgetentities&ids=" + entityId + "&props=sitelinks&sitefilter=enwiki&format=json"));
        JSONObject entities = root.optJSONObject("entities");
        JSONObject entity = entities == null ? null : entities.optJSONObject(entityId);
        JSONObject sitelinks = entity == null ? null : entity.optJSONObject("sitelinks");
        JSONObject enwiki = sitelinks == null ? null : sitelinks.optJSONObject("enwiki");
        return Optional.ofNullable(enwiki)
                .map(link -> link.optString("title", "").trim())
                .filter(title -> !title.isBlank());
    }

    private Optional<BandMetadataProviderCandidate> candidateFrom(PageSearchResult page) throws IOException, JSONException {
        JSONObject summary = new JSONObject(client.get(WIKIPEDIA_REST_API, "/page/summary/" + encodePathSegment(page.title())));
        String type = summary.optString("type", "").trim();
        if ("disambiguation".equals(type)) {
            return Optional.empty();
        }
        String title = summary.optString("title", page.title()).trim();
        Optional<String> biography = Optional.of(summary.optString("extract", "").trim()).filter(value -> !value.isBlank());
        Optional<String> image = imageFrom(summary);
        Optional<String> source = sourceFrom(summary, title);
        if (biography.isEmpty() && image.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new BandMetadataProviderCandidate(
                title.isBlank() ? page.title() : title,
                biography,
                image,
                Optional.empty(),
                Optional.empty(),
                source,
                page.confidence()
        ));
    }

    private Optional<String> imageFrom(JSONObject summary) {
        return nestedString(summary, "originalimage", "source")
                .or(() -> nestedString(summary, "thumbnail", "source"));
    }

    private Optional<String> sourceFrom(JSONObject summary, String title) {
        return Optional.ofNullable(summary.optJSONObject("content_urls"))
                .map(urls -> urls.optJSONObject("desktop"))
                .flatMap(desktop -> Optional.of(desktop.optString("page", "").trim()).filter(value -> !value.isBlank()))
                .or(() -> Optional.of("https://en.wikipedia.org/wiki/" + encodePathSegment(title)));
    }

    private Optional<String> nestedString(JSONObject object, String childName, String fieldName) {
        JSONObject child = object.optJSONObject(childName);
        if (child == null) {
            return Optional.empty();
        }
        return Optional.of(child.optString(fieldName, "").trim()).filter(value -> !value.isBlank());
    }

    private int scoreFor(String title, String searchTerm) {
        if (title != null && title.equalsIgnoreCase(searchTerm)) {
            return 95;
        }
        return 70;
    }

    private String encodePathSegment(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    interface WikimediaHttpClient {
        String get(String baseUrl, String pathAndQuery) throws IOException;
    }

    private record PageSearchResult(String title, int confidence) {
    }

    private static final class UrlConnectionWikimediaHttpClient implements WikimediaHttpClient {
        @Override
        public String get(String baseUrl, String pathAndQuery) throws IOException {
            HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + pathAndQuery).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", BuildConfig.MUSICBRAINZ_USER_AGENT);
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(10_000);
            int status = connection.getResponseCode();
            String response = read(status < 400 ? connection.getInputStream() : connection.getErrorStream());
            if (status >= 400) {
                throw new IOException("Wikimedia returned status " + status);
            }
            return response;
        }

        private String read(InputStream stream) throws IOException {
            if (stream == null) {
                return "";
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        }
    }
}
