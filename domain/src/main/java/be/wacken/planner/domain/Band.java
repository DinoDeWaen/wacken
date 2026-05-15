package be.wacken.planner.domain;

import java.util.Optional;

public record Band(String name, Optional<String> youtubeUrl, Optional<String> spotifyUrl) {
    public Band(String name) {
        this(name, Optional.empty(), Optional.empty());
    }

    public Band {
        name = requireName(name, "Band name must not be blank.");
        youtubeUrl = normalize(youtubeUrl);
        spotifyUrl = normalize(spotifyUrl);
    }

    private static String requireName(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(message);
        }
        return value.trim();
    }

    private static Optional<String> normalize(Optional<String> value) {
        return value
                .map(String::trim)
                .filter(link -> !link.isBlank());
    }
}
