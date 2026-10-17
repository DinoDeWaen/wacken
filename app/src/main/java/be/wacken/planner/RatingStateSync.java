package be.wacken.planner;

import be.wacken.planner.domain.*;
import java.io.IOException;
import java.util.List;

/** Push pending explicit operations before applying complete owner-scoped server snapshots. */
final class RatingStateSync {
    interface Remote {
        void push(FestivalAfterRating rating) throws IOException;
        List<FestivalAfterRating> afterRatings() throws IOException;
        List<PersonalBandRating> currentRatings() throws IOException;
    }
    private final RatingStateSyncStore cache;
    private final Remote remote;
    private final String user;
    RatingStateSync(RatingStateSyncStore cache, Remote remote, String user) {
        this.cache=cache;this.remote=remote;this.user=user;
    }
    void sync() throws IOException {
        for(FestivalAfterRating pending:cache.findPending(user)){
            // A recovered legacy event is evidence to review, not explicit confirmation of a final value.
            if(pending.requiresReview())continue;
            remote.push(pending);
            cache.acknowledge(pending);
        }
        List<FestivalAfterRating> after=remote.afterRatings();
        List<PersonalBandRating> current=remote.currentRatings();
        cache.mergeRemote(after,user);
        cache.replaceCurrent(current,user);
    }
}

