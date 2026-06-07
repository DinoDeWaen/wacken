package be.wacken.planner;

import org.junit.Test;

import java.util.List;
import java.util.OptionalInt;

import be.wacken.planner.application.BandListItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class SelectedBandScrollTargetTest {
    @Test
    public void findsOpenedBandAfterUnchangedRefresh() {
        OptionalInt index = SelectedBandScrollTarget.findIndex(
                "Skyline",
                List.of(band("5th Avenue", 0), band("Skyline", 3), band("Zeal")));

        assertEquals(1, index.orElseThrow());
    }

    @Test
    public void findsOpenedBandAfterRatingChanged() {
        OptionalInt index = SelectedBandScrollTarget.findIndex(
                "Skyline",
                List.of(band("5th Avenue", 0), band("Skyline", 5), band("Zeal")));

        assertEquals(1, index.orElseThrow());
    }

    @Test
    public void returnsEmptyWhenOpenedBandDisappearedAfterRefresh() {
        OptionalInt index = SelectedBandScrollTarget.findIndex(
                "Skyline",
                List.of(band("5th Avenue", 0), band("Zeal")));

        assertTrue(index.isEmpty());
    }

    private static BandListItem band(String name) {
        return band(name, 0);
    }

    private static BandListItem band(String name, int rating) {
        return new BandListItem(name, "TBA", "TBA", "TBA", rating, rating == 0);
    }
}
