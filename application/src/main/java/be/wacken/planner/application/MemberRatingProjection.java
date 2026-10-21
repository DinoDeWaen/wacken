package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.SavedFestivalPlanningRating;
import be.wacken.planner.domain.SavedRating;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** Read-only presentation of an already scoped planning snapshot. */
final class MemberRatingProjection {
    private MemberRatingProjection() {
    }

    static List<PersonRatingStars> forBand(List<SavedRating> scopedRatings, Band band) {
        return scopedRatings.stream()
                .filter(rating -> rating.band().name().equals(band.name()))
                .filter(rating -> rating.rating().value() > 0)
                .sorted(Comparator.comparing(SavedRating::userName, String.CASE_INSENSITIVE_ORDER))
                .map(rating -> new PersonRatingStars(rating.userName(), rating.rating().value()))
                .collect(Collectors.toList());
    }

    static List<PersonRatingStars> forFestivalBand(List<SavedFestivalPlanningRating> scopedRatings, Band band) {
        return forBand(scopedRatings.stream()
                .map(rating -> new SavedRating(rating.userName(), rating.band(), rating.rating()))
                .collect(Collectors.toList()), band);
    }
}
