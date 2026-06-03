package be.wacken.planner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

final class SupabaseAuthenticatedRequest {
    private final SupabaseSessionManager sessions;
    private final String failureMessage;

    SupabaseAuthenticatedRequest(SupabaseSessionManager sessions, String failureMessage) {
        this.sessions = sessions;
        this.failureMessage = failureMessage;
    }

    String execute(Request request) throws IOException {
        AuthSession session = sessions.requireFreshSession();
        Response response = request.send(session);
        if (response.success()) {
            return response.body();
        }
        if (isExpiredJwt(response)) {
            AuthSession refreshedSession = sessions.refreshAfterRejected(session);
            response = request.send(refreshedSession);
            if (response.success()) {
                return response.body();
            }
        }
        throw new IOException(errorMessage(response.body(), response.status()));
    }

    private boolean isExpiredJwt(Response response) {
        if (response.status() < 400) {
            return false;
        }
        String body = response.body() == null ? "" : response.body().toLowerCase(Locale.ROOT);
        return (body.contains("jwt") || body.contains("token"))
                && (body.contains("expired") || body.contains("invalid"));
    }

    private String errorMessage(String response, int status) {
        if (response == null || response.isBlank()) {
            return failureMessage + " with status " + status;
        }
        try {
            JSONObject json = new JSONObject(response);
            return json.optString("message", json.optString("msg", response));
        } catch (JSONException ignored) {
            return response;
        }
    }

    interface Request {
        Response send(AuthSession session) throws IOException;
    }

    record Response(int status, String body) {
        boolean success() {
            return status < 400;
        }
    }
}
