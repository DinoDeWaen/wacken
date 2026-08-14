package be.wacken.planner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Optional;

import be.wacken.planner.application.BiographyText;
import be.wacken.planner.application.PersonalRatingHistoryItem;
import be.wacken.planner.application.ViewArchivedFestivalHistoryUseCase;
import be.wacken.planner.domain.Band;

public final class ArchivedBandDetailActivity extends Activity {
    public static final String EXTRA_FESTIVAL_ID = "be.wacken.planner.ARCHIVED_FESTIVAL_ID";
    public static final String EXTRA_BAND_NAME = "be.wacken.planner.ARCHIVED_BAND_NAME";
    public static final String EXTRA_STAGE = "be.wacken.planner.ARCHIVED_STAGE";
    public static final String EXTRA_DATE = "be.wacken.planner.ARCHIVED_DATE";
    public static final String EXTRA_TIME = "be.wacken.planner.ARCHIVED_TIME";

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
        setContentView(render(bandName, band, history));
    }

    private ScrollView render(String bandName, Optional<Band> band, ViewArchivedFestivalHistoryUseCase.ArchivedFestivalHistory history) {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(WackenTheme.BACKGROUND);
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.CENTER_HORIZONTAL);
        screen.setPadding(dp(16), dp(16), dp(16), dp(16));
        scroll.addView(screen);

        TextView title = new TextView(this);
        title.setText(bandName);
        title.setTextColor(WackenTheme.AMBER);
        title.setTextSize(26);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setPadding(0, 0, 0, dp(10));
        screen.addView(title);

        screen.addView(line("Read-only archive - " + history.festivalName()));
        screen.addView(section("Planning ratings", planningText(bandName, history)));
        screen.addView(section("Personal history", personalText(bandName, history)));
        screen.addView(section("Running order",
                "Stage: " + value(EXTRA_STAGE) + "\n"
                        + "Day: " + value(EXTRA_DATE) + "\n"
                        + "Time: " + value(EXTRA_TIME)));
        band.flatMap(value -> BiographyText.readable(value.biography()))
                .ifPresent(biography -> screen.addView(section("Biography", biography)));

        Button back = WackenTheme.actionButton(this, "Back", WackenTheme.ButtonStyle.SECONDARY, view -> finish());
        screen.addView(back);
        return scroll;
    }

    private String planningText(String bandName, ViewArchivedFestivalHistoryUseCase.ArchivedFestivalHistory history) {
        String text = history.planningRatings().stream()
                .filter(item -> item.bandName().equalsIgnoreCase(bandName))
                .map(ViewArchivedFestivalHistoryUseCase.ArchivedPlanningRatingItem::displayText)
                .reduce((left, right) -> left + "\n" + right)
                .orElse("");
        return text.isBlank() ? "No archived planning ratings for this band." : text;
    }

    private String personalText(String bandName, ViewArchivedFestivalHistoryUseCase.ArchivedFestivalHistory history) {
        String text = history.personalRatings().stream()
                .filter(item -> item.bandName().equalsIgnoreCase(bandName))
                .map(PersonalRatingHistoryItem::displayText)
                .reduce((left, right) -> left + "\n" + right)
                .orElse("");
        return text.isBlank() ? "No real/personal post-show rating history for this band." : text;
    }

    private TextView line(String text) {
        TextView line = new TextView(this);
        line.setText(text);
        line.setTextColor(WackenTheme.MUTED);
        line.setGravity(Gravity.CENTER_HORIZONTAL);
        line.setPadding(0, 0, 0, dp(10));
        return line;
    }

    private TextView section(String title, String body) {
        TextView section = new TextView(this);
        section.setText(title + "\n" + body);
        section.setTextColor(WackenTheme.TEXT);
        section.setTextSize(15);
        section.setPadding(dp(12), dp(10), dp(12), dp(10));
        section.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 6));
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layout.setMargins(0, 0, 0, dp(10));
        section.setLayoutParams(layout);
        return section;
    }

    private String value(String key) {
        String value = getIntent().getStringExtra(key);
        return value == null || value.isBlank() ? "TBA" : value;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
