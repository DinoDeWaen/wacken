package be.wacken.planner.domain;

import java.util.Objects;

/** Stable catalog identity together with the metadata reviewed by the member. */
public record CanonicalBand(String id, Band band) {
    public CanonicalBand {
        if (id == null || id.isBlank()) throw new DomainValidationException("Select a valid catalog band.");
        Objects.requireNonNull(band, "band");
    }
}
