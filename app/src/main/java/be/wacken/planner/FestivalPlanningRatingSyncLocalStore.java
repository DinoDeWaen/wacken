package be.wacken.planner;

import java.util.List;

import be.wacken.planner.domain.FestivalPlanningRatingRepository;
import be.wacken.planner.domain.SavedFestivalPlanningRating;

public interface FestivalPlanningRatingSyncLocalStore extends FestivalPlanningRatingRepository {
    void saveSynced(SavedFestivalPlanningRating rating);

    List<SavedFestivalPlanningRating> findPending(String groupId, String userName);
}
