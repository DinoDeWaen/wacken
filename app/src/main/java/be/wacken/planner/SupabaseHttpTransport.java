package be.wacken.planner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/** HTTP mechanics only; callers retain authentication/retry and operation semantics. */
final class SupabaseHttpTransport {
    static final int CONNECT_TIMEOUT_MS = 15_000;
    static final int READ_TIMEOUT_MS = 30_000;

    private final ConnectionFactory connections;
    private final int connectTimeoutMs;
    private final int readTimeoutMs;

    SupabaseHttpTransport() {
        this(url -> (HttpURLConnection) url.openConnection());
    }

    SupabaseHttpTransport(ConnectionFactory connections) {
        this(connections, CONNECT_TIMEOUT_MS, READ_TIMEOUT_MS);
    }

    SupabaseHttpTransport(ConnectionFactory connections, int connectTimeoutMs, int readTimeoutMs) {
        if (connectTimeoutMs <= 0 || readTimeoutMs <= 0) {
            throw new IllegalArgumentException("Transport timeouts must be positive.");
        }
        this.connections = connections;
        this.connectTimeoutMs = connectTimeoutMs;
        this.readTimeoutMs = readTimeoutMs;
    }

    SupabaseAuthenticatedRequest.Response send(String method, String endpoint, String body,
                                              AuthSession session, boolean upsert) throws IOException {
        HttpURLConnection connection = connections.open(new URL(SupabaseConfig.url() + endpoint));
        try {
            connection.setConnectTimeout(connectTimeoutMs);
            connection.setReadTimeout(readTimeoutMs);
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
        } finally {
            connection.disconnect();
        }
    }

    private static String read(InputStream input) throws IOException {
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

    interface ConnectionFactory {
        HttpURLConnection open(URL url) throws IOException;
    }
}
