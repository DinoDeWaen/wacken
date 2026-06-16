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
import java.util.LinkedHashMap;
import java.util.Map;

final class SupabaseScheduleLockClient implements ScheduleLockStore {
    private final SupabaseAuthenticatedRequest authenticatedRequest;

    SupabaseScheduleLockClient(SupabaseSessionManager sessionManager) {
        this.authenticatedRequest = new SupabaseAuthenticatedRequest(sessionManager, "Supabase schedule lock sync failed");
    }

    @Override
    public Map<String, String> pullGroupLocks() throws IOException {
        AuthSession session = authenticatedSession();
        String response = request(
                "GET",
                "/rest/v1/group_schedule_locks?select=conflict_key,selected_candidate_key&group_id=eq."
                        + encode(session.groupId()),
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
                    .put("conflict_key", conflictKey)
                    .put("selected_candidate_key", selectedCandidateKey)
                    .put("updated_by", session.userId());
            request(
                    "POST",
                    "/rest/v1/group_schedule_locks?on_conflict=group_id,conflict_key",
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
