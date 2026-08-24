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

final class WikidataMetadataProvider implements BandMetadataLookupProvider {
    private static final String PROVIDER_NAME = "Wikidata";
    private static final String API_ROOT = "https://www.wikidata.org/w/api.php";
    private static final String ENTITY_ROOT = "https://www.wikidata.org/wiki/";
    private static final String COMMONS_FILE_ROOT = "https://commons.wikimedia.org/wiki/Special:FilePath/";
    private static final String IMAGE_PROPERTY = "P18";
    private static final String SPOTIFY_ARTIST_PROPERTY = "P1902";
    private static final String YOUTUBE_CHANNEL_PROPERTY = "P2397";
    private static final Pattern ENTITY_ID = Pattern.compile("Q\\d+");
    private static final int LIMIT = 5;

    private final WikidataHttpClient client;

    WikidataMetadataProvider() {
        this(new UrlConnectionWikidataHttpClient());
    }

    WikidataMetadataProvider(WikidataHttpClient client) {
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
            List<EntitySearchResult> entities = entitySearchResults(bandName);
            List<BandMetadataProviderCandidate> candidates = new ArrayList<>();
            for (EntitySearchResult entity : entities) {
                JSONObject details = entityDetails(entity.id());
                candidates.add(candidateFrom(entity, details));
            }
            return candidates;
        } catch (IOException | JSONException error) {
            throw new BandMetadataLookupException("Wikidata lookup failed.", error);
        }
    }

    private List<EntitySearchResult> entitySearchResults(String searchTerm) throws IOException, JSONException {
        String trimmed = searchTerm == null ? "" : searchTerm.trim();
        if (ENTITY_ID.matcher(trimmed).matches()) {
            return List.of(new EntitySearchResult(trimmed, trimmed, 100));
        }
        String encoded = URLEncoder.encode(trimmed, StandardCharsets.UTF_8);
        JSONObject root = new JSONObject(client.get("?action=wbsearchentities&search=" + encoded + "&language=en&type=item&limit=" + LIMIT + "&format=json"));
        JSONArray search = root.optJSONArray("search");
        if (search == null) {
            return List.of();
        }
        List<EntitySearchResult> results = new ArrayList<>();
        for (int index = 0; index < search.length(); index++) {
            JSONObject item = search.getJSONObject(index);
            String id = item.optString("id", "").trim();
            String label = item.optString("label", id).trim();
            if (id.isBlank()) {
                continue;
            }
            results.add(new EntitySearchResult(id, label.isBlank() ? id : label, scoreFor(label, trimmed)));
        }
        return results;
    }

    private int scoreFor(String label, String searchTerm) {
        if (label != null && label.equalsIgnoreCase(searchTerm)) {
            return 95;
        }
        return 70;
    }

    private JSONObject entityDetails(String entityId) throws IOException, JSONException {
        JSONObject root = new JSONObject(client.get("?action=wbgetentities&ids=" + entityId + "&props=labels%7Cdescriptions%7Cclaims&languages=en&format=json"));
        JSONObject entities = root.optJSONObject("entities");
        if (entities == null) {
            return new JSONObject();
        }
        return entities.optJSONObject(entityId) == null ? new JSONObject() : entities.getJSONObject(entityId);
    }

    private BandMetadataProviderCandidate candidateFrom(EntitySearchResult entity, JSONObject details) {
        String label = label(details).orElse(entity.label());
        return new BandMetadataProviderCandidate(
                label,
                Optional.empty(),
                claimValue(details, IMAGE_PROPERTY).map(this::commonsFileUrl),
                claimValue(details, YOUTUBE_CHANNEL_PROPERTY).map(id -> "https://www.youtube.com/channel/" + id),
                claimValue(details, SPOTIFY_ARTIST_PROPERTY).map(id -> "https://open.spotify.com/artist/" + id),
                Optional.of(ENTITY_ROOT + entity.id()),
                entity.confidence()
        );
    }

    private Optional<String> label(JSONObject details) {
        JSONObject labels = details.optJSONObject("labels");
        if (labels == null) {
            return Optional.empty();
        }
        JSONObject english = labels.optJSONObject("en");
        if (english == null) {
            return Optional.empty();
        }
        return Optional.of(english.optString("value", "").trim()).filter(value -> !value.isBlank());
    }

    private Optional<String> claimValue(JSONObject details, String property) {
        JSONObject claims = details.optJSONObject("claims");
        if (claims == null) {
            return Optional.empty();
        }
        JSONArray values = claims.optJSONArray(property);
        if (values == null || values.length() == 0) {
            return Optional.empty();
        }
        for (int index = 0; index < values.length(); index++) {
            JSONObject claim = values.optJSONObject(index);
            if (claim == null) {
                continue;
            }
            JSONObject mainsnak = claim.optJSONObject("mainsnak");
            if (mainsnak == null) {
                continue;
            }
            JSONObject datavalue = mainsnak.optJSONObject("datavalue");
            if (datavalue == null) {
                continue;
            }
            Object value = datavalue.opt("value");
            if (value instanceof String text && !text.isBlank()) {
                return Optional.of(text.trim());
            }
        }
        return Optional.empty();
    }

    private String commonsFileUrl(String filename) {
        return COMMONS_FILE_ROOT + URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
    }

    interface WikidataHttpClient {
        String get(String pathAndQuery) throws IOException;
    }

    private record EntitySearchResult(String id, String label, int confidence) {
    }

    private static final class UrlConnectionWikidataHttpClient implements WikidataHttpClient {
        @Override
        public String get(String pathAndQuery) throws IOException {
            HttpURLConnection connection = (HttpURLConnection) new URL(API_ROOT + pathAndQuery).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", BuildConfig.MUSICBRAINZ_USER_AGENT);
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(10_000);
            int status = connection.getResponseCode();
            String response = read(status < 400 ? connection.getInputStream() : connection.getErrorStream());
            if (status >= 400) {
                throw new IOException("Wikidata returned status " + status);
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
