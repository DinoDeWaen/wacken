package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.DomainValidationException;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RealRatingRepository;

import java.util.Objects;

public final class RateRealBandUseCase {
    private final RealRatingRepository ratings;

    public RateRealBandUseCase(RealRatingRepository ratings) {
        this.ratings = Objects.requireNonNull(ratings, "ratings must not be null");
    }

    public RateBandResult rateBand(String userName, Band band, int ratingValue) {
        try {
            ratings.save(userName, band, Rating.of(ratingValue));
            return RateBandResult.stored();
        } catch (DomainValidationException error) {
            return RateBandResult.failure(error.getMessage());
        }
    }
}
