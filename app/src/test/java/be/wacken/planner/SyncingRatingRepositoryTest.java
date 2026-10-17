package be.wacken.planner;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.SavedRating;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class SyncingRatingRepositoryTest {
    private static final AuthSession SESSION = new AuthSession(
            "access-token",
            "refresh-token",
            "user-1",
            "user@example.test",
            0,
            "group-1",
            "member"
    );

    @Test
    public void savesRatingLocallyAsPendingWithoutRemoteCall() {
        FakeLocalStore local = new FakeLocalStore();
        FakeRemote remote = new FakeRemote(local);
        SyncingRatingRepository repository = new SyncingRatingRepository(local, remote, SESSION);

        repository.save("user-1", new Band("5th Avenue"), Rating.of(4));

        assertEquals(Rating.of(4), repository.findByUserAndBand("user-1", new Band("5th Avenue")).orElseThrow());
        assertEquals(List.of(), remote.pushed);
        assertEquals(List.of(new SavedRating("user-1", new Band("5th Avenue"), Rating.of(4))), local.findPending("group-1", "user-1"));
    }

    @Test
    public void keepsRatingPendingWhenPushFails() {
        FakeLocalStore local = new FakeLocalStore();
        FakeRemote remote = new FakeRemote(local);
        SyncingRatingRepository repository = new SyncingRatingRepository(local, remote, SESSION);

        repository.save("user-1", new Band("5th Avenue"), Rating.of(3));

        assertEquals(Rating.of(3), repository.findByUserAndBand("user-1", new Band("5th Avenue")).orElseThrow());
        assertEquals(List.of(new SavedRating("user-1", new Band("5th Avenue"), Rating.of(3))), local.findPending("group-1", "user-1"));
    }

    @Test
    public void syncsPendingRatingsAndMarksThemSynced() throws Exception {
        FakeLocalStore local = new FakeLocalStore();
        FakeRemote remote = new FakeRemote(local);
        SyncingRatingRepository repository = new SyncingRatingRepository(local, remote, SESSION);
        local.savePending("group-1", "user-1", new Band("5th Avenue"), Rating.of(2));

        repository.syncPendingRatings();

        assertEquals(List.of(new SavedRating("user-1", new Band("5th Avenue"), Rating.of(2))), remote.pushed);
        assertTrue(local.findPending("group-1", "user-1").isEmpty());
    }

    @Test
    public void leavesPendingRatingWhenExplicitSyncFails() {
        FakeLocalStore local = new FakeLocalStore();
        FakeRemote remote = new FakeRemote(local);
        remote.failPush = true;
        SyncingRatingRepository repository = new SyncingRatingRepository(local, remote, SESSION);
        local.savePending("group-1", "user-1", new Band("5th Avenue"), Rating.of(2));

        try {
            repository.syncPendingRatings();
        } catch (IOException ignored) {
            // Expected: pending rating stays available for a later sync.
        }

        assertEquals(List.of(new SavedRating("user-1", new Band("5th Avenue"), Rating.of(2))), local.findPending("group-1", "user-1"));
    }

    @Test
    public void syncsPendingClearAndMarksItSynced() throws Exception {
        FakeLocalStore local = new FakeLocalStore();
        FakeRemote remote = new FakeRemote(local);
        SyncingRatingRepository repository = new SyncingRatingRepository(local, remote, SESSION);
        local.savePending("group-1", "user-1", new Band("5th Avenue"), Rating.of(0));

        repository.syncPendingRatings();

        assertEquals(List.of(new SavedRating("user-1", new Band("5th Avenue"), Rating.of(0))), remote.pushed);
        assertEquals(Rating.of(0), repository.findByUserAndBand("user-1", new Band("5th Avenue")).orElseThrow());
        assertTrue(local.findPending("group-1", "user-1").isEmpty());
    }

    @Test
    public void leavesPendingClearWhenSyncFails() {
        FakeLocalStore local = new FakeLocalStore();
        FakeRemote remote = new FakeRemote(local);
        remote.failPush = true;
        SyncingRatingRepository repository = new SyncingRatingRepository(local, remote, SESSION);
        local.savePending("group-1", "user-1", new Band("5th Avenue"), Rating.of(0));

        try {
            repository.syncPendingRatings();
        } catch (IOException ignored) {
            // Expected: pending clear stays available for a later sync.
        }

        assertEquals(List.of(new SavedRating("user-1", new Band("5th Avenue"), Rating.of(0))), local.findPending("group-1", "user-1"));
    }

    @Test
    public void pullsGroupRatingsIntoLocalStore() throws Exception {
        FakeLocalStore local = new FakeLocalStore();
        FakeRemote remote = new FakeRemote(local);
        remote.pulled.add(new SavedRating("user-2", new Band("5th Avenue"), Rating.of(1)));
        SyncingRatingRepository repository = new SyncingRatingRepository(local, remote, SESSION);

        repository.pullGroupRatings();

        assertEquals(Rating.of(1), repository.findByUserAndBand("user-2", new Band("5th Avenue")).orElseThrow());
    }

    @Test
    public void clearsSyncedGroupRatingsMissingFromRemotePull() throws Exception {
        FakeLocalStore local = new FakeLocalStore();
        FakeRemote remote = new FakeRemote(local);
        local.saveSynced("group-1", "user-2", new Band("5th Avenue"), Rating.of(5));
        remote.pulled.add(new SavedRating("user-3", new Band("Skyline"), Rating.of(4)));
        SyncingRatingRepository repository = new SyncingRatingRepository(local, remote, SESSION);

        repository.pullGroupRatings();

        assertEquals(Rating.of(0), repository.findByUserAndBand("user-2", new Band("5th Avenue")).orElseThrow());
        assertEquals(Rating.of(4), repository.findByUserAndBand("user-3", new Band("Skyline")).orElseThrow());
    }

    private static final class FakeRemote implements SupabaseRatingRemote {
        private final FakeLocalStore local;
        private final List<SavedRating> pushed = new ArrayList<>();
        private final List<SavedRating> pulled = new ArrayList<>();
        private boolean failPush;

        private FakeRemote(FakeLocalStore local) {
            this.local = local;
        }

        @Override
        public void pushRating(AuthSession session, SavedRating rating) throws IOException {
            assertTrue("rating must be local before remote push", local.findByUserAndBand(rating.userName(), rating.band()).isPresent());
            if (failPush) {
                throw new IOException("offline");
            }
            pushed.add(rating);
        }

        @Override
        public List<SavedRating> pullGroupRatings(AuthSession session) {
            return pulled;
        }
    }

    private static final class FakeLocalStore implements RatingSyncLocalStore {
        private final List<StoredRating> ratings = new ArrayList<>();

        @Override
        public void savePending(String groupId, String userName, Band band, Rating rating) {
            save(groupId, new SavedRating(userName, band, rating), "PENDING");
        }

        @Override
        public void saveSynced(String groupId, String userName, Band band, Rating rating) {
            save(groupId, new SavedRating(userName, band, rating), "SYNCED");
        }

        @Override
        public void saveSyncedGroupRating(String groupId, SavedRating rating) {
            save(groupId, rating, "SYNCED");
        }

        @Override
        public void replaceSyncedGroupRatings(String groupId, List<SavedRating> syncedRatings) {
            ratings.stream()
                    .filter(rating -> rating.groupId.equals(groupId))
                    .filter(rating -> rating.status.equals("SYNCED"))
                    .filter(rating -> !syncedRatings.contains(rating.savedRating))
                    .toList()
                    .forEach(rating -> save(groupId, new SavedRating(
                            rating.savedRating.userName(),
                            rating.savedRating.band(),
                            Rating.of(0)
                    ), "SYNCED"));
            for (SavedRating syncedRating : syncedRatings) {
                save(groupId, syncedRating, "SYNCED");
            }
        }

        @Override
        public List<SavedRating> findPending(String groupId, String userName) {
            return ratings.stream()
                    .filter(rating -> rating.groupId.equals(groupId))
                    .filter(rating -> rating.savedRating.userName().equals(userName))
                    .filter(rating -> rating.status.equals("PENDING"))
                    .map(rating -> rating.savedRating)
                    .toList();
        }

        @Override
        public Optional<Rating> findByUserAndBand(String userName, Band band) {
            return ratings.stream()
                    .filter(rating -> rating.savedRating.userName().equals(userName))
                    .filter(rating -> rating.savedRating.band().equals(band))
                    .map(rating -> rating.savedRating.rating())
                    .findFirst();
        }

        @Override
        public List<SavedRating> findAll() {
            return ratings.stream().map(rating -> rating.savedRating).toList();
        }

        private void save(String groupId, SavedRating savedRating, String status) {
            ratings.removeIf(rating -> rating.savedRating.userName().equals(savedRating.userName())
                    && rating.savedRating.band().equals(savedRating.band()));
            ratings.add(new StoredRating(groupId, savedRating, status));
        }
    }

    private record StoredRating(String groupId, SavedRating savedRating, String status) {
    }
}
