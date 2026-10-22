package be.wacken.planner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class ScheduleLegendContentTest {
    @Test
    public void exposesEverySpecialScheduleStateInTheOnDemandKey() {
        String key = ScheduleLegendContent.description();

        assertTrue(key.contains("5-star must see"));
        assertTrue(key.contains("4-star strong choice"));
        assertTrue(key.contains("2-3-star optional"));
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
