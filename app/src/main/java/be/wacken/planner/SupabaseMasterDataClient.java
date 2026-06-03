package be.wacken.planner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import be.wacken.planner.domain.Band;
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

    static Band toBand(JSONObject row) throws JSONException {
        return new Band(
                row.getString("name"),
                optional(row.optString("biography_html", "")),
                optional(row.optString("image_url", "")),
                optional(row.optString("youtube_url", "")),
                spotifyUrl(row.optString("spotify_artist_id", ""))
        );
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

    private JSONArray requestArray(String endpoint) throws IOException {
        String response = request(endpoint);
        try {
            return new JSONArray(response);
        } catch (JSONException error) {
            throw new IOException("Supabase response was not a valid JSON array.", error);
        }
    }

    private String request(String endpoint) throws IOException {
        return authenticatedRequest.execute(session -> send(endpoint, session));
    }

    private SupabaseAuthenticatedRequest.Response send(String endpoint, AuthSession session) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(SupabaseConfig.url() + endpoint).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("apikey", SupabaseConfig.anonKey());
        connection.setRequestProperty("Authorization", "Bearer " + session.accessToken());
        connection.setRequestProperty("Content-Type", "application/json");
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
}
