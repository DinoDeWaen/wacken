package be.wacken.planner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.time.Clock;
import java.util.Optional;

import be.wacken.planner.application.BandDetailItem;
import be.wacken.planner.application.MusicLinks;
import be.wacken.planner.application.RateBandResult;
import be.wacken.planner.application.RateBandUseCase;
import be.wacken.planner.application.RecordFestivalAfterRatingUseCase;
import be.wacken.planner.application.ShowBandDetailUseCase;
import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.FestivalRepository;
import be.wacken.planner.domain.PersonalBandRatingHistoryRepository;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.RealRatingRepository;

public final class BandDetailActivity extends Activity {
    private final BandDetailPresentation presentation = new BandDetailPresentation(this);
    private final BandImageLoader<Bitmap> imageLoader =
            new BandImageLoader<>(BitmapFactory::decodeStream, this::runOnUiThread);
    public static final String EXTRA_BAND_NAME = "be.wacken.planner.BAND_NAME";
    public static final String EXTRA_YOUTUBE_URL = "be.wacken.planner.YOUTUBE_URL";
    public static final String EXTRA_SPOTIFY_URL = "be.wacken.planner.SPOTIFY_URL";
    public static final String EXTRA_STAGE = "be.wacken.planner.STAGE";
    public static final String EXTRA_DATE = "be.wacken.planner.DATE";
    public static final String EXTRA_TIME = "be.wacken.planner.TIME";

    private static final String TBA = "TBA";
    private static final int COLOR_BACKGROUND = WackenTheme.BACKGROUND;
    private static final int COLOR_TEXT = WackenTheme.TEXT;
    private static final int COLOR_ACCENT = WackenTheme.RED;

    private String selectedFestivalId;
    private be.wacken.planner.application.ViewBandRatingContextUseCase ratingContext;
    private TextView contextText;
    private BandRepository bands;
    private RatingRepository ratings;
    private RealRatingRepository realRatings;
    private FestivalRepository festivals;
    private PersonalBandRatingHistoryRepository personalRatings;
    private Band selectedBand;
    private RatingStarsView ratingStars;
    private RatingStarsView realRatingStars;
    private AuthSession currentSession;
    private LinearLayout musicLinksSection;
    private RecordFestivalAfterRatingUseCase afterRatingWriter;
    private be.wacken.planner.domain.FestivalAfterRatingRepository afterRatings;
    private boolean unresolvedLegacyRating;
    private boolean afterExpanded;
    private Button afterDisclosure;
    private LinearLayout afterBody;
    private TextView planningStatus;
    private Button clearPlanning;
    private Button clearAfter;
    private TextView afterValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppearanceController.attach(this);
        afterExpanded=savedInstanceState!=null&&savedInstanceState.getBoolean("afterExpanded");
        currentSession = new AuthSessionStore(this).load();
        if (!currentSession.isPresent()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        AppRepositories repositories = new AppRepositories(this);
        bands = repositories.bands();
        selectedFestivalId = savedInstanceState == null
                ? be.wacken.planner.domain.FestivalLifecycle.activeFestival(repositories.festivals().findAll())
                    .map(be.wacken.planner.domain.Festival::id).orElse("")
                : savedInstanceState.getString("selectedFestivalId", "");
        ratings = new be.wacken.planner.application.FestivalPlanningRatingView(repositories.festivals(),
                repositories.festivalLineups(), repositories.festivalPlanningRatings(), currentSession.groupId(), selectedFestivalId);
        realRatings = new be.wacken.planner.application.FestivalAfterRatingView(repositories.afterRatings(), selectedFestivalId);
        ratingContext = repositories.ratingContext();
        festivals = repositories.festivals();
        personalRatings = repositories.personalBandRatings();
        afterRatings = repositories.afterRatings();
        afterRatingWriter = new RecordFestivalAfterRatingUseCase(festivals, repositories.festivalLineups(), afterRatings, Clock.systemUTC());

        ShowBandDetailUseCase showBand = new ShowBandDetailUseCase(bands, ratings, realRatings, festivals, personalRatings);
        Optional<BandDetailItem> loaded = showBand.showBand(
                currentUser(), getIntent().getStringExtra(EXTRA_BAND_NAME), MusicLinks.none());
        if (loaded.isEmpty()) {
            setContentView(renderMissingBand());
            return;
        }
        BandDetailItem detail = loaded.orElseThrow();
        selectedBand = new Band(detail.bandName());
        unresolvedLegacyRating = repositories.hasUnresolvedLegacyRating(selectedBand.name());
        setContentView(render(detail));
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        state.putString("selectedFestivalId", selectedFestivalId);
        state.putBoolean("afterExpanded",afterExpanded);
        super.onSaveInstanceState(state);
    }

    private void refreshContext() {
        if (contextText != null && selectedBand != null) {
            ratingContext.show(currentUser(), selectedFestivalId, selectedBand.name())
                    .ifPresent(value -> contextText.setText(String.join("\n", value.lines())+"\nDevice sync: "
                            +new AppRepositories(this).pendingSyncSummary().description()));
        }
    }

    private String currentUser() {
        return currentSession.userId();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (selectedBand == null || musicLinksSection == null
                || !(musicLinksSection.getParent() instanceof android.view.ViewGroup parent)) return;
        // Refresh values and links in place, retaining the selected festival and scroll.
        new ShowBandDetailUseCase(bands, ratings, realRatings, festivals, personalRatings)
                .showBand(currentUser(), selectedBand.name(), MusicLinks.none()).ifPresent(detail -> {
                    ratingStars.applySavedRating(detail.rating());
                    realRatingStars.applySavedRating(detail.realRating());
                    afterValue.setText("After-rating: "+RatingValueText.describe(detail.realRating()));
                    planningStatus.setText("Planning: "+RatingValueText.describe(detail.rating()));
                    refreshContext();
                    LinearLayout previous = musicLinksSection;
                    int position = parent.indexOfChild(previous);
                    LinearLayout refreshed = linksSection(detail);
                    parent.removeView(previous);
                    parent.addView(refreshed, position);
                    refreshEditingAvailability();
                });
    }

    private View renderMissingBand() {
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.CENTER);
        screen.setPadding(dp(24), dp(24), dp(24), dp(24));
        WackenTheme.backgroundColor(screen, COLOR_BACKGROUND);

        TextView message = new TextView(this);
        message.setText("Band details are unavailable. Return to the previous screen and try syncing.");
        WackenTheme.textColor(message, COLOR_TEXT);
        message.setTextSize(18);
        message.setGravity(Gravity.CENTER);
        screen.addView(message);

        screen.addView(WackenTheme.actionButton(this, "Back", WackenTheme.ButtonStyle.SECONDARY, view -> finish()));
        return screen;
    }

    private View render(BandDetailItem detail) {
        contextText = presentation.context();
        refreshContext();
        java.util.List<View> sections = new java.util.ArrayList<>();
        sections.add(ratingSection(detail));
        sections.add(contextText);
        sections.add(afterDisclosureSection(detail));
        if (detail.hasPersonalRatingHistory()) sections.add(personalRatingHistorySection(detail));
        sections.add(runningOrderSection());
        sections.add(linksSection(detail));
        return presentation.render(detail, this::loadImage, sections);
    }

    private LinearLayout afterDisclosureSection(BandDetailItem detail) {
        LinearLayout disclosure=new LinearLayout(this);
        disclosure.setOrientation(LinearLayout.VERTICAL);
        afterDisclosure=WackenTheme.actionButton(this,"After the show",WackenTheme.ButtonStyle.SECONDARY,view->{
            afterExpanded=!afterExpanded;updateAfterDisclosure();
        });
        disclosure.addView(afterDisclosure,new LinearLayout.LayoutParams(-1,-2));
        afterBody=realRatingSection(detail);
        disclosure.addView(afterBody);
        updateAfterDisclosure();
        return disclosure;
    }

    private void updateAfterDisclosure() {
        afterBody.setVisibility(afterExpanded?View.VISIBLE:View.GONE);
        afterDisclosure.setSelected(afterExpanded);
        afterDisclosure.setContentDescription("After the show, "+(afterExpanded?"expanded":"collapsed"));
    }

    private String festivalName() {
        return festivals.findAll().stream().filter(f->f.id().equals(selectedFestivalId))
                .map(be.wacken.planner.domain.Festival::name).findFirst().orElse("No selected festival");
    }

    private boolean selectedFestivalActive() {
        return festivals.findAll().stream().anyMatch(f->f.id().equals(selectedFestivalId)&&f.isActive());
    }

    private void refreshEditingAvailability() {
        boolean editable=selectedFestivalActive();
        ratingStars.setEnabled(editable);realRatingStars.setEnabled(editable);
        clearPlanning.setEnabled(editable);clearAfter.setEnabled(editable);
        if(!editable)planningStatus.append("\nSelected festival is not active · read-only");
    }

    private LinearLayout ratingSection(BandDetailItem detail) {
        LinearLayout section = detailSection();
        section.addView(sectionTitle("Festival planning rating", false));
        section.addView(presentation.infoLine("Festival",festivalName()));
        planningStatus=presentation.infoLine("Planning",RatingValueText.describe(detail.rating()));
        planningStatus.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        section.addView(planningStatus);
        ratingStars = new RatingStarsView(this, detail.rating(), !detail.defaultRating(), COLOR_ACCENT);
        ratingStars.setId(R.id.planning_rating);
        ratingStars.setScope("Festival planning rating for "+festivalName());
        ratingStars.showAvailableRating();
        ratingStars.setOnRatingSelected(rating -> {
            RateBandResult result = savePlanningRating(rating);
            if (result.success()) {
                ratingStars.applySavedRating(rating);
                planningStatus.setText("Planning: "+RatingValueText.describe(rating)+"\nSaved on this device · pending sync");
                refreshContext();
            } else planningStatus.setText(result.validationMessage().orElse("Planning rating was not saved."));
        });
        LinearLayout.LayoutParams starsLayout = new LinearLayout.LayoutParams(-1,-2);
        starsLayout.gravity = Gravity.CENTER_HORIZONTAL;
        section.addView(ratingStars, starsLayout);
        Button clearRating = WackenTheme.actionButton(
                this,
                "Clear planning rating",
                WackenTheme.ButtonStyle.SECONDARY,
                null
        );
        clearPlanning=clearRating;
        clearRating.setContentDescription("Clear planning rating");
        clearRating.setTextSize(13);
        clearRating.setOnClickListener(view -> {
            RateBandResult result = savePlanningRating(0);
            if (result.success()) {
                ratingStars.applySavedRating(0);
                planningStatus.setText("Planning: Unrated\nSaved on this device · pending sync");
                refreshContext();
            }
        });
        LinearLayout.LayoutParams resetLayout = new LinearLayout.LayoutParams(-1,-2);
        resetLayout.gravity = Gravity.CENTER_HORIZONTAL;
        resetLayout.setMargins(0, dp(2), 0, dp(4));
        section.addView(clearRating, resetLayout);
        section.addView(groupRatingsView(detail));
        return section;
    }

    private LinearLayout realRatingSection(BandDetailItem detail) {
        LinearLayout section = detailSection();
        section.addView(sectionTitle("Festival after-rating", false));
        section.addView(presentation.infoLine("Festival",festivalName()));
        afterValue=presentation.infoLine("After-rating",RatingValueText.describe(detail.realRating()));
        section.addView(afterValue);
        TextView afterStatus = new TextView(this);
        WackenTheme.textColor(afterStatus, WackenTheme.MUTED);
        afterStatus.setContentDescription("Festival after-rating status");
        afterStatus.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        String festivalId = selectedFestivalId;
        boolean recovered = afterRatings.findByUserFestivalAndBand(currentUser(), festivalId, selectedBand)
                .map(be.wacken.planner.domain.FestivalAfterRating::requiresReview).orElse(false);
        afterStatus.setText(recovered ? "Recovered history: select the final after-rating again before archiving."
                : unresolvedLegacyRating ? "Legacy device ratings are retained with unresolved dates or ownership. Confirm your after-rating for this festival."
                : "After-rating for this festival. Your established band rating changes when the festival is archived.");
        section.addView(afterStatus);
        realRatingStars = new RatingStarsView(this, detail.realRating(), !detail.defaultRealRating(), WackenTheme.AMBER);
        realRatingStars.setId(R.id.after_rating);
        realRatingStars.setScope("Festival after-rating for "+festivalName());
        realRatingStars.showAvailableRating();
        realRatingStars.setOnRatingSelected(rating -> {
            RateBandResult result = saveAfterRating(festivalId, rating);
            if (result.success()) {
                realRatingStars.applySavedRating(rating);
                afterValue.setText("After-rating: "+RatingValueText.describe(rating));
                refreshContext();
                afterStatus.setText("After-rating saved on this device; pending sync.");
            } else {
                afterStatus.setText(result.validationMessage().orElse("After-rating was not saved."));
            }
        });
        LinearLayout.LayoutParams starsLayout = new LinearLayout.LayoutParams(-1,-2);
        starsLayout.gravity = Gravity.CENTER_HORIZONTAL;
        section.addView(realRatingStars, starsLayout);
        Button resetRealRating = WackenTheme.actionButton(
                this,
                "Clear after-rating",
                WackenTheme.ButtonStyle.SECONDARY,
                null
        );
        clearAfter=resetRealRating;
        resetRealRating.setContentDescription("Clear after-rating");
        resetRealRating.setTextSize(13);
        resetRealRating.setOnClickListener(view -> {
            RateBandResult result = saveAfterRating(festivalId, 0);
            if (result.success()) {
                realRatingStars.applySavedRating(0);
                afterValue.setText("After-rating: Unrated");
                refreshContext();
                afterStatus.setText("After-rating reset on this device; pending sync. Established band rating kept.");
            } else {
                afterStatus.setText(result.validationMessage().orElse("After-rating was not saved."));
            }
        });
        LinearLayout.LayoutParams resetLayout = new LinearLayout.LayoutParams(-1,-2);
        resetLayout.gravity = Gravity.CENTER_HORIZONTAL;
        resetLayout.setMargins(0, dp(2), 0, dp(4));
        section.addView(resetRealRating, resetLayout);
        return section;
    }

    private LinearLayout personalRatingHistorySection(BandDetailItem detail) {
        return presentation.history(detail);
    }

    private RateBandResult savePlanningRating(int value) {
        RateBandResult result;
        try {
            result = new RateBandUseCase(ratings).rateBand(currentUser(), selectedBand, value);
        } catch (RuntimeException error) {
            result = RateBandResult.failure("Planning rating could not be saved. Retry.");
        }
        if (!result.success()) android.widget.Toast.makeText(this,
                result.validationMessage().orElse("Rating was not saved."), android.widget.Toast.LENGTH_LONG).show();
        return result;
    }

    private RateBandResult saveAfterRating(String festivalId, int value) {
        try {
            return afterRatingWriter.rateBand(currentUser(), festivalId, selectedBand, value);
        } catch (RuntimeException error) {
            return RateBandResult.failure("After-rating could not be saved. Existing data was kept; retry.");
        }
    }

    private LinearLayout runningOrderSection() {
        return presentation.runningOrder(valueExtra(EXTRA_STAGE), valueExtra(EXTRA_DATE), valueExtra(EXTRA_TIME));
    }

    private LinearLayout linksSection(BandDetailItem detail) {
        LinearLayout section = presentation.links(detail, homeButton());
        musicLinksSection = section;
        // Canonical band lookup does not require a festival; captured archive context stays read-only.
        if(selectedFestivalId.isEmpty()||selectedFestivalActive())section.addView(WackenTheme.actionButton(this, "Find music links", WackenTheme.ButtonStyle.SECONDARY,
                view -> startActivity(new Intent(this, MusicLinksReviewActivity.class)
                        .putExtra(MusicLinksReviewActivity.EXTRA_BAND_NAME, detail.bandName()))));
        return section;
    }


    private void loadImage(String url, ImageView image) {
        imageLoader.load(url, bitmap -> {
            if (!isFinishing() && !isDestroyed()) image.setImageBitmap(bitmap);
        }, () -> {
            if (!isFinishing() && !isDestroyed()) image.setVisibility(View.GONE);
        });
    }

    @Override
    protected void onDestroy() {
        imageLoader.close();
        super.onDestroy();
    }

    private LinearLayout detailSection() {
        return presentation.section();
    }

    private TextView sectionTitle(String text, boolean spacious) {
        return presentation.sectionTitle(text, spacious);
    }


    private TextView infoLine(String label, String value) {
        return presentation.infoLine(label, value);
    }

    private TextView groupRatingsView(BandDetailItem detail) {
        return presentation.groupRatings(detail);
    }

    private Button homeButton() {
        Button button = WackenTheme.actionButton(this,"Back",WackenTheme.ButtonStyle.SECONDARY,view->finish());
        button.setContentDescription("Back");
        return button;
    }


    private Button baseIconButton(String icon, String description, int accentColor) {
        return WackenTheme.iconButton(this, icon, description, accentColor, 44, null);
    }

    private String valueExtra(String key) {
        String value = getIntent().getStringExtra(key);
        if (value == null || value.isBlank()) {
            return TBA;
        }
        return value;
    }

    private int dp(int value) {
        return WackenTheme.dp(this, value);
    }

}
