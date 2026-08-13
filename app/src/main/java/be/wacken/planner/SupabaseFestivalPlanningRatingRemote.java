package be.wacken.planner;

import java.io.IOException;
import java.util.List;

import be.wacken.planner.domain.SavedFestivalPlanningRating;

interface SupabaseFestivalPlanningRatingRemote {
    void pushRating(AuthSession session, SavedFestivalPlanningRating rating) throws IOException;

    List<SavedFestivalPlanningRating> pullGroupRatings(AuthSession session) throws IOException;
}
