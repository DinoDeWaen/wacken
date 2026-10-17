package be.wacken.planner;

import be.wacken.planner.domain.*;
import java.util.List;

public interface RatingStateSyncStore extends FestivalAfterRatingRepository {
    List<FestivalAfterRating> findPending(String user);
    void acknowledge(FestivalAfterRating expected);
    void mergeRemote(List<FestivalAfterRating> ratings,String user);
    void replaceCurrent(List<PersonalBandRating> ratings,String user);
}
