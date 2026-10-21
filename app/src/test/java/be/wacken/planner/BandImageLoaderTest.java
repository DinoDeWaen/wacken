package be.wacken.planner;

import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.SocketTimeoutException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;

public final class BandImageLoaderTest {
    @Test
    public void successfulDecodeClosesStreamAndConnectionBeforeUpdatingTarget() throws Exception {
        Fixture f = new Fixture(input -> "bitmap");
        f.load();
        f.worker.next();
        assertTrue(f.connection.streamClosed);
        assertTrue(f.connection.disconnected);
        assertTrue(f.events.isEmpty());
        f.ui.next();
        assertEquals(List.of("bitmap"), f.events);
        f.loader.close();
    }

    @Test
    public void nullDecodeDecodeExceptionAndNetworkTimeoutUseMissingImageFallback() throws Exception {
        for (int kind = 0; kind < 3; kind++) {
            int outcome = kind;
            Fixture f = new Fixture(input -> {
                if (outcome == 1) throw new IOException("decode failed");
                return null;
            });
            f.connection.fail = kind == 2;
            f.load();
            f.worker.next();
            f.ui.next();
            assertEquals(List.of("hidden"), f.events);
            assertTrue(f.connection.disconnected);
            if (kind != 2) assertTrue(f.connection.streamClosed);
            f.loader.close();
        }
    }

    @Test
    public void replacementIgnoresAlreadyQueuedResultForOldTarget() throws Exception {
        Fixture f = new Fixture(input -> "bitmap");
        f.load();
        f.worker.next();
        f.loader.load("https://fixture.test/next", image -> f.events.add("new " + image), () -> f.events.add("new hidden"));
        f.ui.next();
        assertTrue(f.events.isEmpty());
        f.worker.next();
        f.ui.next();
        assertEquals(List.of("new bitmap"), f.events);
        f.loader.close();
    }

    @Test
    public void destroySuppressesQueuedSuccessAndFailureAndStopsOwnedWorker() throws Exception {
        for (boolean fail : new boolean[] {false, true}) {
            Fixture f = new Fixture(input -> "bitmap");
            f.connection.fail = fail;
            f.load();
            f.worker.next();
            f.loader.close();
            f.ui.next();
            assertTrue(f.events.isEmpty());
            assertTrue(f.worker.isShutdown());
        }
    }

    @Test
    public void cancellingBeforeWorkerStartsOpensNoConnectionAndSendsNoCallback() throws Exception {
        Fixture f = new Fixture(input -> "bitmap");
        f.load();
        f.loader.close();
        assertEquals(0, f.opens);
        assertTrue(f.events.isEmpty());
        f.load();
        assertTrue(f.events.isEmpty());
    }

    @Test(timeout = 5000)
    public void destroyingDuringDecodeDisconnectsActiveRequestAndClosesItsStream() throws Exception {
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        Fixture f = new Fixture(input -> {
            entered.countDown();
            try {
                release.await();
                return "bitmap";
            } catch (InterruptedException error) {
                Thread.currentThread().interrupt();
                throw new IOException("cancelled", error);
            }
        });
        f.load();
        Thread request = new Thread(f.worker::next);
        request.start();
        try {
            assertTrue(entered.await(2, TimeUnit.SECONDS));
            f.loader.close();
            assertTrue(f.connection.disconnected);
        } finally {
            release.countDown();
            request.join(2000);
            f.loader.close();
        }
        assertFalse(request.isAlive());
        assertTrue(f.connection.streamClosed);
        f.ui.next();
        assertTrue(f.events.isEmpty());
    }

    private static final class Fixture {
        final ManualExecutor worker = new ManualExecutor();
        final ManualExecutor ui = new ManualExecutor();
        final ImageConnection connection = new ImageConnection();
        final List<String> events = new ArrayList<>();
        final BandImageLoader<String> loader;
        int opens;
        Fixture(BandImageLoader.Decoder<String> decoder) throws Exception {
            loader = new BandImageLoader<>(decoder, ui, url -> { opens++; return connection; }, worker);
        }
        void load() { loader.load("https://fixture.test/image", events::add, () -> events.add("hidden")); }
    }

    private static final class ImageConnection extends HttpURLConnection {
        boolean streamClosed;
        boolean disconnected;
        boolean fail;
        ImageConnection() throws Exception { super(new URL("https://fixture.test/image")); }
        @Override public InputStream getInputStream() throws IOException {
            assertEquals(5000, getConnectTimeout());
            assertEquals(5000, getReadTimeout());
            if (fail) throw new SocketTimeoutException("read timed out");
            return new ByteArrayInputStream(new byte[] {1}) {
                @Override public void close() { streamClosed = true; }
            };
        }
        @Override public void disconnect() { disconnected = true; }
        @Override public void connect() { }
        @Override public boolean usingProxy() { return false; }
    }

    private static final class ManualExecutor extends AbstractExecutorService {
        private final Queue<Runnable> work = new ArrayDeque<>();
        private boolean shutdown;
        void next() { assertFalse(work.isEmpty()); work.remove().run(); }
        @Override public void execute(Runnable command) { work.add(command); }
        @Override public void shutdown() { shutdown = true; }
        @Override public List<Runnable> shutdownNow() { shutdown = true; var remaining = new ArrayList<>(work); work.clear(); return remaining; }
        @Override public boolean isShutdown() { return shutdown; }
        @Override public boolean isTerminated() { return shutdown && work.isEmpty(); }
        @Override public boolean awaitTermination(long time, TimeUnit unit) { return isTerminated(); }
    }
}
