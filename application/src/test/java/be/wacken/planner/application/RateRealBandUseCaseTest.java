package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.RealRatingRepository;
import be.wacken.planner.domain.SavedRating;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RateRealBandUseCaseTest {
    @Test
    void storesRealPostShowRatingSeparatelyFromPlanningRating() {
        FakeRatingRepository planningRatings = new FakeRatingRepository();
        FakeRealRatingRepository realRatings = new FakeRealRatingRepository();
        RateRealBandUseCase useCase = new RateRealBandUseCase(realRatings);
        Band band = new Band("5th Avenue");
        planningRatings.save("dino", band, Rating.of(5));

        RateBandResult result = useCase.rateBand("dino", band, 3);

        assertEquals(RateBandResult.stored(), result);
        assertEquals(Optional.of(Rating.of(5)), planningRatings.findByUserAndBand("dino", band));
        assertEquals(Optional.of(Rating.of(3)), realRatings.findByUserAndBand("dino", band));
    }

    @Test
    void storesUnratedZeroWhenResettingRealRating() {
        FakeRealRatingRepository realRatings = new FakeRealRatingRepository();
        RateRealBandUseCase useCase = new RateRealBandUseCase(realRatings);
        Band band = new Band("5th Avenue");
        useCase.rateBand("dino", band, 4);

        RateBandResult result = useCase.rateBand("dino", band, 0);

        assertEquals(RateBandResult.stored(), result);
        assertEquals(Optional.of(Rating.of(0)), realRatings.findByUserAndBand("dino", band));
    }

    @Test
    void rejectsRealRatingOutsideScale() {
        FakeRealRatingRepository realRatings = new FakeRealRatingRepository();
        RateRealBandUseCase useCase = new RateRealBandUseCase(realRatings);
        Band band = new Band("5th Avenue");

        RateBandResult result = useCase.rateBand("dino", band, 6);

        assertEquals(RateBandResult.failure("Rating must be between 0 and 5."), result);
        assertEquals(Optional.empty(), realRatings.findByUserAndBand("dino", band));
    }

    @Test
    void exposesRealRatingsForExportStories() {
        FakeRealRatingRepository realRatings = new FakeRealRatingRepository();
        Band band = new Band("5th Avenue");
        realRatings.save("dino", band, Rating.of(4));

        assertEquals(List.of(new SavedRating("dino", band, Rating.of(4))), realRatings.findAll());
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
    }

    private static final class FakeRealRatingRepository implements RealRatingRepository {
        private final Map<Key, Rating> ratings = new HashMap<>();

        @Override
        public void save(String userName, Band band, Rating rating) {
            ratings.put(new Key(userName, band), rating);
        }

        @Override
        public Optional<Rating> findByUserAndBand(String userName, Band band) {
            return Optional.ofNullable(ratings.get(new Key(userName, band)));
        }

        @Override
        public List<SavedRating> findAll() {
            return ratings.entrySet()
                    .stream()
                    .map(entry -> new SavedRating(entry.getKey().userName(), entry.getKey().band(), entry.getValue()))
                    .toList();
        }
    }

    private record Key(String userName, Band band) {
    }
}
