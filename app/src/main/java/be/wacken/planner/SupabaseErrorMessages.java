package be.wacken.planner;

import org.json.JSONException;
import org.json.JSONObject;

/** Display-only error extraction; authentication decisions still use status and original responses. */
final class SupabaseErrorMessages {
    private SupabaseErrorMessages() {
    }

    static String select(String response, int status, String operation) {
        String fallback = operation + " with status " + status;
        if (response == null || response.isBlank()) {
            return fallback;
        }
        try {
            JSONObject json = new JSONObject(response);
            for (String field : new String[] {"message", "msg"}) {
                Object value = json.opt(field);
                if (value instanceof String text && !text.isBlank()) {
                    return SupabaseDiagnostics.sanitize(text.trim());
                }
            }
        } catch (JSONException ignored) {
            // Raw payloads may contain credentials or proxy diagnostics; never display them.
        }
        return fallback;
    }
}
