package be.wacken.planner;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class SyncVisualPolicyTest {
    @Test
    public void usesFullSplashForInitialStartupSync() {
        assertEquals(SyncVisualMode.FULL_STARTUP_SPLASH, SyncVisualPolicy.mode(false, false));
    }

    @Test
    public void usesCompactOverlayAfterStartupSyncWasAttempted() {
        assertEquals(SyncVisualMode.COMPACT_OVERLAY, SyncVisualPolicy.mode(true, false));
    }

    @Test
    public void usesCompactOverlayForSyncAndExit() {
        assertEquals(SyncVisualMode.COMPACT_OVERLAY, SyncVisualPolicy.mode(false, true));
    }
}
