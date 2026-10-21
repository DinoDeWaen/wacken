package be.wacken.planner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.SavedRating;

final class SupabaseRatingClient implements SupabaseRatingRemote {
    private final SupabaseSessionManager sessionManager;
    private final SupabaseAuthenticatedRequest authenticatedRequest;
    private final SupabaseHttpTransport transport;

    SupabaseRatingClient(SupabaseSessionManager sessionManager) {
        this(sessionManager, new SupabaseHttpTransport());
    }

    SupabaseRatingClient(SupabaseSessionManager sessionManager, SupabaseHttpTransport transport) {
        this.transport = transport;
        this.sessionManager = sessionManager;
        this.authenticatedRequest = new SupabaseAuthenticatedRequest(sessionManager, "Supabase rating sync failed");
    }

    @Override
    public void pushRating(AuthSession session, SavedRating rating) throws IOException {
        AuthSession freshSession = authenticatedSession();
        if (!freshSession.userId().equals(rating.userName())) {
            throw new IOException("Cannot sync a rating for another user.");
        }
        String bandId = bandLookup(rating.userName()).idFor(rating.band());
        if (rating.rating().value() == 0) {
            request(rating.userName(),
                    "DELETE",
                    "/rest/v1/ratings?group_id=eq." + encode(freshSession.groupId())
                            + "&user_id=eq." + encode(freshSession.userId())
                            + "&band_id=eq." + encode(bandId),
                    null,
                    false
            );
            return;
        }
        try {
            JSONObject body = new JSONObject()
                    .put("group_id", freshSession.groupId())
                    .put("user_id", freshSession.userId())
                    .put("band_id", bandId)
                    .put("rating", rating.rating().value());
            request(rating.userName(),
                    "POST",
                    "/rest/v1/ratings?on_conflict=group_id,user_id,band_id",
                    body.toString(),
                    true
            );
        } catch (JSONException error) {
            throw new IOException("Supabase rating request could not be created.", error);
        }
    }

    @Override
    public List<SavedRating> pullGroupRatings(AuthSession session) throws IOException {
        AuthSession freshSession = authenticatedSession();
        Map<String, String> bandNamesById = bandLookup(session.userId()).namesById();
        String response = request(session.userId(),
                "GET",
                "/rest/v1/ratings?select=user_id,band_id,rating&group_id=eq." + encode(freshSession.groupId()),
                null,
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

    private SupabaseCanonicalBandLookup bandLookup(String user) {
        return new SupabaseCanonicalBandLookup(endpoint -> request(user, "GET", endpoint, null, false));
    }

    private String request(String expectedUser, String method, String endpoint, String body, boolean upsert) throws IOException {
        return authenticatedRequest.executeForUser(expectedUser, freshSession -> transport.send(method, endpoint, body, freshSession, upsert));
    }


    private AuthSession authenticatedSession() throws IOException {
        return sessionManager.requireFreshSession();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
