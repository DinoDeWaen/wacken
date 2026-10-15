package be.wacken.planner;

import java.util.List;

import be.wacken.planner.domain.FestivalPlanningRatingRepository;
import be.wacken.planner.domain.SavedFestivalPlanningRating;
import be.wacken.planner.application.RecoverPendingPlanningRatingUseCase;

public interface FestivalPlanningRatingSyncLocalStore extends FestivalPlanningRatingRepository, RecoverPendingPlanningRatingUseCase.LocalStore {
    void saveSynced(SavedFestivalPlanningRating rating);

    List<SavedFestivalPlanningRating> findPending(String groupId, String userName);

    void movePendingRating(SavedFestivalPlanningRating expected, String targetFestivalId);
}
