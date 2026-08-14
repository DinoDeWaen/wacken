package be.wacken.planner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import be.wacken.planner.application.BandListItem;
import be.wacken.planner.application.ListArchivedFestivalBandsUseCase;
import be.wacken.planner.application.ViewArchivedFestivalHistoryUseCase;
import be.wacken.planner.domain.Band;

public final class ArchivedFestivalActivity extends Activity {
    public static final String EXTRA_FESTIVAL_ID = "be.wacken.planner.FESTIVAL_ID";

    private static final int TABLE_HEADER_HEIGHT_DP = 44;
    private static final int ROW_HEIGHT_DP = 58;

    private String festivalId;
    private String festivalName;
    private String email;
    private List<BandListItem> bands;
    private Map<String, Band> bandsByName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AuthSession session = new AuthSessionStore(this).load();
        if (!session.isPresent()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        email = session.email();
        festivalId = getIntent().getStringExtra(EXTRA_FESTIVAL_ID);
        AppRepositories repositories = new AppRepositories(this);
        ViewArchivedFestivalHistoryUseCase.ArchivedFestivalHistory history = new ViewArchivedFestivalHistoryUseCase(
                repositories.festivals(),
                repositories.festivalLineups(),
                repositories.festivalPlanningRatings(),
                repositories.personalBandRatings(),
                repositories.realRatings()
        ).show(session.userId(), festivalId);
        festivalName = history.festivalName();
        bands = new ListArchivedFestivalBandsUseCase(
                repositories.festivalLineups(),
                repositories.festivalPlanningRatings(),
                repositories.personalBandRatings(),
                repositories.realRatings(),
                repositories.performances()
        ).listBands(session.userId(), festivalId);
        bandsByName = bandsByName(repositories);
        setContentView(render());
    }

    private LinearLayout render() {
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setBackgroundColor(WackenTheme.BACKGROUND);
        screen.setPadding(dp(14), dp(14), dp(14), dp(14));

        TextView title = new TextView(this);
        title.setText("Bands");
        title.setTextColor(WackenTheme.AMBER);
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.START);
        screen.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Line-up ratings for " + email + " - " + festivalName + " archive");
        subtitle.setTextColor(WackenTheme.MUTED);
        subtitle.setPadding(0, 0, 0, dp(12));
        screen.addView(subtitle);

        screen.addView(headerRow());
        if (bands.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No archived bands or ratings available.");
            empty.setTextColor(WackenTheme.MUTED);
            empty.setGravity(Gravity.CENTER_HORIZONTAL);
            empty.setPadding(0, dp(20), 0, dp(20));
            screen.addView(empty);
        } else {
            ListView list = new ListView(this);
            list.setDivider(null);
            list.setCacheColorHint(WackenTheme.BACKGROUND);
            list.setBackgroundColor(WackenTheme.BACKGROUND);
            list.setAdapter(new ArchivedBandAdapter());
            screen.addView(list, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1
            ));
        }
        return screen;
    }

    private LinearLayout headerRow() {
        LinearLayout row = row(WackenTheme.BACKGROUND, TABLE_HEADER_HEIGHT_DP);
        row.addView(headerCell("Band", 2.45f));
        row.addView(headerCell("Rating", 1.75f));
        row.addView(headerCell("", 1.45f));
        row.addView(headerCell("Stage", 1.25f));
        row.addView(headerCell("Date", 0.95f));
        row.addView(headerCell("Time", 1.15f));
        return row;
    }

    private LinearLayout row(int color, int heightDp) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setBackgroundColor(color);
        row.setPadding(dp(4), 0, dp(4), 0);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(heightDp)
        ));
        return row;
    }

    private TextView cell(String text, float weight, Typeface typeface) {
        TextView cell = new TextView(this);
        cell.setText(text);
        cell.setTextColor(WackenTheme.TEXT);
        cell.setTextSize(12);
        cell.setTypeface(typeface);
        cell.setGravity(Gravity.CENTER_VERTICAL);
        cell.setSingleLine(true);
        cell.setEllipsize(TextUtils.TruncateAt.END);
        cell.setPadding(dp(6), 0, dp(6), 0);
        cell.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, weight));
        return cell;
    }

    private TextView headerCell(String text, float weight) {
        TextView cell = cell(text, weight, Typeface.DEFAULT_BOLD);
        cell.setTextSize(13);
        cell.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 4));
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
        name.setTextColor(WackenTheme.TEXT);
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
        band.flatMap(Band::youtubeUrl).ifPresent(url -> actions.addView(iconButton("▶", "Open YouTube", WackenTheme.RED, url, dp(26))));
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

    private void openBand(BandListItem band) {
        Intent intent = new Intent(this, ArchivedBandDetailActivity.class);
        intent.putExtra(ArchivedBandDetailActivity.EXTRA_FESTIVAL_ID, festivalId);
        intent.putExtra(ArchivedBandDetailActivity.EXTRA_BAND_NAME, band.bandName());
        intent.putExtra(ArchivedBandDetailActivity.EXTRA_STAGE, band.stageName());
        intent.putExtra(ArchivedBandDetailActivity.EXTRA_DATE, band.displayDate());
        intent.putExtra(ArchivedBandDetailActivity.EXTRA_TIME, band.displayTime());
        Optional<Band> storedBand = Optional.ofNullable(bandsByName.get(band.bandName()));
        storedBand.flatMap(Band::youtubeUrl)
                .ifPresent(url -> intent.putExtra(ArchivedBandDetailActivity.EXTRA_YOUTUBE_URL, url));
        storedBand.flatMap(Band::spotifyUrl)
                .ifPresent(url -> intent.putExtra(ArchivedBandDetailActivity.EXTRA_SPOTIFY_URL, url));
        startActivity(intent);
    }

    private Map<String, Band> bandsByName(AppRepositories repositories) {
        Map<String, Band> knownBands = new HashMap<>();
        for (Band band : repositories.bands().findAll()) {
            knownBands.put(band.name(), band);
        }
        return knownBands;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private final class ArchivedBandAdapter extends BaseAdapter {
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
            BandListItem band = getItem(position);
            LinearLayout row = row(position % 2 == 0 ? WackenTheme.PANEL : WackenTheme.ELEVATED_PANEL, ROW_HEIGHT_DP);
            row.setClickable(true);
            row.setFocusable(true);
            row.setOnClickListener(view -> openBand(band));
            TextView name = new TextView(ArchivedFestivalActivity.this);
            name.setText(band.bandName());
            row.addView(bandNameCell(name));

            RatingStarsView rating = new RatingStarsView(ArchivedFestivalActivity.this, band.rating(), band.explicitRating(), WackenTheme.RED);
            rating.setEnabled(false);
            rating.setClickable(false);
            rating.setFocusable(false);
            rating.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.75f));
            rating.showAvailableRating();
            row.addView(rating);

            row.addView(rowActions(Optional.ofNullable(bandsByName.get(band.bandName()))));
            row.addView(cell(band.stageName(), 1.25f, Typeface.DEFAULT_BOLD));
            row.addView(cell(band.displayDate(), 0.95f, Typeface.DEFAULT));
            row.addView(cell(band.displayTime(), 1.15f, Typeface.DEFAULT));
            return row;
        }
    }
}
