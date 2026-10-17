package be.wacken.planner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import be.wacken.planner.application.ScheduleDecisionCandidate;

final class ScheduleRatingFilter {
    private static final int NO_THRESHOLD = 0;

    private final int hideAtOrBelow;
    private final boolean hideBarred;

    private ScheduleRatingFilter(int hideAtOrBelow, boolean hideBarred) {
        this.hideAtOrBelow = Math.max(NO_THRESHOLD, Math.min(5, hideAtOrBelow));
        this.hideBarred = hideBarred;
    }

    static ScheduleRatingFilter none() {
        return new ScheduleRatingFilter(NO_THRESHOLD, false);
    }

    static ScheduleRatingFilter hideAtOrBelow(int rating) {
        return new ScheduleRatingFilter(rating, false);
    }

    static ScheduleRatingFilter hideBarred(int ratingThreshold) {
        return new ScheduleRatingFilter(ratingThreshold, true);
    }

    boolean active() {
        return hideAtOrBelow > NO_THRESHOLD || hideBarred;
    }

    boolean shows(ScheduleDecisionCandidate candidate) {
        return shows(candidate, List.of(candidate));
    }

    boolean shows(ScheduleDecisionCandidate candidate, List<ScheduleDecisionCandidate> visibleCandidates) {
        if (hideAtOrBelow > NO_THRESHOLD && candidate.rating() <= hideAtOrBelow) {
            return false;
        }
        if (hideBarred && ScheduleBlockStyle.from(candidate, visibleCandidates).scratched()) {
            return false;
        }
        return true;
    }

    List<ScheduleDecisionCandidate> visibleCandidates(List<ScheduleDecisionCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }
        List<ScheduleDecisionCandidate> visible = new ArrayList<>();
        for (ScheduleDecisionCandidate candidate : candidates) {
            if (shows(candidate, candidates)) {
                visible.add(candidate);
            }
        }
        return Collections.unmodifiableList(visible);
    }
}
