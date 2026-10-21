package be.wacken.planner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.PersonalBandRatingEvent;
import be.wacken.planner.domain.Rating;

final class SupabasePersonalBandRatingClient implements SupabasePersonalBandRatingRemote {
    private final SupabaseAuthenticatedRequest authenticatedRequest;
    private final SupabaseHttpTransport transport;

    SupabasePersonalBandRatingClient(SupabaseSessionManager sessionManager) {
        this(sessionManager, new SupabaseHttpTransport());
    }

    SupabasePersonalBandRatingClient(SupabaseSessionManager sessionManager, SupabaseHttpTransport transport) {
        this.transport = transport;
        this.authenticatedRequest = new SupabaseAuthenticatedRequest(sessionManager, "Supabase personal rating sync failed");
    }

    @Override
    public void pushEvent(AuthSession session, PersonalBandRatingEvent event) throws IOException {
        if (!session.userId().equals(event.userName())) {
            throw new IOException("Cannot sync a personal rating for another user.");
        }
        try {
            JSONObject body = new JSONObject()
                    .put("id", remoteEventId(event))
                    .put("user_id", event.userName())
                    .put("band_id", bandLookup(event.userName()).idFor(event.band()))
                    .put("rating", event.rating().value())
                    .put("created_at", event.createdAt().toString());
            if (event.festivalId().isPresent()) {
                body.put("festival_id", event.festivalId().orElseThrow());
            }
            if (event.festivalNameSnapshot().isPresent()) {
                body.put("festival_name_snapshot", event.festivalNameSnapshot().orElseThrow());
            }
            request(event.userName(), "POST", "/rest/v1/personal_band_rating_events?on_conflict=id", body.toString(), true);
        } catch (JSONException error) {
            throw new IOException("Supabase personal rating request could not be created.", error);
        }
    }

    static String remoteEventId(PersonalBandRatingEvent event) {
        try {
            return UUID.fromString(event.id()).toString();
        } catch (IllegalArgumentException ignored) {
            return UUID.nameUUIDFromBytes(event.id().getBytes(StandardCharsets.UTF_8)).toString();
        }
    }

    @Override
    public List<PersonalBandRatingEvent> pullUserEvents(AuthSession session) throws IOException {
        Map<String, String> bandNamesById = bandLookup(session.userId()).namesById();
        String response = request(session.userId(),
                "GET",
                "/rest/v1/personal_band_rating_events?select=id,user_id,band_id,festival_id,festival_name_snapshot,rating,created_at&user_id=eq." + encode(session.userId()),
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
                        parseCreatedAt(row.getString("created_at")),
                        optional(row.optString("festival_name_snapshot", ""))
                ));
            }
        }
        return events;
    }

    static Instant parseCreatedAt(String createdAt) {
        try {
            return Instant.parse(createdAt);
        } catch (DateTimeParseException error) {
            return OffsetDateTime.parse(createdAt).toInstant();
        }
    }

    private SupabaseCanonicalBandLookup bandLookup(String user) {
        return new SupabaseCanonicalBandLookup(endpoint -> request(user, "GET", endpoint, null, false));
    }

    private String request(String expectedUser, String method, String endpoint, String body, boolean upsert) throws IOException {
        return authenticatedRequest.executeForUser(expectedUser, session -> transport.send(method, endpoint, body, session, upsert));
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
