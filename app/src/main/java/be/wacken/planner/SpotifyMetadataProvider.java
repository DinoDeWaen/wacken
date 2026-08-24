package be.wacken.planner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import be.wacken.planner.application.BandMetadataLookupException;
import be.wacken.planner.application.BandMetadataLookupProvider;
import be.wacken.planner.application.BandMetadataProviderCandidate;

final class SpotifyMetadataProvider implements BandMetadataLookupProvider {
    private static final String PROVIDER_NAME = "Spotify";
    private static final String AUTH_API = "https://accounts.spotify.com";
    private static final String WEB_API = "https://api.spotify.com";
    private static final int LIMIT = 5;

    private final String clientId;
    private final String clientSecret;
    private final SpotifyHttpClient client;
    private String accessToken;
    private long tokenExpiresAtMillis;

    SpotifyMetadataProvider(String clientId, String clientSecret) {
        this(clientId, clientSecret, new UrlConnectionSpotifyHttpClient());
    }

    SpotifyMetadataProvider(String clientId, String clientSecret, SpotifyHttpClient client) {
        this.clientId = normalize(clientId);
        this.clientSecret = normalize(clientSecret);
        this.client = client;
    }

    @Override
    public String name() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean configured() {
        return !clientId.isBlank() && !clientSecret.isBlank();
    }

    @Override
    public List<BandMetadataProviderCandidate> search(String bandName) throws BandMetadataLookupException {
        if (!configured()) {
            return List.of();
        }
        try {
            String token = bearerToken();
            String query = URLEncoder.encode("artist:\"" + bandName + "\"", StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(client.get(WEB_API, "/v1/search?q=" + query + "&type=artist&limit=" + LIMIT, "Bearer " + token));
            JSONObject artists = root.optJSONObject("artists");
            JSONArray items = artists == null ? null : artists.optJSONArray("items");
            if (items == null) {
                return List.of();
            }
            List<BandMetadataProviderCandidate> candidates = new ArrayList<>();
            for (int index = 0; index < items.length(); index++) {
                JSONObject artist = items.getJSONObject(index);
                String name = artist.optString("name", "").trim();
                if (name.isBlank()) {
                    continue;
                }
                Optional<String> spotifyUrl = nestedString(artist, "external_urls", "spotify");
                candidates.add(new BandMetadataProviderCandidate(
                        name,
                        Optional.empty(),
                        imageFrom(artist),
                        Optional.empty(),
                        spotifyUrl,
                        spotifyUrl,
                        confidenceFor(name, bandName, artist.optInt("popularity", 0))
                ));
            }
            return candidates;
        } catch (IOException | JSONException error) {
            throw new BandMetadataLookupException("Spotify lookup failed.", error);
        }
    }

    private String bearerToken() throws IOException, JSONException {
        long now = System.currentTimeMillis();
        if (accessToken != null && now < tokenExpiresAtMillis) {
            return accessToken;
        }
        String basic = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
        JSONObject root = new JSONObject(client.postForm(AUTH_API, "/api/token", "Basic " + basic, "grant_type=client_credentials"));
        accessToken = root.getString("access_token");
        int expiresInSeconds = Math.max(60, root.optInt("expires_in", 3600));
        tokenExpiresAtMillis = now + ((long) expiresInSeconds - 30L) * 1000L;
        return accessToken;
    }

    private Optional<String> imageFrom(JSONObject artist) {
        JSONArray images = artist.optJSONArray("images");
        if (images == null || images.length() == 0) {
            return Optional.empty();
        }
        JSONObject image = images.optJSONObject(0);
        if (image == null) {
            return Optional.empty();
        }
        return Optional.of(image.optString("url", "").trim()).filter(value -> !value.isBlank());
    }

    private Optional<String> nestedString(JSONObject object, String childName, String fieldName) {
        JSONObject child = object.optJSONObject(childName);
        if (child == null) {
            return Optional.empty();
        }
        return Optional.of(child.optString(fieldName, "").trim()).filter(value -> !value.isBlank());
    }

    private int confidenceFor(String candidateName, String searchTerm, int popularity) {
        if (candidateName.equalsIgnoreCase(searchTerm == null ? "" : searchTerm.trim())) {
            return 95;
        }
        return popularity > 0 ? popularity : 70;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    interface SpotifyHttpClient {
        String postForm(String baseUrl, String path, String authorization, String body) throws IOException;

        String get(String baseUrl, String pathAndQuery, String authorization) throws IOException;
    }

    private static final class UrlConnectionSpotifyHttpClient implements SpotifyHttpClient {
        @Override
        public String postForm(String baseUrl, String path, String authorization, String body) throws IOException {
            HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + path).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", authorization);
            connection.setRequestProperty("User-Agent", BuildConfig.MUSICBRAINZ_USER_AGENT);
            connection.setDoOutput(true);
            try (OutputStream stream = connection.getOutputStream()) {
                stream.write(body.getBytes(StandardCharsets.UTF_8));
            }
            return readResponse(connection, "Spotify auth");
        }

        @Override
        public String get(String baseUrl, String pathAndQuery, String authorization) throws IOException {
            HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + pathAndQuery).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", authorization);
            connection.setRequestProperty("User-Agent", BuildConfig.MUSICBRAINZ_USER_AGENT);
            return readResponse(connection, "Spotify");
        }

        private String readResponse(HttpURLConnection connection, String source) throws IOException {
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(10_000);
            int status = connection.getResponseCode();
            String response = read(status < 400 ? connection.getInputStream() : connection.getErrorStream());
            if (status >= 400) {
                throw new IOException(source + " returned status " + status);
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
