package be.wacken.planner.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PerformanceTest {
    @Test
    void acceptsPerformanceWhenEndIsAfterStart() {
        Band band = new Band("5th Avenue");
        Stage stage = new Stage("Faster Stage");
        LocalDateTime start = LocalDateTime.of(2026, 7, 30, 18, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 30, 19, 0);

        Performance performance = new Performance(band, stage, start, end);

        assertEquals(band, performance.band());
        assertEquals(stage, performance.stage());
        assertEquals(start, performance.start());
        assertEquals(end, performance.end());
    }

    @Test
    void rejectsPerformanceWhenEndEqualsStart() {
        Band band = new Band("5th Avenue");
        Stage stage = new Stage("Faster Stage");
        LocalDateTime start = LocalDateTime.of(2026, 7, 30, 18, 0);

        DomainValidationException error = assertThrows(
                DomainValidationException.class,
                () -> new Performance(band, stage, start, start)
        );

        assertEquals("Performance end time must be after start time.", error.getMessage());
    }

    @Test
    void rejectsPerformanceWhenEndIsBeforeStart() {
        Band band = new Band("5th Avenue");
        Stage stage = new Stage("Faster Stage");
        LocalDateTime start = LocalDateTime.of(2026, 7, 30, 18, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 30, 17, 59);

        DomainValidationException error = assertThrows(
                DomainValidationException.class,
                () -> new Performance(band, stage, start, end)
        );

        assertEquals("Performance end time must be after start time.", error.getMessage());
    }
}
