package be.wacken.planner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.net.URL;
import java.util.Optional;

import be.wacken.planner.application.BandDetailItem;
import be.wacken.planner.application.MusicLinks;
import be.wacken.planner.application.RateBandResult;
import be.wacken.planner.application.RateBandUseCase;
import be.wacken.planner.application.RateRealBandUseCase;
import be.wacken.planner.application.ShowBandDetailUseCase;
import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.RealRatingRepository;

public final class BandDetailActivity extends Activity {
    public static final String EXTRA_BAND_NAME = "be.wacken.planner.BAND_NAME";
    public static final String EXTRA_YOUTUBE_URL = "be.wacken.planner.YOUTUBE_URL";
    public static final String EXTRA_SPOTIFY_URL = "be.wacken.planner.SPOTIFY_URL";
    public static final String EXTRA_STAGE = "be.wacken.planner.STAGE";
    public static final String EXTRA_DATE = "be.wacken.planner.DATE";
    public static final String EXTRA_TIME = "be.wacken.planner.TIME";

    private static final String TBA = "TBA";
    private static final int COLOR_BACKGROUND = WackenTheme.BACKGROUND;
    private static final int COLOR_GRID = WackenTheme.GRID;
    private static final int COLOR_TEXT = WackenTheme.TEXT;
    private static final int COLOR_ACCENT = WackenTheme.RED;

    private BandRepository bands;
    private RatingRepository ratings;
    private RealRatingRepository realRatings;
    private Band selectedBand;
    private RatingStarsView ratingStars;
    private RatingStarsView realRatingStars;
    private AuthSession currentSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentSession = new AuthSessionStore(this).load();
        if (!currentSession.isPresent()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        AppRepositories repositories = new AppRepositories(this);
        bands = repositories.bands();
        ratings = repositories.ratings();
        realRatings = repositories.realRatings();

        selectedBand = new Band(getIntent().getStringExtra(EXTRA_BAND_NAME));
        if (bands.findByName(selectedBand.name()).isEmpty()) {
            bands.save(selectedBand);
        }

        ShowBandDetailUseCase showBand = new ShowBandDetailUseCase(bands, ratings, realRatings);
        BandDetailItem detail = showBand.showBand(currentUser(), selectedBand.name(), musicLinksFromIntent())
                .orElseThrow(() -> new IllegalStateException("Band detail could not be loaded."));
        setContentView(render(detail));
    }

    private String currentUser() {
        return currentSession.userId();
    }

    private View render(BandDetailItem detail) {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(COLOR_BACKGROUND);

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.CENTER_HORIZONTAL);
        screen.setBackgroundColor(COLOR_BACKGROUND);
        int padding = dp(16);
        screen.setPadding(padding, padding, padding, padding);
        scroll.addView(screen, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT
        ));

        TextView title = new TextView(this);
        title.setText(detail.bandName());
        title.setTextColor(WackenTheme.AMBER);
        title.setTextSize(26);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setPadding(0, dp(8), 0, dp(10));
        screen.addView(title);

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(narrowScreen() ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
        main.setGravity(Gravity.CENTER);
        main.setPadding(dp(8), dp(8), dp(8), dp(8));
        screen.addView(main, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        detail.imageUrl().ifPresent(url -> main.addView(imagePanel(url)));

        LinearLayout detailSections = new LinearLayout(this);
        detailSections.setOrientation(LinearLayout.VERTICAL);
        detailSections.setGravity(Gravity.CENTER_HORIZONTAL);
        detailSections.setPadding(narrowScreen() ? 0 : dp(10), 0, 0, 0);
        detailSections.setLayoutParams(new LinearLayout.LayoutParams(
                narrowScreen() ? LinearLayout.LayoutParams.MATCH_PARENT : 0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                narrowScreen() ? 0 : 1
        ));
        main.addView(detailSections);

        detailSections.addView(ratingSection(detail));
        detailSections.addView(realRatingSection(detail));
        detailSections.addView(runningOrderSection());
        detailSections.addView(linksSection(detail));

        detail.biography().ifPresent(biography -> screen.addView(paragraphPanel(biography)));

        return scroll;
    }

    private LinearLayout ratingSection(BandDetailItem detail) {
        LinearLayout section = detailSection();
        section.addView(sectionTitle("Your Rating", false));
        ratingStars = new RatingStarsView(this, detail.rating(), !detail.defaultRating(), COLOR_ACCENT);
        ratingStars.setPadding(dp(12), 0, dp(12), 0);
        ratingStars.showAvailableRating();
        ratingStars.setOnRatingSelected(rating -> {
            RateBandResult result = new RateBandUseCase(ratings).rateBand(currentUser(), selectedBand, rating);
            if (result.success()) {
                ratingStars.applySavedRating(rating);
            }
        });
        LinearLayout.LayoutParams starsLayout = new LinearLayout.LayoutParams(dp(150), dp(42));
        starsLayout.gravity = Gravity.CENTER_HORIZONTAL;
        section.addView(ratingStars, starsLayout);
        Button clearRating = WackenTheme.actionButton(
                this,
                "Reset",
                WackenTheme.ButtonStyle.SECONDARY,
                null
        );
        clearRating.setContentDescription("Clear rating");
        clearRating.setTextSize(13);
        clearRating.setOnClickListener(view -> {
            RateBandResult result = new RateBandUseCase(ratings).rateBand(currentUser(), selectedBand, 0);
            if (result.success()) {
                ratingStars.applySavedRating(0);
            }
        });
        LinearLayout.LayoutParams resetLayout = new LinearLayout.LayoutParams(dp(96), dp(38));
        resetLayout.gravity = Gravity.CENTER_HORIZONTAL;
        resetLayout.setMargins(0, dp(2), 0, dp(4));
        section.addView(clearRating, resetLayout);
        section.addView(groupRatingsView(detail));
        return section;
    }

    private LinearLayout realRatingSection(BandDetailItem detail) {
        LinearLayout section = detailSection();
        section.addView(sectionTitle("Real Rating", false));
        realRatingStars = new RatingStarsView(this, detail.realRating(), !detail.defaultRealRating(), WackenTheme.AMBER);
        realRatingStars.setPadding(dp(12), 0, dp(12), 0);
        realRatingStars.showAvailableRating();
        realRatingStars.setOnRatingSelected(rating -> {
            RateBandResult result = new RateRealBandUseCase(realRatings).rateBand(currentUser(), selectedBand, rating);
            if (result.success()) {
                realRatingStars.applySavedRating(rating);
            }
        });
        LinearLayout.LayoutParams starsLayout = new LinearLayout.LayoutParams(dp(150), dp(42));
        starsLayout.gravity = Gravity.CENTER_HORIZONTAL;
        section.addView(realRatingStars, starsLayout);
        Button resetRealRating = WackenTheme.actionButton(
                this,
                "Reset",
                WackenTheme.ButtonStyle.SECONDARY,
                null
        );
        resetRealRating.setContentDescription("Reset real rating");
        resetRealRating.setTextSize(13);
        resetRealRating.setOnClickListener(view -> {
            RateBandResult result = new RateRealBandUseCase(realRatings).rateBand(currentUser(), selectedBand, 0);
            if (result.success()) {
                realRatingStars.applySavedRating(0);
            }
        });
        LinearLayout.LayoutParams resetLayout = new LinearLayout.LayoutParams(dp(96), dp(38));
        resetLayout.gravity = Gravity.CENTER_HORIZONTAL;
        resetLayout.setMargins(0, dp(2), 0, dp(4));
        section.addView(resetRealRating, resetLayout);
        return section;
    }

    private LinearLayout runningOrderSection() {
        LinearLayout section = detailSection();
        section.addView(sectionTitle("Running Order", false));
        section.addView(infoLine("Stage", valueExtra(EXTRA_STAGE)));
        section.addView(infoLine("Day", valueExtra(EXTRA_DATE)));
        section.addView(infoLine("Time", valueExtra(EXTRA_TIME)));
        return section;
    }

    private LinearLayout linksSection(BandDetailItem detail) {
        LinearLayout section = detailSection();
        section.addView(sectionTitle("Band Links", false));
        LinearLayout links = new LinearLayout(this);
        links.setOrientation(LinearLayout.HORIZONTAL);
        links.setGravity(Gravity.CENTER);
        links.addView(homeButton());
        detail.youtubeUrl().ifPresent(url -> links.addView(iconButton("▶", "Open YouTube", COLOR_ACCENT, url)));
        detail.spotifyUrl().ifPresent(url -> links.addView(iconButton("♬", "Open Spotify", WackenTheme.SUCCESS_GREEN, url)));
        section.addView(links);
        return section;
    }

    private ImageView imagePanel(String url) {
        ImageView image = new ImageView(this);
        image.setAdjustViewBounds(true);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setContentDescription("Band image");
        image.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, COLOR_GRID, 5));
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(dp(140), dp(140));
        layout.setMargins(0, 0, narrowScreen() ? 0 : dp(10), narrowScreen() ? dp(12) : 0);
        image.setLayoutParams(layout);
        loadImage(url, image);
        return image;
    }

    private void loadImage(String url, ImageView image) {
        new Thread(() -> {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new URL(url).openStream());
                runOnUiThread(() -> image.setImageBitmap(bitmap));
            } catch (Exception ignored) {
                runOnUiThread(() -> image.setVisibility(View.GONE));
            }
        }).start();
    }

    private LinearLayout detailSection() {
        LinearLayout section = new LinearLayout(this);
        section.setOrientation(LinearLayout.VERTICAL);
        section.setGravity(Gravity.CENTER_HORIZONTAL);
        section.setPadding(dp(12), dp(8), dp(12), dp(10));
        section.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, COLOR_GRID, 6));
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layout.setMargins(0, 0, 0, dp(10));
        section.setLayoutParams(layout);
        return section;
    }

    private TextView sectionTitle(String text, boolean spacious) {
        TextView title = new TextView(this);
        title.setText(text);
        title.setTextColor(WackenTheme.AMBER);
        title.setTextSize(18);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setPadding(0, spacious ? dp(16) : 0, 0, dp(6));
        return title;
    }

    private View paragraphPanel(String text) {
        FrameLayout panel = new FrameLayout(this);
        panel.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, COLOR_GRID, 6));
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layout.setMargins(0, dp(14), 0, 0);
        panel.setLayoutParams(layout);

        TextView paragraph = new TextView(this);
        paragraph.setText(text);
        paragraph.setTextColor(COLOR_TEXT);
        paragraph.setTextSize(15);
        paragraph.setGravity(Gravity.START);
        paragraph.setLineSpacing(dp(2), 1.0f);
        paragraph.setPadding(dp(14), dp(12), dp(14), dp(12));
        panel.addView(paragraph);
        return panel;
    }

    private TextView infoLine(String label, String value) {
        TextView line = new TextView(this);
        line.setText(label + ": " + value);
        line.setTextColor(COLOR_TEXT);
        line.setTextSize(15);
        line.setGravity(Gravity.CENTER_HORIZONTAL);
        line.setPadding(0, dp(3), 0, dp(3));
        return line;
    }

    private TextView groupRatingsView(BandDetailItem detail) {
        TextView ratings = new TextView(this);
        ratings.setText(detail.hasPersonRatings()
                ? "Group: " + detail.personRatingSummary()
                : "Group ratings: not synced yet");
        ratings.setTextColor(COLOR_TEXT);
        ratings.setTextSize(13);
        ratings.setGravity(Gravity.CENTER_HORIZONTAL);
        ratings.setPadding(0, dp(6), 0, dp(2));
        return ratings;
    }

    private Button homeButton() {
        Button button = baseIconButton("⌂", "Home", WackenTheme.WHITE);
        button.setOnClickListener(view -> finish());
        return button;
    }

    private Button iconButton(String icon, String description, int accentColor, String url) {
        Button button = baseIconButton(icon, description, accentColor);
        button.setOnClickListener(view -> ExternalLinks.open(this, url));
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

    private MusicLinks musicLinksFromIntent() {
        return new MusicLinks(
                Optional.ofNullable(getIntent().getStringExtra(EXTRA_YOUTUBE_URL)),
                Optional.ofNullable(getIntent().getStringExtra(EXTRA_SPOTIFY_URL))
        );
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private boolean narrowScreen() {
        return BandDetailLayoutPolicy.stacksSections(getResources().getConfiguration().screenWidthDp);
    }
}
