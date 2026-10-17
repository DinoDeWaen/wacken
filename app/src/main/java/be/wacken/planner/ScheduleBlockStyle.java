package be.wacken.planner;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import be.wacken.planner.application.ScheduleDecisionCandidate;
import be.wacken.planner.domain.StageWalkingTimePolicy;

final class ScheduleBlockStyle {
    private static final int GOLD_BORDER = 0xFFFFD24A;
    private static final int GOLD_FILL = 0xFF2F2A18;
    private static final int METAL_RED_BORDER = 0xFFFF3B6B;
    private static final int METAL_RED_FILL = 0xFF263033;
    private static final int STEEL_BORDER = 0xFFAAB3B7;
    private static final int STEEL_FILL = 0xFF20282A;
    private static final int SCRATCH_OVERLAP_THRESHOLD_MINUTES = 15;
    private static final int NEARBY_STAGE_WALKING_ALLOWANCE_MINUTES = 5;

    private final int borderColor;
    private final int fillColor;
    private final boolean scratched;

    private ScheduleBlockStyle(int borderColor, int fillColor, boolean scratched) {
        this.borderColor = borderColor;
        this.fillColor = fillColor;
        this.scratched = scratched;
    }

    static ScheduleBlockStyle from(ScheduleDecisionCandidate visible, List<ScheduleDecisionCandidate> visibleCandidates) {
        return new ScheduleBlockStyle(
                borderColor(visible.rating()),
                fillColor(visible.rating()),
                losesVisibleOverlap(visible, visibleCandidates)
        );
    }

    int borderColor() {
        return borderColor;
    }

    int fillColor() {
        return fillColor;
    }

    boolean scratched() {
        return scratched;
    }

    private static int borderColor(int rating) {
        if (rating == 5) {
            return GOLD_BORDER;
        }
        if (rating == 4) {
            return METAL_RED_BORDER;
        }
        return STEEL_BORDER;
    }

    private static int fillColor(int rating) {
        if (rating == 5) {
            return GOLD_FILL;
        }
        if (rating == 4) {
            return METAL_RED_FILL;
        }
        return STEEL_FILL;
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
            if (effectiveConflictMinutes(visible, candidate) > SCRATCH_OVERLAP_THRESHOLD_MINUTES) {
                return true;
            }
        }
        return false;
    }

    private static long effectiveConflictMinutes(
            ScheduleDecisionCandidate visible,
            ScheduleDecisionCandidate candidate
    ) {
        long overlap = overlapMinutes(visible.start(), visible.end(), candidate.start(), candidate.end());
        int walkingMinutes = StageWalkingTimePolicy.defaultWalkingMinutes(visible.stageName(), candidate.stageName());
        int walkingPenalty = Math.max(0, walkingMinutes - NEARBY_STAGE_WALKING_ALLOWANCE_MINUTES);
        return overlap + walkingPenalty;
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
