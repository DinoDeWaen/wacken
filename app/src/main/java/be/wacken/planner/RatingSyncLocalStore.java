package be.wacken.planner;

import java.util.List;
import java.util.Optional;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.SavedRating;

public interface RatingSyncLocalStore {
    void savePending(String groupId, String userName, Band band, Rating rating);

    void saveSynced(String groupId, String userName, Band band, Rating rating);

    void saveSyncedGroupRating(String groupId, SavedRating rating);

    List<SavedRating> findPending(String groupId, String userName);

    Optional<Rating> findByUserAndBand(String userName, Band band);

    List<SavedRating> findAll();
}
