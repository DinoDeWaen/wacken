package be.wacken.planner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import be.wacken.planner.application.RecoverPendingPlanningRatingUseCase;
import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.SavedFestivalPlanningRating;

public final class PendingPlanningRatingRecoveryTest {
    private static final AuthSession SESSION = new AuthSession("token", "refresh", "user-1", "user@test.invalid", 0, "group-1", "member");
    private static final SavedFestivalPlanningRating ORPHAN = new SavedFestivalPlanningRating(
            "group-1", "user-1", "missing", new Band("Airbourne"), Rating.of(4));

    @Test
    public void confirmedRecoveryMovesOnePendingRatingToWacken() throws Exception {
        Local local = new Local();
        local.pending.add(ORPHAN);
        Remote remote = new Remote();
        recovery(local, remote, true).moveToWacken2026(ORPHAN, SESSION.groupId(), SESSION.userId());

        SavedFestivalPlanningRating repaired = new SavedFestivalPlanningRating("group-1", "user-1", "wacken-2026",
                new Band("Airbourne"), Rating.of(4));
        assertEquals(List.of(repaired), local.pending);

        new SyncingFestivalPlanningRatingRepository(local, remote, SESSION, festivals(true)).syncPendingRatings();
        assertEquals(List.of(repaired), remote.pushed);
        assertTrue(local.pending.isEmpty());
    }

    @Test
    public void remoteConflictPreservesOrphan() throws Exception {
        Local local = new Local();
        local.pending.add(ORPHAN);
        Remote remote = new Remote();
        remote.pulled.add(new SavedFestivalPlanningRating("group-1", "user-1", "wacken-2026",
                new Band("Airbourne"), Rating.of(2)));

        expectFailure(() -> recovery(local, remote, true).moveToWacken2026(ORPHAN, SESSION.groupId(), SESSION.userId()));
        assertEquals(List.of(ORPHAN), local.pending);
    }

    @Test
    public void localConflictOrArchivedTargetPreservesOrphan() throws Exception {
        Local local = new Local();
        local.pending.add(ORPHAN);
        local.existingWacken = true;
        expectFailure(() -> recovery(local, new Remote(), true).moveToWacken2026(ORPHAN, SESSION.groupId(), SESSION.userId()));
        local.existingWacken = false;
        expectFailure(() -> recovery(local, new Remote(), false).moveToWacken2026(ORPHAN, SESSION.groupId(), SESSION.userId()));
        assertEquals(List.of(ORPHAN), local.pending);
    }

    @Test
    public void networkFailurePreservesOrphan() throws Exception {
        Local local = new Local();
        local.pending.add(ORPHAN);
        Remote remote = new Remote();
        remote.fail = true;
        expectFailure(() -> recovery(local, remote, true).moveToWacken2026(ORPHAN, SESSION.groupId(), SESSION.userId()));
        assertEquals(List.of(ORPHAN), local.pending);
    }

    @Test
    public void discardRemovesOnlyTheConfirmedPendingRating() throws Exception {
        Local local = new Local();
        SavedFestivalPlanningRating other = new SavedFestivalPlanningRating(
                "group-1", "user-1", "wacken-2026", new Band("Alestorm"), Rating.of(3));
        local.pending.add(ORPHAN);
        local.pending.add(other);

        recovery(local, new Remote(), true).discardPending(ORPHAN, SESSION.groupId(), SESSION.userId());

        assertEquals(List.of(other), local.pending);

        Remote remote = new Remote();
        new SyncingFestivalPlanningRatingRepository(local, remote, SESSION, festivals(true)).syncPendingRatings();
        assertEquals(List.of(other), remote.pushed);
        assertTrue(local.pending.isEmpty());
    }

    @Test
    public void discardRejectsOtherUsersAndChangedRows() throws Exception {
        Local local = new Local();
        local.pending.add(ORPHAN);
        expectFailure(() -> recovery(local, new Remote(), true).discardPending(ORPHAN, "other-group", SESSION.userId()));
        expectFailure(() -> recovery(local, new Remote(), true).discardPending(
                new SavedFestivalPlanningRating("group-1", "user-1", "missing", new Band("Airbourne"), Rating.of(2)),
                SESSION.groupId(), SESSION.userId()));
        assertEquals(List.of(ORPHAN), local.pending);
    }

    private static FestivalRepository festivals(boolean active) {
        return new FestivalRepository() {
            @Override public List<Festival> findAll() {
                return List.of(active ? Festival.active("wacken-2026", "Wacken Open Air 2026")
                        : Festival.archived("wacken-2026", "Wacken Open Air 2026"));
            }
            @Override public void save(Festival festival) { throw new UnsupportedOperationException(); }
        };
    }

    private static RecoverPendingPlanningRatingUseCase recovery(Local local, Remote remote, boolean active) {
        return new RecoverPendingPlanningRatingUseCase(local,
                (festivalId, band) -> remote.hasRating(SESSION, festivalId, band), festivals(active));
    }

    private static void expectFailure(Throwing action) throws Exception {
        try {
            action.run();
            fail("Expected recovery to stop");
        } catch (IOException | IllegalStateException expected) {
            assertTrue(expected.getMessage() != null);
        }
    }

    private interface Throwing { void run() throws Exception; }

    private static final class Remote implements SupabaseFestivalPlanningRatingRemote {
        final List<SavedFestivalPlanningRating> pulled = new ArrayList<>();
        final List<SavedFestivalPlanningRating> pushed = new ArrayList<>();
        boolean fail;
        @Override public void pushRating(AuthSession session, SavedFestivalPlanningRating rating) { pushed.add(rating); }
        @Override public List<SavedFestivalPlanningRating> pullGroupRatings(AuthSession session) throws IOException {
            if (fail) throw new IOException("offline");
            return pulled;
        }
        @Override public boolean hasRating(AuthSession session, String festivalId, Band band) throws IOException {
            if (fail) throw new IOException("offline");
            return pulled.stream().anyMatch(rating -> rating.groupId().equals(session.groupId())
                    && rating.userName().equals(session.userId()) && rating.festivalId().equals(festivalId)
                    && rating.band().equals(band));
        }
    }

    private static final class Local implements FestivalPlanningRatingSyncLocalStore {
        final List<SavedFestivalPlanningRating> pending = new ArrayList<>();
        boolean existingWacken;
        @Override public void save(String groupId, String userName, String festivalId, Band band, Rating rating) { throw new UnsupportedOperationException(); }
        @Override public void saveSynced(SavedFestivalPlanningRating rating) { pending.remove(rating); }
        @Override public List<SavedFestivalPlanningRating> findPending(String groupId, String userName) { return List.copyOf(pending); }
        @Override public Optional<Rating> findByUserFestivalAndBand(String userName, String festivalId, Band band) {
            return existingWacken ? Optional.of(Rating.of(2)) : Optional.empty();
        }
        @Override public List<SavedFestivalPlanningRating> findByFestival(String festivalId) { return List.of(); }
        @Override public List<SavedFestivalPlanningRating> findAll() { return List.copyOf(pending); }
        @Override public void movePendingRating(SavedFestivalPlanningRating expected, String targetFestivalId) {
            if (!pending.remove(expected)) throw new IllegalStateException("changed");
            pending.add(new SavedFestivalPlanningRating(expected.groupId(), expected.userName(), targetFestivalId,
                    expected.band(), expected.rating()));
        }
        @Override public void discardPendingRating(SavedFestivalPlanningRating expected) {
            if (!pending.remove(expected)) throw new IllegalStateException("changed");
        }
    }
}
