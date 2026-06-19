package be.wacken.planner;

import android.app.Activity;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class SettingsActivity extends Activity {
    private static final int COLOR_BACKGROUND = WackenTheme.BACKGROUND;
    private static final int COLOR_TEXT = WackenTheme.TEXT;
    private static final int COLOR_MUTED = WackenTheme.MUTED;
    private static final int COLOR_ACCENT = WackenTheme.RED;
    private static final int COLOR_AMBER = WackenTheme.AMBER;

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

    private LinearLayout content() {
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setBackgroundColor(COLOR_BACKGROUND);
        screen.setPadding(dp(16), dp(16), dp(16), dp(16));

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

        ratingAllocation = new TextView(this);
        ratingAllocation.setTextColor(COLOR_TEXT);
        ratingAllocation.setTextSize(15);
        ratingAllocation.setTypeface(Typeface.DEFAULT_BOLD);
        ratingAllocation.setGravity(Gravity.CENTER_HORIZONTAL);
        ratingAllocation.setPadding(0, 0, 0, dp(18));
        screen.addView(ratingAllocation);
        refreshRatingAllocation();

        screen.addView(actionButton("Share group invite", WackenTheme.ButtonStyle.SECONDARY, view -> shareGroupInvite()));
        screen.addView(actionButton("Import lineup CSV files", WackenTheme.ButtonStyle.PREMIUM, view -> {
            startActivity(new Intent(this, ImportCsvActivity.class));
        }));
        syncButton = actionButton("Sync from Supabase", WackenTheme.ButtonStyle.SECONDARY,
                view -> syncFromSupabase());
        screen.addView(syncButton);
        screen.addView(actionButton("Back to bands", WackenTheme.ButtonStyle.SECONDARY, view -> finish()));

        syncIndicator = new TextView(this);
        syncIndicator.setText("⚡");
        syncIndicator.setTextColor(COLOR_ACCENT);
        syncIndicator.setTextSize(36);
        syncIndicator.setTypeface(Typeface.DEFAULT_BOLD);
        syncIndicator.setGravity(Gravity.CENTER_HORIZONTAL);
        syncIndicator.setVisibility(android.view.View.GONE);
        screen.addView(syncIndicator);

        status = new TextView(this);
        status.setTextColor(COLOR_MUTED);
        status.setGravity(Gravity.CENTER_HORIZONTAL);
        status.setPadding(0, dp(18), 0, 0);
        screen.addView(status);
        return screen;
    }

    @Override
    protected void onDestroy() {
        stopSyncAnimation();
        super.onDestroy();
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
        status.setTextColor(COLOR_MUTED);
        status.setText("Forging latest ratings...");
        new Thread(() -> {
            try {
                AppRepositories repositories = new AppRepositories(this);
                repositories.syncMasterDataFromSource();
                repositories.syncRatings();
                repositories.syncScheduleLocks();
                runOnUiThread(() -> {
                    syncButton.setEnabled(true);
                    stopSyncAnimation();
                    status.setTextColor(COLOR_TEXT);
                    status.setText("Sync complete.");
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
                    status.setTextColor(COLOR_ACCENT);
                    status.setText("Supabase sync failed: " + error.getMessage());
                });
            }
        }).start();
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

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
