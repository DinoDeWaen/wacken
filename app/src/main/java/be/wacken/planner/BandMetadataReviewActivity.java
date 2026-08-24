package be.wacken.planner;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.wacken.planner.application.ApplyBandMetadataProposalsResult;
import be.wacken.planner.application.ApplyBandMetadataProposalsUseCase;
import be.wacken.planner.application.BandMetadataField;
import be.wacken.planner.application.BandMetadataLookupProvider;
import be.wacken.planner.application.BandMetadataProposal;
import be.wacken.planner.application.BandMetadataSearchResult;
import be.wacken.planner.application.SearchBandMetadataUseCase;

public final class BandMetadataReviewActivity extends Activity {
    private TextView status;
    private LinearLayout rows;
    private SearchBandMetadataUseCase searchMetadata;
    private ApplyBandMetadataProposalsUseCase applyMetadata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppRepositories repositories = new AppRepositories(this);
        searchMetadata = new SearchBandMetadataUseCase(repositories.bands(), metadataProviders());
        applyMetadata = new ApplyBandMetadataProposalsUseCase(repositories.bands());
        setContentView(content());
        renderRows();
    }

    private List<BandMetadataLookupProvider> metadataProviders() {
        return List.of(
                new MusicBrainzMetadataProvider(BuildConfig.MUSICBRAINZ_USER_AGENT),
                new WikidataMetadataProvider()
        );
    }

    private ScrollView content() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(WackenTheme.BACKGROUND);

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setPadding(dp(16), dp(16), dp(16), dp(16));
        scroll.addView(screen);

        TextView title = new TextView(this);
        title.setText("Fetch Band Metadata");
        title.setTextColor(WackenTheme.AMBER);
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        screen.addView(title);

        status = new TextView(this);
        status.setText("Review missing-field proposals before saving.");
        status.setTextColor(WackenTheme.MUTED);
        status.setPadding(0, dp(8), 0, dp(12));
        screen.addView(status);

        rows = new LinearLayout(this);
        rows.setOrientation(LinearLayout.VERTICAL);
        screen.addView(rows);

        screen.addView(actionButton("Refresh metadata proposals", view -> renderRows()));
        screen.addView(actionButton("Back to settings", view -> finish()));
        return scroll;
    }

    private void renderRows() {
        rows.removeAllViews();
        rows.addView(messageRow("Searching metadata sources..."));
        new Thread(() -> {
            List<BandMetadataSearchResult> results = searchMetadata.searchMissingMetadata();
            runOnUiThread(() -> showResults(results));
        }).start();
    }

    private void showResults(List<BandMetadataSearchResult> results) {
        rows.removeAllViews();
        if (results.isEmpty()) {
            rows.addView(messageRow("No metadata proposals found."));
            return;
        }
        for (BandMetadataSearchResult result : results) {
            rows.addView(resultRow(result));
        }
    }

    private LinearLayout resultRow(BandMetadataSearchResult result) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(dp(12), dp(10), dp(12), dp(12));
        row.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 6));
        LinearLayout.LayoutParams rowLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rowLayout.setMargins(0, 0, 0, dp(10));
        row.setLayoutParams(rowLayout);

        TextView band = new TextView(this);
        band.setText(result.bandName());
        band.setTextColor(WackenTheme.TEXT);
        band.setTextSize(16);
        row.addView(band);

        List<ProposalChoice> choices = new ArrayList<>();
        for (BandMetadataProposal proposal : result.proposals()) {
            CheckBox choice = new CheckBox(this);
            choice.setText(proposalText(proposal));
            choice.setTextColor(WackenTheme.TEXT);
            choice.setButtonTintList(android.content.res.ColorStateList.valueOf(WackenTheme.AMBER));
            row.addView(choice);
            choices.add(new ProposalChoice(proposal, choice));
        }

        for (String unavailableProvider : result.unavailableProviders()) {
            TextView unavailable = new TextView(this);
            unavailable.setText(unavailableProvider);
            unavailable.setTextColor(WackenTheme.MUTED);
            row.addView(unavailable);
        }

        row.addView(actionButton("Save accepted metadata", view -> saveAccepted(result.bandName(), choices)));
        return row;
    }

    private TextView messageRow(String message) {
        TextView empty = new TextView(this);
        empty.setText(message);
        empty.setTextColor(WackenTheme.TEXT);
        empty.setPadding(dp(12), dp(10), dp(12), dp(10));
        empty.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 6));
        return empty;
    }

    private void saveAccepted(String bandName, List<ProposalChoice> choices) {
        List<BandMetadataProposal> accepted = choices.stream()
                .filter(choice -> choice.checkBox().isChecked())
                .map(ProposalChoice::proposal)
                .toList();
        ApplyBandMetadataProposalsResult result = applyMetadata.apply(bandName, accepted);
        status.setTextColor(result.success() ? WackenTheme.SUCCESS_GREEN : WackenTheme.RED);
        status.setText(result.message());
        renderRows();
    }

    private String proposalText(BandMetadataProposal proposal) {
        return fieldLabel(proposal.field())
                + ": "
                + proposal.proposedValue()
                + " | "
                + proposal.sourceName()
                + " | "
                + proposal.candidateName();
    }

    private String fieldLabel(BandMetadataField field) {
        return switch (field) {
            case BIOGRAPHY -> "Biography";
            case IMAGE_URL -> "Picture";
            case YOUTUBE_URL -> "YouTube";
            case SPOTIFY_URL -> "Spotify";
        };
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

    private record ProposalChoice(BandMetadataProposal proposal, CheckBox checkBox) {
    }
}
