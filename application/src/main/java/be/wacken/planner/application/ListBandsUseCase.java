package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandVisibilityPolicy;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.SavedRating;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ListBandsUseCase {
    private static final String UNSCHEDULED_STAGE = "Not scheduled yet";
    private static final String UNSCHEDULED_TIME = "TBA";

    private final BandRepository bands;
    private final PerformanceRepository performances;
    private final RatingRepository ratingRepository;
    private final EffectiveRatingResolver ratings;
    private final String userName;

    public ListBandsUseCase(BandRepository bands, PerformanceRepository performances, RatingRepository ratings, String userName) {
        this.bands = Objects.requireNonNull(bands, "bands must not be null");
        this.performances = Objects.requireNonNull(performances, "performances must not be null");
        this.ratingRepository = Objects.requireNonNull(ratings, "ratings must not be null");
        this.ratings = new EffectiveRatingResolver(this.ratingRepository);
        this.userName = Objects.requireNonNull(userName, "userName must not be null");
    }

    public List<BandListItem> listBands() {
        List<Performance> importedPerformances = performances.findAll();
        if (!importedPerformances.isEmpty()) {
            return importedPerformances
                .stream()
                .filter(performance -> BandVisibilityPolicy.isVisibleInRatingLists(performance.band()))
                .map(this::toBandListItem)
                .sorted(Comparator.comparing(BandListItem::bandName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
        }

        return bands.findAll()
                .stream()
                .filter(BandVisibilityPolicy::isVisibleInRatingLists)
                .map(this::toUnscheduledBandListItem)
                .sorted(Comparator.comparing(BandListItem::bandName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    private BandListItem toBandListItem(Performance performance) {
        EffectiveRating rating = ratingFor(performance);
        return new BandListItem(
                performance.band().name(),
                performance.stage().name(),
                performance.start().toString(),
                performance.end().toString(),
                rating.value(),
                !rating.explicit(),
                personRatingsFor(performance.band())
        );
    }

    private EffectiveRating ratingFor(Performance performance) {
        return ratings.resolve(userName, performance.band());
    }

    private BandListItem toUnscheduledBandListItem(Band band) {
        EffectiveRating rating = ratings.resolve(userName, band);
        return new BandListItem(
                band.name(),
                UNSCHEDULED_STAGE,
                UNSCHEDULED_TIME,
                UNSCHEDULED_TIME,
                rating.value(),
                !rating.explicit(),
                personRatingsFor(band)
        );
    }

    private List<PersonRatingStars> personRatingsFor(Band band) {
        return ratingRepository.findAll()
                .stream()
                .filter(rating -> rating.band().equals(band))
                .filter(rating -> rating.rating().value() > 0)
                .sorted(Comparator.comparing(SavedRating::userName, String.CASE_INSENSITIVE_ORDER))
                .map(rating -> new PersonRatingStars(rating.userName(), rating.rating().value()))
                .collect(Collectors.toList());
    }
}
