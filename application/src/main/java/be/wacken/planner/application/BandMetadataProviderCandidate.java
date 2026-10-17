package be.wacken.planner.application;

import java.util.Optional;

public record BandMetadataProviderCandidate(
        String candidateName,
        Optional<String> biography,
        Optional<String> imageUrl,
        Optional<String> youtubeUrl,
        Optional<String> spotifyUrl,
        Optional<String> sourceUrl,
        int confidence
) {
    public BandMetadataProviderCandidate {
        candidateName = normalizeText(candidateName);
        biography = normalize(biography);
        imageUrl = normalize(imageUrl);
        youtubeUrl = normalize(youtubeUrl);
        spotifyUrl = normalize(spotifyUrl);
        sourceUrl = normalize(sourceUrl);
        confidence = Math.max(0, Math.min(100, confidence));
    }

    private static String normalizeText(String value) {
        return value == null || value.isBlank() ? "Unknown candidate" : value.trim();
    }

    private static Optional<String> normalize(Optional<String> value) {
        return value == null ? Optional.empty() : value.map(String::trim).filter(text -> !text.isBlank());
    }
}
