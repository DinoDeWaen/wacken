package be.wacken.planner.application;

import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.RatingRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class ListBandsUseCase {
    private final PerformanceRepository performances;
    private final RatingRepository ratings;
    private final String userName;

    public ListBandsUseCase(PerformanceRepository performances, RatingRepository ratings, String userName) {
        this.performances = Objects.requireNonNull(performances, "performances must not be null");
        this.ratings = Objects.requireNonNull(ratings, "ratings must not be null");
        this.userName = Objects.requireNonNull(userName, "userName must not be null");
    }

    public List<BandListItem> listBands() {
        return performances.findAll()
                .stream()
                .sorted(Comparator.comparing(Performance::start))
                .map(performance -> new BandListItem(
                        performance.band().name(),
                        performance.stage().name(),
                        performance.start().toString(),
                        performance.end().toString(),
                        ratingFor(performance)
                ))
                .toList();
    }

    private Optional<Integer> ratingFor(Performance performance) {
        return ratings.findByUserAndBand(userName, performance.band())
                .map(rating -> rating.value());
    }
}
