package be.wacken.planner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    private final SupabaseHttpTransport transport;

    SupabaseFestivalPlanningRatingClient(SupabaseSessionManager sessionManager) {
        this(sessionManager, new SupabaseHttpTransport());
    }

    SupabaseFestivalPlanningRatingClient(SupabaseSessionManager sessionManager, SupabaseHttpTransport transport) {
        this.transport = transport;
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

    @Override
    public boolean hasRating(AuthSession session, String festivalId, Band band) throws IOException {
        String response = request("GET", "/rest/v1/festival_planning_ratings?select=band_id"
                + "&group_id=eq." + encode(session.groupId())
                + "&user_id=eq." + encode(session.userId())
                + "&festival_id=eq." + encode(festivalId)
                + "&band_id=eq." + encode(bandIdFor(band))
                + "&limit=1", null, false);
        try {
            return new JSONArray(response).length() > 0;
        } catch (JSONException error) {
            throw new IOException("Supabase rating conflict check could not be read.", error);
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
        return authenticatedRequest.execute(session -> transport.send(method, endpoint, body, session, upsert));
    }


    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
