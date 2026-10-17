package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.StageDistance;
import be.wacken.planner.domain.StageDistanceRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryStageDistanceRepositoryTest {
    @Test
    void storesAndRetrievesDistanceBetweenStages() {
        StageDistanceRepository repository = new InMemoryStageDistanceRepository();
        Stage faster = new Stage("Faster Stage");
        Stage harder = new Stage("Harder Stage");
        StageDistance distance = StageDistance.between(faster, harder, 12);

        repository.save(distance);

        assertEquals(Optional.of(distance), repository.findBetween(faster, harder));
    }

    @Test
    void resolvesSameStageDistanceToZeroWithoutStoredDistance() {
        StageDistanceRepository repository = new InMemoryStageDistanceRepository();
        Stage faster = new Stage("Faster Stage");

        assertEquals(Optional.of(StageDistance.between(faster, faster, 0)), repository.findBetween(faster, faster));
    }
}
