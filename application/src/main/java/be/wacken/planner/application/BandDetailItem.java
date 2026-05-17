package be.wacken.planner.application;

import java.util.Optional;

public record BandDetailItem(
        String bandName,
        Optional<String> biography,
        Optional<String> imageUrl,
        int rating,
        boolean defaultRating,
        Optional<String> youtubeUrl,
        Optional<String> spotifyUrl
) {
}
