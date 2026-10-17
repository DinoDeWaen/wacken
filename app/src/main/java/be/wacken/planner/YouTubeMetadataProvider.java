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

import be.wacken.planner.application.BandMetadataLookupException;
import be.wacken.planner.application.BandMetadataLookupProvider;
import be.wacken.planner.application.BandMetadataProviderCandidate;

final class YouTubeMetadataProvider implements BandMetadataLookupProvider {
    private static final String PROVIDER_NAME = "YouTube";
    private static final String API_ROOT = "https://www.googleapis.com/youtube/v3";
    private static final String CHANNEL_ROOT = "https://www.youtube.com/channel/";
    private static final int LIMIT = 5;

    private final String apiKey;
    private final YouTubeHttpClient client;

    YouTubeMetadataProvider(String apiKey) {
        this(apiKey, new UrlConnectionYouTubeHttpClient());
    }

    YouTubeMetadataProvider(String apiKey, YouTubeHttpClient client) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.client = client;
    }

    @Override
    public String name() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean configured() {
        return !apiKey.isBlank();
    }

    @Override
    public List<BandMetadataProviderCandidate> search(String bandName) throws BandMetadataLookupException {
        if (!configured()) {
            return List.of();
        }
        try {
            String query = URLEncoder.encode(bandName == null ? "" : bandName.trim(), StandardCharsets.UTF_8);
            String key = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(client.get("/search?part=snippet&q=" + query + "&type=channel&maxResults=" + LIMIT + "&key=" + key));
            JSONArray items = root.optJSONArray("items");
            if (items == null) {
                return List.of();
            }
            List<BandMetadataProviderCandidate> candidates = new ArrayList<>();
            for (int index = 0; index < items.length(); index++) {
                JSONObject item = items.getJSONObject(index);
                Optional<String> channelId = nestedString(item, "id", "channelId");
                JSONObject snippet = item.optJSONObject("snippet");
                String title = snippet == null ? "" : snippet.optString("title", "").trim();
                if (channelId.isEmpty() || title.isBlank()) {
                    continue;
                }
                String channelUrl = CHANNEL_ROOT + channelId.orElseThrow();
                candidates.add(new BandMetadataProviderCandidate(
                        title,
                        Optional.empty(),
                        Optional.empty(),
                        Optional.of(channelUrl),
                        Optional.empty(),
                        Optional.of(channelUrl),
                        confidenceFor(title, bandName)
                ));
            }
            return candidates;
        } catch (IOException | JSONException error) {
            throw new BandMetadataLookupException("YouTube lookup failed.", error);
        }
    }

    private Optional<String> nestedString(JSONObject object, String childName, String fieldName) {
        JSONObject child = object.optJSONObject(childName);
        if (child == null) {
            return Optional.empty();
        }
        return Optional.of(child.optString(fieldName, "").trim()).filter(value -> !value.isBlank());
    }

    private int confidenceFor(String candidateName, String searchTerm) {
        if (candidateName.equalsIgnoreCase(searchTerm == null ? "" : searchTerm.trim())) {
            return 95;
        }
        return 70;
    }

    interface YouTubeHttpClient {
        String get(String pathAndQuery) throws IOException;
    }

    private static final class UrlConnectionYouTubeHttpClient implements YouTubeHttpClient {
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
                throw new IOException("YouTube returned status " + status);
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
