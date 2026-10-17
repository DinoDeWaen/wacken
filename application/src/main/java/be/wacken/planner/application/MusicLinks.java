package be.wacken.planner.application;

import java.util.Optional;

public record MusicLinks(Optional<String> youtubeUrl, Optional<String> spotifyUrl) {
    public MusicLinks {
        youtubeUrl = normalize(youtubeUrl);
        spotifyUrl = normalize(spotifyUrl);
    }

    public static MusicLinks none() {
        return new MusicLinks(Optional.empty(), Optional.empty());
    }

    private static Optional<String> normalize(Optional<String> value) {
        return value
                .map(String::trim)
                .filter(link -> !link.isBlank());
    }
}
