package be.wacken.planner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import be.wacken.planner.application.ScheduleDecisionCandidate;

final class ScheduleRatingFilter {
    private static final int NO_THRESHOLD = 0;

    private final int hideAtOrBelow;

    private ScheduleRatingFilter(int hideAtOrBelow) {
        this.hideAtOrBelow = Math.max(NO_THRESHOLD, Math.min(5, hideAtOrBelow));
    }

    static ScheduleRatingFilter none() {
        return new ScheduleRatingFilter(NO_THRESHOLD);
    }

    static ScheduleRatingFilter hideAtOrBelow(int rating) {
        return new ScheduleRatingFilter(rating);
    }

    boolean active() {
        return hideAtOrBelow > NO_THRESHOLD;
    }

    boolean shows(ScheduleDecisionCandidate candidate) {
        return !active() || candidate.rating() > hideAtOrBelow;
    }

    List<ScheduleDecisionCandidate> visibleCandidates(List<ScheduleDecisionCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }
        List<ScheduleDecisionCandidate> visible = new ArrayList<>();
        for (ScheduleDecisionCandidate candidate : candidates) {
            if (shows(candidate)) {
                visible.add(candidate);
            }
        }
        return Collections.unmodifiableList(visible);
    }
}
