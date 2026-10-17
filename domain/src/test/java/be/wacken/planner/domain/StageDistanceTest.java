package be.wacken.planner.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StageDistanceTest {
    @Test
    void acceptsNonNegativeWalkingMinutesBetweenDifferentStages() {
        Stage faster = new Stage("Faster Stage");
        Stage harder = new Stage("Harder Stage");

        StageDistance distance = StageDistance.between(faster, harder, 12);

        assertEquals(faster, distance.from());
        assertEquals(harder, distance.to());
        assertEquals(12, distance.walkingMinutes());
    }

    @Test
    void rejectsNegativeWalkingMinutes() {
        Stage faster = new Stage("Faster Stage");
        Stage harder = new Stage("Harder Stage");

        DomainValidationException error = assertThrows(
                DomainValidationException.class,
                () -> StageDistance.between(faster, harder, -1)
        );

        assertEquals("Stage distance walking minutes must not be negative.", error.getMessage());
    }

    @Test
    void sameStageDistanceResolvesToZero() {
        Stage faster = new Stage("Faster Stage");

        StageDistance distance = StageDistance.between(faster, faster, 15);

        assertEquals(0, distance.walkingMinutes());
    }
}
