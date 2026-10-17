package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryRatingRepositoryTest {
    @Test
    void storesAndRetrievesRatingForUserAndBand() {
        RatingRepository repository = new InMemoryRatingRepository();
        Band band = new Band("5th Avenue");
        Rating rating = Rating.of(4);

        repository.save("dino", band, rating);

        assertEquals(Optional.of(rating), repository.findByUserAndBand("dino", band));
    }
}
