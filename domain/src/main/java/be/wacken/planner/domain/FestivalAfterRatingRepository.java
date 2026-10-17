package be.wacken.planner.domain;

import java.util.List;
import java.util.Optional;

public interface FestivalAfterRatingRepository {
    /** Saves the after-rating and its history atomically; never establishes a personal band rating. */
    void save(FestivalAfterRating rating);
    Optional<FestivalAfterRating> findByUserFestivalAndBand(String userName, String festivalId, Band band);
    List<FestivalAfterRating> findByUser(String userName);
    List<FestivalAfterRating> findByFestival(String festivalId);
}

