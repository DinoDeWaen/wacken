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
        List<PersonRatingStars> personRatings,
        List<PersonalRatingHistoryItem> personalRatingHistory
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
        this(bandName, biography, imageUrl, rating, defaultRating, youtubeUrl, spotifyUrl, 0, true, personRatings, List.of());
    }

    public BandDetailItem {
        personRatings = List.copyOf(personRatings);
        personalRatingHistory = List.copyOf(personalRatingHistory);
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

    public boolean hasPersonalRatingHistory() {
        return !personalRatingHistory.isEmpty();
    }
}
