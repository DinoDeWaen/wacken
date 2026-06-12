package be.wacken.planner;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import be.wacken.planner.application.ScheduleDecisionCandidate;

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

    static ScheduleBlockStyle from(ScheduleDecisionCandidate visible, List<ScheduleDecisionCandidate> visibleCandidates) {
        return new ScheduleBlockStyle(borderTone(visible.rating()), losesVisibleOverlap(visible, visibleCandidates));
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

    private static boolean losesVisibleOverlap(
            ScheduleDecisionCandidate visible,
            List<ScheduleDecisionCandidate> visibleCandidates
    ) {
        for (ScheduleDecisionCandidate candidate : visibleCandidates) {
            if (candidate.bandName().equals(visible.bandName())) {
                continue;
            }
            if (candidate.rating() <= visible.rating()) {
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
