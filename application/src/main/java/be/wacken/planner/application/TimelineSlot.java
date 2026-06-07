package be.wacken.planner.application;

import be.wacken.planner.domain.GroupDecisionStatus;

import java.time.LocalDateTime;
import java.util.Optional;

public record TimelineSlot(
        String bandName,
        String stageName,
        LocalDateTime start,
        LocalDateTime end,
        GroupDecisionStatus decisionStatus,
        Optional<String> lostAlternativeBandName
) {
    public TimelineSlot {
        if (bandName == null || bandName.isBlank()) {
            throw new IllegalArgumentException("Timeline slot band name must not be blank.");
        }
        if (stageName == null || stageName.isBlank()) {
            throw new IllegalArgumentException("Timeline slot stage name must not be blank.");
        }
        if (start == null) {
            throw new IllegalArgumentException("Timeline slot start must not be null.");
        }
        if (end == null) {
            throw new IllegalArgumentException("Timeline slot end must not be null.");
        }
        if (decisionStatus == null) {
            throw new IllegalArgumentException("Timeline slot decision status must not be null.");
        }
        lostAlternativeBandName = lostAlternativeBandName == null ? Optional.empty() : lostAlternativeBandName;
    }

    public boolean optional() {
        return decisionStatus == GroupDecisionStatus.OPTIONAL;
    }
}
