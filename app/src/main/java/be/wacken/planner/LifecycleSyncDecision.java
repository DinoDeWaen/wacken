package be.wacken.planner;

record LifecycleSyncDecision(boolean renderCache, boolean startBackgroundSync) {
    static LifecycleSyncDecision onResume(boolean syncInProgress, boolean cacheRendered, boolean reloadNeeded) {
        boolean shouldRenderCache = reloadNeeded || !cacheRendered;
        return new LifecycleSyncDecision(shouldRenderCache, !syncInProgress);
    }
}
