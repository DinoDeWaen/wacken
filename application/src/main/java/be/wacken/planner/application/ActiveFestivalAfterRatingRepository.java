package be.wacken.planner.application;

import be.wacken.planner.domain.*;
import java.util.List;
import java.util.Optional;

/** Compatibility read view for existing detail/export consumers. It never falls back to another festival. */
public final class ActiveFestivalAfterRatingRepository implements RealRatingRepository {
    private final FestivalRepository festivals;
    private final FestivalAfterRatingRepository afterRatings;
    public ActiveFestivalAfterRatingRepository(FestivalRepository festivals, FestivalAfterRatingRepository afterRatings) {
        this.festivals = festivals;
        this.afterRatings = afterRatings;
    }
    @Override public void save(String user, Band band, Rating rating) {
        throw new UnsupportedOperationException("Use the explicit festival after-rating use case.");
    }
    @Override public Optional<Rating> findByUserAndBand(String user, Band band) {
        return FestivalLifecycle.activeFestival(festivals.findAll())
                .flatMap(f -> afterRatings.findByUserFestivalAndBand(user, f.id(), band))
                .map(FestivalAfterRating::rating);
    }
    @Override public List<SavedRating> findAll() {
        return FestivalLifecycle.activeFestival(festivals.findAll())
                .map(f -> afterRatings.findByFestival(f.id()).stream()
                        .map(r -> new SavedRating(r.userName(), r.band(), r.rating())).collect(java.util.stream.Collectors.toList()))
                .orElse(List.of());
    }
}
