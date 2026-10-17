package be.wacken.planner.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class StageWalkingTimePolicyTest {
    @Test
    void harderFasterAndLouderAreFiveMinutesApart() {
        assertEquals(5, StageWalkingTimePolicy.defaultWalkingMinutes("Harder", "Faster"));
        assertEquals(5, StageWalkingTimePolicy.defaultWalkingMinutes("Faster Stage", "Louder"));
        assertEquals(5, StageWalkingTimePolicy.defaultWalkingMinutes("Louder", "Harder Stage"));
    }

    @Test
    void headbangersAndWetAreFiveMinutesApart() {
        assertEquals(5, StageWalkingTimePolicy.defaultWalkingMinutes("Headbangers Stage", "W:E:T Stage"));
        assertEquals(5, StageWalkingTimePolicy.defaultWalkingMinutes("WET Stage", "Headbangers Stage"));
    }

    @Test
    void travelBetweenStageGroupsIsFifteenMinutes() {
        assertEquals(15, StageWalkingTimePolicy.defaultWalkingMinutes("Harder", "Headbangers Stage"));
        assertEquals(15, StageWalkingTimePolicy.defaultWalkingMinutes("W:E:T Stage", "Louder"));
        assertEquals(15, StageWalkingTimePolicy.defaultWalkingMinutes("Wackinger Stage", "Faster"));
        assertEquals(15, StageWalkingTimePolicy.defaultWalkingMinutes("Headbangers Stage", "Wackinger Stage"));
    }

    @Test
    void sameStageIsZeroMinutes() {
        assertEquals(0, StageWalkingTimePolicy.defaultWalkingMinutes("Faster", "Faster"));
    }
}
