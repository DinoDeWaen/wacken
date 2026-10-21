package be.wacken.planner.infrastructure;

import be.wacken.planner.application.FestivalCsvFiles;
import be.wacken.planner.application.ImportFestivalCsvResult;
import be.wacken.planner.application.ReviewFestivalCsvUseCase;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalRepository;
import be.wacken.planner.domain.FestivalLineupEntry;
import be.wacken.planner.domain.FestivalLineupRepository;
import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.StageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TsvImportTransactionTest {
    @TempDir Path directory;

    @Test
    void sequentialImportWithoutCoordinatorCanLeavePartialSourceWrites() {
        FileBackedBandRepository bandSource = new FileBackedBandRepository(directory);
        FileBackedStageRepository stageSource = new FileBackedStageRepository(directory);
        bandSource.replaceAll(List.of(new Band("Old Band")));
        stageSource.replaceAll(List.of(new Stage("Old Stage")));
        StageRepository failingCache = new StageRepository() {
            @Override public void save(Stage stage) { throw new IllegalStateException("injected cache failure"); }
            @Override public void replaceAll(List<Stage> stages) { throw new IllegalStateException("injected cache failure"); }
            @Override public Optional<Stage> findByName(String name) { return Optional.empty(); }
            @Override public List<Stage> findAll() { return List.of(); }
        };
        ReviewFestivalCsvUseCase uncoordinated = new ReviewFestivalCsvUseCase(
                new SyncedBandRepository(new InMemoryBandRepository(), bandSource),
                new SyncedStageRepository(failingCache, stageSource),
                new InMemoryPerformanceRepository(), new InMemoryStageDistanceRepository(),
                new InMemoryFoodOptionRepository(), new FestivalRepository() {
                    @Override public List<Festival> findAll() { return List.of(Festival.active("active", "Active")); }
                    @Override public void save(Festival festival) { throw new AssertionError("No festival write expected"); }
                }, new FestivalLineupRepository() {
                    @Override public List<FestivalLineupEntry> findByFestival(String id) { return List.of(); }
                    @Override public void saveAllForFestival(String id, List<FestivalLineupEntry> entries) {
                        throw new AssertionError("Stage failure must occur before lineup writes");
                    }
                }, Runnable::run);

        var review = uncoordinated.review(new FestivalCsvFiles(
                "band_id,name\nnew,New Band\n", "stage_id,name\nnew,New Stage\n",
                "performance_id,band_id,stage_id,start_at,end_at\n",
                "from_stage_id,to_stage_id,walking_minutes\n", "food_id,name,near_stage_id\n"));

        ImportFestivalCsvResult result = uncoordinated.apply(review, review.changes().stream()
                .filter(ReviewFestivalCsvUseCase.Change::valid)
                .map(ReviewFestivalCsvUseCase.Change::key).collect(java.util.stream.Collectors.toSet()));

        assertFalse(result.success());
        assertEquals(List.of(new Band("New Band")), bandSource.findAll());
        assertEquals(List.of(new Stage("New Stage")), stageSource.findAll());
    }

    @Test
    void restoresEverySourceFileWhenAReplacementFailsAfterEarlierWrites() throws IOException {
        Files.writeString(directory.resolve("bands.tsv"), "old bands");
        Files.writeString(directory.resolve("stages.tsv"), "old stages");
        AtomicBoolean databaseRolledBack = new AtomicBoolean();
        AtomicReference<String> cachedBands = new AtomicReference<>("old bands");
        TsvImportTransaction transaction = new TsvImportTransaction(directory, work -> {
            try {
                work.run();
            } catch (RuntimeException failure) {
                cachedBands.set("old bands");
                databaseRolledBack.set(true);
                throw failure;
            }
        });

        IllegalStateException failure = assertThrows(IllegalStateException.class, () -> transaction.accept(() -> {
            write("bands.tsv", "new bands");
            cachedBands.set("new bands");
            write("stages.tsv", "new stages");
            write("performances.tsv", "new performances");
            throw new IllegalStateException("injected persistence failure");
        }));

        assertEquals("injected persistence failure", failure.getMessage());
        assertTrue(databaseRolledBack.get());
        assertEquals("old bands", cachedBands.get());
        assertEquals("old bands", Files.readString(directory.resolve("bands.tsv")));
        assertEquals("old stages", Files.readString(directory.resolve("stages.tsv")));
        assertFalse(Files.exists(directory.resolve("performances.tsv")));
    }

    @Test
    void commitsAllFiveFilesWhenReplacementSucceeds() throws IOException {
        TsvImportTransaction transaction = new TsvImportTransaction(directory, Runnable::run);

        transaction.accept(() -> {
            write("bands.tsv", "bands");
            write("stages.tsv", "stages");
            write("performances.tsv", "performances");
            write("stage-distances.tsv", "distances");
            write("food-options.tsv", "food");
        });

        assertEquals("bands", Files.readString(directory.resolve("bands.tsv")));
        assertEquals("stages", Files.readString(directory.resolve("stages.tsv")));
        assertEquals("performances", Files.readString(directory.resolve("performances.tsv")));
        assertEquals("distances", Files.readString(directory.resolve("stage-distances.tsv")));
        assertEquals("food", Files.readString(directory.resolve("food-options.tsv")));
    }

    private void write(String file, String contents) {
        try {
            Files.writeString(directory.resolve(file), contents);
        } catch (IOException error) {
            throw new IllegalStateException(error);
        }
    }
}
