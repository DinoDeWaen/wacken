package be.wacken.planner;

final class SyncVisualPolicy {
    private SyncVisualPolicy() {
    }

    static SyncVisualMode mode(boolean syncAlreadyAttempted, boolean closeAfterSync) {
        if (!syncAlreadyAttempted && !closeAfterSync) {
            return SyncVisualMode.FULL_STARTUP_SPLASH;
        }
        return SyncVisualMode.COMPACT_OVERLAY;
    }
}
