package be.wacken.planner.domain;

import java.util.Objects;

public record GroupDecision(GroupDecisionStatus status, String reason, int maxRating, int vetoCount) {
    public GroupDecision {
        Objects.requireNonNull(status, "status must not be null");
        if (reason == null || reason.isBlank()) {
            throw new DomainValidationException("Group decision reason must not be blank.");
        }
        if (maxRating < 0 || maxRating > 5) {
            throw new DomainValidationException("Group decision max rating must be between 0 and 5.");
        }
        if (vetoCount < 0) {
            throw new DomainValidationException("Group decision veto count must not be negative.");
        }
    }
}
