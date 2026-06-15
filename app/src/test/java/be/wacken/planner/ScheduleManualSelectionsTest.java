package be.wacken.planner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import be.wacken.planner.application.ScheduleDecisionCandidate;
import be.wacken.planner.application.TimelineSlot;
import be.wacken.planner.domain.GroupDecisionStatus;

public final class ScheduleManualSelectionsTest {
    @Test
    public void selectedAlternativeBecomesVisibleManualChoice() {
        TimelineSlot slot = slot();
        ScheduleDecisionCandidate alternative = slot.candidates().get(1);
        ScheduleManualSelections selections = new ScheduleManualSelections();

        selections.select(slot, alternative);

        assertTrue(selections.isManual(slot));
        assertEquals("Airbourne", selections.visibleCandidate(slot).bandName());
        assertEquals("MANUAL CHOICE", selections.visibleCandidate(slot).status());
        assertTrue(selections.visibleCandidate(slot).selected());
    }

    @Test
    public void previousGeneratedChoiceRemainsInDetails() {
        TimelineSlot slot = slot();
        ScheduleManualSelections selections = new ScheduleManualSelections();

        selections.select(slot, slot.candidates().get(1));

        List<ScheduleDecisionCandidate> detail = selections.detailCandidates(slot);
        assertEquals("Airbourne", detail.get(0).bandName());
        assertEquals("MANUAL CHOICE", detail.get(0).status());
        assertEquals("5th Avenue", detail.get(1).bandName());
        assertEquals("GENERATED CHOICE", detail.get(1).status());
        assertFalse(detail.get(1).selected());
    }

    @Test
    public void tiedAlternativeRemainsFirstAfterGeneratedChoiceInDetails() {
        TimelineSlot slot = tiedSlot();
        ScheduleManualSelections selections = new ScheduleManualSelections();

        List<ScheduleDecisionCandidate> detail = selections.detailCandidates(slot);

        assertEquals("Future Palace", detail.get(0).bandName());
        assertEquals("CHOSEN", detail.get(0).status());
        assertTrue(detail.get(0).selected());
        assertEquals("Grand Magus", detail.get(1).bandName());
        assertEquals("⚖ TIED ALTERNATIVE", detail.get(1).status());
        assertFalse(detail.get(1).selected());
    }

    private TimelineSlot slot() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 30, 18, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 30, 19, 0);
        ScheduleDecisionCandidate chosen = new ScheduleDecisionCandidate(
                "5th Avenue",
                5,
                "Faster",
                start,
                end,
                "CHOSEN",
                true
        );
        ScheduleDecisionCandidate alternative = new ScheduleDecisionCandidate(
                "Airbourne",
                4,
                "Harder",
                start.plusMinutes(30),
                end.plusMinutes(30),
                "LOST ALTERNATIVE",
                false
        );
        return new TimelineSlot(
                "5th Avenue",
                5,
                "Faster",
                start,
                end,
                GroupDecisionStatus.GO,
                Optional.of("Airbourne"),
                Optional.of(4),
                List.of(chosen, alternative)
        );
    }

    private TimelineSlot tiedSlot() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 31, 13, 45);
        LocalDateTime end = LocalDateTime.of(2026, 7, 31, 14, 45);
        ScheduleDecisionCandidate chosen = new ScheduleDecisionCandidate(
                "Future Palace",
                4,
                "Louder",
                start,
                end,
                "CHOSEN",
                true
        );
        ScheduleDecisionCandidate tied = new ScheduleDecisionCandidate(
                "Grand Magus",
                4,
                "Headbangers Stage",
                start.plusMinutes(15),
                end,
                "⚖ TIED ALTERNATIVE",
                false
        );
        return new TimelineSlot(
                "Future Palace",
                4,
                "Louder",
                start,
                end,
                GroupDecisionStatus.GO,
                Optional.of("Grand Magus"),
                Optional.of(4),
                List.of(chosen, tied)
        );
    }
}
