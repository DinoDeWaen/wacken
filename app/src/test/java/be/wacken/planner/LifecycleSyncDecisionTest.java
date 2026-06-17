package be.wacken.planner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class LifecycleSyncDecisionTest {
    @Test
    public void rendersCacheBeforeStartingLifecycleSyncWhenContentNeedsReload() {
        LifecycleSyncDecision decision = LifecycleSyncDecision.onResume(false, false, true);

        assertTrue(decision.renderCache());
        assertTrue(decision.startBackgroundSync());
    }

    @Test
    public void refreshesInBackgroundWithoutReRenderingWhenCachedContentIsAlreadyVisible() {
        LifecycleSyncDecision decision = LifecycleSyncDecision.onResume(false, true, false);

        assertFalse(decision.renderCache());
        assertTrue(decision.startBackgroundSync());
    }

    @Test
    public void rendersCacheButDoesNotStartSecondSyncWhenSyncIsAlreadyRunning() {
        LifecycleSyncDecision decision = LifecycleSyncDecision.onResume(true, false, true);

        assertTrue(decision.renderCache());
        assertFalse(decision.startBackgroundSync());
    }
}
