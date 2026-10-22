package be.wacken.planner;

import android.app.Activity;
import android.app.AlertDialog;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
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
import be.wacken.planner.application.RenameActiveFestivalResult;
import be.wacken.planner.application.RenameActiveFestivalUseCase;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalLifecycle;

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
    private AlertDialog renameDialog;
    private EditText renameInput;
    private Festival renameFestival;
    private String renameDraft;
    private boolean syncing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppearanceController.attach(this);
        sessionStore = new AuthSessionStore(this);
        if (!loadCurrentSession()) {
            redirectToLogin();
            return;
        }
        setContentView(MainNavigation.wrap(this,content(),MainNavigation.Destination.SETTINGS));
        if(savedInstanceState!=null){
            ScrollView scroll=findViewById(R.id.destination_content);
            scroll.post(()->scroll.scrollTo(0,savedInstanceState.getInt("settingsScroll")));
            String feedback=savedInstanceState.getString("feedback");
            if(savedInstanceState.getBoolean("syncPending"))showStatus("Previous sync could not be confirmed after screen recreation. Cached work and pending changes are kept. Retry sync when ready.",COLOR_AMBER);
            else if(feedback!=null)showStatus(feedback,COLOR_MUTED);
            if(savedInstanceState.getBoolean("renameOpen")){
                String id=savedInstanceState.getString("renameFestival");
                activeFestival().filter(festival->festival.id().equals(id)).ifPresentOrElse(festival->{
                    renameDraft=savedInstanceState.getString("renameDraft",festival.name());renameActiveFestival(festival);
                },()->showStatus("Festival context changed. Reopen rename for the current active festival; no rename was submitted by restoring this screen.",COLOR_AMBER));
            }
        }
    }

    @Override protected void onSaveInstanceState(Bundle state) {
        ScrollView scroll=findViewById(R.id.destination_content);
        if(scroll!=null)state.putInt("settingsScroll",scroll.getScrollY());
        if(status!=null)state.putString("feedback",status.getText().toString());state.putBoolean("syncPending",syncing);
        if(renameDialog!=null&&renameDialog.isShowing()){
            state.putBoolean("renameOpen",true);state.putString("renameFestival",renameFestival.id());
            state.putString("renameDraft",renameInput.getText().toString());
        }
        super.onSaveInstanceState(state);
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
        WackenTheme.backgroundColor(scroll, COLOR_BACKGROUND);

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        WackenTheme.backgroundColor(screen, COLOR_BACKGROUND);
        screen.setPadding(dp(16), dp(16), dp(16), dp(16));
        scroll.addView(screen);

        TextView title = new TextView(this);
        title.setText("Settings");
        WackenTheme.textColor(title, COLOR_AMBER);
        title.setTextSize(28);
        WackenTheme.heading(title);
        title.setGravity(Gravity.START);
        screen.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Group, import, and sync");
        WackenTheme.textColor(subtitle, COLOR_MUTED);
        subtitle.setGravity(Gravity.START);
        subtitle.setTextSize(14);
        subtitle.setPadding(0, dp(4), 0, dp(18));
        screen.addView(subtitle);

        LinearLayout appearanceSection = section("Appearance");
        appearanceSection.addView(infoText("System follows this device. Dark and Light stay selected until changed."));
        appearanceSection.addView(WackenTheme.appearanceChoices(this));
        appearanceSection.addView(WackenTheme.appearanceToggle(this));
        screen.addView(appearanceSection);

        LinearLayout groupSection = section("Group");
        groupSection.addView(infoText("Signed in as " + currentSession.email()));
        groupSection.addView(actionButton("Share group invite", WackenTheme.ButtonStyle.SECONDARY, view -> shareGroupInvite()));
        screen.addView(groupSection);

        LinearLayout adminSection = section("Festival administration");
        adminSection.addView(infoText("Imports update shared festival data. Ratings are preserved."));
        activeFestival().ifPresent(festival ->
                adminSection.addView(actionButton("Rename active festival", WackenTheme.ButtonStyle.SECONDARY, view -> renameActiveFestival(festival)))
        );
        adminSection.addView(actionButton("Link imported bands", WackenTheme.ButtonStyle.SECONDARY, view -> {
            startActivity(new Intent(this, BandLinkReviewActivity.class));
        }));
        activeFestival().ifPresent(festival -> adminSection.addView(actionButton("Find music links", WackenTheme.ButtonStyle.SECONDARY,
                view -> startActivity(new Intent(this, MusicLinksReviewActivity.class)
                        .putExtra(MusicLinksReviewActivity.EXTRA_FESTIVAL_ID, festival.id())))));
        adminSection.addView(actionButton("Fetch band metadata", WackenTheme.ButtonStyle.SECONDARY, view -> fetchBandMetadata()));
        adminSection.addView(actionButton("Import lineup CSV files", WackenTheme.ButtonStyle.PREMIUM, view -> {
            startActivityForResult(new Intent(this, ImportCsvActivity.class), REQUEST_IMPORT);
        }));
        screen.addView(adminSection);


        LinearLayout ratingSection = section("Rating allocation");
        ratingAllocation = new TextView(this);
        WackenTheme.textColor(ratingAllocation, COLOR_TEXT);
        ratingAllocation.setTextSize(16);
        ratingAllocation.setTypeface(Typeface.DEFAULT_BOLD);
        ratingAllocation.setGravity(Gravity.START);
        ratingAllocation.setPadding(0, dp(4), 0, 0);
        ratingSection.addView(ratingAllocation);
        ratingSection.addView(actionButton("Export ratings CSV", WackenTheme.ButtonStyle.PREMIUM, view -> exportRatingsCsv()));
        ratingSection.addView(actionButton("Export rating lifecycle CSV (v2)", WackenTheme.ButtonStyle.SECONDARY, view -> exportRatingLifecycleCsv()));
        ratingSection.addView(actionButton("Export archived correction audit CSV", WackenTheme.ButtonStyle.SECONDARY, view -> exportCorrectionsCsv()));
        screen.addView(ratingSection);
        refreshRatingAllocation();

        LinearLayout syncSection = section("Sync");
        syncButton = actionButton("Sync from Supabase", WackenTheme.ButtonStyle.SECONDARY,
                view -> syncFromSupabase());
        syncSection.addView(syncButton);

        syncIndicator = new TextView(this);
        syncIndicator.setText("⚡");
        WackenTheme.textColor(syncIndicator, COLOR_ACCENT);
        syncIndicator.setTextSize(36);
        syncIndicator.setTypeface(Typeface.DEFAULT_BOLD);
        syncIndicator.setGravity(Gravity.CENTER_HORIZONTAL);
        syncIndicator.setVisibility(android.view.View.GONE);
        syncSection.addView(syncIndicator);

        status = new TextView(this);
        status.setTextSize(14);
        status.setAccessibilityLiveRegion(android.view.View.ACCESSIBILITY_LIVE_REGION_POLITE);
        WackenTheme.textColor(status, COLOR_MUTED);
        status.setGravity(Gravity.START);
        status.setPadding(0, dp(8), 0, 0);
        refreshSyncStatus("Cached data", COLOR_MUTED);
        syncSection.addView(status);
        screen.addView(syncSection);


        screen.addView(actionButton("Back to bands", WackenTheme.ButtonStyle.SECONDARY,
                view -> MainNavigation.open(this,MainNavigation.Destination.BANDS)));
        return scroll;
    }

    @Override
    protected void onDestroy() {
        stopSyncAnimation();
        if(renameDialog!=null)renameDialog.dismiss();
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
        WackenTheme.textColor(status,resultCode == RESULT_OK ? WackenTheme.SUCCESS_GREEN : COLOR_AMBER);
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
        WackenTheme.textColor(heading, COLOR_AMBER);
        heading.setTextSize(18);
        WackenTheme.heading(heading);
        heading.setPadding(0, 0, 0, dp(6));
        section.addView(heading);
        return section;
    }

    private TextView infoText(String text) {
        TextView info = new TextView(this);
        info.setText(text);
        WackenTheme.textColor(info, COLOR_MUTED);
        info.setTextSize(14);
        info.setPadding(0, 0, 0, dp(8));
        return info;
    }

    private void shareGroupInvite() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, InviteShareText.subject());
        share.putExtra(Intent.EXTRA_TEXT, InviteShareText.message(currentSession.email()));
        try{startActivity(Intent.createChooser(share,"Share Wacken Planner invite"));showStatus("Invite ready. Choose a sharing app; cancelling keeps data unchanged.",COLOR_MUTED);}
        catch(android.content.ActivityNotFoundException error){showStatus("No sharing app is available. Group and account data are unchanged.",COLOR_AMBER);}
    }

    private void syncFromSupabase() {
        if(syncing)return;syncing=true;
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
                    if(isFinishing()||isDestroyed())return;
                    syncing=false;syncButton.setEnabled(true);
                    stopSyncAnimation();
                    showStatus(SettingsFeedback.syncSuccess(
                            LocalTime.now().format(SYNC_TIME),
                            repositories.pendingSyncSummary().description()
                    ), WackenTheme.SUCCESS_GREEN);
                    refreshRatingAllocation();
                });
            } catch (Exception error) {
                runOnUiThread(() -> {
                    if(isFinishing()||isDestroyed())return;
                    syncing=false;syncButton.setEnabled(true);
                    stopSyncAnimation();
                    if (!loadCurrentSession()) {
                        redirectToLogin();
                        return;
                    }
                    if (MissingFestivalRatingDialog.showIfPresent(this, error,
                            this::syncFromSupabase,
                            message -> showStatus(message, COLOR_AMBER))) {
                        return;
                    }
                    showStatus(SettingsFeedback.offlineRecovery("Sync"), COLOR_AMBER);
                });
            }
        }).start();
    }

    private void exportRatingLifecycleCsv() {
        try {
            AppRepositories repositories = new AppRepositories(this);
            String csv = new be.wacken.planner.application.ExportRatingLifecycleCsvUseCase(
                    repositories.bands(), repositories.festivals(), repositories.festivalLineups(),
                    repositories.festivalPlanningRatings(), repositories.afterRatings(), repositories.currentBandRatings(),
                    repositories.personalBandRatings(), repositories.ratingCorrectionHistory()).export(currentSession.userId());
            File file = writeExportFile(csv, "wacken-rating-lifecycle-v2.csv");
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/csv");
            share.putExtra(Intent.EXTRA_STREAM, exportUri(file));
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(share, "Export rating lifecycle CSV (v2)"));
        } catch (RuntimeException | java.io.IOException error) {
            showStatus("Lifecycle export failed. Cached data remains unchanged.", COLOR_AMBER);
        }
    }

    private void exportCorrectionsCsv() {
        try {
            String csv=new be.wacken.planner.application.ExportRatingCorrectionsCsvUseCase(
                    new AppRepositories(this).ratingCorrectionHistory()).export(currentSession.userId());
            File file=writeExportFile(csv,"wacken-rating-corrections.csv");
            Intent share=new Intent(Intent.ACTION_SEND);
            share.setType("text/csv");share.putExtra(Intent.EXTRA_SUBJECT,"Archived rating corrections");
            share.putExtra(Intent.EXTRA_STREAM,exportUri(file));share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(share,"Export archived correction audit CSV"));
            showStatus("Correction audit CSV ready. Original and correction dates are separate.",WackenTheme.SUCCESS_GREEN);
        } catch(Exception error) {
            showStatus("Audit export failed. Cached data remains unchanged.",COLOR_AMBER);
        }
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

    private java.util.Optional<Festival> activeFestival() {
        try {
            return FestivalLifecycle.activeFestival(new AppRepositories(this).festivals().findAll());
        } catch (Exception error) {
            return java.util.Optional.empty();
        }
    }

    private void renameActiveFestival(Festival festival) {
        EditText input = new EditText(this);
        input.setText(renameDraft==null?festival.name():renameDraft);renameDraft=null;
        renameInput=input;renameFestival=festival;
        input.setContentDescription("Festival name");
        input.setSingleLine(true);
        input.setSelectAllOnFocus(true);
        input.setHint("Festival name");
        WackenTheme.textColor(input, WackenTheme.TEXT);
        WackenTheme.reviewInput(input);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Rename active festival")
                .setView(input)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null)
                .create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            RenameActiveFestivalResult result;
            try {
                result = new RenameActiveFestivalUseCase(new AppRepositories(this).festivals())
                        .rename(input.getText().toString());
            } catch (RuntimeException error) {
                showStatus("Festival name was not changed group-wide. " + error.getMessage(), WackenTheme.RED);
                return;
            }
            if (result.success()) {
                showStatus(result.message(), WackenTheme.SUCCESS_GREEN);
                dialog.dismiss();
                return;
            }
            showStatus(result.message(), COLOR_AMBER);
            if ("Festival name must not be blank.".equals(result.message())) {
                input.setError("Festival name must not be blank.");
            }
        }));
        renameDialog=dialog;
        dialog.setOnDismissListener(ignored->{if(renameDialog==dialog){renameDialog=null;renameInput=null;renameFestival=null;}});
        dialog.show();WackenTheme.dialog(dialog);
    }

    private void fetchBandMetadata() {
        startActivity(new Intent(this, BandMetadataReviewActivity.class));
    }

    private File writeExportFile(String csv) throws java.io.IOException {
        return writeExportFile(csv,"wacken-ratings.csv");
    }

    private File writeExportFile(String csv,String fileName) throws java.io.IOException {
        File directory = new File(getCacheDir(), "exports");
        if (!directory.exists() && !directory.mkdirs()) {
            throw new java.io.IOException("Export directory could not be created.");
        }
        File exportFile = new File(directory, fileName);
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
        WackenTheme.textColor(status,color);
        status.setText(message);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
