package be.wacken.planner.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class StageWalkingTimePolicyTest {
    @Test
    void heavyAndLouderAreFiveMinutesApart() {
        assertEquals(5, StageWalkingTimePolicy.defaultWalkingMinutes("Heavy", "Louder"));
        assertEquals(5, StageWalkingTimePolicy.defaultWalkingMinutes("Louder", "Heavy"));
    }

    @Test
    void heavyOrLouderToOtherStageIsFifteenMinutes() {
        assertEquals(15, StageWalkingTimePolicy.defaultWalkingMinutes("Heavy", "Wackinger Stage"));
        assertEquals(15, StageWalkingTimePolicy.defaultWalkingMinutes("Headbangers Stage", "Louder"));
    }

    @Test
    void otherStagesAreFiveMinutesApart() {
        assertEquals(5, StageWalkingTimePolicy.defaultWalkingMinutes("Wackinger Stage", "Headbangers Stage"));
    }

    @Test
    void sameStageIsZeroMinutes() {
        assertEquals(0, StageWalkingTimePolicy.defaultWalkingMinutes("Faster", "Faster"));
    }
}
