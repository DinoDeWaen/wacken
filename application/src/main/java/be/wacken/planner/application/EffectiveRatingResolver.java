package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.RatingRepository;

import java.util.Objects;

public final class EffectiveRatingResolver {
    private static final int DEFAULT_UNRATED_VALUE = 1;

    private final RatingRepository ratings;

    public EffectiveRatingResolver(RatingRepository ratings) {
        this.ratings = Objects.requireNonNull(ratings, "ratings must not be null");
    }

    public EffectiveRating resolve(String userName, Band band) {
        return ratings.findByUserAndBand(userName, band)
                .map(rating -> new EffectiveRating(rating.value(), true))
                .orElse(new EffectiveRating(DEFAULT_UNRATED_VALUE, false));
    }
}
