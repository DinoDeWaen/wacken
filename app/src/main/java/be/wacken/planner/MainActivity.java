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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import be.wacken.planner.application.BandListItem;
import be.wacken.planner.application.FestivalStartState;
import be.wacken.planner.application.ListActiveFestivalBandsUseCase;
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

    private ListView bandList;
    private TextView status;
    private TextView title;
    private TextView syncStatus;
    private TextView subtitle;
    private View tableHeader;
    private LinearLayout archiveList;
    private AuthSessionStore sessionStore;
    private AuthSession currentSession;
    private Button closeButton;
    private Button archiveButton;
    private Button deleteFestivalButton;
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
    private String query="";
    private boolean unratedOnly;
    private EditText search;
    private TextView resultsStatus;
    private Button clearSearch;
    private Button allFilter;
    private Button unratedFilter;
    private Button retrySync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppearanceController.attach(this);
        if (savedInstanceState != null) {
            pendingScrollBandName = savedInstanceState.getString(STATE_PENDING_SCROLL_BAND_NAME);
            query=savedInstanceState.getString("lineup_query","");
            unratedOnly=savedInstanceState.getBoolean("lineup_unrated");
        }
        sessionStore = new AuthSessionStore(this);
        if (!loadCurrentSession()) {
            redirectToLogin();
            return;
        }

        FrameLayout root = new FrameLayout(this);
        LinearLayout screen = contentView();
        LinearLayout chrome=new LinearLayout(this);
        chrome.setOrientation(LinearLayout.VERTICAL);
        LinearLayout footer=new LinearLayout(this);
        footer.setOrientation(LinearLayout.VERTICAL);

        chrome.addView(header());
        footer.addView(actionRow());
        syncStatus = new TextView(this);
        WackenTheme.textColor(syncStatus, COLOR_MUTED);
        syncStatus.setTextSize(13);
        syncStatus.setGravity(Gravity.CENTER_HORIZONTAL);
        syncStatus.setPadding(0, 0, 0, dp(10));
        footer.addView(syncStatus);
        tableHeader = lineupControls();
        chrome.addView(tableHeader);

        status = new TextView(this);
        WackenTheme.textColor(status, COLOR_MUTED);
        status.setGravity(Gravity.CENTER_HORIZONTAL);
        status.setPadding(0, dp(20), 0, dp(20));
        status.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        footer.addView(status);
        retrySync=WackenTheme.actionButton(this,"Retry sync",WackenTheme.ButtonStyle.SECONDARY,
                view->syncFromSupabase(false,"Refreshing cached bands...",false));
        retrySync.setVisibility(View.GONE);
        footer.addView(retrySync);

        archiveList = new LinearLayout(this);
        archiveList.setOrientation(LinearLayout.VERTICAL);
        archiveList.setVisibility(View.GONE);
        footer.addView(archiveList);

        bandList = new ListView(this);
        bandList.setId(R.id.band_lineup);
        bandList.setDivider(null);
        bandList.setCacheColorHint(COLOR_BACKGROUND);
        WackenTheme.backgroundColor(bandList, COLOR_BACKGROUND);
        bandList.addHeaderView(chrome,null,false);
        bandList.addFooterView(footer,null,false);
        screen.addView(bandList, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1
        ));

        root.addView(screen);
        root.addView(syncOverlay());
        setContentView(MainNavigation.wrap(this,root,MainNavigation.Destination.BANDS));
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
        if(pendingScrollBandName==null&&adapter!=null) {
            int index=Math.max(0,bandList.getFirstVisiblePosition()-bandList.getHeaderViewsCount());
            if(index<adapter.getCount())outState.putString(STATE_PENDING_SCROLL_BAND_NAME,adapter.getItem(index).bandName());
        }
        outState.putString("lineup_query",query);
        outState.putBoolean("lineup_unrated",unratedOnly);
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
        WackenTheme.backgroundColor(screen, COLOR_BACKGROUND);
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
        cachedBands = new ListActiveFestivalBandsUseCase(
                repositories.festivals(),
                repositories.festivalLineups(),
                repositories.performances(),
                repositories.ratings(),
                currentUser()
        ).listBands();
        if (cachedBands.isEmpty()) {
            cachedBands = new ListBandsUseCase(
                    repositories.bands(),
                    repositories.performances(),
                    repositories.ratings(),
                    currentUser()
            ).listBands();
        }
        cachedBandsByName = bandsByName(repositories);
        showFilteredBands(repositories);
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
        if (deleteFestivalButton != null) {
            deleteFestivalButton.setVisibility(content.showArchiveAction() ? View.VISIBLE : View.GONE);
        }
        if (addFestivalButton != null) {
            addFestivalButton.setVisibility(content.showAddFestivalAction() ? View.VISIBLE : View.GONE);
        }
        if (tableHeader != null) {
            tableHeader.setVisibility(content.showBandList() ? View.VISIBLE : View.GONE);
        }
        renderArchiveList(state, content.showAddFestivalAction());
    }

    private void renderArchiveList(FestivalStartState state, boolean showArchiveList) {
        if (archiveList == null) {
            return;
        }
        archiveList.removeAllViews();
        archiveList.setVisibility(showArchiveList ? View.VISIBLE : View.GONE);
        if (!showArchiveList) {
            return;
        }
        for (var festival : state.archivedFestivals()) {
            Button archive = WackenTheme.actionButton(
                    this,
                    festival.name(),
                    WackenTheme.ButtonStyle.SECONDARY,
                    view -> openArchivedFestival(festival.id())
            );
            archive.setContentDescription("Open archived festival " + festival.name());
            archive.setTextSize(13);
            LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layout.setMargins(0, 0, 0, dp(8));
            archiveList.addView(archive, layout);
        }
    }

    private void openArchivedFestival(String festivalId) {
        Intent intent = new Intent(this, ArchivedFestivalActivity.class);
        intent.putExtra(ArchivedFestivalActivity.EXTRA_FESTIVAL_ID, festivalId);
        startActivity(intent);
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
        WackenTheme.textColor(title, WackenTheme.AMBER);
        title.setTextSize(28);
        WackenTheme.heading(title);
        title.setGravity(Gravity.START);
        header.addView(title);

        subtitle = new TextView(this);
        subtitle.setText("Line-up ratings for my group");
        WackenTheme.textColor(subtitle, COLOR_MUTED);
        subtitle.setGravity(Gravity.START);
        header.addView(subtitle);
        header.addView(WackenTheme.appearanceToggle(this));

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
        actions.addView(archiveButton);
        deleteFestivalButton = topActionButton("Delete", "Delete active festival and schedule", WackenTheme.ButtonStyle.DANGER,
                view -> deleteActiveFestival());
        deleteFestivalButton.setTextSize(13);
        actions.addView(deleteFestivalButton);
        addFestivalButton = topActionButton("Add festival", "Add festival", WackenTheme.ButtonStyle.SECONDARY,
                view -> {
                    reloadNeeded = true;
                    Intent intent = new Intent(this, ImportCsvActivity.class);
                    intent.putExtra(ImportCsvActivity.EXTRA_ADD_FESTIVAL_MODE, true);
                    startActivity(intent);
                });
        addFestivalButton.setVisibility(View.GONE);
        actions.addView(addFestivalButton);
        actions.addView(topActionButton("Sync and exit", "Sync and exit", WackenTheme.ButtonStyle.DANGER,
                view -> syncFromSupabase(true, "Sealing scores before exit...")));
        return actions;
    }

    private void archiveActiveFestival() {
        archiveButton.setEnabled(false);
        status.setVisibility(View.VISIBLE);
        status.setText("Archiving festival...");
        new ArchiveFestivalAction(
                command -> new Thread(command, "archive-festival").start(),
                this::runOnUiThread,
                () -> new AppRepositories(this).archiveFestivalUseCase()
        ).execute(state -> {
            AppRepositories repositories = new AppRepositories(this);
            renderFestivalStartState(state);
            cachedBands = List.of();
            cachedBandsByName = Map.of();
            adapter = new BandAdapter(repositories, cachedBands, cachedBandsByName);
            bandList.setAdapter(adapter);
            loading = false;
            reloadNeeded = false;
            status.setVisibility(View.VISIBLE);
            refreshSyncStatus(repositories, "Cached data", COLOR_MUTED);
            archiveButton.setEnabled(true);
        }, error -> {
            status.setVisibility(View.VISIBLE);
            status.setText("Archive failed: " + error);
            archiveButton.setEnabled(true);
        });
    }

    private void deleteActiveFestival() {
        AppRepositories repositories = new AppRepositories(this);
        new ShowFestivalStartUseCase(repositories.festivals()).show().activeFestival()
                .ifPresent(festival -> DeleteFestivalDialog.show(this, festival, () -> {
                    loadBandList();
                    status.setVisibility(View.VISIBLE);
                    status.setText("Festival deleted for the group. Shared bands and personal history remain.");
                }, error -> {
                    status.setVisibility(View.VISIBLE);
                    status.setText("Delete failed: " + error);
                }));
    }

    private Button topActionButton(String icon, String description, WackenTheme.ButtonStyle style, View.OnClickListener listener) {
        Button button = WackenTheme.actionButton(this, icon, style, listener);
        button.setTextSize(14);
        button.setContentDescription(description);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
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
                        retrySync.setVisibility(View.GONE);
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
                    if (MissingFestivalRatingDialog.showIfPresent(this, error,
                            () -> syncFromSupabase(false, "Retrying repaired rating sync..."),
                            recoveryMessage -> {
                                status.setVisibility(View.VISIBLE);
                                status.setText(recoveryMessage);
                            })) {
                        refreshSyncStatus(new AppRepositories(this), "Review pending rating", WackenTheme.AMBER);
                        return;
                    }
                    if (reloadNeeded || adapter == null) {
                        loadBandList();
                    }
                    status.setVisibility(View.VISIBLE);
                    status.setText("Couldn’t refresh. Cached data and pending changes are kept. Retry when connected.");
                    retrySync.setVisibility(View.VISIBLE);
                    refreshSyncStatus(new AppRepositories(this), "Offline · showing cached data", WackenTheme.AMBER);
                });
            }
        }).start();
    }

    private void setSyncActionsEnabled(boolean enabled) {
        if(retrySync!=null)retrySync.setEnabled(enabled);
        if (closeButton != null) {
            closeButton.setEnabled(enabled);
        }
        if (archiveButton != null) {
            archiveButton.setEnabled(enabled);
        }
        if (deleteFestivalButton != null) {
            deleteFestivalButton.setEnabled(enabled);
        }
    }

    private void refreshSyncStatus(AppRepositories repositories, String state, int color) {
        if (syncStatus == null) {
            return;
        }
        WackenTheme.textColor(syncStatus,color);
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
        WackenTheme.backgroundColor(panel,WackenTheme.PANEL);

        TextView title = new TextView(this);
        title.setText("SYNC");
        WackenTheme.textColor(title,WackenTheme.TEXT);
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
        WackenTheme.textColor(syncOverlayMessage, COLOR_TEXT);
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

    private LinearLayout lineupControls() {
        LinearLayout controls=new LinearLayout(this);
        controls.setOrientation(LinearLayout.VERTICAL);
        TextView label=lineupText("Find a band",16,true);
        controls.addView(label);
        search=new EditText(this);
        search.setId(R.id.lineup_search);
        search.setSaveEnabled(false);
        search.setContentDescription("Find a band");
        search.setHint("Search the line-up");
        search.setSingleLine(true);
        search.setTextSize(16);
        search.setMinHeight(dp(48));
        WackenTheme.textColor(search,COLOR_TEXT);
        AppearanceController.bind(search,"search-hint",(view,palette)->((EditText)view).setHintTextColor(palette.muted()));
        search.setBackground(WackenTheme.panelBackground(this,WackenTheme.PANEL,COLOR_GRID,6));
        search.setPadding(dp(12),dp(8),dp(12),dp(8));
        search.setText(query);
        label.setLabelFor(R.id.lineup_search);
        controls.addView(search,new LinearLayout.LayoutParams(-1,-2));
        LinearLayout filters=new LinearLayout(this);
        filters.setOrientation(LinearLayout.HORIZONTAL);
        allFilter=WackenTheme.actionButton(this,"All bands",WackenTheme.ButtonStyle.SECONDARY,v->setUnratedFilter(false));
        unratedFilter=WackenTheme.actionButton(this,"Unrated",WackenTheme.ButtonStyle.SECONDARY,v->setUnratedFilter(true));
        for(Button button:new Button[]{allFilter,unratedFilter}) {
            button.setTextSize(14);
            LinearLayout.LayoutParams layout=new LinearLayout.LayoutParams(0,-2,1);
            layout.setMargins(dp(4),dp(8),dp(4),dp(8));
            filters.addView(button,layout);
        }
        controls.addView(filters);
        updateFilterSelection();
        resultsStatus=lineupText("",14,false);
        resultsStatus.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        controls.addView(resultsStatus);
        clearSearch=WackenTheme.actionButton(this,"Clear search and filters",WackenTheme.ButtonStyle.SECONDARY,v->{
            unratedOnly=false;updateFilterSelection();search.setText("");
        });
        clearSearch.setTextSize(14);
        controls.addView(clearSearch);
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence value,int start,int count,int after) { }
            @Override public void onTextChanged(CharSequence value,int start,int before,int count) {
                query=value.toString();
                if(cachedBands!=null)showFilteredBands(new AppRepositories(MainActivity.this));
            }
            @Override public void afterTextChanged(Editable value) { }
        });
        return controls;
    }

    private void setUnratedFilter(boolean selected) {
        unratedOnly=selected;
        updateFilterSelection();
        if(cachedBands!=null)showFilteredBands(new AppRepositories(this));
    }

    private void updateFilterSelection() {
        for(Button button:new Button[]{allFilter,unratedFilter}) {
            boolean selected=(button==unratedFilter)==unratedOnly;
            button.setSelected(selected);
            button.setContentDescription(button.getText()+(selected?", selected":""));
            button.setBackground(WackenTheme.panelBackground(this,selected?WackenTheme.ELEVATED_PANEL:WackenTheme.PANEL,
                    selected?WackenTheme.AMBER:COLOR_GRID,6));
        }
    }

    private void showFilteredBands(AppRepositories repositories) {
        List<BandListItem> visible=LineupPresentation.select(cachedBands,query,unratedOnly);
        if(adapter==null) {
            adapter=new BandAdapter(repositories,visible,cachedBandsByName);
            bandList.setAdapter(adapter);
        } else adapter.update(repositories,visible,cachedBandsByName);
        boolean filtered=!query.isEmpty()||unratedOnly;
        resultsStatus.setText(visible.isEmpty()&&filtered?"No bands found":visible.size()+" bands");
        clearSearch.setVisibility(filtered?View.VISIBLE:View.GONE);
    }

    private TextView lineupText(String text,int size,boolean bold) {
        TextView view=new TextView(this);
        view.setText(text);
        view.setTextSize(size);
        WackenTheme.textColor(view,COLOR_TEXT);
        view.setTypeface(bold?Typeface.DEFAULT_BOLD:Typeface.DEFAULT);
        view.setPadding(0,dp(4),0,dp(4));
        return view;
    }

    private void addMusicAction(LinearLayout actions,String label,String url) {
        Button button=WackenTheme.actionButton(this,label,WackenTheme.ButtonStyle.SECONDARY,v->ExternalLinks.open(this,url));
        button.setContentDescription("Open "+label);
        button.setTextSize(14);
        actions.addView(button,new LinearLayout.LayoutParams(-1,-2));
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
        OptionalInt targetIndex = SelectedBandScrollTarget.findIndex(pendingScrollBandName, adapter.bands);
        pendingScrollBandName = null;
        if (targetIndex.isPresent()) {
            bandList.post(() -> bandList.setSelection(targetIndex.getAsInt()+bandList.getHeaderViewsCount()));
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
        private AppRepositories repositories;
        private final List<BandListItem> bands;
        private Map<String, Band> bandsByName;

        BandAdapter(AppRepositories repositories, List<BandListItem> bands, Map<String, Band> bandsByName) {
            this.repositories = repositories;
            this.bands = new ArrayList<>(bands);
            this.bandsByName = bandsByName;
        }

        private void update(AppRepositories repositories,List<BandListItem> bands,Map<String,Band> bandsByName) {
            this.repositories=repositories;
            this.bandsByName=bandsByName;
            this.bands.clear();this.bands.addAll(bands);
            notifyDataSetChanged();
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
            LinearLayout row=new LinearLayout(MainActivity.this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(dp(16),dp(16),dp(16),dp(16));
            row.setLayoutParams(new ListView.LayoutParams(-1,-2));
            row.setMinimumHeight(dp(48));
            row.setClickable(true);row.setFocusable(true);
            TextView name=lineupText("",18,true);
            TextView meaning=lineupText("",14,false);
            TextView performance=lineupText("",14,false);
            RatingStarsView rating=new RatingStarsView(MainActivity.this,0,false,COLOR_ACCENT);
            rating.setLayoutParams(new LinearLayout.LayoutParams(-1,LinearLayout.LayoutParams.WRAP_CONTENT));
            LinearLayout actions=new LinearLayout(MainActivity.this);
            actions.setOrientation(LinearLayout.VERTICAL);
            row.addView(name);row.addView(meaning);row.addView(rating);row.addView(performance);row.addView(actions);
            return new RowHolder(row,name,meaning,rating,actions,performance);
        }

        private void bindRow(RowHolder holder,int position) {
            BandListItem band=getItem(position);
            holder.row.setBackground(WackenTheme.panelBackground(MainActivity.this,
                    position%2==0?COLOR_ROW_DARK:COLOR_ROW_LIGHT,COLOR_GRID,6));
            holder.row.setOnClickListener(view->{if(!loading)openBandDetail(bandsByName,band);});
            holder.name.setText(band.bandName());
            holder.meaning.setText(LineupPresentation.planningLabel(band.rating()));
            holder.rating.bind(band.rating(),band.explicitRating());
            holder.rating.setScope("Festival planning rating for "+title.getText());
            holder.rating.setOnRatingSelected(value->saveRating(band,value));
            holder.performance.setText(band.stageName()+" · "+band.displayDate()+" · "+band.displayTime());
            holder.actions.removeAllViews();
            Optional<Band> stored=Optional.ofNullable(bandsByName.get(band.bandName()));
            stored.flatMap(Band::youtubeUrl).ifPresent(url->addMusicAction(holder.actions,"YouTube",url));
            stored.flatMap(Band::spotifyUrl).ifPresent(url->addMusicAction(holder.actions,"Spotify",url));
            if(holder.actions.getChildCount()==0)holder.actions.addView(lineupText("Music links not available",14,false));
        }

        private void saveRating(BandListItem band,int selectedRating) {
            RateBandResult result=new RateBandUseCase(repositories.ratings()).rateBand(currentUser(),new Band(band.bandName()),selectedRating);
            if(result.success()) {
                List<BandListItem> updated=new ArrayList<>(cachedBands);
                for(int index=0;index<updated.size();index++)if(updated.get(index).bandName().equals(band.bandName())) {
                    updated.set(index,new BandListItem(band.bandName(),band.stageName(),band.startTime(),band.endTime(),selectedRating,false,
                            updatedPersonRatings(band,selectedRating)));
                }
                cachedBands=updated;
                pendingScrollBandName=band.bandName();
                showFilteredBands(repositories);
                restorePendingScrollTarget();
                refreshSyncStatus(repositories,"Saved on this device · pending sync",COLOR_MUTED);
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

    private record RowHolder(LinearLayout row,TextView name,TextView meaning,RatingStarsView rating,
                             LinearLayout actions,TextView performance) { }
}
