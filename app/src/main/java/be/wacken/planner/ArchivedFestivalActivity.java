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

import be.wacken.planner.application.ViewArchivedFestivalHistoryUseCase;

public final class ArchivedFestivalActivity extends Activity {
    public static final String EXTRA_FESTIVAL_ID = "be.wacken.planner.FESTIVAL_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AuthSession session = new AuthSessionStore(this).load();
        if (!session.isPresent()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        AppRepositories repositories = new AppRepositories(this);
        ViewArchivedFestivalHistoryUseCase.ArchivedFestivalHistory history = new ViewArchivedFestivalHistoryUseCase(
                repositories.festivals(),
                repositories.festivalLineups(),
                repositories.festivalPlanningRatings(),
                repositories.personalBandRatings()
        ).show(session.userId(), getIntent().getStringExtra(EXTRA_FESTIVAL_ID));
        setContentView(render(history));
    }

    private ScrollView render(ViewArchivedFestivalHistoryUseCase.ArchivedFestivalHistory history) {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(WackenTheme.BACKGROUND);
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setPadding(dp(16), dp(16), dp(16), dp(16));
        scroll.addView(screen);

        screen.addView(title(history.festivalName()));
        screen.addView(line("Read-only archive"));
        screen.addView(section("Lineup", history.bandNames().isEmpty()
                ? "No archived lineup entries available."
                : String.join("\n", history.bandNames())));
        screen.addView(section("Planning ratings", history.planningRatings().isEmpty()
                ? "No archived planning ratings available."
                : history.planningRatings().stream()
                        .map(ViewArchivedFestivalHistoryUseCase.ArchivedPlanningRatingItem::displayText)
                        .reduce((left, right) -> left + "\n" + right)
                        .orElse("")));
        screen.addView(section("Personal history", history.personalRatings().isEmpty()
                ? "No real/personal post-show rating history for this archived festival yet."
                : history.personalRatings().stream()
                        .map(item -> item.bandName() + " - " + item.displayText())
                        .reduce((left, right) -> left + "\n" + right)
                        .orElse("")));

        Button back = WackenTheme.actionButton(this, "Back", WackenTheme.ButtonStyle.SECONDARY, view -> finish());
        screen.addView(back);
        return scroll;
    }

    private TextView title(String text) {
        TextView title = new TextView(this);
        title.setText(text);
        title.setTextColor(WackenTheme.AMBER);
        title.setTextSize(26);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setPadding(0, 0, 0, dp(10));
        return title;
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

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
