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
            if (isMissingScheduleLockTable(message)) {
                return "Locked schedule choices are not available yet. Generated schedule is shown.";
            }
            return message;
        }
        if (error instanceof NullPointerException) {
            return "Unexpected missing schedule data. " + RECOVERY_HINT;
        }
        if ("NetworkOnMainThreadException".equals(error.getClass().getSimpleName())) {
            return "Schedule sync is still loading. Generated schedule is shown while locked choices sync.";
        }
        return "Unexpected schedule error (" + error.getClass().getSimpleName() + "). " + RECOVERY_HINT;
    }

    private static boolean isMissingScheduleLockTable(String message) {
        String normalized = message.toLowerCase();
        return normalized.contains("group_schedule_locks")
                && (normalized.contains("schema cache") || normalized.contains("could not find the table"));
    }
}
