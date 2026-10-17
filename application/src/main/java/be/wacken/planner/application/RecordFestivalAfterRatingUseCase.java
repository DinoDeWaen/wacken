package be.wacken.planner.application;

import be.wacken.planner.domain.*;
import java.time.Clock;
import java.util.Objects;
import java.util.UUID;

public final class RecordFestivalAfterRatingUseCase {
    private final FestivalRepository festivals;
    private final FestivalLineupRepository lineups;
    private final FestivalAfterRatingRepository afterRatings;
    private final Clock clock;

    public RecordFestivalAfterRatingUseCase(FestivalRepository festivals, FestivalLineupRepository lineups,
                                           FestivalAfterRatingRepository afterRatings, Clock clock) {
        this.festivals = Objects.requireNonNull(festivals);
        this.lineups = Objects.requireNonNull(lineups);
        this.afterRatings = Objects.requireNonNull(afterRatings);
        this.clock = Objects.requireNonNull(clock);
    }

    public RateBandResult rateBand(String user, String festivalId, Band band, int value) {
        try {
            Rating rating = Rating.of(value);
            boolean active = festivals.findAll().stream().anyMatch(f -> f.id().equals(festivalId) && f.isActive());
            if (!active) return RateBandResult.failure("Choose an active festival before recording an after-rating.");
            if (lineups.findByFestival(festivalId).stream().noneMatch(entry -> entry.band().name().equals(band.name())))
                return RateBandResult.failure("This band is not in the selected festival lineup.");
            afterRatings.save(new FestivalAfterRating(UUID.randomUUID().toString(), user, festivalId, band,
                    rating, clock.instant()));
            return RateBandResult.stored();
        } catch (DomainValidationException error) {
            return RateBandResult.failure(error.getMessage());
        }
    }
}

