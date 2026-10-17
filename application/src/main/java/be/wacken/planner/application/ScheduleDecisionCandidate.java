package be.wacken.planner.application;

import java.time.LocalDateTime;
import java.util.List;

public record ScheduleDecisionCandidate(
        String bandName,
        int rating,
        String stageName,
        LocalDateTime start,
        LocalDateTime end,
        String status,
        boolean selected,
        List<PersonRatingStars> personRatings
) {
    public ScheduleDecisionCandidate(
            String bandName,
            int rating,
            String stageName,
            LocalDateTime start,
            LocalDateTime end,
            String status,
            boolean selected
    ) {
        this(bandName, rating, stageName, start, end, status, selected, List.of());
    }

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
        personRatings = List.copyOf(personRatings == null ? List.of() : personRatings);
    }

    public boolean hasPersonRatings() {
        return !personRatings.isEmpty();
    }

    public String personRatingSummary() {
        return personRatings.stream()
                .map(PersonRatingStars::displayText)
                .reduce((left, right) -> left + "  " + right)
                .orElse("");
    }
}
