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
        SupabaseDiagnostics.info("request", "send_start", "label=" + failureMessage);
        Response response;
        try {
            response = request.send(session);
        } catch (IOException error) {
            SupabaseDiagnostics.warn("request", "send_exception", "label=" + failureMessage, error);
            throw error;
        }
        if (response.success()) {
            SupabaseDiagnostics.info("request", "send_success", "label=" + failureMessage + " status=" + response.status());
            return response.body();
        }
        if (isExpiredJwt(response)) {
            SupabaseDiagnostics.warn(
                    "request",
                    "expired_jwt_detected",
                    "label=" + failureMessage + " status=" + response.status() + " message=" + errorMessage(response.body(), response.status()),
                    null
            );
            AuthSession refreshedSession = sessions.refreshAfterRejected(session);
            SupabaseDiagnostics.info("request", "retry_start", "label=" + failureMessage);
            try {
                response = request.send(refreshedSession);
            } catch (IOException error) {
                SupabaseDiagnostics.warn("request", "retry_exception", "label=" + failureMessage, error);
                throw error;
            }
            if (response.success()) {
                SupabaseDiagnostics.info("request", "retry_success", "label=" + failureMessage + " status=" + response.status());
                return response.body();
            }
            SupabaseDiagnostics.warn(
                    "request",
                    "retry_failed",
                    "label=" + failureMessage + " status=" + response.status() + " message=" + errorMessage(response.body(), response.status()),
                    null
            );
        } else {
            SupabaseDiagnostics.warn(
                    "request",
                    "send_failed_no_retry",
                    "label=" + failureMessage + " status=" + response.status() + " message=" + errorMessage(response.body(), response.status()),
                    null
            );
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
