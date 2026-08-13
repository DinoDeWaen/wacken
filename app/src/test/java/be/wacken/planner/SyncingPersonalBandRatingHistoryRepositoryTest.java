package be.wacken.planner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.PersonalBandRatingEvent;
import be.wacken.planner.domain.Rating;

public final class SyncingPersonalBandRatingHistoryRepositoryTest {
    private static final AuthSession SESSION = new AuthSession(
            "access-token",
            "refresh-token",
            "user-1",
            "user@example.test",
            0,
            "group-1",
            "member"
    );
    private static final PersonalBandRatingEvent EVENT = new PersonalBandRatingEvent(
            "event-1",
            "user-1",
            new Band("Airbourne"),
            Optional.of("wacken-2026"),
            Rating.of(4),
            Instant.parse("2026-08-03T21:15:00Z")
    );

    @Test
    public void syncsPendingPersonalEventsAndMarksThemSynced() throws Exception {
        FakeLocalStore local = new FakeLocalStore();
        FakeRemote remote = new FakeRemote();
        SyncingPersonalBandRatingHistoryRepository repository = new SyncingPersonalBandRatingHistoryRepository(local, remote, SESSION);
        local.save(EVENT);

        repository.syncPendingEvents();

        assertEquals(List.of(EVENT), remote.pushed);
        assertTrue(local.findPending("user-1").isEmpty());
    }

    @Test
    public void keepsPendingPersonalEventsWhenPushFails() {
        FakeLocalStore local = new FakeLocalStore();
        FakeRemote remote = new FakeRemote();
        remote.failPush = true;
        SyncingPersonalBandRatingHistoryRepository repository = new SyncingPersonalBandRatingHistoryRepository(local, remote, SESSION);
        local.save(EVENT);

        try {
            repository.syncPendingEvents();
        } catch (IOException ignored) {
            // Expected: pending event stays available for a later sync.
        }

        assertEquals(List.of(EVENT), local.findPending("user-1"));
    }

    @Test
    public void mapsSupabasePersonalRatingRowsToDomainEvents() throws Exception {
        List<PersonalBandRatingEvent> events = SupabasePersonalBandRatingClient.parseEvents("""
                [
                  {
                    "id": "event-1",
                    "user_id": "user-1",
                    "band_id": "band-airbourne",
                    "festival_id": "wacken-2026",
                    "rating": 4,
                    "created_at": "2026-08-03T21:15:00Z"
                  },
                  {
                    "id": "event-2",
                    "user_id": "user-1",
                    "band_id": "unknown-band",
                    "festival_id": "wacken-2026",
                    "rating": 5,
                    "created_at": "2026-08-04T21:15:00Z"
                  }
                ]
                """, Map.of("band-airbourne", "Airbourne"));

        assertEquals(List.of(EVENT), events);
    }

    private static final class FakeRemote implements SupabasePersonalBandRatingRemote {
        private final List<PersonalBandRatingEvent> pushed = new ArrayList<>();
        private final List<PersonalBandRatingEvent> pulled = new ArrayList<>();
        private boolean failPush;

        @Override
        public void pushEvent(AuthSession session, PersonalBandRatingEvent event) throws IOException {
            if (failPush) {
                throw new IOException("offline");
            }
            pushed.add(event);
        }

        @Override
        public List<PersonalBandRatingEvent> pullUserEvents(AuthSession session) {
            return pulled;
        }
    }

    private static final class FakeLocalStore implements PersonalBandRatingSyncLocalStore {
        private final List<StoredEvent> events = new ArrayList<>();

        @Override
        public void save(PersonalBandRatingEvent event) {
            events.removeIf(existing -> existing.event.id().equals(event.id()));
            events.add(new StoredEvent(event, "PENDING"));
        }

        @Override
        public void saveSynced(PersonalBandRatingEvent event) {
            events.removeIf(existing -> existing.event.id().equals(event.id()));
            events.add(new StoredEvent(event, "SYNCED"));
        }

        @Override
        public List<PersonalBandRatingEvent> findPending(String userName) {
            return events.stream()
                    .filter(existing -> existing.event.userName().equals(userName))
                    .filter(existing -> existing.status.equals("PENDING"))
                    .map(existing -> existing.event)
                    .toList();
        }

        @Override
        public List<PersonalBandRatingEvent> findByUserAndBand(String userName, Band band) {
            return events.stream()
                    .filter(existing -> existing.event.userName().equals(userName))
                    .filter(existing -> existing.event.band().equals(band))
                    .map(existing -> existing.event)
                    .toList();
        }
    }

    private record StoredEvent(PersonalBandRatingEvent event, String status) {
    }
}
