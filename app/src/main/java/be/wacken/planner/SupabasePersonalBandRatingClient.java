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
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.PersonalBandRatingEvent;
import be.wacken.planner.domain.Rating;

final class SupabasePersonalBandRatingClient implements SupabasePersonalBandRatingRemote {
    private final SupabaseAuthenticatedRequest authenticatedRequest;

    SupabasePersonalBandRatingClient(SupabaseSessionManager sessionManager) {
        this.authenticatedRequest = new SupabaseAuthenticatedRequest(sessionManager, "Supabase personal rating sync failed");
    }

    @Override
    public void pushEvent(AuthSession session, PersonalBandRatingEvent event) throws IOException {
        if (!session.userId().equals(event.userName())) {
            throw new IOException("Cannot sync a personal rating for another user.");
        }
        try {
            JSONObject body = new JSONObject()
                    .put("id", event.id())
                    .put("user_id", event.userName())
                    .put("band_id", bandIdFor(event.band()))
                    .put("rating", event.rating().value())
                    .put("created_at", event.createdAt().toString());
            if (event.festivalId().isPresent()) {
                body.put("festival_id", event.festivalId().orElseThrow());
            }
            request("POST", "/rest/v1/personal_band_rating_events?on_conflict=id", body.toString(), true);
        } catch (JSONException error) {
            throw new IOException("Supabase personal rating request could not be created.", error);
        }
    }

    @Override
    public List<PersonalBandRatingEvent> pullUserEvents(AuthSession session) throws IOException {
        Map<String, String> bandNamesById = bandNamesById();
        String response = request(
                "GET",
                "/rest/v1/personal_band_rating_events?select=id,user_id,band_id,festival_id,rating,created_at&user_id=eq." + encode(session.userId()),
                null,
                false
        );
        try {
            return parseEvents(response, bandNamesById);
        } catch (JSONException error) {
            throw new IOException("Supabase personal rating response could not be read.", error);
        }
    }

    static List<PersonalBandRatingEvent> parseEvents(String response, Map<String, String> bandNamesById) throws JSONException {
        JSONArray rows = new JSONArray(response);
        List<PersonalBandRatingEvent> events = new ArrayList<>();
        for (int index = 0; index < rows.length(); index++) {
            JSONObject row = rows.getJSONObject(index);
            String bandName = bandNamesById.get(row.getString("band_id"));
            if (bandName != null) {
                events.add(new PersonalBandRatingEvent(
                        row.getString("id"),
                        row.getString("user_id"),
                        new Band(bandName),
                        optional(row.optString("festival_id", "")),
                        Rating.of(row.getInt("rating")),
                        Instant.parse(row.getString("created_at"))
                ));
            }
        }
        return events;
    }

    private String bandIdFor(Band band) throws IOException {
        String response = request("GET", "/rest/v1/bands?select=id&name=eq." + encode(band.name()) + "&limit=1", null, false);
        try {
            JSONArray rows = new JSONArray(response);
            if (rows.length() == 0) {
                throw new IOException("Band is not available in Supabase: " + band.name());
            }
            return rows.getJSONObject(0).getString("id");
        } catch (JSONException error) {
            throw new IOException("Supabase band lookup response could not be read.", error);
        }
    }

    private Map<String, String> bandNamesById() throws IOException {
        String response = request("GET", "/rest/v1/bands?select=id,name", null, false);
        try {
            JSONArray rows = new JSONArray(response);
            Map<String, String> names = new HashMap<>();
            for (int index = 0; index < rows.length(); index++) {
                JSONObject row = rows.getJSONObject(index);
                names.put(row.getString("id"), row.getString("name"));
            }
            return names;
        } catch (JSONException error) {
            throw new IOException("Supabase band lookup response could not be read.", error);
        }
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

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
