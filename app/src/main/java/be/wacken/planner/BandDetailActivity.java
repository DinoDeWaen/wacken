package be.wacken.planner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
import be.wacken.planner.application.ShowBandDetailUseCase;
import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.RatingRepository;

public final class BandDetailActivity extends Activity {
    public static final String EXTRA_BAND_NAME = "be.wacken.planner.BAND_NAME";
    public static final String EXTRA_YOUTUBE_URL = "be.wacken.planner.YOUTUBE_URL";
    public static final String EXTRA_SPOTIFY_URL = "be.wacken.planner.SPOTIFY_URL";
    public static final String EXTRA_STAGE = "be.wacken.planner.STAGE";
    public static final String EXTRA_DATE = "be.wacken.planner.DATE";
    public static final String EXTRA_TIME = "be.wacken.planner.TIME";

    private static final String TBA = "TBA";
    private static final int COLOR_BACKGROUND = Color.rgb(29, 36, 38);
    private static final int COLOR_PANEL = Color.rgb(32, 39, 41);
    private static final int COLOR_GRID = Color.rgb(67, 75, 78);
    private static final int COLOR_TEXT = Color.rgb(220, 224, 225);
    private static final int COLOR_ACCENT = Color.rgb(255, 56, 92);

    private BandRepository bands;
    private RatingRepository ratings;
    private Band selectedBand;
    private RatingStarsView ratingStars;
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

        selectedBand = new Band(getIntent().getStringExtra(EXTRA_BAND_NAME));
        if (bands.findByName(selectedBand.name()).isEmpty()) {
            bands.save(selectedBand);
        }

        ShowBandDetailUseCase showBand = new ShowBandDetailUseCase(bands, ratings);
        BandDetailItem detail = showBand.showBand(currentUser(), selectedBand.name(), musicLinksFromIntent())
                .orElseThrow();
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
        title.setTextColor(Color.WHITE);
        title.setTextSize(26);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setPadding(0, dp(8), 0, dp(10));
        screen.addView(title);

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.HORIZONTAL);
        main.setGravity(Gravity.CENTER);
        main.setPadding(0, dp(10), 0, dp(12));
        screen.addView(main, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        detail.imageUrl().ifPresent(url -> main.addView(imagePanel(url)));

        LinearLayout facts = new LinearLayout(this);
        facts.setOrientation(LinearLayout.VERTICAL);
        facts.setGravity(Gravity.CENTER_HORIZONTAL);
        facts.setPadding(dp(8), 0, 0, 0);
        facts.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        ));
        main.addView(facts);

        facts.addView(sectionTitle("Rating"));
        ratingStars = new RatingStarsView(this, detail.rating(), !detail.defaultRating(), COLOR_ACCENT);
        ratingStars.setPadding(dp(12), 0, dp(12), 0);
        ratingStars.setOnRatingSelected(rating -> {
            RateBandResult result = new RateBandUseCase(ratings).rateBand(currentUser(), selectedBand, rating);
            if (result.success()) {
                ratingStars.applySavedRating(rating);
            }
        });
        facts.addView(ratingStars, new LinearLayout.LayoutParams(dp(150), dp(42)));

        facts.addView(sectionTitle("Running Order"));
        facts.addView(infoLine("Stage", valueExtra(EXTRA_STAGE)));
        facts.addView(infoLine("Day", valueExtra(EXTRA_DATE)));
        facts.addView(infoLine("Time", valueExtra(EXTRA_TIME)));

        facts.addView(sectionTitle("Band Links"));
        LinearLayout links = new LinearLayout(this);
        links.setOrientation(LinearLayout.HORIZONTAL);
        links.setGravity(Gravity.CENTER);
        links.addView(homeButton());
        detail.youtubeUrl().ifPresent(url -> links.addView(iconButton("▶", "Open YouTube", COLOR_ACCENT, url)));
        detail.spotifyUrl().ifPresent(url -> links.addView(iconButton("♬", "Open Spotify", Color.rgb(30, 215, 96), url)));
        facts.addView(links);

        detail.biography().ifPresent(biography -> screen.addView(paragraph(biography)));

        return scroll;
    }

    private ImageView imagePanel(String url) {
        ImageView image = new ImageView(this);
        image.setAdjustViewBounds(true);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setContentDescription("Band image");
        image.setBackground(iconBackground(COLOR_GRID));
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(dp(140), dp(140));
        layout.setMargins(0, 0, dp(10), 0);
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

    private TextView sectionTitle(String text) {
        TextView title = new TextView(this);
        title.setText(text);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setPadding(0, dp(20), 0, dp(6));
        return title;
    }

    private TextView paragraph(String text) {
        TextView paragraph = new TextView(this);
        paragraph.setText(text);
        paragraph.setTextColor(COLOR_TEXT);
        paragraph.setTextSize(15);
        paragraph.setGravity(Gravity.CENTER_HORIZONTAL);
        paragraph.setLineSpacing(dp(2), 1.0f);
        paragraph.setPadding(0, dp(8), 0, dp(8));
        return paragraph;
    }

    private TextView infoLine(String label, String value) {
        TextView line = new TextView(this);
        line.setText(label + ": " + value);
        line.setTextColor(COLOR_TEXT);
        line.setTextSize(18);
        line.setGravity(Gravity.CENTER_HORIZONTAL);
        line.setPadding(0, dp(3), 0, dp(3));
        return line;
    }

    private Button homeButton() {
        Button button = baseIconButton("⌂", "Home", Color.WHITE);
        button.setOnClickListener(view -> finish());
        return button;
    }

    private Button iconButton(String icon, String description, int accentColor, String url) {
        Button button = baseIconButton(icon, description, accentColor);
        button.setOnClickListener(view -> ExternalLinks.open(this, url));
        return button;
    }

    private Button baseIconButton(String icon, String description, int accentColor) {
        Button button = new Button(this);
        button.setText(icon);
        button.setTextSize(22);
        button.setTextColor(Color.WHITE);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setContentDescription(description);
        button.setPadding(0, 0, 0, 0);
        button.setBackground(iconBackground(accentColor));
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(dp(44), dp(42));
        layout.setMargins(dp(4), 0, dp(4), 0);
        button.setLayoutParams(layout);
        return button;
    }

    private GradientDrawable iconBackground(int accentColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(COLOR_PANEL);
        drawable.setStroke(dp(1), accentColor == Color.WHITE ? COLOR_GRID : accentColor);
        drawable.setCornerRadius(dp(5));
        return drawable;
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
}
