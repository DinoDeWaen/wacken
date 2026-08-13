package be.wacken.planner.domain;

import java.util.List;
import java.util.Optional;

public interface FestivalPlanningRatingRepository {
    void save(String groupId, String userName, String festivalId, Band band, Rating rating);

    Optional<Rating> findByUserFestivalAndBand(String userName, String festivalId, Band band);

    List<SavedFestivalPlanningRating> findByFestival(String festivalId);

    List<SavedFestivalPlanningRating> findAll();
}
