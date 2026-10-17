package be.wacken.planner;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.Stage;

public final class FestivalScopedPerformanceRepositoryTest {
    @Test
    public void deletingOneFestivalCannotExposeAnotherFestivalsSchedule() {
        Performance wacken = performance("wacken-2026", "Airbourne");
        Performance other = performance("other-2027", "Alestorm");
        Memory source = new Memory();
        source.replaceAll(List.of(wacken, other));

        assertEquals(List.of(wacken), new FestivalScopedPerformanceRepository(source, "wacken-2026").findAll());
        source.rows.remove(wacken);
        assertEquals(List.of(other), new FestivalScopedPerformanceRepository(source, "other-2027").findAll());
    }

    private Performance performance(String festivalId, String bandName) {
        return new Performance(new Band(bandName), new Stage("Main"),
                LocalDateTime.parse("2027-08-01T12:00:00"),
                LocalDateTime.parse("2027-08-01T13:00:00"), festivalId);
    }

    private static final class Memory implements PerformanceRepository {
        final List<Performance> rows = new ArrayList<>();
        @Override public void save(Performance performance) { rows.add(performance); }
        @Override public void replaceAll(List<Performance> performances) { rows.clear(); rows.addAll(performances); }
        @Override public List<Performance> findAll() { return List.copyOf(rows); }
    }
}
