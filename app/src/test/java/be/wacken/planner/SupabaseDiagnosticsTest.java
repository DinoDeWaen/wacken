package be.wacken.planner;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class SupabaseDiagnosticsTest {
    private static final String JWT = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIn0.signature";

    @Test
    public void redactsBearerJwtAndJsonAuthSecrets() {
        String value = "Authorization: Bearer " + JWT
                + " {\"access_token\":\"" + JWT + "\",\"refresh_token\":\"refresh-secret\","
                + "\"apikey\":\"anon-secret\",\"password\":\"db-password\"}";

        String sanitized = SupabaseDiagnostics.sanitize(value);

        assertFalse(sanitized.contains(JWT));
        assertFalse(sanitized.contains("refresh-secret"));
        assertFalse(sanitized.contains("anon-secret"));
        assertFalse(sanitized.contains("db-password"));
        assertTrue(sanitized.contains("Bearer [redacted]"));
        assertTrue(sanitized.contains("\"access_token\":\"[redacted]\""));
        assertTrue(sanitized.contains("\"refresh_token\":\"[redacted]\""));
    }

    @Test
    public void redactsKeyValueSecretsAndDatabaseUrls() {
        String value = "access_token=" + JWT
                + " refresh_token=refresh-secret authorization=Bearer-secret "
                + "postgresql://postgres:secret@db.example.test:5432/postgres";

        String sanitized = SupabaseDiagnostics.sanitize(value);

        assertFalse(sanitized.contains(JWT));
        assertFalse(sanitized.contains("refresh-secret"));
        assertFalse(sanitized.contains("Bearer-secret"));
        assertFalse(sanitized.contains("postgres:secret"));
        assertTrue(sanitized.contains("access_token=[redacted]"));
        assertTrue(sanitized.contains("refresh_token=[redacted]"));
        assertTrue(sanitized.contains("[redacted-database-url]"));
    }

    @Test
    public void formatsDiagnosticsWithSanitizedAreaEventAndDetail() {
        String formatted = SupabaseDiagnostics.format(
                "Supabase Request",
                "Retry Failed",
                "message=JWT expired access_token=" + JWT
        );

        assertTrue(formatted.contains("area=supabase_request"));
        assertTrue(formatted.contains("event=retry_failed"));
        assertTrue(formatted.contains("message=JWT expired"));
        assertFalse(formatted.contains(JWT));
    }
}
