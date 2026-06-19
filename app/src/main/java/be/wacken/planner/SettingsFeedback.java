package be.wacken.planner;

final class SettingsFeedback {
    private SettingsFeedback() {
    }

    static String syncSuccess(String time, String pendingSummary) {
        return "Sync complete at " + time + ". " + pendingSummary + " Next: review your schedule.";
    }

    static String importSuccess() {
        return "Import complete. Existing ratings were preserved. Next: sync from Supabase.";
    }

    static String offlineRecovery(String action) {
        return action + " failed. Cached data remains usable offline. Next: retry "
                + action + " when a connection is available.";
    }
}
