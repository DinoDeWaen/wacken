package be.wacken.planner.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record PerformanceConflictResolution(
        Optional<Performance> selected,
        GroupDecisionStatus status,
        Optional<Performance> lostAlternative,
        List<Performance> rejected,
        String reason
) {
    public PerformanceConflictResolution {
        selected = selected == null ? Optional.empty() : selected;
        lostAlternative = lostAlternative == null ? Optional.empty() : lostAlternative;
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(rejected, "rejected must not be null");
        if (reason == null || reason.isBlank()) {
            throw new DomainValidationException("Performance conflict resolution reason must not be blank.");
        }
        rejected = List.copyOf(rejected);
    }
}
