package be.wacken.planner.domain;

import java.util.Objects;

public record FestivalLineupEntry(String festivalId, Band band, String uploadedDisplayName) {
    public FestivalLineupEntry {
        festivalId = requireText(festivalId, "Festival id must not be blank.");
        Objects.requireNonNull(band, "band must not be null");
        uploadedDisplayName = requireText(uploadedDisplayName, "Uploaded display name must not be blank.");
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(message);
        }
        return value.trim();
    }
}
