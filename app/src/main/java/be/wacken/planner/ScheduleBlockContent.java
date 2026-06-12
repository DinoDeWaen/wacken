package be.wacken.planner;

import java.util.Optional;

import be.wacken.planner.application.ScheduleDecisionCandidate;
import be.wacken.planner.application.TimelineSlot;

final class ScheduleBlockContent {
    private static final int LOST_ALTERNATIVE_MINUTES = 90;
    private static final java.time.format.DateTimeFormatter TIME =
            java.time.format.DateTimeFormatter.ofPattern("HH:mm");

    private final String timeRangeLine;
    private final String bandLine;
    private final String stageLine;
    private final Optional<String> lostAlternativeLine;

    private ScheduleBlockContent(
            String timeRangeLine,
            String bandLine,
            String stageLine,
            Optional<String> lostAlternativeLine
    ) {
        this.timeRangeLine = timeRangeLine;
        this.bandLine = bandLine;
        this.stageLine = stageLine;
        this.lostAlternativeLine = lostAlternativeLine;
    }

    static ScheduleBlockContent from(
            TimelineSlot slot,
            ScheduleDecisionCandidate visible,
            int blockMinutes
    ) {
        String bandLine = ScheduleBandDisplayName.clean(visible.bandName()) + " " + stars(visible.rating());
        Optional<String> lostAlternative = Optional.empty();
        if (blockMinutes >= LOST_ALTERNATIVE_MINUTES) {
            lostAlternative = slot.lostAlternativeBandName()
                    .map(name -> "Lost alt: " + ScheduleBandDisplayName.clean(name)
                            + slot.lostAlternativeRating()
                            .map(rating -> " " + stars(rating))
                            .orElse(""));
        }
        return new ScheduleBlockContent(
                visible.start().format(TIME) + "-" + visible.end().format(TIME),
                bandLine,
                visible.stageName(),
                lostAlternative
        );
    }

    String timeRangeLine() {
        return timeRangeLine;
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
