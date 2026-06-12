package be.wacken.planner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;

import be.wacken.planner.application.ScheduleDecisionCandidate;

public final class ScheduleBlockStyleTest {
    @Test
    public void fiveStarVisibleActsUseGoldBorder() {
        ScheduleDecisionCandidate visible = candidate("Def Leppard", 5, 22, 0, 0);

        ScheduleBlockStyle style = ScheduleBlockStyle.from(visible, List.of(visible));

        assertEquals(ScheduleBlockStyle.BorderTone.GOLD, style.borderTone());
    }

    @Test
    public void twoStarVisibleActsUseLightGreyBorder() {
        ScheduleDecisionCandidate visible = candidate("Weak Winner", 2, 18, 0, 0);

        ScheduleBlockStyle style = ScheduleBlockStyle.from(visible, List.of(visible));

        assertEquals(ScheduleBlockStyle.BorderTone.LIGHT_GREY, style.borderTone());
    }

    @Test
    public void otherVisibleActsUseRedBorder() {
        ScheduleDecisionCandidate visible = candidate("Normal Winner", 4, 18, 0, 0);

        ScheduleBlockStyle style = ScheduleBlockStyle.from(visible, List.of(visible));

        assertEquals(ScheduleBlockStyle.BorderTone.RED, style.borderTone());
    }

    @Test
    public void scratchesOnlyLowerRatedVisibleBlockWhenOverlapIsMoreThanFifteenMinutes() {
        ScheduleDecisionCandidate winner = candidate("Hämatom", 4, 22, 0, 120);
        ScheduleDecisionCandidate lowerRatedOverlap = candidate("Kadavar", 3, 21, 15, 75);

        ScheduleBlockStyle winnerStyle = ScheduleBlockStyle.from(winner, List.of(winner, lowerRatedOverlap));
        ScheduleBlockStyle lowerRatedStyle = ScheduleBlockStyle.from(lowerRatedOverlap, List.of(winner, lowerRatedOverlap));

        assertFalse(winnerStyle.scratched());
        assertTrue(lowerRatedStyle.scratched());
    }

    @Test
    public void doesNotScratchWhenVisibleOverlapIsFifteenMinutes() {
        ScheduleDecisionCandidate first = candidate("Yngwie Malmsteen", 5, 17, 30, 75);
        ScheduleDecisionCandidate second = candidate("Storm Seeker", 4, 18, 30, 60);

        ScheduleBlockStyle style = ScheduleBlockStyle.from(second, List.of(first, second));

        assertFalse(style.scratched());
    }

    @Test
    public void doesNotScratchWhenThereIsNoVisibleOverlap() {
        ScheduleDecisionCandidate visible = candidate("Thundermother", 5, 15, 15, 60);
        ScheduleDecisionCandidate other = candidate("Ricky Warwick", 4, 16, 45, 60);

        ScheduleBlockStyle style = ScheduleBlockStyle.from(visible, List.of(visible, other));

        assertFalse(style.scratched());
    }

    @Test
    public void equalRatedVisibleOverlapsDoNotScratchEachOther() {
        ScheduleDecisionCandidate first = candidate("Uli Jon Roth", 5, 16, 15, 90);
        ScheduleDecisionCandidate second = candidate("Yngwie Malmsteen", 5, 17, 0, 105);

        ScheduleBlockStyle firstStyle = ScheduleBlockStyle.from(first, List.of(first, second));
        ScheduleBlockStyle secondStyle = ScheduleBlockStyle.from(second, List.of(first, second));

        assertFalse(firstStyle.scratched());
        assertFalse(secondStyle.scratched());
    }

    @Test
    public void lostAlternativeTextAloneDoesNotScratchWinningBlock() {
        ScheduleDecisionCandidate winner = candidate("Hämatom", 4, 22, 0, 120);

        ScheduleBlockStyle style = ScheduleBlockStyle.from(winner, List.of(winner));

        assertFalse(style.scratched());
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
