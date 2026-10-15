package be.wacken.planner.infrastructure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/** Coordinates the Android TSV import with its Room cache transaction. */
public final class TsvImportTransaction implements Consumer<Runnable> {
    private static final List<String> MASTER_FILES = List.of(
            "bands.tsv", "stages.tsv", "performances.tsv", "stage-distances.tsv", "food-options.tsv"
    );

    private final Path directory;
    private final Consumer<Runnable> cacheTransaction;

    public TsvImportTransaction(Path directory, Consumer<Runnable> cacheTransaction) {
        this.directory = Objects.requireNonNull(directory, "directory must not be null");
        this.cacheTransaction = Objects.requireNonNull(cacheTransaction, "cacheTransaction must not be null");
    }

    @Override
    public void accept(Runnable replacement) {
        Objects.requireNonNull(replacement, "replacement must not be null");
        Snapshot snapshot = snapshot();
        try {
            cacheTransaction.accept(replacement);
        } catch (RuntimeException | Error failure) {
            try {
                restore(snapshot);
            } catch (IOException | RuntimeException restoreFailure) {
                IllegalStateException incomplete = new IllegalStateException(
                        "Import failed and TSV data could not be fully restored. Backup: " + snapshot.backup(), failure);
                incomplete.addSuppressed(restoreFailure);
                throw incomplete;
            }
            throw failure;
        } finally {
            // Keep the backup when restoration failed so it can be recovered manually.
            if (!snapshot.restoreFailed) {
                discard(snapshot);
            }
        }
    }

    private Snapshot snapshot() {
        try {
            Files.createDirectories(directory);
            Path backup = Files.createTempDirectory(directory, ".festival-import-backup-");
            Set<String> existing = new java.util.HashSet<>();
            for (String name : MASTER_FILES) {
                Path source = directory.resolve(name);
                if (Files.exists(source)) {
                    Files.copy(source, backup.resolve(name), StandardCopyOption.REPLACE_EXISTING);
                    existing.add(name);
                }
            }
            return new Snapshot(backup, existing);
        } catch (IOException error) {
            throw new IllegalStateException("Could not back up festival data before import.", error);
        }
    }

    private void restore(Snapshot snapshot) throws IOException {
        snapshot.restoreFailed = true;
        List<IOException> failures = new ArrayList<>();
        for (String name : MASTER_FILES) {
            try {
                Path target = directory.resolve(name);
                if (snapshot.existing().contains(name)) {
                    Files.copy(snapshot.backup().resolve(name), target, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    Files.deleteIfExists(target);
                }
            } catch (IOException error) {
                failures.add(error);
            }
        }
        if (!failures.isEmpty()) {
            IOException aggregate = new IOException("Could not restore all festival TSV files.");
            failures.forEach(aggregate::addSuppressed);
            throw aggregate;
        }
        snapshot.restoreFailed = false;
    }

    private void discard(Snapshot snapshot) {
        for (String name : MASTER_FILES) {
            try {
                Files.deleteIfExists(snapshot.backup().resolve(name));
            } catch (IOException ignored) {
                // A stale backup is harmless and must not turn a committed import into a failure.
            }
        }
        try {
            Files.deleteIfExists(snapshot.backup());
        } catch (IOException ignored) {
            // See above.
        }
    }

    private static final class Snapshot {
        private final Path backup;
        private final Set<String> existing;
        private boolean restoreFailed;

        private Snapshot(Path backup, Set<String> existing) {
            this.backup = backup;
            this.existing = existing;
        }

        private Path backup() { return backup; }
        private Set<String> existing() { return existing; }
    }
}
