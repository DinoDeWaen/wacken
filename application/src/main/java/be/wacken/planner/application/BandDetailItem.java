package be.wacken.planner.application;

import java.util.List;
import java.util.Optional;

public record BandDetailItem(
        String bandName,
        Optional<String> biography,
        Optional<String> imageUrl,
        int rating,
        boolean defaultRating,
        Optional<String> youtubeUrl,
        Optional<String> spotifyUrl,
        List<PersonRatingStars> personRatings
) {
    public BandDetailItem {
        personRatings = List.copyOf(personRatings);
    }

    public boolean hasPersonRatings() {
        return !personRatings.isEmpty();
    }

    public String personRatingSummary() {
        return personRatings.stream()
                .map(PersonRatingStars::displayText)
                .reduce((left, right) -> left + "  " + right)
                .orElse("");
    }
}
