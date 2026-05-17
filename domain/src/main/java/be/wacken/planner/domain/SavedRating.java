package be.wacken.planner.domain;

import java.util.Objects;

public record SavedRating(String userName, Band band, Rating rating) {
    public SavedRating {
        if (userName == null || userName.isBlank()) {
            throw new DomainValidationException("Rating user name must not be blank.");
        }
        userName = userName.trim();
        Objects.requireNonNull(band, "band must not be null");
        Objects.requireNonNull(rating, "rating must not be null");
    }
}
