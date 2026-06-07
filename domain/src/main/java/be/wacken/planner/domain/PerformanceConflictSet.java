package be.wacken.planner.domain;

import java.util.List;
import java.util.Objects;

public record PerformanceConflictSet(List<Performance> performances) {
    public PerformanceConflictSet {
        Objects.requireNonNull(performances, "performances must not be null");
        if (performances.isEmpty()) {
            throw new DomainValidationException("Performance conflict set must not be empty.");
        }
        performances = List.copyOf(performances);
    }

    public boolean hasConflict() {
        return performances.size() > 1;
    }
}
