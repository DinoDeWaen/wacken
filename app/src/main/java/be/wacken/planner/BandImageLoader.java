package be.wacken.planner;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/** One image target per screen owner; replacement/destruction invalidates queued results. */
final class BandImageLoader<T> implements AutoCloseable {
    private final Decoder<T> decoder;
    private final Executor delivery;
    private final ConnectionFactory connections;
    private final ExecutorService worker;
    private Request current;
    private boolean closed;

    BandImageLoader(Decoder<T> decoder, Executor delivery) {
        this(decoder, delivery, URL::openConnection, Executors.newSingleThreadExecutor());
    }

    BandImageLoader(Decoder<T> decoder, Executor delivery, ConnectionFactory connections, ExecutorService worker) {
        this.decoder = decoder;
        this.delivery = delivery;
        this.connections = connections;
        this.worker = worker;
    }

    synchronized void load(String url, Consumer<T> success, Runnable missing) {
        if (closed) return;
        if (current != null) current.cancel();
        Request request = new Request();
        current = request;
        request.future = worker.submit(() -> {
            try {
                URLConnection connection = connections.open(new URL(url));
                request.connection = connection;
                if (request.cancelled) return;
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                T image;
                try (InputStream input = connection.getInputStream()) {
                    image = decoder.decode(input);
                }
                deliver(request, image == null ? missing : () -> success.accept(image));
            } catch (IOException | RuntimeException ignored) {
                deliver(request, missing);
            } finally {
                request.disconnect();
            }
        });
    }

    private void deliver(Request request, Runnable update) {
        delivery.execute(() -> {
            synchronized (BandImageLoader.this) {
                if (!closed && current == request && !request.cancelled) update.run();
            }
        });
    }

    @Override
    public synchronized void close() {
        if (closed) return;
        closed = true;
        if (current != null) current.cancel();
        current = null;
        worker.shutdownNow();
    }

    interface Decoder<T> {
        T decode(InputStream input) throws IOException;
    }

    interface ConnectionFactory {
        URLConnection open(URL url) throws IOException;
    }

    private static final class Request {
        volatile boolean cancelled;
        volatile URLConnection connection;
        Future<?> future;

        void cancel() {
            cancelled = true;
            if (future != null) future.cancel(true);
            disconnect();
        }

        void disconnect() {
            if (connection instanceof HttpURLConnection http) http.disconnect();
        }
    }
}
