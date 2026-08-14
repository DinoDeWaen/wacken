package be.wacken.planner.application;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandVisibilityPolicy;
import be.wacken.planner.domain.FestivalLineupEntry;
import be.wacken.planner.domain.FestivalLineupRepository;
import be.wacken.planner.domain.FestivalPlanningRatingRepository;
import be.wacken.planner.domain.PersonalBandRatingEvent;
import be.wacken.planner.domain.PersonalBandRatingHistoryRepository;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RealRatingRepository;
import be.wacken.planner.domain.SavedRating;
import be.wacken.planner.domain.SavedFestivalPlanningRating;

public final class ListArchivedFestivalBandsUseCase {
    private static final String LEGACY_WACKEN_FESTIVAL_ID = "wacken-2026";
    private static final String UNSCHEDULED_STAGE = "Not scheduled yet";
    private static final String UNSCHEDULED_TIME = "TBA";

    private final FestivalLineupRepository lineups;
    private final FestivalPlanningRatingRepository planningRatings;
    private final PersonalBandRatingHistoryRepository personalRatings;
    private final RealRatingRepository legacyRealRatings;
    private final PerformanceRepository performances;

    public ListArchivedFestivalBandsUseCase(
            FestivalLineupRepository lineups,
            FestivalPlanningRatingRepository planningRatings,
            PersonalBandRatingHistoryRepository personalRatings,
            RealRatingRepository legacyRealRatings,
            PerformanceRepository performances
    ) {
        this.lineups = Objects.requireNonNull(lineups, "lineups must not be null");
        this.planningRatings = Objects.requireNonNull(planningRatings, "planningRatings must not be null");
        this.personalRatings = Objects.requireNonNull(personalRatings, "personalRatings must not be null");
        this.legacyRealRatings = Objects.requireNonNull(legacyRealRatings, "legacyRealRatings must not be null");
        this.performances = Objects.requireNonNull(performances, "performances must not be null");
    }

    public List<BandListItem> listBands(String userName, String festivalId) {
        TreeSet<String> bandNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        lineups.findByFestival(festivalId).stream()
                .map(FestivalLineupEntry::band)
                .map(Band::name)
                .forEach(bandNames::add);
        planningRatings.findByFestival(festivalId).stream()
                .map(SavedFestivalPlanningRating::band)
                .map(Band::name)
                .forEach(bandNames::add);
        personalRatings.findByUserAndFestival(userName, festivalId).stream()
                .map(PersonalBandRatingEvent::band)
                .map(Band::name)
                .forEach(bandNames::add);
        if (LEGACY_WACKEN_FESTIVAL_ID.equals(festivalId)) {
            legacyRealRatings.findAll().stream()
                    .filter(rating -> rating.userName().equals(userName))
                    .filter(rating -> rating.rating().value() > 0)
                    .map(SavedRating::band)
                    .map(Band::name)
                    .forEach(bandNames::add);
        }
        List<Performance> importedPerformances = performances.findAll();
        return bandNames.stream()
                .map(Band::new)
                .filter(BandVisibilityPolicy::isVisibleInRatingLists)
                .map(band -> toBandListItem(userName, festivalId, band, performanceFor(importedPerformances, band)))
                .sorted(Comparator.comparing(BandListItem::bandName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    private Optional<Performance> performanceFor(List<Performance> performances, Band band) {
        return performances.stream()
                .filter(performance -> performance.band().equals(band))
                .findFirst();
    }

    private BandListItem toBandListItem(String userName, String festivalId, Band band, Optional<Performance> performance) {
        Optional<Rating> rating = planningRatings.findByUserFestivalAndBand(userName, festivalId, band);
        int ratingValue = rating.map(Rating::value).orElse(0);
        boolean defaultRating = rating.isEmpty() || ratingValue == 0;
        return performance
                .map(value -> new BandListItem(
                        band.name(),
                        value.stage().name(),
                        value.start().toString(),
                        value.end().toString(),
                        ratingValue,
                        defaultRating,
                        personRatingsFor(festivalId, band)
                ))
                .orElseGet(() -> new BandListItem(
                        band.name(),
                        UNSCHEDULED_STAGE,
                        UNSCHEDULED_TIME,
                        UNSCHEDULED_TIME,
                        ratingValue,
                        defaultRating,
                        personRatingsFor(festivalId, band)
                ));
    }

    private List<PersonRatingStars> personRatingsFor(String festivalId, Band band) {
        return planningRatings.findByFestival(festivalId)
                .stream()
                .filter(rating -> rating.band().equals(band))
                .filter(rating -> rating.rating().value() > 0)
                .sorted(Comparator.comparing(SavedFestivalPlanningRating::userName, String.CASE_INSENSITIVE_ORDER))
                .map(rating -> new PersonRatingStars(rating.userName(), rating.rating().value()))
                .toList();
    }
}
