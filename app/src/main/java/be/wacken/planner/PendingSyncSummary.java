package be.wacken.planner;

final class PendingSyncSummary {
    private final int pendingRatings;
    private final int pendingScheduleChoices;

    private PendingSyncSummary(int pendingRatings, int pendingScheduleChoices) {
        this.pendingRatings = Math.max(0, pendingRatings);
        this.pendingScheduleChoices = Math.max(0, pendingScheduleChoices);
    }

    static PendingSyncSummary of(int pendingRatings, int pendingScheduleChoices) {
        return new PendingSyncSummary(pendingRatings, pendingScheduleChoices);
    }

    int totalCount() {
        return pendingRatings + pendingScheduleChoices;
    }

    boolean hasPendingChanges() {
        return totalCount() > 0;
    }

    String description() {
        if (!hasPendingChanges()) {
            return "No pending changes.";
        }
        String total = totalCount() + " pending " + (totalCount() == 1 ? "change" : "changes");
        String ratings = pendingRatings == 0
                ? ""
                : pendingRatings + " " + (pendingRatings == 1 ? "rating" : "ratings");
        String choices = pendingScheduleChoices == 0
                ? ""
                : pendingScheduleChoices + " schedule " + (pendingScheduleChoices == 1 ? "choice" : "choices");
        String details = ratings.isEmpty() ? choices : choices.isEmpty() ? ratings : ratings + ", " + choices;
        return total + ": " + details + ".";
    }
}
