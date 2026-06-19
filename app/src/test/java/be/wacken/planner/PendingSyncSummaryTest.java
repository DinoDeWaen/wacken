package be.wacken.planner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class PendingSyncSummaryTest {
    @Test
    public void describesCachedDataWhenNoChangesAreWaitingToSync() {
        PendingSyncSummary summary = PendingSyncSummary.of(0, 0);

        assertEquals(0, summary.totalCount());
        assertFalse(summary.hasPendingChanges());
        assertEquals("No pending changes.", summary.description());
    }

    @Test
    public void combinesPendingRatingAndScheduleChoiceCounts() {
        PendingSyncSummary summary = PendingSyncSummary.of(2, 1);

        assertEquals(3, summary.totalCount());
        assertTrue(summary.hasPendingChanges());
        assertEquals("3 pending changes: 2 ratings, 1 schedule choice.", summary.description());
    }

    @Test
    public void usesSingularWordingForOnePendingRating() {
        PendingSyncSummary summary = PendingSyncSummary.of(1, 0);

        assertEquals("1 pending change: 1 rating.", summary.description());
    }
}
