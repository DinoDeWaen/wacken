package be.wacken.planner.application;

import java.time.LocalDateTime;

public record ScheduleDecisionCandidate(
        String bandName,
        int rating,
        String stageName,
        LocalDateTime start,
        LocalDateTime end,
        String status,
        boolean selected
) {
    public ScheduleDecisionCandidate {
        if (bandName == null || bandName.isBlank()) {
            throw new IllegalArgumentException("Schedule decision candidate band name must not be blank.");
        }
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Schedule decision candidate rating must be between 0 and 5.");
        }
        if (stageName == null || stageName.isBlank()) {
            throw new IllegalArgumentException("Schedule decision candidate stage name must not be blank.");
        }
        if (start == null) {
            throw new IllegalArgumentException("Schedule decision candidate start must not be null.");
        }
        if (end == null) {
            throw new IllegalArgumentException("Schedule decision candidate end must not be null.");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Schedule decision candidate status must not be blank.");
        }
    }
}
