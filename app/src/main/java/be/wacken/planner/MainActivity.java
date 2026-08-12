package be.wacken.planner;

import android.app.Activity;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import be.wacken.planner.application.BandListItem;
import be.wacken.planner.application.ArchiveActiveFestivalUseCase;
import be.wacken.planner.application.FestivalStartState;
import be.wacken.planner.application.ListBandsUseCase;
import be.wacken.planner.application.PersonRatingStars;
import be.wacken.planner.application.RateBandResult;
import be.wacken.planner.application.RateBandUseCase;
import be.wacken.planner.application.ShowFestivalStartUseCase;
import be.wacken.planner.domain.Band;

public final class MainActivity extends Activity {
    private static final String STATE_PENDING_SCROLL_BAND_NAME = "pending_scroll_band_name";
    private static final int COLOR_BACKGROUND = WackenTheme.BACKGROUND;
    private static final int COLOR_ROW_DARK = WackenTheme.PANEL;
    private static final int COLOR_ROW_LIGHT = WackenTheme.ELEVATED_PANEL;
    private static final int COLOR_GRID = WackenTheme.GRID;
    private static final int COLOR_TEXT = WackenTheme.TEXT;
    private static final int COLOR_MUTED = WackenTheme.MUTED;
    private static final int COLOR_ACCENT = WackenTheme.RED;
    private static final int TABLE_HEADER_HEIGHT_DP = 44;
    private static final int BAND_ROW_HEIGHT_DP = 58;

    private ListView bandList;
    private TextView status;
    private TextView title;
    private TextView syncStatus;
    private TextView subtitle;
    private View tableHeader;
    private AuthSessionStore sessionStore;
    private AuthSession currentSession;
    private Button closeButton;
    private Button archiveButton;
    private Button scheduleButton;
    private Button addFestivalButton;
    private FrameLayout syncOverlay;
    private ImageView syncSplash;
    private View syncScrim;
    private TextView syncOverlayMessage;
    private MetalSyncView syncAnimation;
    private ValueAnimator syncAnimator;
    private BandAdapter adapter;
    private List<BandListItem> cachedBands;
    private Map<String, Band> cachedBandsByName;
    private boolean loading;
    private boolean reloadNeeded = true;
    private boolean syncAttempted;
    private boolean syncInProgress;
    private String pendingScrollBandName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            pendingScrollBandName = savedInstanceState.getString(STATE_PENDING_SCROLL_BAND_NAME);
        }
        sessionStore = new AuthSessionStore(this);
        if (!loadCurrentSession()) {
            redirectToLogin();
            return;
        }

        FrameLayout root = new FrameLayout(this);
        LinearLayout screen = contentView();

        screen.addView(header());
        screen.addView(actionRow());
        syncStatus = new TextView(this);
        syncStatus.setTextColor(COLOR_MUTED);
        syncStatus.setTextSize(13);
        syncStatus.setGravity(Gravity.CENTER_HORIZONTAL);
        syncStatus.setPadding(0, 0, 0, dp(10));
        screen.addView(syncStatus);
        tableHeader = tableHeader();
        screen.addView(tableHeader);

        status = new TextView(this);
        status.setTextColor(COLOR_MUTED);
        status.setGravity(Gravity.CENTER_HORIZONTAL);
        status.setPadding(0, dp(20), 0, dp(20));
        screen.addView(status);

        bandList = new ListView(this);
        bandList.setDivider(null);
        bandList.setCacheColorHint(COLOR_BACKGROUND);
        bandList.setBackgroundColor(COLOR_BACKGROUND);
        screen.addView(bandList, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1
        ));

        root.addView(screen);
        root.addView(syncOverlay());
        setContentView(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!loadCurrentSession()) {
            redirectToLogin();
            return;
        }
        LifecycleSyncDecision syncDecision = LifecycleSyncDecision.onResume(syncInProgress, adapter != null, reloadNeeded);
        if (syncDecision.renderCache()) {
            showLoadingState();
            bandList.post(() -> {
                loadBandList();
                if (syncDecision.startBackgroundSync()) {
                    syncFromSupabase(false, "Refreshing from Supabase...", false);
                }
            });
            return;
        }
        if (syncDecision.startBackgroundSync()) {
            syncFromSupabase(false, "Refreshing from Supabase...", false);
        }
    }

    @Override
    protected void onDestroy() {
        stopSyncAnimation();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_PENDING_SCROLL_BAND_NAME, pendingScrollBandName);
    }

    private boolean loadCurrentSession() {
        currentSession = sessionStore.load();
        return currentSession.isPresent();
    }

    private void redirectToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private String currentUser() {
        return currentSession.userId();
    }

    private LinearLayout contentView() {
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setBackgroundColor(COLOR_BACKGROUND);
        int padding = dp(14);
        screen.setPadding(padding, padding, padding, padding);
        return screen;
    }

    private void loadBandList() {
        AppRepositories repositories = new AppRepositories(this);
        FestivalStartState startState = new ShowFestivalStartUseCase(repositories.festivals()).show();
        renderFestivalStartState(startState);
        if (!startState.hasActiveFestival()) {
            cachedBands = List.of();
            cachedBandsByName = Map.of();
            adapter = new BandAdapter(repositories, cachedBands, cachedBandsByName);
            bandList.setAdapter(adapter);
            loading = false;
            reloadNeeded = false;
            status.setVisibility(View.VISIBLE);
            refreshSyncStatus(repositories, "Cached data", COLOR_MUTED);
            return;
        }
        cachedBands = new ListBandsUseCase(
                repositories.bands(),
                repositories.performances(),
                repositories.ratings(),
                currentUser()
        ).listBands();
        cachedBandsByName = bandsByName(repositories);
        adapter = new BandAdapter(repositories, cachedBands, cachedBandsByName);
        bandList.setAdapter(adapter);
        restorePendingScrollTarget();
        loading = false;
        reloadNeeded = false;
        status.setVisibility(cachedBands.isEmpty() ? View.VISIBLE : View.GONE);
        status.setText(cachedBands.isEmpty() ? getString(R.string.empty_band_list) : "");
        refreshSyncStatus(repositories, "Cached data", COLOR_MUTED);
    }

    private void renderFestivalStartState(FestivalStartState state) {
        FestivalStartScreenContent content = FestivalStartScreenContent.from(state, currentSession.email());
        title.setText(content.title());
        subtitle.setText(content.subtitle());
        status.setText(content.statusText());
        if (archiveButton != null) {
            archiveButton.setVisibility(content.showArchiveAction() ? View.VISIBLE : View.GONE);
        }
        if (addFestivalButton != null) {
            addFestivalButton.setVisibility(content.showAddFestivalAction() ? View.VISIBLE : View.GONE);
        }
        if (scheduleButton != null) {
            scheduleButton.setEnabled(content.showBandList());
        }
        if (tableHeader != null) {
            tableHeader.setVisibility(content.showBandList() ? View.VISIBLE : View.GONE);
        }
        if (bandList != null) {
            bandList.setVisibility(content.showBandList() ? View.VISIBLE : View.GONE);
        }
    }

    private void showLoadingState() {
        loading = true;
        status.setText("Loading bands...");
        status.setVisibility(View.VISIBLE);
    }

    private Map<String, Band> bandsByName(AppRepositories repositories) {
        Map<String, Band> bandsByName = new HashMap<>();
        for (Band band : repositories.bands().findAll()) {
            bandsByName.put(band.name(), band);
        }
        return bandsByName;
    }

    private LinearLayout header() {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setGravity(Gravity.CENTER_HORIZONTAL);
        header.setPadding(0, dp(8), 0, dp(18));

        title = new TextView(this);
        title.setText("Bands");
        title.setTextColor(WackenTheme.AMBER);
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.START);
        header.addView(title);

        subtitle = new TextView(this);
        subtitle.setText("Line-up ratings for my group");
        subtitle.setTextColor(COLOR_MUTED);
        subtitle.setGravity(Gravity.START);
        header.addView(subtitle);

        return header;
    }

    private LinearLayout actionRow() {
        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.CENTER_VERTICAL);
        actions.setPadding(0, 0, 0, dp(14));
        archiveButton = topActionButton("Archive", "Archive active festival", WackenTheme.ButtonStyle.DANGER,
                view -> archiveActiveFestival());
        archiveButton.setTextSize(13);
        archiveButton.setSingleLine(true);
        actions.addView(archiveButton);
        addFestivalButton = topActionButton("+", "Add festival", WackenTheme.ButtonStyle.SECONDARY,
                view -> status.setText("Add festival will be implemented in the next story."));
        addFestivalButton.setVisibility(View.GONE);
        actions.addView(addFestivalButton);
        actions.addView(topActionButton("⚙", "Settings", WackenTheme.ButtonStyle.SECONDARY,
                view -> startActivity(new Intent(this, SettingsActivity.class))));
        scheduleButton = topActionButton("📅", "Group schedule", WackenTheme.ButtonStyle.SECONDARY,
                view -> startActivity(new Intent(this, ScheduleActivity.class)));
        actions.addView(scheduleButton);
        actions.addView(topActionButton("⏻", "Sync and exit", WackenTheme.ButtonStyle.DANGER,
                view -> syncFromSupabase(true, "Sealing scores before exit...")));
        return actions;
    }

    private void archiveActiveFestival() {
        AppRepositories repositories = new AppRepositories(this);
        try {
            FestivalStartState state = new ArchiveActiveFestivalUseCase(repositories.festivals()).archiveActiveFestival();
            renderFestivalStartState(state);
            cachedBands = List.of();
            cachedBandsByName = Map.of();
            adapter = new BandAdapter(repositories, cachedBands, cachedBandsByName);
            bandList.setAdapter(adapter);
            loading = false;
            reloadNeeded = false;
            status.setVisibility(View.VISIBLE);
            refreshSyncStatus(repositories, "Cached data", COLOR_MUTED);
        } catch (RuntimeException error) {
            status.setVisibility(View.VISIBLE);
            status.setText(error.getMessage());
        }
    }

    private Button topActionButton(String icon, String description, WackenTheme.ButtonStyle style, View.OnClickListener listener) {
        Button button = WackenTheme.actionButton(this, icon, style, listener);
        button.setTextSize(22);
        button.setContentDescription(description);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                0,
                dp(48),
                1
        );
        layout.setMargins(dp(3), 0, dp(3), 0);
        button.setLayoutParams(layout);
        if ("Sync and exit".equals(description)) {
            closeButton = button;
        }
        return button;
    }

    private void syncFromSupabase(boolean closeAfterSync, String message) {
        syncFromSupabase(closeAfterSync, message, true);
    }

    private void syncFromSupabase(boolean closeAfterSync, String message, boolean showOverlay) {
        if (syncInProgress) {
            return;
        }
        SyncVisualMode visualMode = SyncVisualPolicy.mode(syncAttempted, closeAfterSync);
        syncAttempted = true;
        syncInProgress = true;
        setSyncActionsEnabled(false);
        status.setVisibility(View.VISIBLE);
        status.setText(message);
        refreshSyncStatus(new AppRepositories(this), "Syncing", WackenTheme.AMBER);
        if (showOverlay) {
            showSyncOverlay(message, visualMode);
        }
        new Thread(() -> {
            try {
                AppRepositories repositories = new AppRepositories(this);
                repositories.syncMasterDataFromSource();
                repositories.syncRatings();
                repositories.syncScheduleLocks();
                runOnUiThread(() -> {
                    syncInProgress = false;
                    setSyncActionsEnabled(true);
                    reloadNeeded = true;
                    if (closeAfterSync) {
                        hideSyncOverlay();
                        finishAndRemoveTask();
                    } else {
                        loadBandList();
                        refreshSyncStatus(repositories, "Up to date", WackenTheme.SUCCESS_GREEN);
                        if (showOverlay) {
                            hideSyncOverlay();
                        }
                    }
                });
            } catch (Exception error) {
                runOnUiThread(() -> {
                    syncInProgress = false;
                    setSyncActionsEnabled(true);
                    if (showOverlay) {
                        hideSyncOverlay();
                    }
                    if (!loadCurrentSession()) {
                        redirectToLogin();
                        return;
                    }
                    status.setVisibility(View.VISIBLE);
                    status.setText("Showing cached data. Supabase sync failed: " + error.getMessage());
                    if (reloadNeeded || adapter == null) {
                        loadBandList();
                    }
                    refreshSyncStatus(new AppRepositories(this), "Offline - cached data", WackenTheme.AMBER);
                });
            }
        }).start();
    }

    private void setSyncActionsEnabled(boolean enabled) {
        if (closeButton != null) {
            closeButton.setEnabled(enabled);
        }
        if (archiveButton != null) {
            archiveButton.setEnabled(enabled);
        }
        if (scheduleButton != null && bandList != null) {
            scheduleButton.setEnabled(enabled && bandList.getVisibility() == View.VISIBLE);
        }
    }

    private void refreshSyncStatus(AppRepositories repositories, String state, int color) {
        if (syncStatus == null) {
            return;
        }
        syncStatus.setTextColor(color);
        syncStatus.setText(state + " · " + repositories.pendingSyncSummary().description());
    }

    private FrameLayout syncOverlay() {
        syncOverlay = new FrameLayout(this);
        syncOverlay.setVisibility(View.GONE);
        syncOverlay.setClickable(true);

        syncSplash = new ImageView(this);
        syncSplash.setBackgroundColor(Color.BLACK);
        syncSplash.setImageResource(R.drawable.splash_dino_metal);
        syncSplash.setScaleType(ImageView.ScaleType.FIT_CENTER);
        syncOverlay.addView(syncSplash, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        syncScrim = new View(this);
        syncScrim.setBackgroundColor(Color.argb(56, 0, 0, 0));
        syncOverlay.addView(syncScrim, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.HORIZONTAL);
        panel.setGravity(Gravity.CENTER);
        panel.setPadding(dp(18), dp(16), dp(18), dp(16));
        panel.setBackgroundColor(Color.argb(188, 6, 8, 9));

        TextView title = new TextView(this);
        title.setText("SYNC");
        title.setTextColor(Color.WHITE);
        title.setTextSize(16);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        title.setShadowLayer(dp(6), 0, dp(1), COLOR_ACCENT);
        panel.addView(title);

        syncAnimation = new MetalSyncView(this);
        LinearLayout.LayoutParams animationLayout = new LinearLayout.LayoutParams(dp(72), dp(72));
        animationLayout.setMargins(dp(16), 0, dp(16), 0);
        panel.addView(syncAnimation, animationLayout);

        syncOverlayMessage = new TextView(this);
        syncOverlayMessage.setTextColor(COLOR_TEXT);
        syncOverlayMessage.setTextSize(14);
        syncOverlayMessage.setTypeface(Typeface.DEFAULT_BOLD);
        syncOverlayMessage.setGravity(Gravity.CENTER_VERTICAL);
        syncOverlayMessage.setMaxLines(2);
        syncOverlayMessage.setEllipsize(TextUtils.TruncateAt.END);
        panel.addView(syncOverlayMessage, new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        ));

        FrameLayout.LayoutParams panelLayout = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP
        );
        panelLayout.setMargins(0, dp(20), 0, 0);
        syncOverlay.addView(panel, panelLayout);
        return syncOverlay;
    }

    private void showSyncOverlay(String message, SyncVisualMode visualMode) {
        if (syncOverlay == null || syncAnimation == null) {
            return;
        }
        boolean fullSplash = visualMode == SyncVisualMode.FULL_STARTUP_SPLASH;
        if (syncSplash != null) {
            syncSplash.setVisibility(fullSplash ? View.VISIBLE : View.GONE);
        }
        if (syncScrim != null) {
            syncScrim.setVisibility(fullSplash ? View.VISIBLE : View.GONE);
        }
        syncOverlayMessage.setText(message);
        syncOverlay.setAlpha(0f);
        syncOverlay.setVisibility(View.VISIBLE);
        syncOverlay.animate().alpha(1f).setDuration(180).start();
        startSyncAnimation();
    }

    private void hideSyncOverlay() {
        if (syncOverlay == null) {
            return;
        }
        stopSyncAnimation();
        syncOverlay.animate()
                .alpha(0f)
                .setDuration(180)
                .withEndAction(() -> syncOverlay.setVisibility(View.GONE))
                .start();
    }

    private void startSyncAnimation() {
        stopSyncAnimation();
        syncAnimator = ValueAnimator.ofFloat(0f, 360f);
        syncAnimator.setDuration(950);
        syncAnimator.setRepeatCount(ValueAnimator.INFINITE);
        syncAnimator.addUpdateListener(animation -> {
            if (syncAnimation != null) {
                syncAnimation.setRotationDegrees((float) animation.getAnimatedValue());
            }
        });
        syncAnimator.start();
    }

    private void stopSyncAnimation() {
        if (syncAnimator != null) {
            syncAnimator.cancel();
            syncAnimator = null;
        }
    }

    private LinearLayout tableHeader() {
        LinearLayout header = tableRow(COLOR_BACKGROUND);
        header.addView(headerCell("Band", 2.45f));
        header.addView(headerCell("Rating", 1.75f));
        header.addView(headerCell("", 1.45f));
        header.addView(headerCell("Stage", 1.25f));
        header.addView(headerCell("Date", 0.95f));
        header.addView(headerCell("Time", 1.15f));
        return header;
    }

    private TextView headerCell(String text, float weight) {
        TextView cell = cell(text, Typeface.DEFAULT_BOLD, weight);
        cell.setTextColor(COLOR_TEXT);
        cell.setTextSize(13);
        cell.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, COLOR_GRID, 4));
        return cell;
    }

    private LinearLayout tableRow(int backgroundColor) {
        return tableRow(backgroundColor, TABLE_HEADER_HEIGHT_DP);
    }

    private LinearLayout tableRow(int backgroundColor, int heightDp) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setBackgroundColor(backgroundColor);
        row.setLayoutParams(new ListView.LayoutParams(
                ListView.LayoutParams.MATCH_PARENT,
                dp(heightDp)
        ));
        return row;
    }

    private TextView cell(String text, Typeface typeface, float weight) {
        TextView cell = new TextView(this);
        cell.setText(text);
        cell.setSingleLine(true);
        cell.setEllipsize(TextUtils.TruncateAt.END);
        cell.setTextColor(COLOR_TEXT);
        cell.setTextSize(12);
        cell.setTypeface(typeface);
        cell.setGravity(Gravity.CENTER_VERTICAL);
        cell.setPadding(dp(6), 0, dp(6), 0);
        cell.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, weight));
        return cell;
    }

    private LinearLayout bandNameCell(TextView name) {
        LinearLayout cell = new LinearLayout(this);
        cell.setOrientation(LinearLayout.VERTICAL);
        cell.setGravity(Gravity.CENTER_VERTICAL);
        cell.setPadding(dp(5), 0, dp(5), 0);
        cell.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 2.45f));

        name.setSingleLine(true);
        name.setEllipsize(TextUtils.TruncateAt.END);
        name.setTextColor(COLOR_TEXT);
        name.setTextSize(12);
        name.setTypeface(Typeface.DEFAULT_BOLD);

        cell.addView(name, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        return cell;
    }

    private LinearLayout rowActions(Optional<Band> band) {
        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.CENTER);
        actions.setPadding(dp(2), 0, dp(2), 0);
        actions.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.45f));
        band.flatMap(Band::youtubeUrl).ifPresent(url -> actions.addView(iconButton("▶", "Open YouTube", COLOR_ACCENT, url, dp(26))));
        band.flatMap(Band::spotifyUrl).ifPresent(url -> actions.addView(iconButton("♬", "Open Spotify", WackenTheme.SUCCESS_GREEN, url, dp(26))));
        return actions;
    }

    private Button iconButton(String icon, String description, int accentColor, String url, int size) {
        Button button = WackenTheme.iconButton(this, icon, description, accentColor, Math.max(26, Math.round(size / getResources().getDisplayMetrics().density)), view -> ExternalLinks.open(this, url));
        button.setTextSize(14);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(size, size);
        layout.setMargins(dp(1), 0, dp(1), 0);
        button.setLayoutParams(layout);
        return button;
    }

    private void openBandDetail(Map<String, Band> bandsByName, BandListItem band) {
        Intent intent = new Intent(this, BandDetailActivity.class);
        intent.putExtra(BandDetailActivity.EXTRA_BAND_NAME, band.bandName());
        intent.putExtra(BandDetailActivity.EXTRA_STAGE, band.stageName());
        intent.putExtra(BandDetailActivity.EXTRA_DATE, band.displayDate());
        intent.putExtra(BandDetailActivity.EXTRA_TIME, band.displayTime());

        Optional<Band> storedBand = Optional.ofNullable(bandsByName.get(band.bandName()));
        storedBand.flatMap(Band::youtubeUrl)
                .ifPresent(url -> intent.putExtra(BandDetailActivity.EXTRA_YOUTUBE_URL, url));
        storedBand.flatMap(Band::spotifyUrl)
                .ifPresent(url -> intent.putExtra(BandDetailActivity.EXTRA_SPOTIFY_URL, url));

        reloadNeeded = true;
        pendingScrollBandName = band.bandName();
        startActivity(intent);
    }

    private void restorePendingScrollTarget() {
        OptionalInt targetIndex = SelectedBandScrollTarget.findIndex(pendingScrollBandName, cachedBands);
        pendingScrollBandName = null;
        if (targetIndex.isPresent()) {
            bandList.post(() -> bandList.setSelection(targetIndex.getAsInt()));
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private static final class MetalSyncView extends View {
        private final Paint ring = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint inner = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint bolt = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF bounds = new RectF();
        private float rotationDegrees;

        MetalSyncView(Activity activity) {
            super(activity);
            ring.setStyle(Paint.Style.STROKE);
            ring.setStrokeWidth(activity.getResources().getDisplayMetrics().density * 7f);
            ring.setStrokeCap(Paint.Cap.ROUND);
            inner.setStyle(Paint.Style.FILL);
            inner.setColor(WackenTheme.BACKGROUND);
            bolt.setColor(WackenTheme.RED);
            bolt.setTextAlign(Paint.Align.CENTER);
            bolt.setTypeface(Typeface.DEFAULT_BOLD);
            bolt.setTextSize(activity.getResources().getDisplayMetrics().density * 54f);
        }

        void setRotationDegrees(float rotationDegrees) {
            this.rotationDegrees = rotationDegrees;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float size = Math.min(getWidth(), getHeight());
            float padding = ring.getStrokeWidth() + 4f;
            bounds.set(padding, padding, size - padding, size - padding);
            float center = size / 2f;
            ring.setShader(new SweepGradient(
                    center,
                    center,
                    new int[]{
                            WackenTheme.GRID,
                            Color.WHITE,
                            WackenTheme.STEEL_GREY,
                            WackenTheme.RED,
                            WackenTheme.GRID
                    },
                    null
            ));
            canvas.save();
            canvas.rotate(rotationDegrees, center, center);
            canvas.drawArc(bounds, 20, 280, false, ring);
            canvas.drawArc(bounds, 324, 42, false, ring);
            canvas.restore();

            inner.setShader(new SweepGradient(
                    center,
                    center,
                    WackenTheme.GRID,
                    WackenTheme.VOID
            ));
            canvas.drawCircle(center, center, size * 0.25f, inner);
            inner.setShader(null);
            bolt.setShadowLayer(12f, 0f, 0f, WackenTheme.RED);
            canvas.drawText("⚡", center, center + (bolt.getTextSize() * 0.34f), bolt);
        }
    }

    private final class BandAdapter extends BaseAdapter {
        private final AppRepositories repositories;
        private final List<BandListItem> bands;
        private final Map<String, Band> bandsByName;

        BandAdapter(AppRepositories repositories, List<BandListItem> bands, Map<String, Band> bandsByName) {
            this.repositories = repositories;
            this.bands = new ArrayList<>(bands);
            this.bandsByName = bandsByName;
        }

        @Override
        public int getCount() {
            return bands.size();
        }

        @Override
        public BandListItem getItem(int position) {
            return bands.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RowHolder holder;
            if (convertView == null) {
                holder = createRow();
                convertView = holder.row;
                convertView.setTag(holder);
            } else {
                holder = (RowHolder) convertView.getTag();
            }
            bindRow(holder, position);
            return convertView;
        }

        private RowHolder createRow() {
            LinearLayout row = tableRow(COLOR_ROW_DARK, BAND_ROW_HEIGHT_DP);
            row.setClickable(true);
            row.setFocusable(true);

            TextView name = new TextView(MainActivity.this);
            LinearLayout bandName = bandNameCell(name);
            RatingStarsView rating = new RatingStarsView(MainActivity.this, 0, false, COLOR_ACCENT);
            rating.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.75f));
            LinearLayout actions = new LinearLayout(MainActivity.this);
            actions.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.45f));
            TextView stage = cell("", Typeface.DEFAULT_BOLD, 1.25f);
            TextView date = cell("", Typeface.DEFAULT_BOLD, 0.95f);
            TextView time = cell("", Typeface.DEFAULT_BOLD, 1.15f);

            row.addView(bandName);
            row.addView(rating);
            row.addView(actions);
            row.addView(stage);
            row.addView(date);
            row.addView(time);
            return new RowHolder(row, name, rating, actions, stage, date, time);
        }

        private void bindRow(RowHolder holder, int position) {
            BandListItem band = getItem(position);
            holder.row.setBackgroundColor(position % 2 == 0 ? COLOR_ROW_DARK : COLOR_ROW_LIGHT);
            holder.row.setOnClickListener(view -> {
                if (!loading) {
                    openBandDetail(bandsByName, band);
                }
            });

            holder.name.setText(band.bandName());
            holder.rating.bind(band.rating(), band.explicitRating());
            holder.rating.setOnRatingSelected(selectedRating -> saveRating(position, band, holder.rating, selectedRating));
            holder.row.setOnHoverListener((view, event) -> {
                if (event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
                    holder.rating.showAvailableRating();
                }
                if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT && !holder.row.hasFocus()) {
                    holder.rating.restoreRestingState();
                }
                return false;
            });
            holder.row.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    holder.rating.showAvailableRating();
                } else {
                    holder.rating.restoreRestingState();
                }
            });

            holder.actions.removeAllViews();
            Optional<Band> storedBand = Optional.ofNullable(bandsByName.get(band.bandName()));
            LinearLayout actions = rowActions(storedBand);
            for (int index = 0; index < actions.getChildCount(); index++) {
                View child = actions.getChildAt(index);
                actions.removeView(child);
                holder.actions.addView(child);
                index--;
            }
            holder.actions.setOrientation(LinearLayout.HORIZONTAL);
            holder.actions.setGravity(Gravity.CENTER);
            holder.actions.setPadding(dp(2), 0, dp(2), 0);
            holder.actions.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.45f));

            holder.stage.setText(band.stageName());
            holder.date.setText(band.displayDate());
            holder.time.setText(band.displayTime());
        }

        private void saveRating(int position, BandListItem band, RatingStarsView rating, int selectedRating) {
            RateBandResult result = new RateBandUseCase(repositories.ratings())
                    .rateBand(currentUser(), new Band(band.bandName()), selectedRating);
            if (result.success()) {
                rating.applySavedRating(selectedRating);
                bands.set(position, new BandListItem(
                        band.bandName(),
                        band.stageName(),
                        band.startTime(),
                        band.endTime(),
                        selectedRating,
                        false,
                        updatedPersonRatings(band, selectedRating)
                ));
                notifyDataSetChanged();
            }
        }

        private List<PersonRatingStars> updatedPersonRatings(BandListItem band, int selectedRating) {
            List<PersonRatingStars> updated = new ArrayList<>();
            for (PersonRatingStars personRating : band.personRatings()) {
                if (!personRating.personName().equals(currentUser())) {
                    updated.add(personRating);
                }
            }
            if (selectedRating > 0) {
                updated.add(new PersonRatingStars(currentUser(), selectedRating));
            }
            updated.sort(Comparator.comparing(PersonRatingStars::personName, String.CASE_INSENSITIVE_ORDER));
            return updated;
        }
    }

    private record RowHolder(
            LinearLayout row,
            TextView name,
            RatingStarsView rating,
            LinearLayout actions,
            TextView stage,
            TextView date,
            TextView time
    ) {
    }
}
