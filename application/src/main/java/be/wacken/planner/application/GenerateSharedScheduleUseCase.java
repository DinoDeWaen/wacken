package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceConflictDetector;
import be.wacken.planner.domain.PerformanceConflictResolution;
import be.wacken.planner.domain.PerformanceConflictResolver;
import be.wacken.planner.domain.PerformanceConflictSet;
import be.wacken.planner.domain.PerformanceOverlapPolicy;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.SavedRating;
import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.StageDistanceRepository;
import be.wacken.planner.domain.StageWalkingTimePolicy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public final class GenerateSharedScheduleUseCase {
    private static final LocalTime FESTIVAL_DAY_CUTOFF = LocalTime.of(2, 0);
    private final PerformanceRepository performances;
    private final RatingRepository ratings;
    private final Optional<StageDistanceRepository> distances;
    private final PerformanceConflictDetector conflictDetector;
    private final PerformanceConflictResolver conflictResolver;
    private final PerformanceOverlapPolicy overlapPolicy;

    public GenerateSharedScheduleUseCase(PerformanceRepository performances, RatingRepository ratings) {
        this(performances, ratings, null);
    }

    public GenerateSharedScheduleUseCase(
            PerformanceRepository performances,
            RatingRepository ratings,
            StageDistanceRepository distances
    ) {
        this(performances, ratings, distances, new PerformanceConflictDetector(), new PerformanceConflictResolver());
    }

    GenerateSharedScheduleUseCase(
            PerformanceRepository performances,
            RatingRepository ratings,
            StageDistanceRepository distances,
            PerformanceConflictDetector conflictDetector,
            PerformanceConflictResolver conflictResolver
    ) {
        this.performances = Objects.requireNonNull(performances, "performances must not be null");
        this.ratings = Objects.requireNonNull(ratings, "ratings must not be null");
        this.distances = Optional.ofNullable(distances);
        this.conflictDetector = Objects.requireNonNull(conflictDetector, "conflictDetector must not be null");
        this.conflictResolver = Objects.requireNonNull(conflictResolver, "conflictResolver must not be null");
        this.overlapPolicy = new PerformanceOverlapPolicy();
    }

    public SharedSchedule generate() {
        List<Performance> scheduled = performances.findAll();
        if (scheduled.isEmpty()) {
            return SharedSchedule.noScheduledPerformances();
        }

        Map<Band, List<Rating>> groupRatings = groupRatingsByBand();
        List<TimelineSlot> slots = conflictDetector.detect(scheduled)
                .stream()
                .flatMap(conflictSet -> toSlots(conflictSet, groupRatings).stream())
                .sorted(Comparator.comparing(TimelineSlot::start))
                .collect(Collectors.toList());

        if (slots.isEmpty()) {
            return SharedSchedule.noSelections();
        }
        slots = withWalkingTimes(slots);
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

    private List<TimelineSlot> toSlots(PerformanceConflictSet conflictSet, Map<Band, List<Rating>> groupRatings) {
        List<Performance> remaining = new ArrayList<>(conflictSet.performances());
        List<TimelineSlot> slots = new ArrayList<>();
        while (!remaining.isEmpty()) {
            PerformanceConflictResolution resolution = conflictResolver.resolve(
                    new PerformanceConflictSet(remaining),
                    groupRatings
            );
            Optional<TimelineSlot> slot = toSlot(resolution, groupRatings);
            if (slot.isEmpty()) {
                break;
            }
            Performance selected = resolution.selected().get();
            slots.add(slot.get());
            remaining = remaining.stream()
                    .filter(performance -> !performance.equals(selected))
                    .filter(performance -> !overlaps(selected, performance))
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        return slots;
    }

    private Optional<TimelineSlot> toSlot(PerformanceConflictResolution resolution, Map<Band, List<Rating>> groupRatings) {
        return resolution.selected().map(selected -> {
            Optional<Performance> visibleLostAlternative = resolution.lostAlternative()
                    .filter(alternative -> overlaps(selected, alternative))
                    .filter(alternative -> visibleAlternative(alternative, groupRatings));
            return new TimelineSlot(
                    selected.band().name(),
                    highestRating(selected.band(), groupRatings),
                    selected.stage().name(),
                    selected.start(),
                    selected.end(),
                    resolution.status(),
                    visibleLostAlternative.map(alternative -> alternative.band().name()),
                    visibleLostAlternative.map(alternative -> highestRating(alternative.band(), groupRatings)),
                    candidates(resolution, groupRatings, visibleLostAlternative)
            );
        });
    }

    private List<ScheduleDecisionCandidate> candidates(
            PerformanceConflictResolution resolution,
            Map<Band, List<Rating>> groupRatings,
            Optional<Performance> visibleLostAlternative
    ) {
        List<ScheduleDecisionCandidate> candidates = new ArrayList<>();
        if (resolution.selected().isPresent()) {
            Performance selected = resolution.selected().get();
            candidates.add(candidate(selected, groupRatings, "CHOSEN", true));
            visibleLostAlternative.ifPresent(lost ->
                    candidates.add(candidate(lost, groupRatings, lostAlternativeStatus(resolution), false))
            );
            for (Performance rejected : resolution.rejected()) {
                if (overlaps(selected, rejected)
                        && visibleAlternative(rejected, groupRatings)
                        && !visibleLostAlternative.filter(rejected::equals).isPresent()) {
                    candidates.add(candidate(rejected, groupRatings, rejectedStatus(rejected, visibleLostAlternative), false));
                }
            }
            return candidates;
        }
        for (Performance rejected : resolution.rejected()) {
            if (visibleAlternative(rejected, groupRatings)) {
                candidates.add(candidate(rejected, groupRatings, rejectedStatus(rejected, visibleLostAlternative), false));
            }
        }
        return candidates;
    }

    private String rejectedStatus(Performance rejected, Optional<Performance> lostAlternative) {
        return lostAlternative
                .filter(rejected::equals)
                .map(ignored -> "LOST ALTERNATIVE")
                .orElse("NOT SELECTED");
    }

    private String lostAlternativeStatus(PerformanceConflictResolution resolution) {
        return resolution.lostAlternativeTied() ? "⚖ TIED ALTERNATIVE" : "LOST ALTERNATIVE";
    }

    private boolean overlaps(Performance first, Performance second) {
        return overlapPolicy.overlapsForScheduling(first, second);
    }

    private boolean visibleAlternative(Performance performance, Map<Band, List<Rating>> groupRatings) {
        return highestRating(performance.band(), groupRatings) > 1;
    }

    private ScheduleDecisionCandidate candidate(
            Performance performance,
            Map<Band, List<Rating>> groupRatings,
            String status,
            boolean selected
    ) {
        return new ScheduleDecisionCandidate(
                performance.band().name(),
                highestRating(performance.band(), groupRatings),
                performance.stage().name(),
                performance.start(),
                performance.end(),
                status,
                selected
        );
    }

    private int highestRating(Band band, Map<Band, List<Rating>> groupRatings) {
        return groupRatings.getOrDefault(band, java.util.Collections.emptyList())
                .stream()
                .mapToInt(Rating::value)
                .max()
                .orElse(0);
    }

    private List<ScheduleDay> groupByDay(List<TimelineSlot> slots) {
        Map<LocalDate, List<TimelineSlot>> slotsByDay = new LinkedHashMap<>();
        for (TimelineSlot slot : slots) {
            slotsByDay.computeIfAbsent(festivalDay(slot.start()), ignored -> new ArrayList<>()).add(slot);
        }
        return slotsByDay.entrySet()
                .stream()
                .map(entry -> new ScheduleDay(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private List<TimelineSlot> withWalkingTimes(List<TimelineSlot> slots) {
        List<TimelineSlot> enriched = new ArrayList<>(slots.size());
        for (int index = 0; index < slots.size(); index++) {
            OptionalInt walkingMinutes = OptionalInt.empty();
            if (index < slots.size() - 1) {
                walkingMinutes = walkingMinutes(slots.get(index).stageName(), slots.get(index + 1).stageName());
            }
            enriched.add(slots.get(index).withWalkingMinutesToNext(walkingMinutes));
        }
        return enriched;
    }

    private OptionalInt walkingMinutes(String fromStageName, String toStageName) {
        Stage from = new Stage(fromStageName);
        Stage to = new Stage(toStageName);
        return distances
                .flatMap(repository -> repository.findBetween(from, to))
                .map(distance -> OptionalInt.of(distance.walkingMinutes()))
                .orElseGet(() -> OptionalInt.of(StageWalkingTimePolicy.defaultWalkingMinutes(fromStageName, toStageName)));
    }

    private LocalDate festivalDay(java.time.LocalDateTime time) {
        if (time.toLocalTime().isBefore(FESTIVAL_DAY_CUTOFF)) {
            return time.toLocalDate().minusDays(1);
        }
        return time.toLocalDate();
    }
}
