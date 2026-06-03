package be.wacken.planner;

import android.util.Log;

import java.util.Locale;
import java.util.regex.Pattern;

final class SupabaseDiagnostics {
    static final String TAG = "WackenSupabase";

    private static final Pattern JWT = Pattern.compile("eyJ[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+");
    private static final Pattern BEARER = Pattern.compile("(?i)Bearer\\s+\\S+");
    private static final Pattern DATABASE_URL = Pattern.compile("(?i)postgres(?:ql)?://\\S+");
    private static final Pattern QUOTED_SECRET = Pattern.compile(
            "(?i)(\"(?:access_token|refresh_token|anon_key|apikey|api_key|password|authorization)\"\\s*:\\s*\")([^\"]*)(\")"
    );
    private static final Pattern KEY_VALUE_SECRET = Pattern.compile(
            "(?i)\\b(access_token|refresh_token|anon_key|apikey|api_key|password|authorization)\\b\\s*=\\s*[^\\s,;}]+"
    );

    private SupabaseDiagnostics() {
    }

    static void info(String area, String event, String detail) {
        try {
            Log.i(TAG, format(area, event, detail));
        } catch (RuntimeException ignored) {
            // Android's local unit-test Log stub throws; diagnostics must not affect behavior.
        }
    }

    static void warn(String area, String event, String detail, Throwable error) {
        String message = detail;
        if (error != null) {
            message = append(message, "error_type=" + error.getClass().getSimpleName());
            message = append(message, "error_message=" + sanitize(error.getMessage()));
        }
        try {
            Log.w(TAG, format(area, event, message));
        } catch (RuntimeException ignored) {
            // Android's local unit-test Log stub throws; diagnostics must not affect behavior.
        }
    }

    static String format(String area, String event, String detail) {
        String message = "area=" + sanitize(segment(area)) + " event=" + sanitize(segment(event));
        if (detail != null && !detail.isBlank()) {
            message += " " + sanitize(detail);
        }
        return message;
    }

    static String sanitize(String value) {
        if (value == null) {
            return "";
        }
        String sanitized = QUOTED_SECRET.matcher(value).replaceAll("$1[redacted]$3");
        sanitized = KEY_VALUE_SECRET.matcher(sanitized).replaceAll("$1=[redacted]");
        sanitized = BEARER.matcher(sanitized).replaceAll("Bearer [redacted]");
        sanitized = JWT.matcher(sanitized).replaceAll("[redacted-jwt]");
        sanitized = DATABASE_URL.matcher(sanitized).replaceAll("[redacted-database-url]");
        return sanitized;
    }

    private static String segment(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }
        return value.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]+", "_");
    }

    private static String append(String current, String addition) {
        if (current == null || current.isBlank()) {
            return addition;
        }
        return current + " " + addition;
    }
}
