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
import java.nio.charset.StandardCharsets;

final class SupabaseAuthClient implements SupabaseAuthGateway {
    private static final String DEFAULT_GROUP_ID = "00000000-0000-0000-0000-000000000001";

    AuthSession signIn(String email, String password) throws IOException {
        try {
            JSONObject body = new JSONObject()
                    .put("email", email)
                    .put("password", password);
            JSONObject response = requestJson(
                    "POST",
                    SupabaseConfig.url() + "/auth/v1/token?grant_type=password",
                    body.toString(),
                    null
            );
            return sessionFromTokenResponse(response, email, null);
        } catch (JSONException error) {
            throw new IOException("Supabase sign-in response could not be read.", error);
        }
    }

    @Override
    public AuthSession refresh(AuthSession session) throws IOException {
        if (session.refreshToken() == null || session.refreshToken().isBlank()) {
            throw new InvalidAuthSessionException("Cannot refresh Supabase session without a refresh token.");
        }
        try {
            JSONObject body = new JSONObject()
                    .put("refresh_token", session.refreshToken());
            JSONObject response = requestJson(
                    "POST",
                    SupabaseConfig.url() + "/auth/v1/token?grant_type=refresh_token",
                    body.toString(),
                    null
            );
            return sessionFromTokenResponse(response, session.email(), session);
        } catch (SupabaseHttpException error) {
            if (error.authenticationFailure()) {
                throw new InvalidAuthSessionException(error.getMessage());
            }
            throw error;
        } catch (JSONException error) {
            throw new IOException("Supabase refresh response could not be read.", error);
        }
    }

    private AuthSession sessionFromTokenResponse(JSONObject response, String fallbackEmail, AuthSession previousSession) throws IOException, JSONException {
        String accessToken = response.getString("access_token");
        String refreshToken = response.optString("refresh_token", previousSession == null ? "" : previousSession.refreshToken());
        int expiresIn = response.optInt("expires_in", 3600);
        JSONObject user = response.optJSONObject("user");
        if (user == null && previousSession == null) {
            throw new IOException("Supabase token response did not include a user.");
        }
        String userId = user == null ? previousSession.userId() : user.getString("id");
        String userEmail = user == null ? fallbackEmail : user.optString("email", fallbackEmail);
        Membership membership = fetchDefaultMembership(accessToken, userId);
        return new AuthSession(
                accessToken,
                refreshToken,
                userId,
                userEmail,
                System.currentTimeMillis() / 1000L + expiresIn,
                membership.groupId(),
                membership.role()
        );
    }

    private Membership fetchDefaultMembership(String accessToken, String userId) throws IOException {
        String endpoint = SupabaseConfig.url()
                + "/rest/v1/group_members?select=group_id,role"
                + "&user_id=eq." + userId
                + "&group_id=eq." + DEFAULT_GROUP_ID;
        JSONArray memberships = requestArray("GET", endpoint, accessToken);
        if (memberships.length() == 0) {
            throw new IOException("Signed-in user is not a member of the Wacken 2026 group.");
        }
        try {
            JSONObject membership = memberships.getJSONObject(0);
            return new Membership(membership.getString("group_id"), membership.optString("role", "member"));
        } catch (JSONException error) {
            throw new IOException("Supabase group membership response could not be read.", error);
        }
    }

    private JSONObject requestJson(String method, String endpoint, String body, String accessToken) throws IOException {
        String response = request(method, endpoint, body, accessToken);
        try {
            return new JSONObject(response);
        } catch (JSONException error) {
            throw new IOException("Supabase response was not valid JSON.", error);
        }
    }

    private JSONArray requestArray(String method, String endpoint, String accessToken) throws IOException {
        String response = request(method, endpoint, null, accessToken);
        try {
            return new JSONArray(response);
        } catch (JSONException error) {
            throw new IOException("Supabase response was not a valid JSON array.", error);
        }
    }

    private String request(String method, String endpoint, String body, String accessToken) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("apikey", SupabaseConfig.anonKey());
        connection.setRequestProperty("Authorization", "Bearer " + (accessToken == null ? SupabaseConfig.anonKey() : accessToken));
        connection.setRequestProperty("Content-Type", "application/json");
        if (body != null) {
            connection.setDoOutput(true);
            try (OutputStream stream = connection.getOutputStream()) {
                stream.write(body.getBytes(StandardCharsets.UTF_8));
            }
        }

        int status = connection.getResponseCode();
        String response = read(status >= 400 ? connection.getErrorStream() : connection.getInputStream());
        if (status >= 400) {
            throw new SupabaseHttpException(status, errorMessage(response, status));
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
            return "Supabase request failed with status " + status;
        }
        try {
            JSONObject json = new JSONObject(response);
            return json.optString("msg", json.optString("message", response));
        } catch (Exception ignored) {
            return response;
        }
    }

    private record Membership(String groupId, String role) {
    }

    private static final class SupabaseHttpException extends IOException {
        private final int status;

        private SupabaseHttpException(int status, String message) {
            super(message);
            this.status = status;
        }

        private boolean authenticationFailure() {
            return status == 400 || status == 401 || status == 403;
        }
    }
}
