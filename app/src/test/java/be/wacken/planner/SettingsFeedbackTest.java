package be.wacken.planner;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class SettingsFeedbackTest {
    @Test
    public void describesSuccessfulSyncWithTimeAndNextStep() {
        String message = SettingsFeedback.syncSuccess("18:55", "No pending changes.");

        assertTrue(message.contains("Sync complete at 18:55"));
        assertTrue(message.contains("No pending changes."));
        assertTrue(message.contains("Next: review your schedule."));
    }

    @Test
    public void explainsOfflineRecoveryAfterFailedSyncOrImport() {
        String message = SettingsFeedback.offlineRecovery("Sync");

        assertTrue(message.contains("Cached data remains usable offline."));
        assertTrue(message.contains("Next: retry Sync when a connection is available."));
    }
}
