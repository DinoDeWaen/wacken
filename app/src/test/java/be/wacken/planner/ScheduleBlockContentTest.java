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

public final class ScheduleBlockContentTest {
    @Test
    public void shortBlockKeepsOnlyEssentialActContent() {
        TimelineSlot slot = slot(45);
        ScheduleBlockContent content = ScheduleBlockContent.from(slot, slot.candidates().get(0), 45);

        assertEquals("13:30", content.startTimeLine());
        assertEquals("Alien Rockin Explosion ★★★★☆", content.bandLine());
        assertEquals("Wackinger Stage", content.stageLine());
        assertEquals("14:15", content.endTimeLine());
        assertFalse(content.lostAlternativeLine().isPresent());
        assertFalse(content.bandLine().contains("GO"));
        assertFalse(content.stageLine().contains("13:30"));
    }

    @Test
    public void tallBlockCanShowLostAlternative() {
        TimelineSlot slot = slot(120);
        ScheduleBlockContent content = ScheduleBlockContent.from(slot, slot.candidates().get(0), 120);

        assertTrue(content.lostAlternativeLine().isPresent());
        assertEquals("Lost alt: Rose Tattoo ★★★★☆", content.lostAlternativeLine().get());
    }

    @Test
    public void hidesUuidFromVisibleBandAndLostAlternativeLines() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 30, 13, 30);
        ScheduleDecisionCandidate chosen = new ScheduleDecisionCandidate(
                "Def Leppard - 123e4567-e89b-12d3-a456-426614174000",
                5,
                "Harder",
                start,
                start.plusMinutes(120),
                "CHOSEN",
                true
        );
        TimelineSlot slot = new TimelineSlot(
                chosen.bandName(),
                5,
                "Harder",
                start,
                start.plusMinutes(120),
                GroupDecisionStatus.GO,
                Optional.of("Year of the Goat - 123e4567-e89b-12d3-a456-426614174000"),
                Optional.of(4),
                List.of(chosen),
                OptionalInt.of(5)
        );

        ScheduleBlockContent content = ScheduleBlockContent.from(slot, chosen, 120);

        assertEquals("Def Leppard ★★★★★", content.bandLine());
        assertEquals("Lost alt: Year of the Goat ★★★★☆", content.lostAlternativeLine().get());
    }

    private TimelineSlot slot(int minutes) {
        LocalDateTime start = LocalDateTime.of(2026, 7, 30, 13, 30);
        LocalDateTime end = start.plusMinutes(minutes);
        ScheduleDecisionCandidate chosen = new ScheduleDecisionCandidate(
                "Alien Rockin Explosion",
                4,
                "Wackinger Stage",
                start,
                end,
                "CHOSEN",
                true
        );
        return new TimelineSlot(
                "Alien Rockin Explosion",
                4,
                "Wackinger Stage",
                start,
                end,
                GroupDecisionStatus.GO,
                Optional.of("Rose Tattoo"),
                Optional.of(4),
                List.of(chosen),
                OptionalInt.of(15)
        );
    }
}
