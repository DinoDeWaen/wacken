package be.wacken.planner.domain;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public final class GroupDecisionPolicy {
    private static final LocalTime LUNCH_START = LocalTime.NOON;
    private static final LocalTime LUNCH_END = LocalTime.of(14, 0);

    public GroupDecision decide(Performance performance, List<Rating> ratings) {
        Objects.requireNonNull(performance, "performance must not be null");
        Objects.requireNonNull(ratings, "ratings must not be null");

        int maxRating = ratings.stream()
                .filter(Objects::nonNull)
                .mapToInt(Rating::value)
                .max()
                .orElse(0);
        int vetoCount = (int) ratings.stream()
                .filter(Objects::nonNull)
                .filter(rating -> rating.value() == 1)
                .count();

        if (maxRating == 5) {
            return decision(GroupDecisionStatus.GO, "At least one group member marked this band as must-see.", maxRating, vetoCount);
        }
        if (maxRating == 4) {
            if (vetoCount >= 2) {
                return decision(GroupDecisionStatus.BLOCKED, "Two or more vetoes block a want-to-see band.", maxRating, vetoCount);
            }
            return decision(GroupDecisionStatus.GO, "Highest group rating is want-to-see and fewer than two vetoes exist.", maxRating, vetoCount);
        }
        if (maxRating == 3) {
            if (vetoCount > 0) {
                return decision(GroupDecisionStatus.BLOCKED, "Any veto blocks a liked-but-missable band.", maxRating, vetoCount);
            }
            if (isDuringLunchWindow(performance)) {
                return decision(GroupDecisionStatus.OPTIONAL, "Liked-but-missable performance occurs during the lunch window.", maxRating, vetoCount);
            }
            return decision(GroupDecisionStatus.GO, "Highest group rating is like and no veto exists.", maxRating, vetoCount);
        }
        if (maxRating == 2) {
            return decision(GroupDecisionStatus.OPTIONAL, "Highest group rating is OK or indifferent.", maxRating, vetoCount);
        }
        return decision(GroupDecisionStatus.UNRATED, "No group member has rated this band yet.", maxRating, vetoCount);
    }

    private boolean isDuringLunchWindow(Performance performance) {
        LocalTime start = performance.start().toLocalTime();
        LocalTime end = performance.end().toLocalTime();
        return start.isBefore(LUNCH_END) && end.isAfter(LUNCH_START);
    }

    private GroupDecision decision(GroupDecisionStatus status, String reason, int maxRating, int vetoCount) {
        return new GroupDecision(status, reason, maxRating, vetoCount);
    }
}
