package be.wacken.planner.domain;

import java.util.Optional;

public record Band(String name, Optional<String> biography, Optional<String> imageUrl, Optional<String> youtubeUrl, Optional<String> spotifyUrl) {
    public Band(String name) {
        this(name, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public Band(String name, Optional<String> youtubeUrl, Optional<String> spotifyUrl) {
        this(name, Optional.empty(), Optional.empty(), youtubeUrl, spotifyUrl);
    }

    public Band(String name, Optional<String> biography, Optional<String> youtubeUrl, Optional<String> spotifyUrl) {
        this(name, biography, Optional.empty(), youtubeUrl, spotifyUrl);
    }

    public Band {
        name = requireName(name, "Band name must not be blank.");
        biography = normalize(biography);
        imageUrl = normalize(imageUrl);
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
