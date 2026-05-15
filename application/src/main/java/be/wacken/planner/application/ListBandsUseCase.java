package be.wacken.planner.application;

import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.RatingRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ListBandsUseCase {
    private final PerformanceRepository performances;
    private final EffectiveRatingResolver ratings;
    private final String userName;

    public ListBandsUseCase(PerformanceRepository performances, RatingRepository ratings, String userName) {
        this.performances = Objects.requireNonNull(performances, "performances must not be null");
        this.ratings = new EffectiveRatingResolver(ratings);
        this.userName = Objects.requireNonNull(userName, "userName must not be null");
    }

    public List<BandListItem> listBands() {
        return performances.findAll()
                .stream()
                .sorted(Comparator.comparing(Performance::start))
                .map(this::toBandListItem)
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
                !rating.explicit()
        );
    }

    private EffectiveRating ratingFor(Performance performance) {
        return ratings.resolve(userName, performance.band());
    }
}
