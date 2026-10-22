package be.wacken.planner;

import android.app.Activity;
import android.app.AlertDialog;

import java.util.function.Consumer;

import be.wacken.planner.domain.Festival;

final class DeleteFestivalDialog {
    private DeleteFestivalDialog() { }

    static void show(Activity activity, Festival festival, Runnable onSuccess, Consumer<String> onFailure) {
        WackenTheme.dialog(new AlertDialog.Builder(activity)
                .setTitle("Delete " + festival.name() + "?")
                .setMessage("This removes the festival for everyone in the group, including its lineup, "
                        + "performances, schedule decisions, and planning ratings. "
                        + "Bands in the central catalog and personal rating history remain. "
                        + "This cannot be undone.")
                .setNegativeButton("Cancel", (dialog, which) -> { })
                .setPositiveButton("Delete festival", (dialog, which) -> {
                    AlertDialog progress = new AlertDialog.Builder(activity)
                            .setMessage("Deleting " + festival.name() + "...")
                            .setCancelable(false).create();
                    progress.show();WackenTheme.dialog(progress);
                    new Thread(() -> {
                            try {
                                new AppRepositories(activity).deleteFestival(festival.id());
                                activity.runOnUiThread(() -> {
                                    progress.dismiss();
                                    if(activity.isFinishing()||activity.isDestroyed())return;
                                    onSuccess.run();
                                });
                            } catch (Exception error) {
                                activity.runOnUiThread(() -> {
                                    progress.dismiss();
                                    if(activity.isFinishing()||activity.isDestroyed())return;
                                    onFailure.accept(error.getMessage() == null
                                            ? "Deletion failed. Try again." : error.getMessage());
                                });
                            }
                        }, "delete-festival").start();
                })
                .show(),WackenTheme.ButtonStyle.DANGER);
    }
}
