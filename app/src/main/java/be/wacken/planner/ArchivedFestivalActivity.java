package be.wacken.planner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import be.wacken.planner.application.BandListItem;
import be.wacken.planner.application.ListArchivedFestivalBandsUseCase;
import be.wacken.planner.application.ViewArchivedFestivalHistoryUseCase;

public final class ArchivedFestivalActivity extends Activity {
    public static final String EXTRA_FESTIVAL_ID = "be.wacken.planner.FESTIVAL_ID";

    private static final int ROW_HEIGHT_DP = 58;

    private String festivalId;
    private String festivalName;
    private List<BandListItem> bands;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AuthSession session = new AuthSessionStore(this).load();
        if (!session.isPresent()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
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
        setContentView(render());
    }

    private LinearLayout render() {
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setBackgroundColor(WackenTheme.BACKGROUND);
        screen.setPadding(dp(14), dp(14), dp(14), dp(14));

        TextView title = new TextView(this);
        title.setText(festivalName);
        title.setTextColor(WackenTheme.AMBER);
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        screen.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Read-only archive");
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

        Button back = WackenTheme.actionButton(this, "Back", WackenTheme.ButtonStyle.SECONDARY, view -> finish());
        screen.addView(back);
        return screen;
    }

    private LinearLayout headerRow() {
        LinearLayout row = row(WackenTheme.ELEVATED_PANEL, 38);
        row.addView(cell("Band", 1.6f, Typeface.DEFAULT_BOLD));
        row.addView(cell("Rating", 1.15f, Typeface.DEFAULT_BOLD));
        row.addView(cell("Stage", 1.2f, Typeface.DEFAULT_BOLD));
        row.addView(cell("Date", 0.95f, Typeface.DEFAULT_BOLD));
        row.addView(cell("Time", 1.15f, Typeface.DEFAULT_BOLD));
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
        cell.setTextSize(13);
        cell.setTypeface(typeface);
        cell.setGravity(Gravity.CENTER_VERTICAL);
        cell.setSingleLine(false);
        cell.setPadding(dp(4), 0, dp(4), 0);
        cell.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, weight));
        return cell;
    }

    private String stars(BandListItem band) {
        if (!band.explicitRating()) {
            return "";
        }
        return "★".repeat(Math.max(0, Math.min(5, band.rating())));
    }

    private void openBand(BandListItem band) {
        Intent intent = new Intent(this, ArchivedBandDetailActivity.class);
        intent.putExtra(ArchivedBandDetailActivity.EXTRA_FESTIVAL_ID, festivalId);
        intent.putExtra(ArchivedBandDetailActivity.EXTRA_BAND_NAME, band.bandName());
        intent.putExtra(ArchivedBandDetailActivity.EXTRA_STAGE, band.stageName());
        intent.putExtra(ArchivedBandDetailActivity.EXTRA_DATE, band.displayDate());
        intent.putExtra(ArchivedBandDetailActivity.EXTRA_TIME, band.displayTime());
        startActivity(intent);
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
            row.addView(cell(band.bandName(), 1.6f, Typeface.DEFAULT_BOLD));
            row.addView(cell(stars(band), 1.15f, Typeface.DEFAULT_BOLD));
            row.addView(cell(band.stageName(), 1.2f, Typeface.DEFAULT));
            row.addView(cell(band.displayDate(), 0.95f, Typeface.DEFAULT));
            row.addView(cell(band.displayTime(), 1.15f, Typeface.DEFAULT));
            return row;
        }
    }
}
