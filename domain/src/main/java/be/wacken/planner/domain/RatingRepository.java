package be.wacken.planner.domain;

import java.util.Optional;

public interface RatingRepository {
    void save(String userName, Band band, Rating rating);

    Optional<Rating> findByUserAndBand(String userName, Band band);
}
