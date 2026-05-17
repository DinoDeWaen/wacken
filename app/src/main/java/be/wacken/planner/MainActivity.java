package be.wacken.planner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import be.wacken.planner.application.BandListItem;
import be.wacken.planner.application.ListBandsUseCase;
import be.wacken.planner.application.RateBandResult;
import be.wacken.planner.application.RateBandUseCase;
import be.wacken.planner.domain.Band;

public final class MainActivity extends Activity {
    private static final String CURRENT_USER = "my group";
    private static final int COLOR_BACKGROUND = Color.rgb(29, 36, 38);
    private static final int COLOR_ROW_DARK = Color.rgb(32, 39, 41);
    private static final int COLOR_ROW_LIGHT = Color.rgb(41, 48, 50);
    private static final int COLOR_GRID = Color.rgb(67, 75, 78);
    private static final int COLOR_TEXT = Color.rgb(220, 224, 225);
    private static final int COLOR_MUTED = Color.rgb(162, 169, 171);
    private static final int COLOR_ACCENT = Color.rgb(255, 56, 92);

    private ListView bandList;
    private TextView status;
    private BandAdapter adapter;
    private List<BandListItem> cachedBands;
    private Map<String, Band> cachedBandsByName;
    private boolean loading;
    private boolean reloadNeeded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setBackgroundColor(COLOR_BACKGROUND);
        int padding = dp(14);
        screen.setPadding(padding, padding, padding, padding);

        screen.addView(header());
        screen.addView(importButton());
        screen.addView(tableHeader());

        status = new TextView(this);
        status.setTextColor(COLOR_MUTED);
        status.setGravity(Gravity.CENTER_HORIZONTAL);
        status.setPadding(0, dp(20), 0, dp(20));
        screen.addView(status);

        bandList = new ListView(this);
        bandList.setDivider(null);
        bandList.setCacheColorHint(COLOR_BACKGROUND);
        bandList.setBackgroundColor(COLOR_BACKGROUND);
        screen.addView(bandList, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1
        ));

        setContentView(screen);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (reloadNeeded || adapter == null) {
            showLoadingState();
            bandList.post(this::loadBandList);
        }
    }

    private void loadBandList() {
        AppRepositories repositories = new AppRepositories(this);
        cachedBands = new ListBandsUseCase(
                repositories.bands(),
                repositories.performances(),
                repositories.ratings(),
                CURRENT_USER
        ).listBands();
        cachedBandsByName = bandsByName(repositories);
        adapter = new BandAdapter(repositories, cachedBands, cachedBandsByName);
        bandList.setAdapter(adapter);
        loading = false;
        reloadNeeded = false;
        status.setVisibility(cachedBands.isEmpty() ? View.VISIBLE : View.GONE);
        status.setText(cachedBands.isEmpty() ? getString(R.string.empty_band_list) : "");
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

        TextView title = new TextView(this);
        title.setText("Bands");
        title.setTextColor(COLOR_MUTED);
        title.setTextSize(24);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.START);
        header.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Line-up ratings for my group");
        subtitle.setTextColor(COLOR_MUTED);
        subtitle.setGravity(Gravity.START);
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
        importButton.setOnClickListener(view -> {
            reloadNeeded = true;
            startActivity(new Intent(this, ImportCsvActivity.class));
        });
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layout.setMargins(0, 0, 0, dp(14));
        importButton.setLayoutParams(layout);
        return importButton;
    }

    private LinearLayout tableHeader() {
        LinearLayout header = tableRow(COLOR_BACKGROUND);
        header.addView(headerCell("Band", 2.45f));
        header.addView(headerCell("Rating", 1.75f));
        header.addView(headerCell("", 1.45f));
        header.addView(headerCell("Stage", 1.25f));
        header.addView(headerCell("Date", 0.95f));
        header.addView(headerCell("Time", 1.15f));
        return header;
    }

    private TextView headerCell(String text, float weight) {
        TextView cell = cell(text, Typeface.DEFAULT_BOLD, weight);
        cell.setTextColor(COLOR_TEXT);
        cell.setTextSize(13);
        cell.setBackground(columnHeaderBackground());
        return cell;
    }

    private LinearLayout tableRow(int backgroundColor) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setBackgroundColor(backgroundColor);
        row.setLayoutParams(new ListView.LayoutParams(
                ListView.LayoutParams.MATCH_PARENT,
                dp(44)
        ));
        return row;
    }

    private TextView cell(String text, Typeface typeface, float weight) {
        TextView cell = new TextView(this);
        cell.setText(text);
        cell.setSingleLine(true);
        cell.setEllipsize(TextUtils.TruncateAt.END);
        cell.setTextColor(COLOR_TEXT);
        cell.setTextSize(12);
        cell.setTypeface(typeface);
        cell.setGravity(Gravity.CENTER_VERTICAL);
        cell.setPadding(dp(5), 0, dp(5), 0);
        cell.setLayoutParams(new LinearLayout.LayoutParams(0, dp(44), weight));
        return cell;
    }

    private LinearLayout rowActions(Optional<Band> band) {
        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.CENTER);
        actions.setPadding(dp(2), 0, dp(2), 0);
        actions.setLayoutParams(new LinearLayout.LayoutParams(0, dp(44), 1.45f));
        band.flatMap(Band::youtubeUrl).ifPresent(url -> actions.addView(iconButton("▶", "Open YouTube", COLOR_ACCENT, url, dp(26))));
        band.flatMap(Band::spotifyUrl).ifPresent(url -> actions.addView(iconButton("♬", "Open Spotify", Color.rgb(30, 215, 96), url, dp(26))));
        return actions;
    }

    private Button iconButton(String icon, String description, int accentColor, String url, int size) {
        Button button = new Button(this);
        button.setText(icon);
        button.setTextSize(14);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setTextColor(Color.WHITE);
        button.setContentDescription(description);
        button.setPadding(0, 0, 0, 0);
        button.setBackground(iconBackground(accentColor));
        button.setOnClickListener(view -> ExternalLinks.open(this, url));
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(size, size);
        layout.setMargins(dp(1), 0, dp(1), 0);
        button.setLayoutParams(layout);
        return button;
    }

    private GradientDrawable iconBackground(int accentColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.rgb(49, 56, 58));
        drawable.setStroke(dp(1), accentColor);
        drawable.setCornerRadius(dp(4));
        return drawable;
    }

    private GradientDrawable columnHeaderBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(COLOR_BACKGROUND);
        drawable.setStroke(dp(1), COLOR_GRID);
        return drawable;
    }

    private void openBandDetail(Map<String, Band> bandsByName, BandListItem band) {
        Intent intent = new Intent(this, BandDetailActivity.class);
        intent.putExtra(BandDetailActivity.EXTRA_BAND_NAME, band.bandName());
        intent.putExtra(BandDetailActivity.EXTRA_RATING, band.rating());
        intent.putExtra(BandDetailActivity.EXTRA_DEFAULT_RATING, band.defaultRating());
        intent.putExtra(BandDetailActivity.EXTRA_STAGE, band.stageName());
        intent.putExtra(BandDetailActivity.EXTRA_DATE, band.displayDate());
        intent.putExtra(BandDetailActivity.EXTRA_TIME, band.displayTime());

        Optional<Band> storedBand = Optional.ofNullable(bandsByName.get(band.bandName()));
        storedBand.flatMap(Band::youtubeUrl)
                .ifPresent(url -> intent.putExtra(BandDetailActivity.EXTRA_YOUTUBE_URL, url));
        storedBand.flatMap(Band::spotifyUrl)
                .ifPresent(url -> intent.putExtra(BandDetailActivity.EXTRA_SPOTIFY_URL, url));

        startActivity(intent);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private final class BandAdapter extends BaseAdapter {
        private final AppRepositories repositories;
        private final List<BandListItem> bands;
        private final Map<String, Band> bandsByName;

        BandAdapter(AppRepositories repositories, List<BandListItem> bands, Map<String, Band> bandsByName) {
            this.repositories = repositories;
            this.bands = new ArrayList<>(bands);
            this.bandsByName = bandsByName;
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
            LinearLayout row = tableRow(COLOR_ROW_DARK);
            row.setClickable(true);
            row.setFocusable(true);

            TextView name = cell("", Typeface.DEFAULT_BOLD, 2.45f);
            RatingStarsView rating = new RatingStarsView(MainActivity.this, 0, false, COLOR_ACCENT);
            rating.setLayoutParams(new LinearLayout.LayoutParams(0, dp(44), 1.75f));
            LinearLayout actions = new LinearLayout(MainActivity.this);
            actions.setLayoutParams(new LinearLayout.LayoutParams(0, dp(44), 1.45f));
            TextView stage = cell("", Typeface.DEFAULT_BOLD, 1.25f);
            TextView date = cell("", Typeface.DEFAULT_BOLD, 0.95f);
            TextView time = cell("", Typeface.DEFAULT_BOLD, 1.15f);

            row.addView(name);
            row.addView(rating);
            row.addView(actions);
            row.addView(stage);
            row.addView(date);
            row.addView(time);
            return new RowHolder(row, name, rating, actions, stage, date, time);
        }

        private void bindRow(RowHolder holder, int position) {
            BandListItem band = getItem(position);
            holder.row.setBackgroundColor(position % 2 == 0 ? COLOR_ROW_DARK : COLOR_ROW_LIGHT);
            holder.row.setOnClickListener(view -> {
                if (!loading) {
                    openBandDetail(bandsByName, band);
                }
            });

            holder.name.setText(band.bandName());
            holder.rating.bind(band.rating(), band.explicitRating());
            holder.rating.setOnRatingSelected(selectedRating -> saveRating(position, band, holder.rating, selectedRating));
            holder.row.setOnHoverListener((view, event) -> {
                if (event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
                    holder.rating.showAvailableRating();
                }
                if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT && !holder.row.hasFocus()) {
                    holder.rating.restoreRestingState();
                }
                return false;
            });
            holder.row.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    holder.rating.showAvailableRating();
                } else {
                    holder.rating.restoreRestingState();
                }
            });

            holder.actions.removeAllViews();
            Optional<Band> storedBand = Optional.ofNullable(bandsByName.get(band.bandName()));
            LinearLayout actions = rowActions(storedBand);
            for (int index = 0; index < actions.getChildCount(); index++) {
                View child = actions.getChildAt(index);
                actions.removeView(child);
                holder.actions.addView(child);
                index--;
            }
            holder.actions.setOrientation(LinearLayout.HORIZONTAL);
            holder.actions.setGravity(Gravity.CENTER);
            holder.actions.setPadding(dp(2), 0, dp(2), 0);
            holder.actions.setLayoutParams(new LinearLayout.LayoutParams(0, dp(44), 1.45f));

            holder.stage.setText(band.stageName());
            holder.date.setText(band.displayDate());
            holder.time.setText(band.displayTime());
        }

        private void saveRating(int position, BandListItem band, RatingStarsView rating, int selectedRating) {
            RateBandResult result = new RateBandUseCase(repositories.ratings())
                    .rateBand(CURRENT_USER, new Band(band.bandName()), selectedRating);
            if (result.success()) {
                rating.applySavedRating(selectedRating);
                bands.set(position, new BandListItem(
                        band.bandName(),
                        band.stageName(),
                        band.startTime(),
                        band.endTime(),
                        selectedRating,
                        false
                ));
            }
        }
    }

    private record RowHolder(
            LinearLayout row,
            TextView name,
            RatingStarsView rating,
            LinearLayout actions,
            TextView stage,
            TextView date,
            TextView time
    ) {
    }
}
