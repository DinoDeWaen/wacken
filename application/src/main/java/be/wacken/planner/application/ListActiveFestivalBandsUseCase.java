package be.wacken.planner.application;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandVisibilityPolicy;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalLifecycle;
import be.wacken.planner.domain.FestivalLineupEntry;
import be.wacken.planner.domain.FestivalLineupRepository;
import be.wacken.planner.domain.FestivalRepository;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.RatingRepository;

public final class ListActiveFestivalBandsUseCase {
    private static final String UNSCHEDULED_STAGE = "Not scheduled yet";
    private static final String UNSCHEDULED_TIME = "TBA";

    private final FestivalRepository festivals;
    private final FestivalLineupRepository lineups;
    private final PerformanceRepository performances;
    private final EffectiveRatingResolver ratings;
    private final String userName;

    public ListActiveFestivalBandsUseCase(
            FestivalRepository festivals,
            FestivalLineupRepository lineups,
            PerformanceRepository performances,
            RatingRepository ratings,
            String userName
    ) {
        this.festivals = Objects.requireNonNull(festivals, "festivals must not be null");
        this.lineups = Objects.requireNonNull(lineups, "lineups must not be null");
        this.performances = Objects.requireNonNull(performances, "performances must not be null");
        this.ratings = new EffectiveRatingResolver(Objects.requireNonNull(ratings, "ratings must not be null"));
        this.userName = Objects.requireNonNull(userName, "userName must not be null");
    }

    public List<BandListItem> listBands() {
        Optional<Festival> active = FestivalLifecycle.activeFestival(festivals.findAll());
        if (active.isEmpty()) {
            return List.of();
        }
        List<FestivalLineupEntry> entries = lineups.findByFestival(active.get().id());
        if (entries.isEmpty()) {
            return List.of();
        }
        List<Performance> importedPerformances = performances.findAll();
        return entries.stream()
                .map(FestivalLineupEntry::band)
                .filter(BandVisibilityPolicy::isVisibleInRatingLists)
                .map(band -> toBandListItem(band, performanceFor(importedPerformances, band)))
                .sorted(Comparator.comparing(BandListItem::bandName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    private Optional<Performance> performanceFor(List<Performance> performances, Band band) {
        return performances.stream()
                .filter(performance -> performance.band().equals(band))
                .findFirst();
    }

    private BandListItem toBandListItem(Band band, Optional<Performance> performance) {
        EffectiveRating rating = ratings.resolve(userName, band);
        return performance
                .map(value -> new BandListItem(
                        band.name(),
                        value.stage().name(),
                        value.start().toString(),
                        value.end().toString(),
                        rating.value(),
                        !rating.explicit(),
                        List.of()
                ))
                .orElseGet(() -> new BandListItem(
                        band.name(),
                        UNSCHEDULED_STAGE,
                        UNSCHEDULED_TIME,
                        UNSCHEDULED_TIME,
                        rating.value(),
                        !rating.explicit(),
                        List.of()
                ));
    }
}
