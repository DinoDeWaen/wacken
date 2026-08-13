package be.wacken.planner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.SavedFestivalPlanningRating;

public final class SyncingFestivalPlanningRatingRepositoryTest {
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
    public void syncsPendingPlanningRatingsAndMarksThemSynced() throws Exception {
        FakeLocalStore local = new FakeLocalStore();
        FakeRemote remote = new FakeRemote();
        SyncingFestivalPlanningRatingRepository repository = new SyncingFestivalPlanningRatingRepository(local, remote, SESSION);
        local.save("group-1", "user-1", "wacken-2026", new Band("Airbourne"), Rating.of(5));

        repository.syncPendingRatings();

        assertEquals(List.of(new SavedFestivalPlanningRating("group-1", "user-1", "wacken-2026", new Band("Airbourne"), Rating.of(5))), remote.pushed);
        assertTrue(local.findPending("group-1", "user-1").isEmpty());
    }

    @Test
    public void keepsPendingPlanningRatingsWhenPushFails() {
        FakeLocalStore local = new FakeLocalStore();
        FakeRemote remote = new FakeRemote();
        remote.failPush = true;
        SyncingFestivalPlanningRatingRepository repository = new SyncingFestivalPlanningRatingRepository(local, remote, SESSION);
        local.save("group-1", "user-1", "wacken-2026", new Band("Airbourne"), Rating.of(4));

        try {
            repository.syncPendingRatings();
        } catch (IOException ignored) {
            // Expected: pending rating stays available for a later sync.
        }

        assertEquals(List.of(new SavedFestivalPlanningRating("group-1", "user-1", "wacken-2026", new Band("Airbourne"), Rating.of(4))), local.findPending("group-1", "user-1"));
    }

    @Test
    public void mapsSupabasePlanningRatingRowsToDomainRatings() throws Exception {
        List<SavedFestivalPlanningRating> ratings = SupabaseFestivalPlanningRatingClient.parseRatings("""
                [
                  {
                    "group_id": "group-1",
                    "user_id": "user-1",
                    "festival_id": "wacken-2026",
                    "band_id": "band-airbourne",
                    "rating": 5
                  },
                  {
                    "group_id": "group-1",
                    "user_id": "user-1",
                    "festival_id": "wacken-2026",
                    "band_id": "unknown-band",
                    "rating": 3
                  }
                ]
                """, Map.of("band-airbourne", "Airbourne"));

        assertEquals(List.of(new SavedFestivalPlanningRating("group-1", "user-1", "wacken-2026", new Band("Airbourne"), Rating.of(5))), ratings);
    }

    private static final class FakeRemote implements SupabaseFestivalPlanningRatingRemote {
        private final List<SavedFestivalPlanningRating> pushed = new ArrayList<>();
        private final List<SavedFestivalPlanningRating> pulled = new ArrayList<>();
        private boolean failPush;

        @Override
        public void pushRating(AuthSession session, SavedFestivalPlanningRating rating) throws IOException {
            if (failPush) {
                throw new IOException("offline");
            }
            pushed.add(rating);
        }

        @Override
        public List<SavedFestivalPlanningRating> pullGroupRatings(AuthSession session) {
            return pulled;
        }
    }

    private static final class FakeLocalStore implements FestivalPlanningRatingSyncLocalStore {
        private final List<StoredRating> ratings = new ArrayList<>();

        @Override
        public void save(String groupId, String userName, String festivalId, Band band, Rating rating) {
            ratings.removeIf(existing -> existing.rating.groupId().equals(groupId)
                    && existing.rating.userName().equals(userName)
                    && existing.rating.festivalId().equals(festivalId)
                    && existing.rating.band().equals(band));
            ratings.add(new StoredRating(new SavedFestivalPlanningRating(groupId, userName, festivalId, band, rating), "PENDING"));
        }

        @Override
        public void saveSynced(SavedFestivalPlanningRating rating) {
            ratings.removeIf(existing -> existing.rating.groupId().equals(rating.groupId())
                    && existing.rating.userName().equals(rating.userName())
                    && existing.rating.festivalId().equals(rating.festivalId())
                    && existing.rating.band().equals(rating.band()));
            ratings.add(new StoredRating(rating, "SYNCED"));
        }

        @Override
        public Optional<Rating> findByUserFestivalAndBand(String userName, String festivalId, Band band) {
            return ratings.stream()
                    .filter(existing -> existing.rating.userName().equals(userName))
                    .filter(existing -> existing.rating.festivalId().equals(festivalId))
                    .filter(existing -> existing.rating.band().equals(band))
                    .map(existing -> existing.rating.rating())
                    .findFirst();
        }

        @Override
        public List<SavedFestivalPlanningRating> findByFestival(String festivalId) {
            return ratings.stream()
                    .map(existing -> existing.rating)
                    .filter(rating -> rating.festivalId().equals(festivalId))
                    .toList();
        }

        @Override
        public List<SavedFestivalPlanningRating> findAll() {
            return ratings.stream().map(existing -> existing.rating).toList();
        }

        @Override
        public List<SavedFestivalPlanningRating> findPending(String groupId, String userName) {
            return ratings.stream()
                    .filter(existing -> existing.rating.groupId().equals(groupId))
                    .filter(existing -> existing.rating.userName().equals(userName))
                    .filter(existing -> existing.status.equals("PENDING"))
                    .map(existing -> existing.rating)
                    .toList();
        }
    }

    private record StoredRating(SavedFestivalPlanningRating rating, String status) {
    }
}
