package be.wacken.planner.application;

import java.time.Clock;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.DomainValidationException;
import be.wacken.planner.domain.FestivalLifecycle;
import be.wacken.planner.domain.FestivalRepository;
import be.wacken.planner.domain.PersonalBandRatingEvent;
import be.wacken.planner.domain.PersonalBandRatingHistoryRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RealRatingRepository;

public final class RecordPersonalBandRatingUseCase {
    private final FestivalRepository festivals;
    private final PersonalBandRatingHistoryRepository personalRatings;
    private final RealRatingRepository latestRealRatings;
    private final Clock clock;

    public RecordPersonalBandRatingUseCase(
            FestivalRepository festivals,
            PersonalBandRatingHistoryRepository personalRatings,
            RealRatingRepository latestRealRatings,
            Clock clock
    ) {
        this.festivals = Objects.requireNonNull(festivals, "festivals must not be null");
        this.personalRatings = Objects.requireNonNull(personalRatings, "personalRatings must not be null");
        this.latestRealRatings = Objects.requireNonNull(latestRealRatings, "latestRealRatings must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public RateBandResult rateBand(String userName, Band band, int ratingValue) {
        try {
            Rating rating = Rating.of(ratingValue);
            latestRealRatings.save(userName, band, rating);
            if (rating.value() > 0) {
                Optional<String> festivalId = FestivalLifecycle.activeFestival(festivals.findAll()).map(festival -> festival.id());
                personalRatings.save(new PersonalBandRatingEvent(
                        UUID.randomUUID().toString(),
                        userName,
                        band,
                        festivalId,
                        rating,
                        clock.instant()
                ));
            }
            return RateBandResult.stored();
        } catch (DomainValidationException error) {
            return RateBandResult.failure(error.getMessage());
        }
    }
}
