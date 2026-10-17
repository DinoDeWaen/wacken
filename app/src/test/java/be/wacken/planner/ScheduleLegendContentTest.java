package be.wacken.planner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class ScheduleLegendContentTest {
    @Test
    public void exposesEverySpecialScheduleStateInTheOnDemandKey() {
        String key = ScheduleLegendContent.description();

        assertTrue(key.contains("Gold border"));
        assertTrue(key.contains("Red border"));
        assertTrue(key.contains("Grey border"));
        assertTrue(key.contains("Scratched"));
        assertTrue(key.contains("locked group choice"));
        assertTrue(key.contains("barred overlaps"));
    }

    @Test
    public void usesDistinctLabelsForTheTwoScheduleFilters() {
        assertEquals("Hide barred overlaps", ScheduleLegendContent.hideBarredLabel());
        assertEquals("Hide ratings at/below", ScheduleLegendContent.ratingThresholdLabel());
    }
}
