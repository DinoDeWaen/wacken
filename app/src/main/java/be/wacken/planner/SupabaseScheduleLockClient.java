package be.wacken.planner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

final class SupabaseScheduleLockClient implements ScheduleLockStore {
    private final SupabaseAuthenticatedRequest authenticatedRequest;
    private final SupabaseHttpTransport transport;
    private final String festivalId;

    SupabaseScheduleLockClient(SupabaseSessionManager sessionManager) {
        this(sessionManager, "wacken-2026");
    }

    SupabaseScheduleLockClient(SupabaseSessionManager sessionManager, String festivalId) {
        this(sessionManager, festivalId, new SupabaseHttpTransport());
    }

    SupabaseScheduleLockClient(SupabaseSessionManager sessionManager, String festivalId, SupabaseHttpTransport transport) {
        this.transport = transport;
        this.authenticatedRequest = new SupabaseAuthenticatedRequest(sessionManager, "Supabase schedule lock sync failed");
        this.festivalId = festivalId;
    }

    @Override
    public Map<String, String> pullGroupLocks() throws IOException {
        AuthSession session = authenticatedSession();
        String response = request(
                "GET",
                "/rest/v1/group_schedule_locks?select=conflict_key,selected_candidate_key&group_id=eq."
                        + encode(session.groupId()) + "&festival_id=eq." + encode(festivalId),
                null,
                false
        );
        return parseLocks(response);
    }

    @Override
    public void saveGroupLock(String conflictKey, String selectedCandidateKey) throws IOException {
        try {
            AuthSession session = authenticatedSession();
            JSONObject body = new JSONObject()
                    .put("group_id", session.groupId())
                    .put("festival_id", festivalId)
                    .put("conflict_key", conflictKey)
                    .put("selected_candidate_key", selectedCandidateKey)
                    .put("updated_by", session.userId());
            request(
                    "POST",
                    "/rest/v1/group_schedule_locks?on_conflict=group_id,festival_id,conflict_key",
                    body.toString(),
                    true
            );
        } catch (JSONException error) {
            throw new IOException("Supabase schedule lock request could not be created.", error);
        }
    }

    @Override
    public void clearGroupLock(String conflictKey) throws IOException {
        AuthSession session = authenticatedSession();
        request(
                "DELETE",
                "/rest/v1/group_schedule_locks?group_id=eq." + encode(session.groupId())
                        + "&festival_id=eq." + encode(festivalId)
                        + "&conflict_key=eq." + encode(conflictKey),
                null,
                false
        );
    }

    static Map<String, String> parseLocks(String response) throws IOException {
        try {
            JSONArray rows = new JSONArray(response);
            Map<String, String> locks = new LinkedHashMap<>();
            for (int index = 0; index < rows.length(); index++) {
                JSONObject row = rows.getJSONObject(index);
                locks.put(row.getString("conflict_key"), row.getString("selected_candidate_key"));
            }
            return locks;
        } catch (JSONException error) {
            throw new IOException("Supabase schedule lock response could not be read.", error);
        }
    }

    private AuthSession authenticatedSession() throws IOException {
        final AuthSession[] holder = new AuthSession[1];
        authenticatedRequest.execute(session -> {
            holder[0] = session;
            return new SupabaseAuthenticatedRequest.Response(200, "");
        });
        return holder[0];
    }

    private String request(String method, String endpoint, String body, boolean upsert) throws IOException {
        return authenticatedRequest.execute(session -> transport.send(method, endpoint, body, session, upsert));
    }


    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
