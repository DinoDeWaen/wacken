package be.wacken.planner;

final class ScheduleErrorMessage {
    private static final String RECOVERY_HINT = "Please sync from Supabase and try again.";

    private ScheduleErrorMessage() {
    }

    static String generationFailure(Throwable error) {
        return "Schedule could not be generated: " + userMessage(error);
    }

    static String lockLoadFailure(Throwable error) {
        return "Locked schedule choices could not be synced. Showing generated schedule. "
                + userMessage(error);
    }

    static String userMessage(Throwable error) {
        if (error == null) {
            return "Unexpected schedule error. " + RECOVERY_HINT;
        }
        String message = error.getMessage();
        if (message != null && !message.isBlank() && !"null".equalsIgnoreCase(message.trim())) {
            return message;
        }
        if (error instanceof NullPointerException) {
            return "Unexpected missing schedule data. " + RECOVERY_HINT;
        }
        return "Unexpected schedule error (" + error.getClass().getSimpleName() + "). " + RECOVERY_HINT;
    }
}
