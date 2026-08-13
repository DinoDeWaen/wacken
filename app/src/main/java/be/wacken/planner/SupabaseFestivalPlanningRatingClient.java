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
import be.wacken.planner.domain.SavedFestivalPlanningRating;

final class SupabaseFestivalPlanningRatingClient implements SupabaseFestivalPlanningRatingRemote {
    private final SupabaseAuthenticatedRequest authenticatedRequest;

    SupabaseFestivalPlanningRatingClient(SupabaseSessionManager sessionManager) {
        this.authenticatedRequest = new SupabaseAuthenticatedRequest(sessionManager, "Supabase festival planning rating sync failed");
    }

    @Override
    public void pushRating(AuthSession session, SavedFestivalPlanningRating rating) throws IOException {
        if (!session.groupId().equals(rating.groupId()) || !session.userId().equals(rating.userName())) {
            throw new IOException("Cannot sync a planning rating for another group or user.");
        }
        if (rating.rating().value() == 0) {
            request(
                    "DELETE",
                    "/rest/v1/festival_planning_ratings?group_id=eq." + encode(rating.groupId())
                            + "&user_id=eq." + encode(rating.userName())
                            + "&festival_id=eq." + encode(rating.festivalId())
                            + "&band_id=eq." + encode(bandIdFor(rating.band())),
                    null,
                    false
            );
            return;
        }
        try {
            JSONObject body = new JSONObject()
                    .put("group_id", rating.groupId())
                    .put("user_id", rating.userName())
                    .put("festival_id", rating.festivalId())
                    .put("band_id", bandIdFor(rating.band()))
                    .put("rating", rating.rating().value());
            request("POST", "/rest/v1/festival_planning_ratings?on_conflict=group_id,user_id,festival_id,band_id", body.toString(), true);
        } catch (JSONException error) {
            throw new IOException("Supabase planning rating request could not be created.", error);
        }
    }

    @Override
    public List<SavedFestivalPlanningRating> pullGroupRatings(AuthSession session) throws IOException {
        Map<String, String> bandNamesById = bandNamesById();
        String response = request(
                "GET",
                "/rest/v1/festival_planning_ratings?select=group_id,user_id,festival_id,band_id,rating&group_id=eq." + encode(session.groupId()),
                null,
                false
        );
        try {
            return parseRatings(response, bandNamesById);
        } catch (JSONException error) {
            throw new IOException("Supabase planning ratings response could not be read.", error);
        }
    }

    static List<SavedFestivalPlanningRating> parseRatings(String response, Map<String, String> bandNamesById) throws JSONException {
        JSONArray rows = new JSONArray(response);
        List<SavedFestivalPlanningRating> ratings = new ArrayList<>();
        for (int index = 0; index < rows.length(); index++) {
            JSONObject row = rows.getJSONObject(index);
            String bandName = bandNamesById.get(row.getString("band_id"));
            if (bandName != null) {
                ratings.add(new SavedFestivalPlanningRating(
                        row.getString("group_id"),
                        row.getString("user_id"),
                        row.getString("festival_id"),
                        new Band(bandName),
                        Rating.of(row.getInt("rating"))
                ));
            }
        }
        return ratings;
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

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
