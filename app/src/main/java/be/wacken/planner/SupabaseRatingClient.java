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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.SavedRating;

final class SupabaseRatingClient implements SupabaseRatingRemote {
    @Override
    public void pushRating(AuthSession session, SavedRating rating) throws IOException {
        if (!session.userId().equals(rating.userName())) {
            throw new IOException("Cannot sync a rating for another user.");
        }
        String bandId = bandIdFor(session, rating.band());
        try {
            JSONObject body = new JSONObject()
                    .put("group_id", session.groupId())
                    .put("user_id", session.userId())
                    .put("band_id", bandId)
                    .put("rating", rating.rating().value());
            request(
                    "POST",
                    "/rest/v1/ratings?on_conflict=group_id,user_id,band_id",
                    body.toString(),
                    session,
                    true
            );
        } catch (JSONException error) {
            throw new IOException("Supabase rating request could not be created.", error);
        }
    }

    @Override
    public List<SavedRating> pullGroupRatings(AuthSession session) throws IOException {
        Map<String, String> bandNamesById = bandNamesById(session);
        String response = request(
                "GET",
                "/rest/v1/ratings?select=user_id,band_id,rating&group_id=eq." + encode(session.groupId()),
                null,
                session,
                false
        );
        try {
            JSONArray rows = new JSONArray(response);
            List<SavedRating> ratings = new ArrayList<>();
            for (int index = 0; index < rows.length(); index++) {
                JSONObject row = rows.getJSONObject(index);
                String bandName = bandNamesById.get(row.getString("band_id"));
                if (bandName != null) {
                    ratings.add(new SavedRating(
                            row.getString("user_id"),
                            new Band(bandName),
                            Rating.of(row.getInt("rating"))
                    ));
                }
            }
            return ratings;
        } catch (JSONException error) {
            throw new IOException("Supabase ratings response could not be read.", error);
        }
    }

    private String bandIdFor(AuthSession session, Band band) throws IOException {
        String response = request(
                "GET",
                "/rest/v1/bands?select=id&name=eq." + encode(band.name()) + "&limit=1",
                null,
                session,
                false
        );
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

    private Map<String, String> bandNamesById(AuthSession session) throws IOException {
        String response = request("GET", "/rest/v1/bands?select=id,name", null, session, false);
        try {
            JSONArray rows = new JSONArray(response);
            Map<String, String> bandNamesById = new HashMap<>();
            for (int index = 0; index < rows.length(); index++) {
                JSONObject row = rows.getJSONObject(index);
                bandNamesById.put(row.getString("id"), row.getString("name"));
            }
            return bandNamesById;
        } catch (JSONException error) {
            throw new IOException("Supabase band lookup response could not be read.", error);
        }
    }

    private String request(String method, String endpoint, String body, AuthSession session, boolean upsert) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(SupabaseConfig.url() + endpoint).openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("apikey", SupabaseConfig.anonKey());
        connection.setRequestProperty("Authorization", "Bearer " + (session == null ? SupabaseConfig.anonKey() : session.accessToken()));
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
        if (status >= 400) {
            throw new IOException(errorMessage(response, status));
        }
        return response;
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

    private String errorMessage(String response, int status) {
        if (response == null || response.isBlank()) {
            return "Supabase rating sync failed with status " + status;
        }
        try {
            JSONObject json = new JSONObject(response);
            return json.optString("message", json.optString("msg", response));
        } catch (JSONException ignored) {
            return response;
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
