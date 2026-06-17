package be.wacken.planner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

import be.wacken.planner.application.ScheduleDay;

public final class ScheduleDaySelectionTest {
    @Test
    public void selectsRequestedFestivalDayWhenAvailable() {
        ScheduleDay first = new ScheduleDay(LocalDate.of(2026, 7, 30), List.of());
        ScheduleDay second = new ScheduleDay(LocalDate.of(2026, 7, 31), List.of());

        ScheduleDay selected = ScheduleDaySelection.selectedDay(
                List.of(first, second),
                LocalDate.of(2026, 7, 31)
        ).orElseThrow();

        assertEquals(second, selected);
        assertTrue(ScheduleDaySelection.isSelected(second, selected));
        assertFalse(ScheduleDaySelection.isSelected(first, selected));
    }

    @Test
    public void fallsBackToFirstFestivalDayWhenRequestedDayIsMissing() {
        ScheduleDay first = new ScheduleDay(LocalDate.of(2026, 7, 30), List.of());
        ScheduleDay second = new ScheduleDay(LocalDate.of(2026, 7, 31), List.of());

        ScheduleDay selected = ScheduleDaySelection.selectedDay(
                List.of(first, second),
                LocalDate.of(2026, 8, 2)
        ).orElseThrow();

        assertEquals(first, selected);
    }

    @Test
    public void returnsEmptyWhenThereAreNoScheduleDays() {
        assertTrue(ScheduleDaySelection.selectedDay(List.of(), LocalDate.of(2026, 7, 31)).isEmpty());
    }
}
