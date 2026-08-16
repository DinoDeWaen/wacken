package be.wacken.planner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import be.wacken.planner.application.BandDetailItem;
import be.wacken.planner.application.BiographyText;
import be.wacken.planner.application.PersonRatingStars;
import be.wacken.planner.application.PersonalRatingHistoryItem;
import be.wacken.planner.application.ViewArchivedFestivalHistoryUseCase;
import be.wacken.planner.domain.Band;

public final class ArchivedBandDetailActivity extends Activity {
    public static final String EXTRA_FESTIVAL_ID = "be.wacken.planner.ARCHIVED_FESTIVAL_ID";
    public static final String EXTRA_BAND_NAME = "be.wacken.planner.ARCHIVED_BAND_NAME";
    public static final String EXTRA_STAGE = "be.wacken.planner.ARCHIVED_STAGE";
    public static final String EXTRA_DATE = "be.wacken.planner.ARCHIVED_DATE";
    public static final String EXTRA_TIME = "be.wacken.planner.ARCHIVED_TIME";
    public static final String EXTRA_YOUTUBE_URL = "be.wacken.planner.ARCHIVED_YOUTUBE_URL";
    public static final String EXTRA_SPOTIFY_URL = "be.wacken.planner.ARCHIVED_SPOTIFY_URL";

    private static final String TBA = "TBA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AuthSession session = new AuthSessionStore(this).load();
        if (!session.isPresent()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        String festivalId = getIntent().getStringExtra(EXTRA_FESTIVAL_ID);
        String bandName = getIntent().getStringExtra(EXTRA_BAND_NAME);
        AppRepositories repositories = new AppRepositories(this);
        ViewArchivedFestivalHistoryUseCase.ArchivedFestivalHistory history = new ViewArchivedFestivalHistoryUseCase(
                repositories.festivals(),
                repositories.festivalLineups(),
                repositories.festivalPlanningRatings(),
                repositories.personalBandRatings(),
                repositories.realRatings()
        ).show(session.userId(), festivalId);
        Optional<Band> band = repositories.bands().findByName(bandName);
        setContentView(render(archivedDetail(session.userId(), bandName, band, history)));
    }

    private BandDetailItem archivedDetail(
            String userName,
            String bandName,
            Optional<Band> band,
            ViewArchivedFestivalHistoryUseCase.ArchivedFestivalHistory history
    ) {
        Optional<Integer> planningRating = history.planningRatings()
                .stream()
                .filter(item -> item.bandName().equalsIgnoreCase(bandName))
                .filter(item -> item.userName().equals(userName))
                .map(ViewArchivedFestivalHistoryUseCase.ArchivedPlanningRatingItem::rating)
                .findFirst();
        List<PersonRatingStars> groupPlanningRatings = history.planningRatings()
                .stream()
                .filter(item -> item.bandName().equalsIgnoreCase(bandName))
                .filter(item -> item.rating() > 0)
                .map(item -> new PersonRatingStars(item.userName(), item.rating()))
                .collect(Collectors.toList());
        List<PersonalRatingHistoryItem> bandHistory = history.personalRatings()
                .stream()
                .filter(item -> item.bandName().equalsIgnoreCase(bandName))
                .collect(Collectors.toList());
        Optional<PersonalRatingHistoryItem> festivalRealRating = bandHistory.stream()
                .filter(item -> item.festivalName().filter(history.festivalName()::equals).isPresent())
                .findFirst()
                .or(() -> bandHistory.stream().findFirst());
        return new BandDetailItem(
                bandName,
                band.flatMap(value -> BiographyText.readable(value.biography())),
                band.flatMap(Band::imageUrl),
                planningRating.orElse(0),
                planningRating.isEmpty() || planningRating.get() == 0,
                band.flatMap(Band::youtubeUrl).or(() -> Optional.ofNullable(getIntent().getStringExtra(EXTRA_YOUTUBE_URL))),
                band.flatMap(Band::spotifyUrl).or(() -> Optional.ofNullable(getIntent().getStringExtra(EXTRA_SPOTIFY_URL))),
                festivalRealRating.map(PersonalRatingHistoryItem::rating).orElse(0),
                festivalRealRating.isEmpty() || festivalRealRating.get().rating() == 0,
                groupPlanningRatings,
                bandHistory
        );
    }

    private View render(BandDetailItem detail) {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(WackenTheme.BACKGROUND);

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.CENTER_HORIZONTAL);
        screen.setBackgroundColor(WackenTheme.BACKGROUND);
        screen.setPadding(dp(16), dp(16), dp(16), dp(16));
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
        if (detail.hasPersonalRatingHistory()) {
            detailSections.addView(personalRatingHistorySection(detail));
        }
        detailSections.addView(runningOrderSection());
        detailSections.addView(linksSection(detail));

        detail.biography().ifPresent(biography -> screen.addView(paragraphPanel(biography)));
        return scroll;
    }

    private LinearLayout ratingSection(BandDetailItem detail) {
        LinearLayout section = detailSection();
        section.addView(sectionTitle("Your Rating"));
        section.addView(disabledStars(detail.rating(), !detail.defaultRating(), WackenTheme.RED));
        section.addView(disabledResetButton("Clear archived planning rating"));
        section.addView(groupRatingsView(detail));
        return section;
    }

    private LinearLayout realRatingSection(BandDetailItem detail) {
        LinearLayout section = detailSection();
        section.addView(sectionTitle("Real Rating"));
        section.addView(disabledStars(detail.realRating(), !detail.defaultRealRating(), WackenTheme.AMBER));
        section.addView(disabledResetButton("Reset archived real rating"));
        return section;
    }

    private RatingStarsView disabledStars(int value, boolean explicitRating, int color) {
        RatingStarsView stars = new RatingStarsView(this, value, explicitRating, color);
        stars.setEnabled(false);
        stars.setClickable(false);
        stars.setFocusable(false);
        stars.setPadding(dp(12), 0, dp(12), 0);
        stars.showAvailableRating();
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(dp(150), dp(42));
        layout.gravity = Gravity.CENTER_HORIZONTAL;
        stars.setLayoutParams(layout);
        return stars;
    }

    private Button disabledResetButton(String description) {
        Button reset = WackenTheme.actionButton(this, "Reset", WackenTheme.ButtonStyle.SECONDARY, null);
        reset.setEnabled(false);
        reset.setContentDescription(description);
        reset.setTextSize(13);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(dp(96), dp(38));
        layout.gravity = Gravity.CENTER_HORIZONTAL;
        layout.setMargins(0, dp(2), 0, dp(4));
        reset.setLayoutParams(layout);
        return reset;
    }

    private LinearLayout personalRatingHistorySection(BandDetailItem detail) {
        LinearLayout section = detailSection();
        section.addView(sectionTitle("Personal History"));
        for (PersonalRatingHistoryItem item : detail.personalRatingHistory()) {
            TextView line = new TextView(this);
            line.setText(item.displayText());
            line.setTextColor(WackenTheme.TEXT);
            line.setTextSize(14);
            line.setGravity(Gravity.CENTER_HORIZONTAL);
            line.setPadding(0, dp(3), 0, dp(3));
            section.addView(line);
        }
        return section;
    }

    private LinearLayout runningOrderSection() {
        LinearLayout section = detailSection();
        section.addView(sectionTitle("Running Order"));
        section.addView(infoLine("Stage", valueExtra(EXTRA_STAGE)));
        section.addView(infoLine("Day", valueExtra(EXTRA_DATE)));
        section.addView(infoLine("Time", valueExtra(EXTRA_TIME)));
        return section;
    }

    private LinearLayout linksSection(BandDetailItem detail) {
        LinearLayout section = detailSection();
        section.addView(sectionTitle("Band Links"));
        LinearLayout links = new LinearLayout(this);
        links.setOrientation(LinearLayout.HORIZONTAL);
        links.setGravity(Gravity.CENTER);
        links.addView(homeButton());
        detail.youtubeUrl().ifPresent(url -> links.addView(iconButton("▶", "Open YouTube", WackenTheme.RED, url)));
        detail.spotifyUrl().ifPresent(url -> links.addView(iconButton("♬", "Open Spotify", WackenTheme.SUCCESS_GREEN, url)));
        section.addView(links);
        return section;
    }

    private ImageView imagePanel(String url) {
        ImageView image = new ImageView(this);
        image.setAdjustViewBounds(true);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setContentDescription("Band image");
        image.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 5));
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
        section.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 6));
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layout.setMargins(0, 0, 0, dp(10));
        section.setLayoutParams(layout);
        return section;
    }

    private TextView sectionTitle(String text) {
        TextView title = new TextView(this);
        title.setText(text);
        title.setTextColor(WackenTheme.AMBER);
        title.setTextSize(18);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setPadding(0, 0, 0, dp(6));
        return title;
    }

    private View paragraphPanel(String text) {
        FrameLayout panel = new FrameLayout(this);
        panel.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 6));
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layout.setMargins(0, dp(14), 0, 0);
        panel.setLayoutParams(layout);

        TextView paragraph = new TextView(this);
        paragraph.setText(text);
        paragraph.setTextColor(WackenTheme.TEXT);
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
        line.setTextColor(WackenTheme.TEXT);
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
        ratings.setTextColor(WackenTheme.TEXT);
        ratings.setTextSize(13);
        ratings.setGravity(Gravity.CENTER_HORIZONTAL);
        ratings.setPadding(0, dp(6), 0, dp(2));
        return ratings;
    }

    private Button homeButton() {
        Button button = WackenTheme.iconButton(this, "⌂", "Back", WackenTheme.WHITE, 44, view -> finish());
        return button;
    }

    private Button iconButton(String icon, String description, int accentColor, String url) {
        return WackenTheme.iconButton(this, icon, description, accentColor, 44, view -> ExternalLinks.open(this, url));
    }

    private String valueExtra(String key) {
        String value = getIntent().getStringExtra(key);
        if (value == null || value.isBlank()) {
            return TBA;
        }
        return value;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private boolean narrowScreen() {
        return BandDetailLayoutPolicy.stacksSections(getResources().getConfiguration().screenWidthDp);
    }
}
