package be.wacken.planner;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.SavedRating;

final class SyncingRatingRepository implements RatingRepository {
    private final RatingSyncLocalStore cache;
    private final SupabaseRatingRemote remote;
    private final AuthSession session;

    SyncingRatingRepository(RatingSyncLocalStore cache, SupabaseRatingRemote remote, AuthSession session) {
        this.cache = cache;
        this.remote = remote;
        this.session = session;
    }

    @Override
    public void save(String userName, Band band, Rating rating) {
        cache.savePending(session.groupId(), userName, band, rating);
    }

    @Override
    public Optional<Rating> findByUserAndBand(String userName, Band band) {
        return cache.findByUserAndBand(userName, band);
    }

    @Override
    public List<SavedRating> findAll() {
        return cache.findAll();
    }

    void syncPendingRatings() throws IOException {
        for (SavedRating rating : cache.findPending(session.groupId(), session.userId())) {
            remote.pushRating(session, rating);
            cache.saveSynced(session.groupId(), rating.userName(), rating.band(), rating.rating());
        }
    }

    void pullGroupRatings() throws IOException {
        cache.replaceSyncedGroupRatings(session.groupId(), remote.pullGroupRatings(session));
    }
}
