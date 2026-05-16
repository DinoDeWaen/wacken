package be.wacken.planner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;
import java.util.Optional;

import be.wacken.planner.application.BandListItem;
import be.wacken.planner.application.ListBandsUseCase;
import be.wacken.planner.domain.Band;

public final class MainActivity extends Activity {
    private static final String CURRENT_USER = "my group";

    private LinearLayout bandList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setBackgroundColor(Color.rgb(12, 12, 12));
        int padding = dp(16);
        screen.setPadding(padding, padding, padding, padding);

        screen.addView(header());
        screen.addView(importButton());

        bandList = new LinearLayout(this);
        bandList.setOrientation(LinearLayout.VERTICAL);
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.rgb(12, 12, 12));
        scrollView.addView(bandList);
        screen.addView(scrollView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1
        ));

        setContentView(screen);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppRepositories repositories = new AppRepositories(this);
        List<BandListItem> bands = new ListBandsUseCase(
                repositories.bands(),
                repositories.performances(),
                repositories.ratings(),
                CURRENT_USER
        ).listBands();
        renderBandList(repositories, bands);
    }

    private void renderBandList(AppRepositories repositories, List<BandListItem> bands) {
        bandList.removeAllViews();
        if (bands.isEmpty()) {
            TextView emptyState = new TextView(this);
            emptyState.setText(getString(R.string.empty_band_list));
            emptyState.setTextColor(Color.LTGRAY);
            emptyState.setGravity(Gravity.CENTER_HORIZONTAL);
            emptyState.setPadding(0, dp(40), 0, 0);
            bandList.addView(emptyState);
            return;
        }

        for (BandListItem band : bands) {
            bandList.addView(bandCard(repositories, band));
        }
    }

    private LinearLayout header() {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setGravity(Gravity.CENTER_HORIZONTAL);
        header.setPadding(0, dp(8), 0, dp(18));

        TextView title = new TextView(this);
        title.setText("WACKEN PLANNER");
        title.setTextColor(Color.rgb(255, 199, 44));
        title.setTextSize(30);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        header.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Line-up ratings for my group");
        subtitle.setTextColor(Color.rgb(220, 220, 220));
        subtitle.setGravity(Gravity.CENTER_HORIZONTAL);
        header.addView(subtitle);

        return header;
    }

    private Button importButton() {
        Button importButton = new Button(this);
        importButton.setAllCaps(false);
        importButton.setText("Import lineup CSV files");
        importButton.setTextColor(Color.BLACK);
        importButton.setTypeface(Typeface.DEFAULT_BOLD);
        importButton.setBackgroundColor(Color.rgb(255, 199, 44));
        importButton.setOnClickListener(view -> startActivity(new Intent(this, ImportCsvActivity.class)));
        return importButton;
    }

    private LinearLayout bandCard(AppRepositories repositories, BandListItem band) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(12), dp(14), dp(12));
        card.setBackground(cardBackground());
        card.setOnClickListener(view -> openBandDetail(repositories, band));

        LinearLayout.LayoutParams cardLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardLayout.setMargins(0, dp(10), 0, 0);
        card.setLayoutParams(cardLayout);

        TextView name = new TextView(this);
        name.setText(band.bandName());
        name.setTextColor(Color.WHITE);
        name.setTextSize(22);
        name.setTypeface(Typeface.DEFAULT_BOLD);
        card.addView(name);

        TextView meta = new TextView(this);
        meta.setText(band.stageName() + " | " + band.startTime() + " - " + band.endTime());
        meta.setTextColor(Color.rgb(200, 200, 200));
        meta.setPadding(0, dp(4), 0, dp(8));
        card.addView(meta);

        TextView rating = new TextView(this);
        rating.setText(ratingText(band));
        rating.setTextColor(Color.rgb(255, 199, 44));
        rating.setTypeface(Typeface.DEFAULT_BOLD);
        card.addView(rating);

        return card;
    }

    private GradientDrawable cardBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.rgb(31, 31, 31));
        drawable.setStroke(dp(1), Color.rgb(255, 199, 44));
        drawable.setCornerRadius(dp(6));
        return drawable;
    }

    private String ratingText(BandListItem band) {
        return "Rating: " + starsFor(band.rating()) + (band.defaultRating() ? "  default" : "");
    }

    private String starsFor(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int selected = 0; selected < rating; selected++) {
            stars.append("★");
        }
        for (int unselected = rating; unselected < 4; unselected++) {
            stars.append("☆");
        }
        return stars.toString();
    }

    private void openBandDetail(AppRepositories repositories, BandListItem band) {
        Intent intent = new Intent(this, BandDetailActivity.class);
        intent.putExtra(BandDetailActivity.EXTRA_BAND_NAME, band.bandName());
        intent.putExtra(BandDetailActivity.EXTRA_RATING, band.rating());
        intent.putExtra(BandDetailActivity.EXTRA_DEFAULT_RATING, band.defaultRating());

        Optional<Band> storedBand = repositories.bands().findByName(band.bandName());
        storedBand.flatMap(Band::youtubeUrl)
                .ifPresent(url -> intent.putExtra(BandDetailActivity.EXTRA_YOUTUBE_URL, url));
        storedBand.flatMap(Band::spotifyUrl)
                .ifPresent(url -> intent.putExtra(BandDetailActivity.EXTRA_SPOTIFY_URL, url));

        startActivity(intent);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
