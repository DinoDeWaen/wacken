package be.wacken.planner.domain;

import java.time.Instant;
import java.util.Objects;

public record ConfirmedBandAlias(String name, CanonicalBand canonical, String confirmedBy, Instant confirmedAt) {
    public ConfirmedBandAlias {
        name = new Band(name).name();
        Objects.requireNonNull(canonical, "canonical");
        if (confirmedBy == null || confirmedBy.isBlank()) throw new DomainValidationException("Alias confirmation requires a member.");
        Objects.requireNonNull(confirmedAt, "confirmedAt");
    }

    /** Fold ASCII case and spacing only; retain punctuation and non-ASCII spelling. */
    public static String key(String name) {
        String spaced = name.replaceAll("[ \\t\\r\\n\\f]+", " ").trim();
        StringBuilder result = new StringBuilder();
        for (char value : spaced.toCharArray()) result.append(value >= 'A' && value <= 'Z' ? (char)(value + 32) : value);
        return result.toString();
    }
}
