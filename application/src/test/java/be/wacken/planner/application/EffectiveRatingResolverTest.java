package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EffectiveRatingResolverTest {
    @Test
    void treatsMissingMemberRatingAsOneStarDefault() {
        EffectiveRatingResolver resolver = new EffectiveRatingResolver(new FakeRatingRepository());

        EffectiveRating rating = resolver.resolve("dino", new Band("5th Avenue"));

        assertEquals(new EffectiveRating(1, false), rating);
    }

    @Test
    void returnsExplicitMemberRatingWhenSaved() {
        FakeRatingRepository ratings = new FakeRatingRepository();
        Band band = new Band("5th Avenue");
        ratings.save("dino", band, Rating.of(4));
        EffectiveRatingResolver resolver = new EffectiveRatingResolver(ratings);

        EffectiveRating rating = resolver.resolve("dino", band);

        assertEquals(new EffectiveRating(4, true), rating);
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
