package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.Stage;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListBandsUseCaseTest {
    @Test
    void returnsBandsWithStageAndTimeSortedByStartTime() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        performances.save(performance("Later Band", "Harder Stage", 21, 0, 22, 0));
        performances.save(performance("Earlier Band", "Faster Stage", 18, 0, 19, 0));

        ListBandsUseCase useCase = new ListBandsUseCase(performances);

        assertEquals(
                List.of(
                        new BandListItem("Earlier Band", "Faster Stage", "2026-07-30T18:00", "2026-07-30T19:00"),
                        new BandListItem("Later Band", "Harder Stage", "2026-07-30T21:00", "2026-07-30T22:00")
                ),
                useCase.listBands()
        );
    }

    @Test
    void returnsEmptyListWhenNoPerformancesAreImported() {
        ListBandsUseCase useCase = new ListBandsUseCase(new FakePerformanceRepository());

        assertEquals(List.of(), useCase.listBands());
    }

    private static Performance performance(String bandName, String stageName, int startHour, int startMinute, int endHour, int endMinute) {
        return new Performance(
                new Band(bandName),
                new Stage(stageName),
                LocalDateTime.of(2026, 7, 30, startHour, startMinute),
                LocalDateTime.of(2026, 7, 30, endHour, endMinute)
        );
    }

    private static final class FakePerformanceRepository implements PerformanceRepository {
        private final List<Performance> performances = new ArrayList<>();

        @Override
        public void save(Performance performance) {
            performances.add(performance);
        }

        @Override
        public List<Performance> findAll() {
            return new ArrayList<>(performances);
        }
    }
}
