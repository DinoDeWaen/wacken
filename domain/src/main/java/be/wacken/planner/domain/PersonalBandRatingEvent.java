package be.wacken.planner.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public record PersonalBandRatingEvent(String id, String userName, Band band, Optional<String> festivalId,
                                      Rating rating, Instant createdAt, Optional<String> festivalNameSnapshot) {
    public PersonalBandRatingEvent(String id, String userName, Band band, Optional<String> festivalId,
                                   Rating rating, Instant createdAt) {
        this(id, userName, band, festivalId, rating, createdAt, Optional.empty());
    }

    public PersonalBandRatingEvent {
        id = requireText(id, "Personal rating id must not be blank.");
        userName = requireText(userName, "Personal rating user name must not be blank.");
        Objects.requireNonNull(band, "band must not be null");
        festivalId = normalize(festivalId);
        festivalNameSnapshot = normalize(festivalNameSnapshot);
        Objects.requireNonNull(rating, "rating must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(message);
        }
        return value.trim();
    }

    private static Optional<String> normalize(Optional<String> value) {
        return Objects.requireNonNull(value, "festivalId must not be null")
                .map(String::trim)
                .filter(text -> !text.isBlank());
    }
}
