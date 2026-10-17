package be.wacken.planner.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Current after-rating for one member's performance assessment at one festival. Zero records a reset. */
public record FestivalAfterRating(String operationId, String userName, String festivalId,
                                  Band band, Rating rating, Instant recordedAt, boolean requiresReview) {
    public FestivalAfterRating(String operationId, String userName, String festivalId,
                               Band band, Rating rating, Instant recordedAt) {
        this(operationId, userName, festivalId, band, rating, recordedAt, false);
    }
    public FestivalAfterRating {
        operationId = UUID.fromString(Objects.requireNonNull(operationId)).toString();
        userName = text(userName);
        festivalId = text(festivalId);
        Objects.requireNonNull(band, "band");
        Objects.requireNonNull(rating, "rating");
        Objects.requireNonNull(recordedAt, "recordedAt");
    }
    private static String text(String value) {
        if (value == null || value.isBlank()) throw new DomainValidationException("Rating user and festival are required.");
        return value.trim();
    }
}
