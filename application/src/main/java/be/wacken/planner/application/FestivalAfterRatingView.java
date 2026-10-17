package be.wacken.planner.application;

import be.wacken.planner.domain.*;
import java.util.*;

/** Read-only compatibility view bound to one explicit festival. */
public final class FestivalAfterRatingView implements RealRatingRepository {
    private final FestivalAfterRatingRepository after;
    private final String festival;
    public FestivalAfterRatingView(FestivalAfterRatingRepository after,String festival){this.after=after;this.festival=festival;}
    @Override public void save(String user,Band band,Rating value){throw new UnsupportedOperationException("Use the explicit after-rating command.");}
    @Override public Optional<Rating> findByUserAndBand(String user,Band band) {
        return after.findByUserFestivalAndBand(user,festival,band).map(FestivalAfterRating::rating);
    }
    @Override public List<SavedRating> findAll() {
        return after.findByFestival(festival).stream().map(r->new SavedRating(r.userName(),r.band(),r.rating())).toList();
    }
}
