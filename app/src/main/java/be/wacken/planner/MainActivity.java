package be.wacken.planner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
        int padding = 32;
        screen.setPadding(padding, padding, padding, padding);

        Button importButton = new Button(this);
        importButton.setText("Import CSV");
        importButton.setOnClickListener(view -> startActivity(new Intent(this, ImportCsvActivity.class)));
        screen.addView(importButton);

        bandList = new LinearLayout(this);
        bandList.setOrientation(LinearLayout.VERTICAL);
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(bandList);
        screen.addView(scrollView);

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
            bandList.addView(emptyState);
            return;
        }

        for (BandListItem band : bands) {
            Button row = new Button(this);
            row.setAllCaps(false);
            row.setText(rowText(band));
            row.setOnClickListener(view -> openBandDetail(repositories, band));
            bandList.addView(row);
        }
    }

    private String rowText(BandListItem band) {
        return new StringBuilder()
                .append(band.bandName())
                    .append('\n')
                    .append(band.stageName())
                    .append(" | ")
                    .append(band.startTime())
                    .append(" - ")
                    .append(band.endTime())
                    .append('\n')
                    .append("Rating: ")
                    .append(band.rating())
                    .append(band.defaultRating() ? " (default)" : "")
                    .toString();
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
}
