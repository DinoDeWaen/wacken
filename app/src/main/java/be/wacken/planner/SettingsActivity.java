package be.wacken.planner;

import android.app.Activity;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import androidx.core.content.FileProvider;

import be.wacken.planner.application.ExportRatingsCsvUseCase;

public final class SettingsActivity extends Activity {
    private static final int COLOR_BACKGROUND = WackenTheme.BACKGROUND;
    private static final int COLOR_TEXT = WackenTheme.TEXT;
    private static final int COLOR_MUTED = WackenTheme.MUTED;
    private static final int COLOR_ACCENT = WackenTheme.RED;
    private static final int COLOR_AMBER = WackenTheme.AMBER;
    private static final int REQUEST_IMPORT = 101;
    private static final DateTimeFormatter SYNC_TIME = DateTimeFormatter.ofPattern("HH:mm");

    private AuthSessionStore sessionStore;
    private AuthSession currentSession;
    private TextView status;
    private TextView ratingAllocation;
    private TextView syncIndicator;
    private Button syncButton;
    private ValueAnimator syncAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionStore = new AuthSessionStore(this);
        if (!loadCurrentSession()) {
            redirectToLogin();
            return;
        }
        setContentView(content());
    }

    private boolean loadCurrentSession() {
        currentSession = sessionStore.load();
        return currentSession.isPresent();
    }

    private void redirectToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private ScrollView content() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(COLOR_BACKGROUND);

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setBackgroundColor(COLOR_BACKGROUND);
        screen.setPadding(dp(16), dp(16), dp(16), dp(16));
        scroll.addView(screen);

        TextView title = new TextView(this);
        title.setText("Settings");
        title.setTextColor(COLOR_AMBER);
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        screen.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Group, import, and sync");
        subtitle.setTextColor(COLOR_MUTED);
        subtitle.setGravity(Gravity.CENTER_HORIZONTAL);
        subtitle.setPadding(0, dp(4), 0, dp(18));
        screen.addView(subtitle);

        LinearLayout groupSection = section("Group");
        groupSection.addView(infoText("Signed in as " + currentSession.email()));
        groupSection.addView(actionButton("Share group invite", WackenTheme.ButtonStyle.SECONDARY, view -> shareGroupInvite()));
        screen.addView(groupSection);

        LinearLayout ratingSection = section("Rating allocation");
        ratingAllocation = new TextView(this);
        ratingAllocation.setTextColor(COLOR_TEXT);
        ratingAllocation.setTextSize(15);
        ratingAllocation.setTypeface(Typeface.DEFAULT_BOLD);
        ratingAllocation.setGravity(Gravity.START);
        ratingAllocation.setPadding(0, dp(4), 0, 0);
        ratingSection.addView(ratingAllocation);
        ratingSection.addView(actionButton("Export ratings CSV", WackenTheme.ButtonStyle.PREMIUM, view -> exportRatingsCsv()));
        screen.addView(ratingSection);
        refreshRatingAllocation();

        LinearLayout syncSection = section("Sync");
        syncButton = actionButton("Sync from Supabase", WackenTheme.ButtonStyle.SECONDARY,
                view -> syncFromSupabase());
        syncSection.addView(syncButton);

        syncIndicator = new TextView(this);
        syncIndicator.setText("⚡");
        syncIndicator.setTextColor(COLOR_ACCENT);
        syncIndicator.setTextSize(36);
        syncIndicator.setTypeface(Typeface.DEFAULT_BOLD);
        syncIndicator.setGravity(Gravity.CENTER_HORIZONTAL);
        syncIndicator.setVisibility(android.view.View.GONE);
        syncSection.addView(syncIndicator);

        status = new TextView(this);
        status.setTextColor(COLOR_MUTED);
        status.setGravity(Gravity.START);
        status.setPadding(0, dp(8), 0, 0);
        refreshSyncStatus("Cached data", COLOR_MUTED);
        syncSection.addView(status);
        screen.addView(syncSection);

        LinearLayout adminSection = section("Admin");
        adminSection.addView(infoText("Imports update shared festival data. Ratings are preserved."));
        adminSection.addView(actionButton("Import lineup CSV files", WackenTheme.ButtonStyle.PREMIUM, view -> {
            startActivityForResult(new Intent(this, ImportCsvActivity.class), REQUEST_IMPORT);
        }));
        screen.addView(adminSection);

        screen.addView(actionButton("Back to bands", WackenTheme.ButtonStyle.SECONDARY, view -> finish()));
        return scroll;
    }

    @Override
    protected void onDestroy() {
        stopSyncAnimation();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_IMPORT || data == null) {
            return;
        }
        String feedback = data.getStringExtra(ImportCsvActivity.EXTRA_IMPORT_FEEDBACK);
        if (feedback == null || feedback.isBlank()) {
            return;
        }
        status.setTextColor(resultCode == RESULT_OK ? WackenTheme.SUCCESS_GREEN : COLOR_AMBER);
        status.setText(feedback);
    }

    private Button actionButton(String text, WackenTheme.ButtonStyle style, android.view.View.OnClickListener listener) {
        Button button = WackenTheme.actionButton(this, text, style, listener);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layout.setMargins(0, 0, 0, dp(10));
        button.setLayoutParams(layout);
        return button;
    }

    private LinearLayout section(String title) {
        LinearLayout section = new LinearLayout(this);
        section.setOrientation(LinearLayout.VERTICAL);
        section.setPadding(dp(12), dp(10), dp(12), dp(12));
        section.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 6));
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layout.setMargins(0, 0, 0, dp(12));
        section.setLayoutParams(layout);

        TextView heading = new TextView(this);
        heading.setText(title);
        heading.setTextColor(COLOR_AMBER);
        heading.setTextSize(15);
        heading.setTypeface(Typeface.DEFAULT_BOLD);
        heading.setPadding(0, 0, 0, dp(6));
        section.addView(heading);
        return section;
    }

    private TextView infoText(String text) {
        TextView info = new TextView(this);
        info.setText(text);
        info.setTextColor(COLOR_MUTED);
        info.setTextSize(13);
        info.setPadding(0, 0, 0, dp(8));
        return info;
    }

    private void shareGroupInvite() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, InviteShareText.subject());
        share.putExtra(Intent.EXTRA_TEXT, InviteShareText.message(currentSession.email()));
        startActivity(Intent.createChooser(share, "Share Wacken Planner invite"));
    }

    private void syncFromSupabase() {
        syncButton.setEnabled(false);
        startSyncAnimation();
        refreshSyncStatus("Syncing", COLOR_AMBER);
        new Thread(() -> {
            try {
                AppRepositories repositories = new AppRepositories(this);
                repositories.syncMasterDataFromSource();
                repositories.syncRatings();
                repositories.syncScheduleLocks();
                runOnUiThread(() -> {
                    syncButton.setEnabled(true);
                    stopSyncAnimation();
                    showStatus(SettingsFeedback.syncSuccess(
                            LocalTime.now().format(SYNC_TIME),
                            repositories.pendingSyncSummary().description()
                    ), WackenTheme.SUCCESS_GREEN);
                    refreshRatingAllocation();
                });
            } catch (Exception error) {
                runOnUiThread(() -> {
                    syncButton.setEnabled(true);
                    stopSyncAnimation();
                    if (!loadCurrentSession()) {
                        redirectToLogin();
                        return;
                    }
                    showStatus(SettingsFeedback.offlineRecovery("Sync"), COLOR_AMBER);
                });
            }
        }).start();
    }

    private void exportRatingsCsv() {
        try {
            AppRepositories repositories = new AppRepositories(this);
            String csv = new ExportRatingsCsvUseCase(
                    repositories.bands(),
                    repositories.performances(),
                    repositories.ratings(),
                    repositories.realRatings()
            ).export(currentSession.userId());
            File exportFile = writeExportFile(csv);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/csv");
            share.putExtra(Intent.EXTRA_SUBJECT, "Wacken ratings export");
            share.putExtra(Intent.EXTRA_STREAM, exportUri(exportFile));
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(share, "Export ratings CSV"));
            showStatus("Ratings CSV ready. Choose where to share or save it.", WackenTheme.SUCCESS_GREEN);
        } catch (Exception error) {
            showStatus("Export failed. Cached data remains unchanged.", COLOR_AMBER);
        }
    }

    private File writeExportFile(String csv) throws java.io.IOException {
        File directory = new File(getCacheDir(), "exports");
        if (!directory.exists() && !directory.mkdirs()) {
            throw new java.io.IOException("Export directory could not be created.");
        }
        File exportFile = new File(directory, "wacken-ratings.csv");
        try (FileOutputStream output = new FileOutputStream(exportFile)) {
            output.write(csv.getBytes(StandardCharsets.UTF_8));
        }
        return exportFile;
    }

    private Uri exportUri(File exportFile) {
        return FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", exportFile);
    }

    private void startSyncAnimation() {
        stopSyncAnimation();
        syncIndicator.setVisibility(android.view.View.VISIBLE);
        syncAnimator = ValueAnimator.ofFloat(0f, 360f);
        syncAnimator.setDuration(950);
        syncAnimator.setRepeatCount(ValueAnimator.INFINITE);
        syncAnimator.addUpdateListener(animation ->
                syncIndicator.setRotation((float) animation.getAnimatedValue())
        );
        syncAnimator.start();
    }

    private void stopSyncAnimation() {
        if (syncAnimator != null) {
            syncAnimator.cancel();
            syncAnimator = null;
        }
        if (syncIndicator != null) {
            syncIndicator.setRotation(0f);
            syncIndicator.setVisibility(android.view.View.GONE);
        }
    }

    private void refreshRatingAllocation() {
        if (ratingAllocation == null) {
            return;
        }
        try {
            AppRepositories repositories = new AppRepositories(this);
            ratingAllocation.setText(RatingAllocationSummary.format(
                    RatingAllocationSummary.countForUser(currentSession.userId(), repositories.ratings().findAll())
            ));
        } catch (Exception error) {
            ratingAllocation.setText(RatingAllocationSummary.format(
                    RatingAllocationSummary.countForUser(currentSession.userId(), java.util.List.of())
            ));
        }
    }

    private void refreshSyncStatus(String state, int color) {
        if (status == null) {
            return;
        }
        try {
            showStatus(state + " · " + new AppRepositories(this).pendingSyncSummary().description(), color);
        } catch (Exception error) {
            showStatus(state + " · Cached data remains available.", color);
        }
    }

    private void showStatus(String message, int color) {
        status.setTextColor(color);
        status.setText(message);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
