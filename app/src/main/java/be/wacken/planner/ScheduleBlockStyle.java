package be.wacken.planner;

import java.time.Duration;
import java.time.LocalDateTime;

import be.wacken.planner.application.ScheduleDecisionCandidate;
import be.wacken.planner.application.TimelineSlot;

final class ScheduleBlockStyle {
    private static final int SCRATCH_OVERLAP_THRESHOLD_MINUTES = 15;

    enum BorderTone {
        GOLD,
        LIGHT_GREY,
        RED
    }

    private final BorderTone borderTone;
    private final boolean scratched;

    private ScheduleBlockStyle(BorderTone borderTone, boolean scratched) {
        this.borderTone = borderTone;
        this.scratched = scratched;
    }

    static ScheduleBlockStyle from(TimelineSlot slot, ScheduleDecisionCandidate visible) {
        return new ScheduleBlockStyle(borderTone(visible.rating()), hasLongLostAlternativeOverlap(slot, visible));
    }

    BorderTone borderTone() {
        return borderTone;
    }

    boolean scratched() {
        return scratched;
    }

    private static BorderTone borderTone(int rating) {
        if (rating == 5) {
            return BorderTone.GOLD;
        }
        if (rating == 2) {
            return BorderTone.LIGHT_GREY;
        }
        return BorderTone.RED;
    }

    private static boolean hasLongLostAlternativeOverlap(TimelineSlot slot, ScheduleDecisionCandidate visible) {
        if (slot.lostAlternativeBandName().isEmpty()) {
            return false;
        }
        String lostAlternativeBandName = slot.lostAlternativeBandName().get();
        for (ScheduleDecisionCandidate candidate : slot.candidates()) {
            if (candidate.bandName().equals(visible.bandName())) {
                continue;
            }
            if (!candidate.bandName().equals(lostAlternativeBandName)) {
                continue;
            }
            if (overlapMinutes(visible.start(), visible.end(), candidate.start(), candidate.end())
                    > SCRATCH_OVERLAP_THRESHOLD_MINUTES) {
                return true;
            }
        }
        return false;
    }

    private static long overlapMinutes(
            LocalDateTime firstStart,
            LocalDateTime firstEnd,
            LocalDateTime secondStart,
            LocalDateTime secondEnd
    ) {
        LocalDateTime start = firstStart.isAfter(secondStart) ? firstStart : secondStart;
        LocalDateTime end = firstEnd.isBefore(secondEnd) ? firstEnd : secondEnd;
        if (!start.isBefore(end)) {
            return 0;
        }
        return Duration.between(start, end).toMinutes();
    }
}
