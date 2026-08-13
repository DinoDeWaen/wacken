package be.wacken.planner.domain;

import java.util.Objects;

public record SavedFestivalPlanningRating(String groupId, String userName, String festivalId, Band band, Rating rating) {
    public SavedFestivalPlanningRating {
        groupId = requireText(groupId, "Rating group id must not be blank.");
        userName = requireText(userName, "Rating user name must not be blank.");
        festivalId = requireText(festivalId, "Rating festival id must not be blank.");
        Objects.requireNonNull(band, "band must not be null");
        Objects.requireNonNull(rating, "rating must not be null");
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(message);
        }
        return value.trim();
    }
}
