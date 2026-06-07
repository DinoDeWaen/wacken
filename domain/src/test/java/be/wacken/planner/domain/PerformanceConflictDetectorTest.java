package be.wacken.planner.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PerformanceConflictDetectorTest {
    private final PerformanceConflictDetector detector = new PerformanceConflictDetector();

    @Test
    void groupsSameDayOverlappingPerformancesIntoTheSameConflictSet() {
        Performance first = performance("5th Avenue", 30, 18, 0, 19, 0);
        Performance second = performance("Airbourne", 30, 18, 30, 19, 30);

        List<PerformanceConflictSet> sets = detector.detect(List.of(first, second));

        assertEquals(1, sets.size());
        assertTrue(sets.get(0).hasConflict());
        assertEquals(List.of(first, second), sets.get(0).performances());
    }

    @Test
    void keepsSameTimePerformancesOnDifferentDaysSeparate() {
        Performance first = performance("5th Avenue", 30, 18, 0, 19, 0);
        Performance second = performance("Airbourne", 31, 18, 0, 19, 0);

        List<PerformanceConflictSet> sets = detector.detect(List.of(first, second));

        assertEquals(List.of(
                new PerformanceConflictSet(List.of(first)),
                new PerformanceConflictSet(List.of(second))
        ), sets);
        assertFalse(sets.get(0).hasConflict());
        assertFalse(sets.get(1).hasConflict());
    }

    @Test
    void keepsBoundaryTouchingPerformancesSeparate() {
        Performance first = performance("5th Avenue", 30, 18, 0, 19, 0);
        Performance second = performance("Airbourne", 30, 19, 0, 20, 0);

        List<PerformanceConflictSet> sets = detector.detect(List.of(first, second));

        assertEquals(List.of(
                new PerformanceConflictSet(List.of(first)),
                new PerformanceConflictSet(List.of(second))
        ), sets);
    }

    @Test
    void keepsIndependentNonOverlappingPerformancesSeparate() {
        Performance first = performance("5th Avenue", 30, 16, 0, 17, 0);
        Performance second = performance("Airbourne", 30, 18, 0, 19, 0);
        Performance third = performance("Iron Maiden", 31, 20, 0, 22, 0);

        List<PerformanceConflictSet> sets = detector.detect(List.of(second, third, first));

        assertEquals(List.of(
                new PerformanceConflictSet(List.of(first)),
                new PerformanceConflictSet(List.of(second)),
                new PerformanceConflictSet(List.of(third))
        ), sets);
    }

    @Test
    void groupsChainedOverlapsIntoOneConnectedConflictSet() {
        Performance first = performance("5th Avenue", 30, 18, 0, 19, 0);
        Performance second = performance("Airbourne", 30, 18, 45, 19, 30);
        Performance third = performance("Iron Maiden", 30, 19, 15, 20, 0);

        List<PerformanceConflictSet> sets = detector.detect(List.of(first, third, second));

        assertEquals(1, sets.size());
        assertTrue(sets.get(0).hasConflict());
        assertEquals(List.of(first, second, third), sets.get(0).performances());
    }

    private Performance performance(String bandName, int day, int startHour, int startMinute, int endHour, int endMinute) {
        return new Performance(
                new Band(bandName),
                new Stage("Stage " + bandName),
                LocalDateTime.of(2026, 7, day, startHour, startMinute),
                LocalDateTime.of(2026, 7, day, endHour, endMinute)
        );
    }
}
