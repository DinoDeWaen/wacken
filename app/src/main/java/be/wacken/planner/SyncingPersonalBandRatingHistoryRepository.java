package be.wacken.planner;

import java.io.IOException;
import java.util.List;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.PersonalBandRatingEvent;
import be.wacken.planner.domain.PersonalBandRatingHistoryRepository;

final class SyncingPersonalBandRatingHistoryRepository implements PersonalBandRatingHistoryRepository {
    private final PersonalBandRatingSyncLocalStore cache;
    private final SupabasePersonalBandRatingRemote remote;
    private final AuthSession session;

    SyncingPersonalBandRatingHistoryRepository(PersonalBandRatingSyncLocalStore cache, SupabasePersonalBandRatingRemote remote, AuthSession session) {
        this.cache = cache;
        this.remote = remote;
        this.session = session;
    }

    @Override
    public void save(PersonalBandRatingEvent event) {
        cache.save(event);
    }

    @Override
    public List<PersonalBandRatingEvent> findByUserAndBand(String userName, Band band) {
        return cache.findByUserAndBand(userName, band);
    }

    @Override
    public List<PersonalBandRatingEvent> findByUserAndFestival(String userName, String festivalId) {
        return cache.findByUserAndFestival(userName, festivalId);
    }

    void syncPendingEvents() throws IOException {
        for (PersonalBandRatingEvent event : cache.findPending(session.userId())) {
            remote.pushEvent(session, event);
            cache.saveSynced(event);
        }
    }

    void pullUserEvents() throws IOException {
        for (PersonalBandRatingEvent event : remote.pullUserEvents(session)) {
            cache.saveSynced(event);
        }
    }
}
