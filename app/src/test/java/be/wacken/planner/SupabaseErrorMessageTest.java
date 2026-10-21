package be.wacken.planner;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.Test;

/** Exercises both existing display seams without making real authentication requests. */
public final class SupabaseErrorMessageTest {
    @Test
    public void authPrefersMessageWhenBothFieldsExist() throws Exception {
        assertEquals("detail", auth("{\"message\":\"detail\",\"msg\":\"short\"}", 401));
    }

    @Test
    public void syncPrefersMessageWhenBothFieldsExist() throws Exception {
        assertEquals("detail", sync("{\"message\":\"detail\",\"msg\":\"short\"}", 403));
    }

    @Test
    public void emptyBodyKeepsOperationAndStatusFallback() throws Exception {
        for (String body : new String[] {null, "", "  "}) {
            assertEquals("Supabase request failed with status 401", auth(body, 401));
            assertEquals("Schedule sync failed with status 503", sync(body, 503));
        }
    }

    @Test
    public void missingOrMalformedMessageUsesOperationAndStatusWithoutEchoingPayload() throws Exception {
        for (String body : new String[] {"upstream unavailable", "<html>proxy failure</html>", "{",
                "[]", "null", "{}", "{\"access_token\":\"private-token\"}",
                "{\"message\":null,\"msg\":null}", "{\"message\":\"  \",\"msg\":\"\"}",
                "{\"message\":{},\"msg\":[]}", "{\"message\":42,\"msg\":false}"}) {
            assertEquals("Supabase request failed with status 502", auth(body, 502));
            assertEquals("Schedule sync failed with status 502", sync(body, 502));
        }
    }

    @Test
    public void msgIsUsedWhenMessageIsMissingBlankNullOrNotAString() throws Exception {
        for (String body : new String[] {"{\"msg\":\"  Retry later  \"}",
                "{\"message\":\" \",\"msg\":\"Retry later\"}",
                "{\"message\":null,\"msg\":\"Retry later\"}",
                "{\"message\":{},\"msg\":\"Retry later\"}",
                "{\"message\":false,\"msg\":\"Retry later\"}"}) {
            assertEquals("Retry later", auth(body, 503));
            assertEquals("Retry later", sync(body, 503));
        }
    }

    @Test
    public void messageIsTrimmedAndNeverCoercesPayloadValues() throws Exception {
        assertEquals("Invalid login credentials", auth("{\"message\":\"  Invalid login credentials  \"}", 400));
        assertEquals("JWT expired", sync("{\"message\":\"JWT expired\"}", 401));
    }

    @Test
    public void selectedMessagesRedactKnownCredentialFormats() throws Exception {
        for (String field : new String[] {"message", "msg"}) {
            String response = "{\"" + field + "\":\"Bearer private-token password=secret refresh_token=refresh-secret\"}";
            String expected = "Bearer [redacted] password=[redacted] refresh_token=[redacted]";
            assertEquals(expected, auth(response, 401));
            assertEquals(expected, sync(response, 401));
        }
    }

    private static String auth(String body, int status) throws Exception {
        return extract(new SupabaseAuthClient(), body, status);
    }

    private static String sync(String body, int status) throws Exception {
        return extract(new SupabaseAuthenticatedRequest(null, "Schedule sync failed"), body, status);
    }

    private static String extract(Object client, String body, int status) throws Exception {
        Method method = client.getClass().getDeclaredMethod("errorMessage", String.class, int.class);
        method.setAccessible(true);
        return (String) method.invoke(client, body, status);
    }
}
