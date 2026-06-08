package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceConflictDetector;
import be.wacken.planner.domain.PerformanceConflictResolution;
import be.wacken.planner.domain.PerformanceConflictResolver;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.SavedRating;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class GenerateSharedScheduleUseCase {
    private final PerformanceRepository performances;
    private final RatingRepository ratings;
    private final PerformanceConflictDetector conflictDetector;
    private final PerformanceConflictResolver conflictResolver;

    public GenerateSharedScheduleUseCase(PerformanceRepository performances, RatingRepository ratings) {
        this(performances, ratings, new PerformanceConflictDetector(), new PerformanceConflictResolver());
    }

    GenerateSharedScheduleUseCase(
            PerformanceRepository performances,
            RatingRepository ratings,
            PerformanceConflictDetector conflictDetector,
            PerformanceConflictResolver conflictResolver
    ) {
        this.performances = Objects.requireNonNull(performances, "performances must not be null");
        this.ratings = Objects.requireNonNull(ratings, "ratings must not be null");
        this.conflictDetector = Objects.requireNonNull(conflictDetector, "conflictDetector must not be null");
        this.conflictResolver = Objects.requireNonNull(conflictResolver, "conflictResolver must not be null");
    }

    public SharedSchedule generate() {
        List<Performance> scheduled = performances.findAll();
        if (scheduled.isEmpty()) {
            return SharedSchedule.noScheduledPerformances();
        }

        Map<Band, List<Rating>> groupRatings = groupRatingsByBand();
        List<TimelineSlot> slots = conflictDetector.detect(scheduled)
                .stream()
                .map(conflictSet -> conflictResolver.resolve(conflictSet, groupRatings))
                .map(this::toSlot)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparing(TimelineSlot::start))
                .collect(Collectors.toList());

        if (slots.isEmpty()) {
            return SharedSchedule.noSelections();
        }
        return SharedSchedule.generated(groupByDay(slots));
    }

    private Map<Band, List<Rating>> groupRatingsByBand() {
        return ratings.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        SavedRating::band,
                        LinkedHashMap::new,
                        Collectors.mapping(SavedRating::rating, Collectors.toList())
                ));
    }

    private Optional<TimelineSlot> toSlot(PerformanceConflictResolution resolution) {
        return resolution.selected().map(selected -> new TimelineSlot(
                selected.band().name(),
                selected.stage().name(),
                selected.start(),
                selected.end(),
                resolution.status(),
                resolution.lostAlternative().map(alternative -> alternative.band().name())
        ));
    }

    private List<ScheduleDay> groupByDay(List<TimelineSlot> slots) {
        Map<LocalDate, List<TimelineSlot>> slotsByDay = new LinkedHashMap<>();
        for (TimelineSlot slot : slots) {
            slotsByDay.computeIfAbsent(slot.start().toLocalDate(), ignored -> new ArrayList<>()).add(slot);
        }
        return slotsByDay.entrySet()
                .stream()
                .map(entry -> new ScheduleDay(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
