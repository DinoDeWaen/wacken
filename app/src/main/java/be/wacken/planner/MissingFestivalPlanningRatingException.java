package be.wacken.planner;

import java.io.IOException;

import be.wacken.planner.domain.SavedFestivalPlanningRating;

final class MissingFestivalPlanningRatingException extends IOException {
    private final SavedFestivalPlanningRating rating;

    MissingFestivalPlanningRatingException(SavedFestivalPlanningRating rating) {
        super("Pending rating for " + rating.band().name() + " (" + rating.rating().value()
                + " stars) belongs to missing festival " + rating.festivalId()
                + ". Review it before moving the rating to Wacken 2026.");
        this.rating = rating;
    }

    SavedFestivalPlanningRating rating() {
        return rating;
    }
}
