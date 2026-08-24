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

final class MusicBrainzMetadataProvider implements BandMetadataLookupProvider {
    private static final String PROVIDER_NAME = "MusicBrainz";
    private static final String API_ROOT = "https://musicbrainz.org/ws/2";
    private static final String ARTIST_ROOT = "https://musicbrainz.org/artist/";
    private static final int LIMIT = 5;

    private final MusicBrainzHttpClient client;

    MusicBrainzMetadataProvider(String userAgent) {
        this(new RateLimitedMusicBrainzHttpClient(userAgent));
    }

    MusicBrainzMetadataProvider(MusicBrainzHttpClient client) {
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
            List<ArtistSearchResult> artists = artists(bandName);
            List<BandMetadataProviderCandidate> candidates = new ArrayList<>();
            for (ArtistSearchResult artist : artists) {
                candidates.add(candidateFrom(artist, artistDetails(artist.id())));
            }
            return candidates;
        } catch (IOException | JSONException error) {
            throw new BandMetadataLookupException("MusicBrainz lookup failed.", error);
        }
    }

    private List<ArtistSearchResult> artists(String bandName) throws IOException, JSONException {
        String query = URLEncoder.encode("artist:\"" + bandName + "\"", StandardCharsets.UTF_8);
        JSONObject root = new JSONObject(client.get("/artist?query=" + query + "&limit=" + LIMIT + "&fmt=json"));
        JSONArray artists = root.optJSONArray("artists");
        if (artists == null) {
            return List.of();
        }
        List<ArtistSearchResult> results = new ArrayList<>();
        for (int index = 0; index < artists.length(); index++) {
            JSONObject artist = artists.getJSONObject(index);
            String id = artist.optString("id", "").trim();
            String name = artist.optString("name", "").trim();
            if (id.isBlank() || name.isBlank()) {
                continue;
            }
            results.add(new ArtistSearchResult(id, name, artist.optInt("score", 0)));
        }
        return results;
    }

    private JSONObject artistDetails(String artistId) throws IOException, JSONException {
        return new JSONObject(client.get("/artist/" + artistId + "?inc=url-rels+aliases&fmt=json"));
    }

    private BandMetadataProviderCandidate candidateFrom(ArtistSearchResult artist, JSONObject details) {
        return new BandMetadataProviderCandidate(
                artist.name(),
                Optional.empty(),
                Optional.empty(),
                relationshipUrl(details, "youtube.com", "youtu.be"),
                relationshipUrl(details, "spotify.com"),
                Optional.of(ARTIST_ROOT + artist.id()),
                artist.score()
        );
    }

    private Optional<String> relationshipUrl(JSONObject details, String... domains) {
        JSONArray relations = details.optJSONArray("relations");
        if (relations == null) {
            return Optional.empty();
        }
        for (int index = 0; index < relations.length(); index++) {
            JSONObject relation = relations.optJSONObject(index);
            if (relation == null) {
                continue;
            }
            JSONObject url = relation.optJSONObject("url");
            if (url == null) {
                continue;
            }
            String resource = url.optString("resource", "").trim();
            if (resource.isBlank()) {
                continue;
            }
            String normalized = resource.toLowerCase();
            for (String domain : domains) {
                if (normalized.contains(domain)) {
                    return Optional.of(resource);
                }
            }
        }
        return Optional.empty();
    }

    interface MusicBrainzHttpClient {
        String get(String path) throws IOException;
    }

    private record ArtistSearchResult(String id, String name, int score) {
    }

    private static final class RateLimitedMusicBrainzHttpClient implements MusicBrainzHttpClient {
        private final String userAgent;
        private long lastRequestAtMillis;

        private RateLimitedMusicBrainzHttpClient(String userAgent) {
            this.userAgent = userAgent == null || userAgent.isBlank()
                    ? "WackenPlanner/2.30 ( local-maintainer )"
                    : userAgent.trim();
        }

        @Override
        public synchronized String get(String path) throws IOException {
            throttle();
            HttpURLConnection connection = (HttpURLConnection) new URL(API_ROOT + path).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(10_000);
            int status = connection.getResponseCode();
            String response = read(status < 400 ? connection.getInputStream() : connection.getErrorStream());
            if (status >= 400) {
                throw new IOException("MusicBrainz returned status " + status);
            }
            return response;
        }

        private void throttle() {
            long now = System.currentTimeMillis();
            long waitMillis = 1_000 - (now - lastRequestAtMillis);
            if (waitMillis > 0) {
                try {
                    Thread.sleep(waitMillis);
                } catch (InterruptedException error) {
                    Thread.currentThread().interrupt();
                }
            }
            lastRequestAtMillis = System.currentTimeMillis();
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
