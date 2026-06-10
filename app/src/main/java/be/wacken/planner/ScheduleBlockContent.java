package be.wacken.planner;

import java.util.Optional;

import be.wacken.planner.application.ScheduleDecisionCandidate;
import be.wacken.planner.application.TimelineSlot;

final class ScheduleBlockContent {
    private static final int LOST_ALTERNATIVE_MINUTES = 90;

    private final String bandLine;
    private final String stageLine;
    private final Optional<String> lostAlternativeLine;

    private ScheduleBlockContent(String bandLine, String stageLine, Optional<String> lostAlternativeLine) {
        this.bandLine = bandLine;
        this.stageLine = stageLine;
        this.lostAlternativeLine = lostAlternativeLine;
    }

    static ScheduleBlockContent from(
            TimelineSlot slot,
            ScheduleDecisionCandidate visible,
            int blockMinutes
    ) {
        String bandLine = visible.bandName() + " " + stars(visible.rating());
        Optional<String> lostAlternative = Optional.empty();
        if (blockMinutes >= LOST_ALTERNATIVE_MINUTES) {
            lostAlternative = slot.lostAlternativeBandName()
                    .map(name -> "Lost alt: " + name
                            + slot.lostAlternativeRating()
                            .map(rating -> " " + stars(rating))
                            .orElse(""));
        }
        return new ScheduleBlockContent(bandLine, visible.stageName(), lostAlternative);
    }

    String bandLine() {
        return bandLine;
    }

    String stageLine() {
        return stageLine;
    }

    Optional<String> lostAlternativeLine() {
        return lostAlternativeLine;
    }

    private static String stars(int rating) {
        int safeRating = Math.max(0, Math.min(5, rating));
        StringBuilder text = new StringBuilder(5);
        for (int index = 0; index < 5; index++) {
            text.append(index < safeRating ? "★" : "☆");
        }
        return text.toString();
    }
}
