package be.wacken.planner.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GroupDecisionPolicyTest {
    private final GroupDecisionPolicy policy = new GroupDecisionPolicy();

    @Test
    void anyMustSeeRatingMakesPerformanceGo() {
        GroupDecision decision = policy.decide(performanceAt(18, 0), ratings(0, 2, 5, 1));

        assertEquals(GroupDecisionStatus.GO, decision.status());
        assertEquals("At least one group member marked this band as must-see.", decision.reason());
        assertEquals(5, decision.maxRating());
        assertEquals(1, decision.vetoCount());
    }

    @Test
    void maximumFourGoesUnlessTwoOrMoreVetoesExist() {
        GroupDecision decision = policy.decide(performanceAt(18, 0), ratings(4, 4, 1));

        assertEquals(GroupDecisionStatus.GO, decision.status());
        assertEquals("Highest group rating is want-to-see and fewer than two vetoes exist.", decision.reason());
    }

    @Test
    void maximumFourIsBlockedByTwoVetoes() {
        GroupDecision decision = policy.decide(performanceAt(18, 0), ratings(4, 1, 1, 0));

        assertEquals(GroupDecisionStatus.BLOCKED, decision.status());
        assertEquals("Two or more vetoes block a want-to-see band.", decision.reason());
        assertEquals(2, decision.vetoCount());
    }

    @Test
    void maximumThreeGoesUnlessAnyVetoExists() {
        GroupDecision decision = policy.decide(performanceAt(18, 0), ratings(3, 2, 0));

        assertEquals(GroupDecisionStatus.GO, decision.status());
        assertEquals("Highest group rating is like and no veto exists.", decision.reason());
    }

    @Test
    void maximumThreeIsOptionalDuringLunchWindow() {
        GroupDecision decision = policy.decide(performanceAt(12, 30), ratings(3, 2, 0));

        assertEquals(GroupDecisionStatus.OPTIONAL, decision.status());
        assertEquals("Liked-but-missable performance occurs during the lunch window.", decision.reason());
    }

    @Test
    void maximumThreeIsBlockedByOneVeto() {
        GroupDecision decision = policy.decide(performanceAt(18, 0), ratings(3, 1, 0));

        assertEquals(GroupDecisionStatus.BLOCKED, decision.status());
        assertEquals("Any veto blocks a liked-but-missable band.", decision.reason());
        assertEquals(1, decision.vetoCount());
    }

    @Test
    void maximumTwoIsOptional() {
        GroupDecision decision = policy.decide(performanceAt(18, 0), ratings(2, 2, 0, 1));

        assertEquals(GroupDecisionStatus.OPTIONAL, decision.status());
        assertEquals("Highest group rating is OK or indifferent.", decision.reason());
        assertEquals(2, decision.maxRating());
    }

    @Test
    void onlyUnratedValuesRemainUnrated() {
        GroupDecision decision = policy.decide(performanceAt(18, 0), ratings(0, 0, 0));

        assertEquals(GroupDecisionStatus.UNRATED, decision.status());
        assertEquals("No group member has rated this band yet.", decision.reason());
        assertEquals(0, decision.maxRating());
        assertEquals(0, decision.vetoCount());
    }

    @Test
    void missingGroupRatingsAreTreatedAsUnrated() {
        GroupDecision decision = policy.decide(performanceAt(18, 0), List.of());

        assertEquals(GroupDecisionStatus.UNRATED, decision.status());
        assertEquals("No group member has rated this band yet.", decision.reason());
    }

    private Performance performanceAt(int hour, int minute) {
        LocalDateTime start = LocalDateTime.of(2026, 7, 30, hour, minute);
        return new Performance(
                new Band("5th Avenue"),
                new Stage("Faster Stage"),
                start,
                start.plusMinutes(60)
        );
    }

    private List<Rating> ratings(int... values) {
        return java.util.Arrays.stream(values)
                .mapToObj(Rating::of)
                .toList();
    }
}
