package be.wacken.planner;

import java.io.IOException;
import java.util.List;

import be.wacken.planner.domain.SavedRating;

interface SupabaseRatingRemote {
    void pushRating(AuthSession session, SavedRating rating) throws IOException;

    List<SavedRating> pullGroupRatings(AuthSession session) throws IOException;
}
