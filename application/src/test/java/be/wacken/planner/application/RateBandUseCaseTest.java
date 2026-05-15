package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RateBandUseCaseTest {
    @Test
    void storesValidRatingForBand() {
        FakeRatingRepository ratings = new FakeRatingRepository();
        RateBandUseCase useCase = new RateBandUseCase(ratings);
        Band band = new Band("5th Avenue");

        RateBandResult result = useCase.rateBand("dino", band, 4);

        assertEquals(RateBandResult.stored(), result);
        assertEquals(Optional.of(Rating.of(4)), ratings.findByUserAndBand("dino", band));
    }

    @Test
    void rejectsInvalidRatingWithValidationMessage() {
        FakeRatingRepository ratings = new FakeRatingRepository();
        RateBandUseCase useCase = new RateBandUseCase(ratings);
        Band band = new Band("5th Avenue");

        RateBandResult result = useCase.rateBand("dino", band, 5);

        assertEquals(RateBandResult.failure("Rating must be between 0 and 4."), result);
        assertEquals(Optional.empty(), ratings.findByUserAndBand("dino", band));
    }

    private static final class FakeRatingRepository implements RatingRepository {
        private final Map<Key, Rating> ratings = new HashMap<>();

        @Override
        public void save(String userName, Band band, Rating rating) {
            ratings.put(new Key(userName, band), rating);
        }

        @Override
        public Optional<Rating> findByUserAndBand(String userName, Band band) {
            return Optional.ofNullable(ratings.get(new Key(userName, band)));
        }

        private record Key(String userName, Band band) {
        }
    }
}
