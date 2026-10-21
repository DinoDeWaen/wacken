package be.wacken.planner;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public final class SupabaseHttpTransportTest {
    private static final AuthSession SESSION = new AuthSession("access", "refresh", "member", "member@example.test",
            Long.MAX_VALUE, "group", "member");

    @Test
    public void configuresBoundedWaitsBeforeWritingAndPreservesJsonUpsertContract() throws Exception {
        RecordingConnection connection = new RecordingConnection(201, "{\"confirmed\":true}");
        SupabaseHttpTransport transport = new SupabaseHttpTransport(url -> connection);

        SupabaseAuthenticatedRequest.Response result = transport.send("POST", "/rest/v1/ratings", "{\"rating\":5}", SESSION, true);

        assertEquals("POST", connection.getRequestMethod());
        assertEquals(SupabaseConfig.anonKey(), connection.getRequestProperty("apikey"));
        assertEquals("Bearer access", connection.getRequestProperty("Authorization"));
        assertEquals("application/json", connection.getRequestProperty("Content-Type"));
        assertEquals("resolution=merge-duplicates", connection.getRequestProperty("Prefer"));
        assertEquals("{\"rating\":5}", connection.output.toString(StandardCharsets.UTF_8));
        assertTrue(connection.getDoOutput());
        assertTrue(connection.outputClosed);
        assertTrue(connection.inputClosed);
        assertTrue(connection.disconnected);
        assertEquals(201, result.status());
        assertEquals("{\"confirmed\":true}", result.body());
    }

    @Test
    public void bodylessDeleteKeepsFiltersAndDoesNotAcquireAnOutputStream() throws Exception {
        RecordingConnection connection = new RecordingConnection(204, "");
        String endpoint = "/rest/v1/ratings?group_id=eq.group&user_id=eq.member&band_id=eq.band";
        SupabaseHttpTransport transport = new SupabaseHttpTransport(url -> {
            assertEquals(SupabaseConfig.url() + endpoint, url.toString());
            return connection;
        }, 15000, 30000);

        assertEquals(204, transport.send("DELETE", endpoint, null, SESSION, false).status());

        assertNull(connection.getRequestProperty("Prefer"));
        assertFalse(connection.getDoOutput());
        assertFalse(connection.outputOpened);
        assertTrue(connection.disconnected);
    }

    @Test
    public void returnsErrorStatusAndBodyForExistingAuthRetryAndErrorSelection() throws Exception {
        RecordingConnection connection = new RecordingConnection(401, "{\"message\":\"JWT expired\"}");
        SupabaseAuthenticatedRequest.Response response = new SupabaseHttpTransport(url -> connection, 15000, 30000)
                .send("GET", "/rest/v1/bands", null, SESSION, false);
        assertEquals(401, response.status());
        assertEquals("{\"message\":\"JWT expired\"}", response.body());
        assertTrue(connection.inputClosed);
        assertTrue(connection.disconnected);
    }

    @Test
    public void missingErrorStreamReturnsEmptyBody() throws Exception {
        RecordingConnection connection = new RecordingConnection(503, "") {
            @Override public InputStream getErrorStream() { return null; }
        };
        assertEquals("", new SupabaseHttpTransport(url -> connection, 15000, 30000)
                .send("GET", "/rest/v1/bands", null, SESSION, false).body());
        assertTrue(connection.disconnected);
    }

    @Test
    public void connectFailureAndReadFailureDisconnectWithoutClaimingSuccess() throws Exception {
        for (boolean failDuringRead : new boolean[] {false, true}) {
            RecordingConnection connection = new RecordingConnection(200, "") {
                @Override public int getResponseCode() throws IOException {
                    assertWaits();
                    if (!failDuringRead) throw new SocketTimeoutException("connect timed out");
                    return 200;
                }
                @Override public InputStream getInputStream() {
                    return new InputStream() {
                        @Override public int read() throws IOException { throw new SocketTimeoutException("read timed out"); }
                        @Override public void close() { inputClosed = true; }
                    };
                }
            };
            assertThrows(SocketTimeoutException.class, () -> new SupabaseHttpTransport(url -> connection, 15000, 30000)
                    .send("GET", "/rest/v1/bands", null, SESSION, false));
            assertTrue(connection.disconnected);
            if (failDuringRead) assertTrue(connection.inputClosed);
        }
    }

    @Test
    public void writeFailureClosesOutputAndDisconnects() throws Exception {
        RecordingConnection connection = new RecordingConnection(200, "") {
            @Override public java.io.OutputStream getOutputStream() {
                return new java.io.OutputStream() {
                    @Override public void write(int value) throws IOException { throw new IOException("write failed"); }
                    @Override public void close() { outputClosed = true; }
                };
            }
        };
        assertThrows(IOException.class, () -> new SupabaseHttpTransport(url -> connection, 15000, 30000)
                .send("POST", "/rest/v1/ratings", "{}", SESSION, true));
        assertTrue(connection.outputClosed);
        assertTrue(connection.disconnected);
    }

    @Test(timeout = 10000)
    public void realLocalServerStallingHeadersOrBodyTriggersReadTimeout() throws Exception {
        for (boolean sendHeaders : new boolean[] {false, true}) {
            CountDownLatch release = new CountDownLatch(1);
            ExecutorService worker = Executors.newSingleThreadExecutor();
            try (ServerSocket server = new ServerSocket(0, 1, java.net.InetAddress.getByName("127.0.0.1"))) {
                Future<?> peer = worker.submit(() -> {
                    try (Socket socket = server.accept()) {
                        socket.setSoTimeout(3000);
                        // Consume the complete request headers before deliberately stalling.
                        int matched = 0;
                        byte[] end = {'\r', '\n', '\r', '\n'};
                        while (matched < end.length) {
                            int value = socket.getInputStream().read();
                            if (value < 0) throw new IOException("Client closed before request headers");
                            matched = value == end[matched] ? matched + 1 : 0;
                        }
                        if (sendHeaders) {
                            socket.getOutputStream().write("HTTP/1.1 200 OK\r\nContent-Length: 8\r\n\r\n"
                                    .getBytes(StandardCharsets.US_ASCII));
                            socket.getOutputStream().flush();
                        }
                        if (!release.await(5, TimeUnit.SECONDS)) throw new IOException("Client did not time out");
                    } catch (Exception error) {
                        throw new RuntimeException(error);
                    }
                });
                SupabaseHttpTransport transport = new SupabaseHttpTransport(url ->
                        (HttpURLConnection) new URL("http://" + server.getInetAddress().getHostAddress()
                                + ":" + server.getLocalPort() + url.getFile()).openConnection(), 1000, 100);
                try {
                    assertThrows(SocketTimeoutException.class, () ->
                            transport.send("GET", "/rest/v1/bands", null, SESSION, false));
                } finally {
                    release.countDown();
                }
                peer.get(3, TimeUnit.SECONDS);
            } finally {
                release.countDown();
                worker.shutdownNow();
            }
        }
    }

    static class RecordingConnection extends HttpURLConnection {
        final ByteArrayOutputStream output = new ByteArrayOutputStream() {
            @Override public void close() { outputClosed = true; }
        };
        boolean outputOpened;
        boolean outputClosed;
        boolean inputClosed;
        boolean disconnected;
        private final int status;
        private final String response;

        RecordingConnection(int status, String response) throws Exception {
            super(new URL("http://fixture.test"));
            this.status = status;
            this.response = response;
        }

        void assertWaits() {
            assertEquals(15000, getConnectTimeout());
            assertEquals(30000, getReadTimeout());
        }

        @Override public java.io.OutputStream getOutputStream() {
            assertWaits();
            outputOpened = true;
            return output;
        }
        @Override public int getResponseCode() throws IOException { assertWaits(); return status; }
        @Override public InputStream getInputStream() {
            return new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8)) {
                @Override public void close() { inputClosed = true; }
            };
        }
        @Override public InputStream getErrorStream() { return getInputStream(); }
        @Override public void disconnect() { disconnected = true; }
        @Override public boolean usingProxy() { return false; }
        @Override public void connect() { }
    }
}
