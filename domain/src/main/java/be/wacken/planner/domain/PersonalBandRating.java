package be.wacken.planner.domain;

import java.time.Instant;
import java.util.Objects;

/** Established personal opinion with retained source identity, even after festival deletion. */
public record PersonalBandRating(String userName, Band band, Rating rating, String sourceFestivalId,
                                 String sourceFestivalName, String sourceEventId, Instant establishedAt) {
    public PersonalBandRating {
        if (userName == null || userName.isBlank() || sourceFestivalId == null || sourceFestivalId.isBlank()
                || sourceEventId == null || sourceEventId.isBlank())
            throw new DomainValidationException("An established band rating requires its user and source.");
        Objects.requireNonNull(band, "band");
        Objects.requireNonNull(rating, "rating");
        Objects.requireNonNull(establishedAt, "establishedAt");
        if (rating.value() == 0) throw new DomainValidationException("An empty after-rating cannot establish a band rating.");
        sourceFestivalName = sourceFestivalName == null ? "" : sourceFestivalName;
    }
}

