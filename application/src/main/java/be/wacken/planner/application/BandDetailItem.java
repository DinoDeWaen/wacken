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
        int realRating,
        boolean defaultRealRating,
        List<PersonRatingStars> personRatings
) {
    public BandDetailItem(
            String bandName,
            Optional<String> biography,
            Optional<String> imageUrl,
            int rating,
            boolean defaultRating,
            Optional<String> youtubeUrl,
            Optional<String> spotifyUrl,
            List<PersonRatingStars> personRatings
    ) {
        this(bandName, biography, imageUrl, rating, defaultRating, youtubeUrl, spotifyUrl, 0, true, personRatings);
    }

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
