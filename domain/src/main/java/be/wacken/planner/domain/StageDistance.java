package be.wacken.planner.domain;

import java.util.Objects;

public record StageDistance(Stage from, Stage to, int walkingMinutes) {
    public StageDistance {
        Objects.requireNonNull(from, "from must not be null");
        Objects.requireNonNull(to, "to must not be null");
        if (walkingMinutes < 0) {
            throw new DomainValidationException("Stage distance walking minutes must not be negative.");
        }
        if (from.equals(to)) {
            walkingMinutes = 0;
        }
    }

    public static StageDistance between(Stage from, Stage to, int walkingMinutes) {
        return new StageDistance(from, to, walkingMinutes);
    }
}
