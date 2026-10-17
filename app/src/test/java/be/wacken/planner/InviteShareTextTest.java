package be.wacken.planner;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class InviteShareTextTest {
    @Test
    public void explainsInstallSignInSyncAndSingleGroupContext() {
        String message = InviteShareText.message("dino@example.test");

        assertTrue(message.contains("Sofie and Dino"));
        assertTrue(message.contains("Install the APK"));
        assertTrue(message.contains("provisioned Supabase account"));
        assertTrue(message.contains("one shared planning group only"));
        assertTrue(message.contains("Sync from Supabase"));
        assertTrue(message.contains("participate in the MVP2 schedule"));
    }

    @Test
    public void doesNotExposeSecretsOrTokenBasedInviteDetails() {
        String message = InviteShareText.message("dino@example.test").toLowerCase();

        assertFalse(message.contains("password"));
        assertFalse(message.contains("service_role"));
        assertFalse(message.contains("apikey"));
        assertFalse(message.contains("anon key"));
        assertFalse(message.contains("refresh token"));
        assertFalse(message.contains("invite token"));
        assertFalse(message.contains("wackenplanner://join"));
    }

    @Test
    public void doesNotImplyCreatingSeparateGroups() {
        String message = InviteShareText.message("dino@example.test").toLowerCase();

        assertFalse(message.contains("create a group"));
        assertFalse(message.contains("choose a group"));
        assertFalse(message.contains("switch groups"));
    }
}
