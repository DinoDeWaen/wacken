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
        assertEquals(10, layout.endOffsetMinutes(candidate(slot)));
    }

    @Test
    public void positionsWalkingMarkerBetweenConsecutiveBlocks() {
        TimelineSlot first = slot("Alien Rockin Explosion", 13, 30, 14, 15);
        TimelineSlot second = slot("Thundermother", 15, 15, 16, 15);

        ScheduleCalendarLayout layout = ScheduleCalendarLayout.forSlots(List.of(first, second));

        assertEquals(75, layout.endOffsetMinutes(candidate(first)));
        assertEquals(135, layout.topOffsetMinutes(candidate(second)));
        assertEquals(105, layout.walkingMarkerOffsetMinutes(candidate(first), candidate(second)));
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

    @Test
    public void ordersStageColumnsWithLouderAndHarderFirstAndAdjacent() {
        be.wacken.planner.application.ScheduleDecisionCandidate wackinger = candidate(slot("Storm Seeker", "Wackinger Stage", 18, 0, 19, 0));
        be.wacken.planner.application.ScheduleDecisionCandidate harder = candidate(slot("Def Leppard", "Harder", 22, 15, 23, 45));
        be.wacken.planner.application.ScheduleDecisionCandidate louder = candidate(slot("Future Palace", "Louder", 13, 45, 14, 30));
        be.wacken.planner.application.ScheduleDecisionCandidate faster = candidate(slot("Paradise Lost", "Faster", 14, 30, 15, 30));

        ScheduleCalendarLayout layout = ScheduleCalendarLayout.forCandidates(List.of(wackinger, harder, louder, faster), LocalDate.of(2026, 7, 30));

        assertEquals(List.of("Louder", "Harder", "Faster", "Wackinger Stage"), layout.stageColumns());
        assertEquals(0, layout.stageColumnIndex(louder));
        assertEquals(1, layout.stageColumnIndex(harder));
        assertEquals(4, layout.stageColumnCount());
    }

    @Test
    public void exposesStageRowsForRotatedSchedule() {
        be.wacken.planner.application.ScheduleDecisionCandidate wackinger = candidate(slot("Storm Seeker", "Wackinger Stage", 18, 0, 19, 0));
        be.wacken.planner.application.ScheduleDecisionCandidate louder = candidate(slot("Future Palace", "Louder", 13, 45, 14, 30));

        ScheduleCalendarLayout layout = ScheduleCalendarLayout.forCandidates(List.of(wackinger, louder), LocalDate.of(2026, 7, 30));

        assertEquals(List.of("Louder", "Wackinger Stage"), layout.stageRows());
        assertEquals(0, layout.stageRowIndex(louder));
        assertEquals(1, layout.stageRowIndex(wackinger));
        assertEquals(2, layout.stageRowCount());
    }

    @Test
    public void assignsOverlappingDifferentStageActsToDifferentColumns() {
        be.wacken.planner.application.ScheduleDecisionCandidate louder = candidate(slot("Future Palace", "Louder", 13, 45, 14, 30));
        be.wacken.planner.application.ScheduleDecisionCandidate faster = candidate(slot("Paradise Lost", "Faster", 14, 0, 15, 30));

        ScheduleCalendarLayout layout = ScheduleCalendarLayout.forCandidates(List.of(louder, faster), LocalDate.of(2026, 7, 30));

        assertEquals(0, layout.stageColumnIndex(louder));
        assertEquals(1, layout.stageColumnIndex(faster));
        assertEquals(45, layout.topOffsetMinutes(louder));
        assertEquals(60, layout.topOffsetMinutes(faster));
    }

    @Test
    public void positionsRotatedBlocksByMinutesFromFirstHour() {
        be.wacken.planner.application.ScheduleDecisionCandidate louder = candidate(slot("Future Palace", "Louder", 13, 45, 14, 30));
        be.wacken.planner.application.ScheduleDecisionCandidate faster = candidate(slot("Paradise Lost", "Faster", 14, 0, 15, 30));

        ScheduleCalendarLayout layout = ScheduleCalendarLayout.forCandidates(List.of(louder, faster), LocalDate.of(2026, 7, 30));

        assertEquals(45, layout.leftOffsetMinutes(louder));
        assertEquals(60, layout.leftOffsetMinutes(faster));
        assertEquals(45, layout.durationMinutes(louder));
        assertEquals(90, layout.durationMinutes(faster));
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
        return slot(band, "Faster", startHour, startMinute, endHour, endMinute);
    }

    private TimelineSlot slot(String band, String stage, int startHour, int startMinute, int endHour, int endMinute) {
        return new TimelineSlot(
                band,
                4,
                stage,
                LocalDateTime.of(2026, 7, 30, startHour, startMinute),
                LocalDateTime.of(2026, 7, 30, endHour, endMinute),
                GroupDecisionStatus.GO,
                Optional.empty(),
                Optional.empty()
        );
    }
}
