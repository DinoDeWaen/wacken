package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.Stage;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryPerformanceRepositoryTest {
    @Test
    void storesAndRetrievesPerformances() {
        PerformanceRepository repository = new InMemoryPerformanceRepository();
        Performance performance = new Performance(
                new Band("5th Avenue"),
                new Stage("Faster Stage"),
                LocalDateTime.of(2026, 7, 30, 18, 0),
                LocalDateTime.of(2026, 7, 30, 19, 0)
        );

        repository.save(performance);

        assertEquals(List.of(performance), repository.findAll());
    }
}
