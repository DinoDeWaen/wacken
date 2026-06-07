package be.wacken.planner.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PerformanceConflictResolverTest {
    private final PerformanceConflictResolver resolver = new PerformanceConflictResolver();

    @Test
    void mustSeePerformanceWinsOverLowerRatedAlternatives() {
        Performance mustSee = performance("5th Avenue", "Faster Stage", 18, 0, 19, 0);
        Performance lowerRated = performance("Airbourne", "Harder Stage", 18, 15, 19, 15);

        PerformanceConflictResolution resolution = resolver.resolve(
                new PerformanceConflictSet(List.of(lowerRated, mustSee)),
                ratings(
                        rated(mustSee, 5, 2),
                        rated(lowerRated, 4, 4)
                )
        );

        assertEquals(Optional.of(mustSee), resolution.selected());
        assertEquals(GroupDecisionStatus.GO, resolution.status());
        assertEquals(Optional.of(lowerRated), resolution.lostAlternative());
        assertEquals(List.of(lowerRated), resolution.rejected());
    }

    @Test
    void mustSeeTieUsesBetterDistancePositionAndRecordsLostAlternative() {
        Stage previous = new Stage("Previous Stage");
        Performance close = performance("5th Avenue", "Faster Stage", 18, 0, 19, 0);
        Performance far = performance("Airbourne", "Harder Stage", 18, 15, 19, 15);

        PerformanceConflictResolution resolution = resolver.resolve(
                new PerformanceConflictSet(List.of(far, close)),
                ratings(rated(close, 5), rated(far, 5)),
                ConflictDistanceContext.fromPreviousStage(previous, List.of(
                        StageDistance.between(previous, far.stage(), 18),
                        StageDistance.between(previous, close.stage(), 4)
                ))
        );

        assertEquals(Optional.of(close), resolution.selected());
        assertEquals(Optional.of(far), resolution.lostAlternative());
    }

    @Test
    void ratingFourConflictChoosesMostFourRatings() {
        Performance oneFour = performance("5th Avenue", "Faster Stage", 18, 0, 19, 0);
        Performance twoFours = performance("Airbourne", "Harder Stage", 18, 15, 19, 15);

        PerformanceConflictResolution resolution = resolver.resolve(
                new PerformanceConflictSet(List.of(oneFour, twoFours)),
                ratings(rated(oneFour, 4, 2), rated(twoFours, 4, 4, 2))
        );

        assertEquals(Optional.of(twoFours), resolution.selected());
        assertEquals(GroupDecisionStatus.GO, resolution.status());
        assertEquals(Optional.of(oneFour), resolution.lostAlternative());
    }

    @Test
    void ratingFourTieChoosesFewerVetoes() {
        Performance oneVeto = performance("5th Avenue", "Faster Stage", 18, 0, 19, 0);
        Performance noVeto = performance("Airbourne", "Harder Stage", 18, 15, 19, 15);

        PerformanceConflictResolution resolution = resolver.resolve(
                new PerformanceConflictSet(List.of(oneVeto, noVeto)),
                ratings(rated(oneVeto, 4, 1), rated(noVeto, 4, 2))
        );

        assertEquals(Optional.of(noVeto), resolution.selected());
        assertEquals(Optional.of(oneVeto), resolution.lostAlternative());
    }

    @Test
    void ratingFourTieChoosesShortestDistanceWhenCountsAndVetoesTie() {
        Stage previous = new Stage("Previous Stage");
        Performance close = performance("5th Avenue", "Faster Stage", 18, 0, 19, 0);
        Performance far = performance("Airbourne", "Harder Stage", 18, 15, 19, 15);

        PerformanceConflictResolution resolution = resolver.resolve(
                new PerformanceConflictSet(List.of(far, close)),
                ratings(rated(close, 4, 2), rated(far, 4, 2)),
                ConflictDistanceContext.fromPreviousStage(previous, List.of(
                        StageDistance.between(previous, far.stage(), 14),
                        StageDistance.between(previous, close.stage(), 3)
                ))
        );

        assertEquals(Optional.of(close), resolution.selected());
        assertEquals(Optional.of(far), resolution.lostAlternative());
    }

    @Test
    void ratingThreeConflictIsOptionalAndChoosesMostThreeRatings() {
        Performance oneLike = performance("5th Avenue", "Faster Stage", 18, 0, 19, 0);
        Performance twoLikes = performance("Airbourne", "Harder Stage", 18, 15, 19, 15);

        PerformanceConflictResolution resolution = resolver.resolve(
                new PerformanceConflictSet(List.of(oneLike, twoLikes)),
                ratings(rated(oneLike, 3, 2), rated(twoLikes, 3, 3, 2))
        );

        assertEquals(Optional.of(twoLikes), resolution.selected());
        assertEquals(GroupDecisionStatus.OPTIONAL, resolution.status());
        assertEquals(Optional.of(oneLike), resolution.lostAlternative());
    }

    @Test
    void ratingTwoConflictRemainsOptional() {
        Performance first = performance("5th Avenue", "Faster Stage", 18, 0, 19, 0);
        Performance second = performance("Airbourne", "Harder Stage", 18, 15, 19, 15);

        PerformanceConflictResolution resolution = resolver.resolve(
                new PerformanceConflictSet(List.of(first, second)),
                ratings(rated(first, 2), rated(second, 2, 0))
        );

        assertEquals(Optional.of(first), resolution.selected());
        assertEquals(GroupDecisionStatus.OPTIONAL, resolution.status());
        assertEquals(Optional.of(second), resolution.lostAlternative());
    }

    @Test
    void allVetoedConflictSelectsNoPerformance() {
        Performance first = performance("5th Avenue", "Faster Stage", 18, 0, 19, 0);
        Performance second = performance("Airbourne", "Harder Stage", 18, 15, 19, 15);

        PerformanceConflictResolution resolution = resolver.resolve(
                new PerformanceConflictSet(List.of(first, second)),
                ratings(rated(first, 3, 1), rated(second, 4, 1, 1))
        );

        assertEquals(Optional.empty(), resolution.selected());
        assertEquals(GroupDecisionStatus.BLOCKED, resolution.status());
        assertEquals(Optional.empty(), resolution.lostAlternative());
        assertEquals(List.of(first, second), resolution.rejected());
        assertEquals("All overlapping options are blocked by veto rules.", resolution.reason());
    }

    private Performance performance(String bandName, String stageName, int startHour, int startMinute, int endHour, int endMinute) {
        return new Performance(
                new Band(bandName),
                new Stage(stageName),
                LocalDateTime.of(2026, 7, 30, startHour, startMinute),
                LocalDateTime.of(2026, 7, 30, endHour, endMinute)
        );
    }

    private Map<Band, List<Rating>> ratings(RatedPerformance... ratings) {
        java.util.Map<Band, List<Rating>> byBand = new java.util.HashMap<>();
        for (RatedPerformance rated : ratings) {
            byBand.put(rated.performance().band(), rated.ratings());
        }
        return byBand;
    }

    private RatedPerformance rated(Performance performance, int... values) {
        return new RatedPerformance(
                performance,
                java.util.Arrays.stream(values).mapToObj(Rating::of).toList()
        );
    }

    private record RatedPerformance(Performance performance, List<Rating> ratings) {
    }
}
