package be.wacken.planner;

import android.app.Activity;
import android.app.AlertDialog;

import java.util.function.Consumer;

import be.wacken.planner.domain.SavedFestivalPlanningRating;

final class MissingFestivalRatingDialog {
    private MissingFestivalRatingDialog() { }

    static boolean showIfPresent(Activity activity, Throwable failure,
                                 Runnable retrySync, Consumer<String> feedback) {
        MissingFestivalPlanningRatingException missing = find(failure);
        if (missing == null || activity.isFinishing() || activity.isDestroyed()) {
            return false;
        }
        SavedFestivalPlanningRating rating = missing.rating();
        String detail = "Band: " + rating.band().name() + "\nRating: " + rating.rating().value()
                + " stars\nStored festival ID: " + rating.festivalId()
                + "\n\nIf this rating belongs to Wacken Open Air 2026,"
                + " confirm to move this one pending rating there, then retry sync."
                + " Existing Wacken ratings will not be overwritten.";
        feedback.accept("Pending rating needs review: " + rating.band().name()
                + " (" + rating.rating().value() + " stars), festival " + rating.festivalId() + ".");
        WackenTheme.dialog(new AlertDialog.Builder(activity)
                .setTitle("Review unsynced rating")
                .setMessage(detail)
                .setNegativeButton("Keep pending", (dialog, which) ->
                        feedback.accept("Rating kept on this device. Review it before syncing again."))
                .setNeutralButton("Discard pending", (dialog, which) ->
                        WackenTheme.dialog(new AlertDialog.Builder(activity)
                                .setTitle("Discard this pending rating?")
                                .setMessage("Band: " + rating.band().name() + "\nRating: "
                                        + rating.rating().value() + " stars\nStored festival ID: "
                                        + rating.festivalId()
                                        + "\n\nThis removes only this unsynced rating from this device. It cannot be undone.")
                                .setNegativeButton("Cancel", (confirmation, ignored) ->
                                        feedback.accept("Pending rating kept on this device."))
                                .setPositiveButton("Discard rating", (confirmation, ignored) -> {
                                    new Thread(() -> {
                                        try {
                                            new AppRepositories(activity).discardPendingRating(rating);
                                            activity.runOnUiThread(() -> {
                                                if(activity.isFinishing()||activity.isDestroyed())return;
                                                feedback.accept("Pending rating discarded. Retrying sync...");
                                                retrySync.run();
                                            });
                                        } catch (Exception error) {
                                            activity.runOnUiThread(() -> {if(!activity.isFinishing()&&!activity.isDestroyed())feedback.accept("Discard stopped: "
                                                    + error.getMessage() + " The pending rating remains on this device.");});
                                        }
                                    }, "discard-pending-rating").start();
                                })
                                .show(),WackenTheme.ButtonStyle.DANGER))
                .setPositiveButton("Move to Wacken 2026", (dialog, which) -> {
                    feedback.accept("Checking Wacken rating and repairing pending change...");
                    new Thread(() -> {
                        try {
                            new AppRepositories(activity).recoverPendingWackenRating(rating);
                            activity.runOnUiThread(() -> {
                                                if(activity.isFinishing()||activity.isDestroyed())return;
                                feedback.accept("Rating moved to Wacken 2026 locally. Retrying sync...");
                                retrySync.run();
                            });
                        } catch (Exception error) {
                            activity.runOnUiThread(() -> {if(!activity.isFinishing()&&!activity.isDestroyed())feedback.accept("Recovery stopped: "
                                    + error.getMessage() + " The pending rating remains on this device.");});
                        }
                    }, "recover-pending-rating").start();
                })
                .show());
        return true;
    }

    private static MissingFestivalPlanningRatingException find(Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause instanceof MissingFestivalPlanningRatingException missing) {
                return missing;
            }
        }
        return null;
    }
}
