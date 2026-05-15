package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class InMemoryRatingRepository implements RatingRepository {
    private final Map<RatingKey, Rating> ratingsByUserAndBand = new HashMap<>();

    @Override
    public void save(String userName, Band band, Rating rating) {
        ratingsByUserAndBand.put(new RatingKey(userName, band.name()), rating);
    }

    @Override
    public Optional<Rating> findByUserAndBand(String userName, Band band) {
        return Optional.ofNullable(ratingsByUserAndBand.get(new RatingKey(userName, band.name())));
    }

    private record RatingKey(String userName, String bandName) {
    }
}
