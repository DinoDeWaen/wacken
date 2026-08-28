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
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalLineupEntry;
import be.wacken.planner.domain.FestivalStatus;
import be.wacken.planner.domain.FoodOption;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.StageDistance;

final class SupabaseMasterDataClient {
    private final SupabaseAuthenticatedRequest authenticatedRequest;

    SupabaseMasterDataClient(SupabaseSessionManager sessionManager) {
        this.authenticatedRequest = new SupabaseAuthenticatedRequest(sessionManager, "Supabase sync failed");
    }

    List<Band> bands() throws IOException {
        JSONArray rows = requestArray("/rest/v1/bands?select=id,name,biography_html,image_url,youtube_url,spotify_artist_id&active=eq.true&order=name.asc");
        List<Band> bands = new ArrayList<>();
        for (int index = 0; index < rows.length(); index++) {
            try {
                bands.add(toBand(rows.getJSONObject(index)));
            } catch (JSONException error) {
                throw new IOException("Supabase band row could not be read.", error);
            }
        }
        return bands;
    }

    List<Stage> stages() throws IOException {
        JSONArray rows = requestArray("/rest/v1/stages?select=id,name&order=name.asc");
        List<Stage> stages = new ArrayList<>();
        for (int index = 0; index < rows.length(); index++) {
            try {
                stages.add(new Stage(rows.getJSONObject(index).getString("name")));
            } catch (JSONException error) {
                throw new IOException("Supabase stage row could not be read.", error);
            }
        }
        return stages;
    }

    List<Performance> performances() throws IOException {
        Map<String, String> bandNamesById = namesById("/rest/v1/bands?select=id,name");
        Map<String, String> stageNamesById = namesById("/rest/v1/stages?select=id,name");
        JSONArray rows = requestArray("/rest/v1/performances?select=band_id,stage_id,start_at,end_at&order=start_at.asc");
        List<Performance> performances = new ArrayList<>();
        for (int index = 0; index < rows.length(); index++) {
            try {
                JSONObject row = rows.getJSONObject(index);
                String bandName = bandNamesById.get(row.getString("band_id"));
                String stageName = stageNamesById.get(row.getString("stage_id"));
                if (bandName != null && stageName != null) {
                    performances.add(new Performance(
                            new Band(bandName),
                            new Stage(stageName),
                            toLocalDateTime(row.getString("start_at")),
                            toLocalDateTime(row.getString("end_at"))
                    ));
                }
            } catch (JSONException error) {
                throw new IOException("Supabase performance row could not be read.", error);
            }
        }
        return performances;
    }

    List<StageDistance> stageDistances() throws IOException {
        Map<String, String> stageNamesById = namesById("/rest/v1/stages?select=id,name");
        JSONArray rows = requestArray("/rest/v1/stage_distances?select=from_stage_id,to_stage_id,walking_minutes");
        List<StageDistance> distances = new ArrayList<>();
        for (int index = 0; index < rows.length(); index++) {
            try {
                JSONObject row = rows.getJSONObject(index);
                String fromStageName = stageNamesById.get(row.getString("from_stage_id"));
                String toStageName = stageNamesById.get(row.getString("to_stage_id"));
                if (fromStageName != null && toStageName != null) {
                    distances.add(StageDistance.between(
                            new Stage(fromStageName),
                            new Stage(toStageName),
                            row.getInt("walking_minutes")
                    ));
                }
            } catch (JSONException error) {
                throw new IOException("Supabase stage distance row could not be read.", error);
            }
        }
        return distances;
    }

    List<FoodOption> foodOptions() throws IOException {
        JSONArray rows = requestArray("/rest/v1/food_options?select=id,name&order=name.asc");
        List<FoodOption> foodOptions = new ArrayList<>();
        for (int index = 0; index < rows.length(); index++) {
            try {
                foodOptions.add(new FoodOption(rows.getJSONObject(index).getString("name")));
            } catch (JSONException error) {
                throw new IOException("Supabase food option row could not be read.", error);
            }
        }
        return foodOptions;
    }

    List<Festival> festivals() throws IOException {
        JSONArray rows = requestArray("/rest/v1/festivals?select=id,name,status&order=status.asc,name.asc");
        List<Festival> festivals = new ArrayList<>();
        for (int index = 0; index < rows.length(); index++) {
            try {
                festivals.add(toFestival(rows.getJSONObject(index)));
            } catch (JSONException error) {
                throw new IOException("Supabase festival row could not be read.", error);
            }
        }
        return festivals;
    }

    void saveFestival(Festival festival) throws IOException {
        try {
            JSONObject body = new JSONObject()
                    .put("id", festival.id())
                    .put("name", festival.name())
                    .put("status", festival.status().name())
                    .put("archived_at", festival.isArchived() ? OffsetDateTime.now().toString() : JSONObject.NULL);
            request("POST", "/rest/v1/festivals?on_conflict=id", body.toString(), true);
        } catch (JSONException error) {
            throw new IOException("Supabase festival request could not be created.", error);
        }
    }

    List<FestivalLineupEntry> festivalLineupEntries() throws IOException {
        return festivalLineupEntriesFromEndpoint("/rest/v1/festival_lineup_entries?select=festival_id,band_id,uploaded_display_name&order=festival_id.asc,uploaded_display_name.asc");
    }

    List<FestivalLineupEntry> festivalLineupEntries(String festivalId) throws IOException {
        return festivalLineupEntriesFromEndpoint("/rest/v1/festival_lineup_entries?select=festival_id,band_id,uploaded_display_name&festival_id=eq."
                + encode(festivalId) + "&order=uploaded_display_name.asc");
    }

    void saveFestivalLineup(String festivalId, List<FestivalLineupEntry> entries) throws IOException {
        request("DELETE", "/rest/v1/festival_lineup_entries?festival_id=eq." + encode(festivalId), null, false);
        if (entries.isEmpty()) {
            return;
        }
        try {
            JSONArray rows = new JSONArray();
            for (FestivalLineupEntry entry : entries) {
                saveBand(entry.band());
                rows.put(new JSONObject()
                        .put("festival_id", festivalId)
                        .put("band_id", bandIdForWrite(entry.band()))
                        .put("uploaded_display_name", entry.uploadedDisplayName()));
            }
            request("POST", "/rest/v1/festival_lineup_entries?on_conflict=festival_id,band_id", rows.toString(), true);
        } catch (JSONException error) {
            throw new IOException("Supabase festival lineup request could not be created.", error);
        }
    }

    void saveBand(Band band) throws IOException {
        try {
            Optional<JSONObject> existing = findBandRowByName(band.name());
            if (existing.isPresent()) {
                JSONObject body = missingMetadataPatch(existing.get(), band).put("active", true);
                request("PATCH", "/rest/v1/bands?id=eq." + encode(existing.get().getString("id")), body.toString(), false);
                return;
            }
            request("POST", "/rest/v1/bands?on_conflict=id", newBandBody(band).toString(), true);
        } catch (JSONException error) {
            throw new IOException("Supabase band request could not be created.", error);
        }
    }

    static Band toBand(JSONObject row) throws JSONException {
        return new Band(
                row.getString("name"),
                optional(row.optString("biography_html", "")),
                optional(row.optString("image_url", "")),
                optional(row.optString("youtube_url", "")),
                spotifyUrl(row.optString("spotify_artist_id", ""))
        );
    }

    static Festival toFestival(JSONObject row) throws JSONException {
        return new Festival(
                row.getString("id"),
                row.getString("name"),
                FestivalStatus.valueOf(row.getString("status"))
        );
    }

    static List<FestivalLineupEntry> parseFestivalLineupEntries(JSONArray rows, Map<String, String> bandNamesById) throws JSONException {
        List<FestivalLineupEntry> entries = new ArrayList<>();
        for (int index = 0; index < rows.length(); index++) {
            JSONObject row = rows.getJSONObject(index);
            String bandName = bandNamesById.get(row.getString("band_id"));
            if (bandName != null) {
                entries.add(new FestivalLineupEntry(
                        row.getString("festival_id"),
                        new Band(bandName),
                        row.getString("uploaded_display_name")
                ));
            }
        }
        return entries;
    }

    static String bandIdFor(Band band) {
        return slug(band.name());
    }

    static Optional<String> spotifyArtistId(Optional<String> spotifyUrl) {
        return spotifyUrl
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(value -> {
                    int artistMarker = value.indexOf("/artist/");
                    if (artistMarker < 0) {
                        return value;
                    }
                    String id = value.substring(artistMarker + "/artist/".length());
                    int queryIndex = id.indexOf('?');
                    if (queryIndex >= 0) {
                        id = id.substring(0, queryIndex);
                    }
                    int slashIndex = id.indexOf('/');
                    if (slashIndex >= 0) {
                        id = id.substring(0, slashIndex);
                    }
                    return id;
                })
                .filter(value -> !value.isBlank());
    }

    static LocalDateTime toLocalDateTime(String value) {
        if (value.endsWith("Z") || value.contains("+")) {
            return OffsetDateTime.parse(value).toLocalDateTime();
        }
        return LocalDateTime.parse(value);
    }

    private Map<String, String> namesById(String endpoint) throws IOException {
        JSONArray rows = requestArray(endpoint);
        Map<String, String> namesById = new HashMap<>();
        for (int index = 0; index < rows.length(); index++) {
            try {
                JSONObject row = rows.getJSONObject(index);
                namesById.put(row.getString("id"), row.getString("name"));
            } catch (JSONException error) {
                throw new IOException("Supabase lookup row could not be read.", error);
            }
        }
        return namesById;
    }

    private List<FestivalLineupEntry> festivalLineupEntriesFromEndpoint(String endpoint) throws IOException {
        Map<String, String> bandNamesById = namesById("/rest/v1/bands?select=id,name");
        JSONArray rows = requestArray(endpoint);
        try {
            return parseFestivalLineupEntries(rows, bandNamesById);
        } catch (JSONException error) {
            throw new IOException("Supabase festival lineup row could not be read.", error);
        }
    }

    private JSONArray requestArray(String endpoint) throws IOException {
        String response = request(endpoint);
        try {
            return new JSONArray(response);
        } catch (JSONException error) {
            throw new IOException("Supabase response was not a valid JSON array.", error);
        }
    }

    private String request(String endpoint) throws IOException {
        return request("GET", endpoint, null, false);
    }

    private String request(String method, String endpoint, String body, boolean upsert) throws IOException {
        return authenticatedRequest.execute(session -> send(method, endpoint, body, session, upsert));
    }

    private SupabaseAuthenticatedRequest.Response send(String method, String endpoint, String body, AuthSession session, boolean upsert) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(SupabaseConfig.url() + endpoint).openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("apikey", SupabaseConfig.anonKey());
        connection.setRequestProperty("Authorization", "Bearer " + session.accessToken());
        connection.setRequestProperty("Content-Type", "application/json");
        if (upsert) {
            connection.setRequestProperty("Prefer", "resolution=merge-duplicates");
        }
        if (body != null) {
            connection.setDoOutput(true);
            try (OutputStream stream = connection.getOutputStream()) {
                stream.write(body.getBytes(StandardCharsets.UTF_8));
            }
        }
        int status = connection.getResponseCode();
        String response = read(status >= 400 ? connection.getErrorStream() : connection.getInputStream());
        return new SupabaseAuthenticatedRequest.Response(status, response);
    }

    private String read(InputStream input) throws IOException {
        if (input == null) {
            return "";
        }
        StringBuilder text = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line);
            }
        }
        return text.toString();
    }

    private static Optional<String> optional(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

    private static Optional<String> spotifyUrl(String artistId) {
        if (artistId == null || artistId.isBlank()) {
            return Optional.empty();
        }
        return Optional.of("https://open.spotify.com/artist/" + artistId.trim());
    }

    private String bandIdForWrite(Band band) throws IOException {
        Optional<String> existing = findBandIdByName(band.name());
        return existing.orElseGet(() -> bandIdFor(band));
    }

    private Optional<String> findBandIdByName(String name) throws IOException {
        Optional<JSONObject> row = findBandRowByName(name);
        try {
            return row.map(value -> value.optString("id", "")).filter(value -> !value.isBlank());
        } catch (RuntimeException error) {
            throw new IOException("Supabase band lookup response could not be read.", error);
        }
    }

    private Optional<JSONObject> findBandRowByName(String name) throws IOException {
        JSONArray rows = requestArray("/rest/v1/bands?select=id,name,biography_html,image_url,youtube_url,spotify_artist_id&name=eq." + encode(name) + "&limit=1");
        try {
            if (rows.length() == 0) {
                return Optional.empty();
            }
            return Optional.of(rows.getJSONObject(0));
        } catch (JSONException error) {
            throw new IOException("Supabase band lookup response could not be read.", error);
        }
    }

    private static Object valueOrNull(Optional<String> value) {
        return value.<Object>map(text -> text).orElse(JSONObject.NULL);
    }

    private static JSONObject newBandBody(Band band) throws JSONException {
        return new JSONObject()
                .put("id", bandIdFor(band))
                .put("name", band.name())
                .put("slug", slug(band.name()))
                .put("biography_html", valueOrNull(band.biography()))
                .put("image_url", valueOrNull(band.imageUrl()))
                .put("youtube_url", valueOrNull(band.youtubeUrl()))
                .put("spotify_artist_id", valueOrNull(spotifyArtistId(band.spotifyUrl())))
                .put("active", true);
    }

    static JSONObject missingMetadataPatch(JSONObject existing, Band band) throws JSONException {
        JSONObject patch = new JSONObject();
        putIfMissing(patch, existing, "biography_html", band.biography());
        putIfMissing(patch, existing, "image_url", band.imageUrl());
        putIfMissing(patch, existing, "youtube_url", band.youtubeUrl());
        putIfMissing(patch, existing, "spotify_artist_id", spotifyArtistId(band.spotifyUrl()));
        return patch;
    }

    private static void putIfMissing(JSONObject patch, JSONObject existing, String field, Optional<String> candidate) throws JSONException {
        if (candidate.isPresent() && existing.optString(field, "").isBlank()) {
            patch.put(field, candidate.get());
        }
    }

    private static String slug(String value) {
        String slug = value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (slug.isBlank()) {
            return "band-" + Integer.toUnsignedString(value.hashCode());
        }
        return slug;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
