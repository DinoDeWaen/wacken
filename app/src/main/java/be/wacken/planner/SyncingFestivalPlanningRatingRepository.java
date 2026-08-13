package be.wacken.planner;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.FestivalPlanningRatingRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.SavedFestivalPlanningRating;

final class SyncingFestivalPlanningRatingRepository implements FestivalPlanningRatingRepository {
    private final FestivalPlanningRatingSyncLocalStore cache;
    private final SupabaseFestivalPlanningRatingRemote remote;
    private final AuthSession session;

    SyncingFestivalPlanningRatingRepository(FestivalPlanningRatingSyncLocalStore cache, SupabaseFestivalPlanningRatingRemote remote, AuthSession session) {
        this.cache = cache;
        this.remote = remote;
        this.session = session;
    }

    @Override
    public void save(String groupId, String userName, String festivalId, Band band, Rating rating) {
        cache.save(groupId, userName, festivalId, band, rating);
    }

    @Override
    public Optional<Rating> findByUserFestivalAndBand(String userName, String festivalId, Band band) {
        return cache.findByUserFestivalAndBand(userName, festivalId, band);
    }

    @Override
    public List<SavedFestivalPlanningRating> findByFestival(String festivalId) {
        return cache.findByFestival(festivalId);
    }

    @Override
    public List<SavedFestivalPlanningRating> findAll() {
        return cache.findAll();
    }

    void syncPendingRatings() throws IOException {
        for (SavedFestivalPlanningRating rating : cache.findPending(session.groupId(), session.userId())) {
            remote.pushRating(session, rating);
            cache.saveSynced(rating);
        }
    }

    void pullGroupRatings() throws IOException {
        for (SavedFestivalPlanningRating rating : remote.pullGroupRatings(session)) {
            cache.saveSynced(rating);
        }
    }
}
