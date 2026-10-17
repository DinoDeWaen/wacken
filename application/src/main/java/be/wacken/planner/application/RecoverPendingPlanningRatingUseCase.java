package be.wacken.planner.application;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.SavedFestivalPlanningRating;

public final class RecoverPendingPlanningRatingUseCase {
    public static final String WACKEN_2026_ID = "wacken-2026";

    private final LocalStore local;
    private final RemoteLookup remote;
    private final FestivalRepository festivals;

    public RecoverPendingPlanningRatingUseCase(LocalStore local, RemoteLookup remote, FestivalRepository festivals) {
        this.local = local;
        this.remote = remote;
        this.festivals = festivals;
    }

    public void moveToWacken2026(SavedFestivalPlanningRating expected, String groupId, String userId) throws IOException {
        if (!groupId.equals(expected.groupId()) || !userId.equals(expected.userName())) {
            throw new IllegalStateException("This rating does not belong to the signed-in user.");
        }
        if (WACKEN_2026_ID.equals(expected.festivalId())) {
            throw new IllegalStateException("The rating already belongs to Wacken 2026.");
        }
        List<Festival> available = festivals.findAll();
        if (available.stream().anyMatch(festival -> festival.id().equals(expected.festivalId()))) {
            throw new IllegalStateException("The original festival is available again. Retry sync before moving the rating.");
        }
        Festival target = available.stream()
                .filter(festival -> WACKEN_2026_ID.equals(festival.id()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Wacken 2026 is not available. The pending rating was kept."));
        if (!target.isActive()) {
            throw new IllegalStateException("Wacken 2026 is archived. The pending rating was kept.");
        }
        if (!local.findPending(groupId, userId).contains(expected)) {
            throw new IllegalStateException("The pending rating changed. Review it again before recovery.");
        }
        if (local.findByUserFestivalAndBand(userId, WACKEN_2026_ID, expected.band()).isPresent()) {
            throw new IllegalStateException("A Wacken rating already exists for this band. The pending rating was kept.");
        }
        if (remote.hasRating(WACKEN_2026_ID, expected.band())) {
            throw new IllegalStateException("A Wacken rating already exists in Supabase for this band. The pending rating was kept.");
        }
        local.movePendingRating(expected, WACKEN_2026_ID);
    }

    public void discardPending(SavedFestivalPlanningRating expected, String groupId, String userId) {
        if (!groupId.equals(expected.groupId()) || !userId.equals(expected.userName())) {
            throw new IllegalStateException("This rating does not belong to the signed-in user.");
        }
        if (!local.findPending(groupId, userId).contains(expected)) {
            throw new IllegalStateException("Pending rating changed. Review it again before discarding.");
        }
        local.discardPendingRating(expected);
    }

    public interface LocalStore {
        List<SavedFestivalPlanningRating> findPending(String groupId, String userId);
        Optional<Rating> findByUserFestivalAndBand(String userId, String festivalId, Band band);
        void movePendingRating(SavedFestivalPlanningRating expected, String targetFestivalId);
        void discardPendingRating(SavedFestivalPlanningRating expected);
    }

    public interface RemoteLookup {
        boolean hasRating(String festivalId, Band band) throws IOException;
    }
}
