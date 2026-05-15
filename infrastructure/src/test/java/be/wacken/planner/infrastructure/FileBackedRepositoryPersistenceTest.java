package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.FoodOption;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.StageDistance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedRepositoryPersistenceTest {
    @TempDir
    Path storageDirectory;

    @Test
    void persistsBandsAcrossRepositoryRecreation() {
        new FileBackedBandRepository(storageDirectory).save(new Band("5th Avenue"));

        FileBackedBandRepository reopened = new FileBackedBandRepository(storageDirectory);

        assertEquals(Optional.of(new Band("5th Avenue")), reopened.findByName("5th Avenue"));
        assertEquals(List.of(new Band("5th Avenue")), reopened.findAll());
    }

    @Test
    void persistsStagesAcrossRepositoryRecreation() {
        new FileBackedStageRepository(storageDirectory).save(new Stage("Faster"));

        FileBackedStageRepository reopened = new FileBackedStageRepository(storageDirectory);

        assertEquals(Optional.of(new Stage("Faster")), reopened.findByName("Faster"));
        assertEquals(List.of(new Stage("Faster")), reopened.findAll());
    }

    @Test
    void persistsPerformancesAcrossRepositoryRecreation() {
        Performance performance = new Performance(
                new Band("5th Avenue"),
                new Stage("Faster"),
                LocalDateTime.parse("2026-07-30T18:00:00"),
                LocalDateTime.parse("2026-07-30T19:00:00")
        );
        new FileBackedPerformanceRepository(storageDirectory).save(performance);

        FileBackedPerformanceRepository reopened = new FileBackedPerformanceRepository(storageDirectory);

        assertEquals(List.of(performance), reopened.findAll());
    }

    @Test
    void persistsStageDistancesAcrossRepositoryRecreation() {
        Stage faster = new Stage("Faster");
        Stage harder = new Stage("Harder");
        StageDistance distance = StageDistance.between(faster, harder, 7);
        new FileBackedStageDistanceRepository(storageDirectory).save(distance);

        FileBackedStageDistanceRepository reopened = new FileBackedStageDistanceRepository(storageDirectory);

        assertEquals(Optional.of(distance), reopened.findBetween(faster, harder));
        assertEquals(Optional.of(StageDistance.between(faster, faster, 0)), reopened.findBetween(faster, faster));
    }

    @Test
    void persistsFoodOptionsAcrossRepositoryRecreation() {
        new FileBackedFoodOptionRepository(storageDirectory).save(new FoodOption("Pizza"));

        FileBackedFoodOptionRepository reopened = new FileBackedFoodOptionRepository(storageDirectory);

        assertEquals(List.of(new FoodOption("Pizza")), reopened.findAll());
    }

    @Test
    void persistsRatingsAcrossRepositoryRecreation() {
        Band band = new Band("5th Avenue");
        new FileBackedRatingRepository(storageDirectory).save("dino", band, Rating.of(4));

        FileBackedRatingRepository reopened = new FileBackedRatingRepository(storageDirectory);

        assertEquals(Optional.of(Rating.of(4)), reopened.findByUserAndBand("dino", band));
    }
}
