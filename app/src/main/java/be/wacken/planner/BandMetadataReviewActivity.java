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
import be.wacken.planner.application.BandMetadataSearchRun;
import be.wacken.planner.application.BandMetadataSearchResult;
import be.wacken.planner.application.SearchBandMetadataUseCase;

public final class BandMetadataReviewActivity extends Activity {
    private TextView status;
    private LinearLayout taskStatus;
    private LinearLayout rows;
    private SearchBandMetadataUseCase searchMetadata;
    private ApplyBandMetadataProposalsUseCase applyMetadata;
    private String lastSaveMessage;
    private boolean lastSaveSucceeded;

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
                new WikidataMetadataProvider(),
                new WikipediaMetadataProvider(),
                new SpotifyMetadataProvider(BuildConfig.SPOTIFY_CLIENT_ID, BuildConfig.SPOTIFY_CLIENT_SECRET),
                new YouTubeMetadataProvider(BuildConfig.YOUTUBE_API_KEY)
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

        taskStatus = new LinearLayout(this);
        taskStatus.setOrientation(LinearLayout.VERTICAL);
        screen.addView(taskStatus);

        rows = new LinearLayout(this);
        rows.setOrientation(LinearLayout.VERTICAL);
        screen.addView(rows);

        screen.addView(actionButton("Refresh metadata proposals", view -> renderRows()));
        screen.addView(actionButton("Back to settings", view -> finish()));
        return scroll;
    }

    private void renderRows() {
        status.setTextColor(WackenTheme.MUTED);
        status.setText("Searching metadata sources...");
        showSearchingTaskStatus();
        rows.removeAllViews();
        rows.addView(messageRow("Searching metadata sources..."));
        new Thread(() -> {
            BandMetadataSearchRun run = searchMetadata.searchMissingMetadataRun();
            runOnUiThread(() -> showResults(run));
        }).start();
    }

    private void showResults(BandMetadataSearchRun run) {
        showTaskStatus(run);
        rows.removeAllViews();
        if (run.results().isEmpty()) {
            status.setTextColor(WackenTheme.MUTED);
            status.setText("No metadata proposals found. Check the task status above.");
            rows.addView(messageRow("No metadata proposals found. See Metadata enrichment tasks for completed bands, missing metadata, and provider status."));
            return;
        }
        status.setTextColor(WackenTheme.MUTED);
        status.setText(run.proposalCount() == 0
                ? "No metadata proposals found. Check the task status above."
                : "Review missing-field proposals before saving.");
        for (BandMetadataSearchResult result : run.results()) {
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

        if (choices.isEmpty()) {
            TextView noProposals = new TextView(this);
            noProposals.setText("No proposals for this band yet.");
            noProposals.setTextColor(WackenTheme.MUTED);
            row.addView(noProposals);
        } else {
            row.addView(actionButton("Save accepted metadata", view -> saveAccepted(result.bandName(), choices)));
        }
        return row;
    }

    private void showSearchingTaskStatus() {
        taskStatus.removeAllViews();
        taskStatus.addView(taskPanelTitle("Metadata enrichment tasks"));
        taskStatus.addView(taskLine("In progress: checking own band database and configured metadata providers."));
    }

    private void showTaskStatus(BandMetadataSearchRun run) {
        taskStatus.removeAllViews();
        taskStatus.addView(taskPanelTitle("Metadata enrichment tasks"));
        taskStatus.addView(taskLine("Checked: " + run.totalBands() + " bands."));
        taskStatus.addView(taskLine("Done: " + run.completeBands() + " bands already have complete metadata."));
        taskStatus.addView(taskLine("Needed: " + run.bandsMissingMetadata() + " bands are missing metadata."));
        taskStatus.addView(taskLine("Needs review: " + run.bandsNeedingReview() + " bands with " + run.proposalCount() + " proposals."));
        if (!run.bandsWithoutProposals().isEmpty()) {
            taskStatus.addView(taskLine("Still needed: " + summarizeNames(run.bandsWithoutProposals()) + " have missing metadata but no proposals yet."));
        }
        if (!run.providerMessages().isEmpty()) {
            taskStatus.addView(taskLine("Provider status: " + String.join(" ", run.providerMessages())));
        }
        if (lastSaveMessage != null) {
            TextView save = taskLine("Last save: " + lastSaveMessage);
            save.setTextColor(lastSaveSucceeded ? WackenTheme.SUCCESS_GREEN : WackenTheme.RED);
            taskStatus.addView(save);
        }
    }

    private TextView taskPanelTitle(String title) {
        TextView label = new TextView(this);
        label.setText(title);
        label.setTextColor(WackenTheme.AMBER);
        label.setTextSize(16);
        label.setPadding(dp(12), dp(10), dp(12), dp(4));
        label.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 6));
        return label;
    }

    private TextView taskLine(String message) {
        TextView line = new TextView(this);
        line.setText(message);
        line.setTextColor(WackenTheme.TEXT);
        line.setPadding(dp(12), dp(2), dp(12), dp(2));
        line.setBackgroundColor(WackenTheme.PANEL);
        return line;
    }

    private String summarizeNames(List<String> names) {
        int limit = Math.min(5, names.size());
        String summary = String.join(", ", names.subList(0, limit));
        if (names.size() > limit) {
            summary += " and " + (names.size() - limit) + " more";
        }
        return summary;
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
        lastSaveMessage = result.message();
        lastSaveSucceeded = result.success();
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
