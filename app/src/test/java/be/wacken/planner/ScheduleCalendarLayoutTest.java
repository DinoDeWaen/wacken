package be.wacken.planner;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import be.wacken.planner.application.TimelineSlot;
import be.wacken.planner.domain.GroupDecisionStatus;

public final class ScheduleCalendarLayoutTest {
    @Test
    public void spansHourLinesAroundScheduledSlots() {
        ScheduleCalendarLayout layout = ScheduleCalendarLayout.forSlots(List.of(
                slot("5th Avenue", 18, 30, 19, 15),
                slot("Airbourne", 21, 0, 22, 0)
        ));

        assertEquals(18, layout.startHour());
        assertEquals(26, layout.endHour());
        assertEquals(8, layout.hourCount());
        assertEquals("18:00", layout.hourLabel(0));
        assertEquals("21:00", layout.hourLabel(3));
        assertEquals("02:00", layout.hourLabel(8));
    }

    @Test
    public void positionsBlocksByMinutesFromFirstHour() {
        TimelineSlot slot = slot("5th Avenue", 18, 30, 19, 15);

        ScheduleCalendarLayout layout = ScheduleCalendarLayout.forSlots(List.of(slot));

        assertEquals(30, layout.topOffsetMinutes(slot));
        assertEquals(45, layout.durationMinutes(slot));
    }

    @Test
    public void keepsShortBlocksReadable() {
        TimelineSlot slot = slot("5th Avenue", 18, 0, 18, 10);

        ScheduleCalendarLayout layout = ScheduleCalendarLayout.forSlots(List.of(slot));

        assertEquals(30, layout.durationMinutes(slot));
    }

    @Test
    public void positionsAfterMidnightBlocksInsidePreviousFestivalDay() {
        TimelineSlot slot = new TimelineSlot(
                "Sepultura",
                5,
                "Harder",
                LocalDateTime.of(2026, 7, 31, 1, 0),
                LocalDateTime.of(2026, 7, 31, 2, 0),
                GroupDecisionStatus.GO,
                Optional.empty(),
                Optional.empty()
        );

        ScheduleCalendarLayout layout = ScheduleCalendarLayout.forCandidates(List.of(candidate(slot)), LocalDate.of(2026, 7, 30));

        assertEquals(25, layout.startHour());
        assertEquals(26, layout.endHour());
        assertEquals("01:00", layout.hourLabel(0));
        assertEquals("02:00", layout.hourLabel(1));
        assertEquals(0, layout.topOffsetMinutes(slot));
        assertEquals(60, layout.durationMinutes(slot));
    }

    private be.wacken.planner.application.ScheduleDecisionCandidate candidate(TimelineSlot slot) {
        return new be.wacken.planner.application.ScheduleDecisionCandidate(
                slot.bandName(),
                slot.rating(),
                slot.stageName(),
                slot.start(),
                slot.end(),
                "CHOSEN",
                true
        );
    }

    private TimelineSlot slot(String band, int startHour, int startMinute, int endHour, int endMinute) {
        return new TimelineSlot(
                band,
                4,
                "Faster",
                LocalDateTime.of(2026, 7, 30, startHour, startMinute),
                LocalDateTime.of(2026, 7, 30, endHour, endMinute),
                GroupDecisionStatus.GO,
                Optional.empty(),
                Optional.empty()
        );
    }
}
