package be.wacken.planner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.junit.Test;

import be.wacken.planner.application.ScheduleDecisionCandidate;
import be.wacken.planner.application.TimelineSlot;
import be.wacken.planner.domain.GroupDecisionStatus;

public final class ScheduleBlockStyleTest {
    @Test
    public void fiveStarVisibleActsUseGoldBorder() {
        ScheduleDecisionCandidate visible = candidate("Def Leppard", 5, 22, 0, 0);

        ScheduleBlockStyle style = ScheduleBlockStyle.from(slot(visible, List.of(visible)), visible);

        assertEquals(ScheduleBlockStyle.BorderTone.GOLD, style.borderTone());
    }

    @Test
    public void twoStarVisibleActsUseLightGreyBorder() {
        ScheduleDecisionCandidate visible = candidate("Weak Winner", 2, 18, 0, 0);

        ScheduleBlockStyle style = ScheduleBlockStyle.from(slot(visible, List.of(visible)), visible);

        assertEquals(ScheduleBlockStyle.BorderTone.LIGHT_GREY, style.borderTone());
    }

    @Test
    public void otherVisibleActsUseRedBorder() {
        ScheduleDecisionCandidate visible = candidate("Normal Winner", 4, 18, 0, 0);

        ScheduleBlockStyle style = ScheduleBlockStyle.from(slot(visible, List.of(visible)), visible);

        assertEquals(ScheduleBlockStyle.BorderTone.RED, style.borderTone());
    }

    @Test
    public void scratchesVisibleBlockWhenLostAlternativeOverlapsMoreThanFifteenMinutes() {
        ScheduleDecisionCandidate visible = candidate("Chosen", 4, 18, 0, 60);
        ScheduleDecisionCandidate lost = candidate("Skipped", 5, 18, 20, 60, "LOST ALTERNATIVE", false);

        ScheduleBlockStyle style = ScheduleBlockStyle.from(slot(visible, List.of(visible, lost)), visible);

        assertTrue(style.scratched());
    }

    @Test
    public void doesNotScratchWhenLostAlternativeOverlapIsFifteenMinutes() {
        ScheduleDecisionCandidate visible = candidate("Chosen", 4, 18, 0, 60);
        ScheduleDecisionCandidate lost = candidate("Skipped", 5, 18, 45, 60, "LOST ALTERNATIVE", false);

        ScheduleBlockStyle style = ScheduleBlockStyle.from(slot(visible, List.of(visible, lost)), visible);

        assertFalse(style.scratched());
    }

    @Test
    public void doesNotScratchWhenThereIsNoLostAlternative() {
        ScheduleDecisionCandidate visible = candidate("Chosen", 4, 18, 0, 60);
        ScheduleDecisionCandidate rejected = candidate("Rejected", 5, 18, 20, 60, "NOT SELECTED", false);

        ScheduleBlockStyle style = ScheduleBlockStyle.from(slot(visible, List.of(visible, rejected)), visible);

        assertFalse(style.scratched());
    }

    private TimelineSlot slot(
            ScheduleDecisionCandidate visible,
            List<ScheduleDecisionCandidate> candidates
    ) {
        Optional<String> lostAlternative = candidates.stream()
                .filter(candidate -> "LOST ALTERNATIVE".equals(candidate.status()))
                .map(ScheduleDecisionCandidate::bandName)
                .findFirst();
        return new TimelineSlot(
                visible.bandName(),
                visible.rating(),
                visible.stageName(),
                visible.start(),
                visible.end(),
                GroupDecisionStatus.GO,
                lostAlternative,
                Optional.of(5),
                candidates,
                OptionalInt.empty()
        );
    }

    private ScheduleDecisionCandidate candidate(
            String bandName,
            int rating,
            int startHour,
            int startMinute,
            int durationMinutes
    ) {
        return candidate(bandName, rating, startHour, startMinute, durationMinutes, "CHOSEN", true);
    }

    private ScheduleDecisionCandidate candidate(
            String bandName,
            int rating,
            int startHour,
            int startMinute,
            int durationMinutes,
            String status,
            boolean selected
    ) {
        LocalDateTime start = LocalDateTime.of(2026, 7, 30, startHour, startMinute);
        return new ScheduleDecisionCandidate(
                bandName,
                rating,
                "Harder",
                start,
                start.plusMinutes(durationMinutes),
                status,
                selected
        );
    }
}
