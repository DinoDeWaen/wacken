package be.wacken.planner;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.wacken.planner.application.BandLinkCandidate;
import be.wacken.planner.application.BandLinkResult;
import be.wacken.planner.application.LinkImportedBandUseCase;
import be.wacken.planner.application.ReviewImportedBandLinksUseCase;

public final class BandLinkReviewActivity extends Activity {
    private static final String NO_MATCH = "No match";

    private LinearLayout rows;
    private TextView status;
    private AppRepositories repositories;
    private ReviewImportedBandLinksUseCase reviewBands;
    private LinkImportedBandUseCase linkBand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repositories = new AppRepositories(this);
        reviewBands = new ReviewImportedBandLinksUseCase(
                repositories.festivals(),
                repositories.festivalLineups(),
                repositories.bands()
        );
        linkBand = new LinkImportedBandUseCase(
                repositories.festivals(),
                repositories.festivalLineups(),
                repositories.bands()
        );
        setContentView(content());
        renderRows();
    }

    private ScrollView content() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(WackenTheme.BACKGROUND);

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setPadding(dp(16), dp(16), dp(16), dp(16));
        scroll.addView(screen);

        TextView title = new TextView(this);
        title.setText("Link Imported Bands");
        title.setTextColor(WackenTheme.AMBER);
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        screen.addView(title);

        status = new TextView(this);
        status.setTextColor(WackenTheme.MUTED);
        status.setPadding(0, dp(8), 0, dp(12));
        status.setText("Review possible matches. No match is valid.");
        screen.addView(status);

        rows = new LinearLayout(this);
        rows.setOrientation(LinearLayout.VERTICAL);
        screen.addView(rows);

        screen.addView(actionButton("Refresh matches", view -> renderRows()));
        screen.addView(actionButton("Back to settings", view -> finish()));
        return scroll;
    }

    private void renderRows() {
        rows.removeAllViews();
        List<BandLinkCandidate> candidates = reviewBands.review();
        if (candidates.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No likely imported-band matches found.");
            empty.setTextColor(WackenTheme.TEXT);
            empty.setPadding(dp(12), dp(10), dp(12), dp(10));
            empty.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 6));
            rows.addView(empty);
            return;
        }
        for (BandLinkCandidate candidate : candidates) {
            rows.addView(candidateRow(candidate));
        }
    }

    private LinearLayout candidateRow(BandLinkCandidate candidate) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dp(12), dp(10), dp(12), dp(12));
        row.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 6));
        LinearLayout.LayoutParams rowLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rowLayout.setMargins(0, 0, 0, dp(10));
        row.setLayoutParams(rowLayout);

        TextView imported = new TextView(this);
        imported.setText(candidate.uploadedDisplayName());
        imported.setTextColor(WackenTheme.TEXT);
        imported.setTextSize(16);
        LinearLayout.LayoutParams importedLayout = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );
        importedLayout.setMargins(0, 0, dp(10), 0);
        imported.setLayoutParams(importedLayout);
        row.addView(imported);

        LinearLayout controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.VERTICAL);
        controls.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2f
        ));

        EditText search = new EditText(this);
        search.setText(candidate.searchTerm());
        search.setSingleLine(true);
        search.setHint("Search own band database");
        search.setTextColor(WackenTheme.TEXT);
        search.setHintTextColor(WackenTheme.MUTED);
        controls.addView(search);

        Spinner matches = new Spinner(this);
        setMatches(matches, candidate.candidateBandNames());
        controls.addView(matches);

        Button link = actionButton("Link selected match", view -> {
            String selected = matches.getSelectedItem() == null ? NO_MATCH : matches.getSelectedItem().toString();
            BandLinkResult result = linkBand.link(
                    candidate.currentBandName(),
                    candidate.uploadedDisplayName(),
                    NO_MATCH.equals(selected) ? "" : selected
            );
            status.setTextColor(result.success() ? WackenTheme.SUCCESS_GREEN : WackenTheme.RED);
            status.setText(result.message());
            renderRows();
        });
        controls.addView(link);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable text) {
                BandLinkCandidate updated = reviewBands.search(
                        candidate.uploadedDisplayName(),
                        candidate.currentBandName(),
                        text.toString()
                );
                setMatches(matches, updated.candidateBandNames());
            }
        });

        row.addView(controls);
        return row;
    }

    private void setMatches(Spinner matches, List<String> candidateNames) {
        List<String> values = new ArrayList<>();
        values.add(NO_MATCH);
        values.addAll(candidateNames);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        matches.setAdapter(adapter);
        if (values.size() > 1) {
            matches.setSelection(1);
        }
    }

    private Button actionButton(String text, android.view.View.OnClickListener listener) {
        Button button = WackenTheme.actionButton(this, text, WackenTheme.ButtonStyle.SECONDARY, listener);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layout.setMargins(0, dp(8), 0, 0);
        button.setLayoutParams(layout);
        return button;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
