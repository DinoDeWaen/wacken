package be.wacken.planner;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import be.wacken.planner.application.ScheduleDecisionCandidate;
import be.wacken.planner.domain.StageWalkingTimePolicy;

final class ScheduleBlockStyle {
    private static final int SCRATCH_OVERLAP_THRESHOLD_MINUTES = 15;
    private static final int NEARBY_STAGE_WALKING_ALLOWANCE_MINUTES = 5;

    private final int rating;
    private final boolean scratched;

    private ScheduleBlockStyle(int rating, boolean scratched) {
        this.rating = rating;
        this.scratched = scratched;
    }

    static ScheduleBlockStyle from(ScheduleDecisionCandidate visible, List<ScheduleDecisionCandidate> visibleCandidates) {
        return new ScheduleBlockStyle(
                visible.rating(),
                losesVisibleOverlap(visible, visibleCandidates)
        );
    }

    int borderColor(AppearancePreference.Palette palette) {
        if(rating==5)return palette.accent();
        if(rating==4)return palette.error();
        return palette.muted();
    }

    int fillColor(AppearancePreference.Palette palette) {
        return rating==5?palette.raised():palette.panel();
    }

    boolean scratched() {
        return scratched;
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
