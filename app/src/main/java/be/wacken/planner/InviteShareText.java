package be.wacken.planner;

final class InviteShareText {
    static final String GROUP_NAME = "Sofie and Dino";
    private static final String APP_NAME = "Wacken Planner 2026";

    private InviteShareText() {
    }

    static String message(String inviterEmail) {
        String from = inviterEmail == null || inviterEmail.isBlank()
                ? ""
                : " from " + inviterEmail.trim();
        return "Join " + GROUP_NAME + " in " + APP_NAME + from + ".\n\n"
                + "Install the APK we shared, then sign in with your provisioned Supabase account.\n"
                + "This version has one shared planning group only: " + GROUP_NAME + ".\n"
                + "After you sign in, tap Sync from Supabase. Your band ratings will sync into the shared group and participate in the MVP2 schedule.";
    }

    static String subject() {
        return "Join " + GROUP_NAME + " in " + APP_NAME;
    }
}
