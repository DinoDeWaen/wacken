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
import be.wacken.planner.application.ListBandsUseCase;
import be.wacken.planner.application.PersonRatingStars;
import be.wacken.planner.application.RateBandResult;
import be.wacken.planner.application.RateBandUseCase;
import be.wacken.planner.domain.Band;

public final class MainActivity extends Activity {
    private static final String STATE_PENDING_SCROLL_BAND_NAME = "pending_scroll_band_name";
    private static final int COLOR_BACKGROUND = Color.rgb(29, 36, 38);
    private static final int COLOR_ROW_DARK = Color.rgb(32, 39, 41);
    private static final int COLOR_ROW_LIGHT = Color.rgb(41, 48, 50);
    private static final int COLOR_GRID = Color.rgb(67, 75, 78);
    private static final int COLOR_TEXT = Color.rgb(220, 224, 225);
    private static final int COLOR_MUTED = Color.rgb(162, 169, 171);
    private static final int COLOR_ACCENT = Color.rgb(255, 56, 92);
    private static final int TABLE_HEADER_HEIGHT_DP = 44;
    private static final int BAND_ROW_HEIGHT_DP = 58;

    private ListView bandList;
    private TextView status;
    private TextView subtitle;
    private AuthSessionStore sessionStore;
    private AuthSession currentSession;
    private Button closeButton;
    private FrameLayout syncOverlay;
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
        screen.addView(tableHeader());

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
        if (subtitle != null) {
            subtitle.setText("Line-up ratings for " + currentSession.email());
        }
        if (!syncInProgress) {
            syncFromSupabase(false, "Forging latest ratings...");
            return;
        }
        if (reloadNeeded || adapter == null) {
            showLoadingState();
            bandList.post(this::loadBandList);
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
        if (cachedBands.isEmpty() && !syncAttempted) {
            syncMasterDataFromSupabase();
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

        TextView title = new TextView(this);
        title.setText("Bands");
        title.setTextColor(COLOR_MUTED);
        title.setTextSize(24);
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
        actions.addView(topActionButton("⚙", "Settings", Color.rgb(78, 67, 50),
                view -> startActivity(new Intent(this, SettingsActivity.class))));
        actions.addView(topActionButton("📅", "Group schedule", Color.rgb(64, 76, 79),
                view -> startActivity(new Intent(this, ScheduleActivity.class))));
        actions.addView(topActionButton("⏻", "Sync and exit", Color.rgb(91, 27, 39),
                view -> syncFromSupabase(true, "Sealing scores before exit...")));
        return actions;
    }

    private Button topActionButton(String icon, String description, int color, View.OnClickListener listener) {
        Button button = new Button(this);
        button.setAllCaps(false);
        button.setText(icon);
        button.setTextSize(22);
        button.setContentDescription(description);
        button.setTextColor(Color.WHITE);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setBackgroundColor(color);
        button.setOnClickListener(listener);
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

    private void syncMasterDataFromSupabase() {
        syncFromSupabase(false, "Forging latest festival data...");
    }

    private void syncFromSupabase(boolean closeAfterSync, String message) {
        if (syncInProgress) {
            return;
        }
        syncAttempted = true;
        syncInProgress = true;
        setSyncActionsEnabled(false);
        status.setVisibility(View.VISIBLE);
        status.setText(message);
        showSyncOverlay(message);
        new Thread(() -> {
            try {
                AppRepositories repositories = new AppRepositories(this);
                repositories.syncMasterDataFromSource();
                repositories.syncRatings();
                runOnUiThread(() -> {
                    syncInProgress = false;
                    setSyncActionsEnabled(true);
                    reloadNeeded = true;
                    if (closeAfterSync) {
                        hideSyncOverlay();
                        finishAndRemoveTask();
                    } else {
                        loadBandList();
                        hideSyncOverlay();
                    }
                });
            } catch (Exception error) {
                runOnUiThread(() -> {
                    syncInProgress = false;
                    setSyncActionsEnabled(true);
                    hideSyncOverlay();
                    if (!loadCurrentSession()) {
                        redirectToLogin();
                        return;
                    }
                    status.setVisibility(View.VISIBLE);
                    status.setText("Showing cached data. Supabase sync failed: " + error.getMessage());
                    if (reloadNeeded || adapter == null) {
                        loadBandList();
                    }
                });
            }
        }).start();
    }

    private void setSyncActionsEnabled(boolean enabled) {
        if (closeButton != null) {
            closeButton.setEnabled(enabled);
        }
    }

    private FrameLayout syncOverlay() {
        syncOverlay = new FrameLayout(this);
        syncOverlay.setVisibility(View.GONE);
        syncOverlay.setClickable(true);

        ImageView splash = new ImageView(this);
        splash.setImageResource(R.drawable.splash_dino_metal);
        splash.setScaleType(ImageView.ScaleType.CENTER_CROP);
        syncOverlay.addView(splash, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        View scrim = new View(this);
        scrim.setBackgroundColor(Color.argb(56, 0, 0, 0));
        syncOverlay.addView(scrim, new FrameLayout.LayoutParams(
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

    private void showSyncOverlay(String message) {
        if (syncOverlay == null || syncAnimation == null) {
            return;
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
        cell.setBackground(columnHeaderBackground());
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
        cell.setPadding(dp(5), 0, dp(5), 0);
        cell.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, weight));
        return cell;
    }

    private LinearLayout bandNameCell(TextView name, TextView personRatings) {
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

        personRatings.setSingleLine(true);
        personRatings.setEllipsize(TextUtils.TruncateAt.END);
        personRatings.setTextColor(COLOR_MUTED);
        personRatings.setTextSize(9);

        cell.addView(name, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        cell.addView(personRatings, new LinearLayout.LayoutParams(
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
        band.flatMap(Band::spotifyUrl).ifPresent(url -> actions.addView(iconButton("♬", "Open Spotify", Color.rgb(30, 215, 96), url, dp(26))));
        return actions;
    }

    private Button iconButton(String icon, String description, int accentColor, String url, int size) {
        Button button = new Button(this);
        button.setText(icon);
        button.setTextSize(14);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setTextColor(Color.WHITE);
        button.setContentDescription(description);
        button.setPadding(0, 0, 0, 0);
        button.setBackground(iconBackground(accentColor));
        button.setOnClickListener(view -> ExternalLinks.open(this, url));
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(size, size);
        layout.setMargins(dp(1), 0, dp(1), 0);
        button.setLayoutParams(layout);
        return button;
    }

    private GradientDrawable iconBackground(int accentColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.rgb(49, 56, 58));
        drawable.setStroke(dp(1), accentColor);
        drawable.setCornerRadius(dp(4));
        return drawable;
    }

    private GradientDrawable columnHeaderBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(COLOR_BACKGROUND);
        drawable.setStroke(dp(1), COLOR_GRID);
        return drawable;
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
            inner.setColor(Color.rgb(29, 36, 38));
            bolt.setColor(Color.rgb(255, 56, 92));
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
                            Color.rgb(77, 83, 85),
                            Color.WHITE,
                            Color.rgb(132, 140, 143),
                            Color.rgb(255, 56, 92),
                            Color.rgb(77, 83, 85)
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
                    Color.rgb(84, 92, 95),
                    Color.rgb(14, 17, 18)
            ));
            canvas.drawCircle(center, center, size * 0.25f, inner);
            inner.setShader(null);
            bolt.setShadowLayer(12f, 0f, 0f, Color.rgb(255, 56, 92));
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
            TextView personRatings = new TextView(MainActivity.this);
            LinearLayout bandName = bandNameCell(name, personRatings);
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
            return new RowHolder(row, name, personRatings, rating, actions, stage, date, time);
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
            holder.personRatings.setText(band.personRatingSummary());
            holder.personRatings.setVisibility(band.hasPersonRatings() ? View.VISIBLE : View.GONE);
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
            TextView personRatings,
            RatingStarsView rating,
            LinearLayout actions,
            TextView stage,
            TextView date,
            TextView time
    ) {
    }
}
