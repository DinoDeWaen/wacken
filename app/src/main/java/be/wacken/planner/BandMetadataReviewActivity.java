package be.wacken.planner;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import java.util.HashSet;
import java.util.Set;
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
    private ScrollView scroll;
    private Button refresh,cancel;
    private Session session;

    private static final class Session {
        final SearchBandMetadataUseCase searchMetadata;
        final ApplyBandMetadataProposalsUseCase applyMetadata;
        final Set<BandMetadataProposal> checked=new HashSet<>();
        volatile BandMetadataSearchRun run;
        volatile boolean busy,cancelled;
        volatile String failure;
        String lastSaveMessage;
        boolean lastSaveSucceeded;
        volatile Runnable changed;
        volatile Thread worker;
        Session(SearchBandMetadataUseCase search,ApplyBandMetadataProposalsUseCase apply){searchMetadata=search;applyMetadata=apply;}
        void notifyUi(){Runnable callback=changed;if(callback!=null)callback.run();}
    }

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);AppearanceController.attach(this);
        session=(Session)getLastNonConfigurationInstance();
        if(session==null){
            AppRepositories repositories=new AppRepositories(getApplicationContext());
            session=new Session(new SearchBandMetadataUseCase(repositories.bands(),metadataProviders()),
                    new ApplyBandMetadataProposalsUseCase(repositories.bands()));
        }
        setContentView(content());
        session.changed=()->runOnUiThread(()->{if(!isFinishing()&&!isDestroyed())renderState();});
        if(!session.busy&&session.run==null&&session.failure==null)renderRows();else renderState();
        if(state!=null)scroll.post(()->scroll.scrollTo(0,state.getInt("scroll")));
    }
    @Override public Object onRetainNonConfigurationInstance(){return session;}
    @Override protected void onSaveInstanceState(Bundle state){state.putInt("scroll",scroll.getScrollY());super.onSaveInstanceState(state);}
    @Override protected void onDestroy(){
        session.changed=null;
        if(!isChangingConfigurations()){session.cancelled=true;Thread worker=session.worker;if(worker!=null)worker.interrupt();}
        super.onDestroy();
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
        scroll = new ScrollView(this);
        WackenTheme.backgroundColor(scroll, WackenTheme.BACKGROUND);

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setPadding(dp(16), dp(16), dp(16), dp(16));
        scroll.addView(screen);

        TextView title = new TextView(this);
        title.setText("Fetch Band Metadata");
        WackenTheme.textColor(title, WackenTheme.AMBER);
        title.setTextSize(26);
        title.setGravity(Gravity.START);WackenTheme.heading(title);
        screen.addView(title);

        status = new TextView(this);
        status.setTextSize(14);status.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        status.setText("Review missing-field proposals before saving.");
        WackenTheme.textColor(status, WackenTheme.MUTED);
        status.setPadding(0, dp(8), 0, dp(12));
        screen.addView(status);

        taskStatus = new LinearLayout(this);
        taskStatus.setOrientation(LinearLayout.VERTICAL);
        screen.addView(taskStatus);

        rows = new LinearLayout(this);
        rows.setOrientation(LinearLayout.VERTICAL);
        screen.addView(rows);

        refresh=actionButton("Refresh metadata proposals",view->renderRows());screen.addView(refresh);
        cancel=actionButton("Cancel metadata search",view->{
            session.cancelled=true;Thread worker=session.worker;if(worker!=null)worker.interrupt();
            status.setText("Cancellation requested. Existing metadata is kept.");cancel.setEnabled(false);
        });screen.addView(cancel);
        screen.addView(actionButton("Back to settings", view -> finish()));
        return scroll;
    }

    private void renderRows() {
        if(session.busy)return;
        session.busy=true;session.cancelled=false;session.failure=null;session.run=null;session.checked.clear();
        renderState();Session work=session;
        work.worker=new Thread(()->{
            try {
                BandMetadataSearchRun result=work.searchMetadata.searchMissingMetadataRun();
                if(!work.cancelled)work.run=result;
            } catch(RuntimeException error){work.failure="Search could not finish. Existing metadata is kept. Retry metadata proposals.";}
            finally {
                if(work.cancelled)work.failure="Search cancelled. Existing metadata is kept. Retry metadata proposals.";
                Thread.interrupted();work.worker=null;work.busy=false;work.notifyUi();
            }
        });work.worker.start();
    }

    private void renderState(){
        refresh.setEnabled(!session.busy);cancel.setEnabled(session.busy&&!session.cancelled);
        if(session.busy){
            status.setText(session.cancelled?"Cancellation requested. Existing metadata is kept.":"Searching metadata sources...");
            showSearchingTaskStatus();rows.removeAllViews();rows.addView(messageRow("Searching metadata sources..."));return;
        }
        if(session.failure!=null){
            status.setText(session.failure);taskStatus.removeAllViews();rows.removeAllViews();rows.addView(messageRow(session.failure));return;
        }
        if(session.run!=null)showResults(session.run);
    }

    private void showResults(BandMetadataSearchRun run) {
        showTaskStatus(run);
        rows.removeAllViews();
        if (run.results().isEmpty()) {
            WackenTheme.textColor(status, WackenTheme.MUTED);
            status.setText("No metadata proposals found. Check the task status above.");
            rows.addView(messageRow("No metadata proposals found. See Metadata enrichment tasks for completed bands, missing metadata, and provider status."));
            return;
        }
        WackenTheme.textColor(status, WackenTheme.MUTED);
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
        WackenTheme.textColor(band, WackenTheme.TEXT);
        band.setTextSize(18);
        row.addView(band);

        List<ProposalChoice> choices = new ArrayList<>();
        for (BandMetadataProposal proposal : result.proposals()) {
            CheckBox choice = new CheckBox(this);
            choice.setText(proposalText(proposal));
            WackenTheme.textColor(choice, WackenTheme.TEXT);
            WackenTheme.reviewChoice(choice);
            choice.setChecked(session.checked.contains(proposal));
            choice.setOnCheckedChangeListener((view,checked)->{if(checked)session.checked.add(proposal);else session.checked.remove(proposal);});
            row.addView(choice);
            choices.add(new ProposalChoice(proposal, choice));
            proposal.sourceUrl().filter(url->url.startsWith("https://")).ifPresent(url->
                    row.addView(actionButton("Open source\n"+url,view->{
                        try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));}
                        catch(android.content.ActivityNotFoundException error){status.setText("No app available to open this source. Review is kept.");}
                    })));
        }

        for (String unavailableProvider : result.unavailableProviders()) {
            TextView unavailable = new TextView(this);
            unavailable.setText(unavailableProvider);
            WackenTheme.textColor(unavailable, WackenTheme.MUTED);
            row.addView(unavailable);
        }

        if (choices.isEmpty()) {
            TextView noProposals = new TextView(this);
            noProposals.setText("No proposals for this band yet.");
            WackenTheme.textColor(noProposals, WackenTheme.MUTED);
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
        if (session.lastSaveMessage != null) {
            TextView save = taskLine("Last save: " + session.lastSaveMessage);
            WackenTheme.textColor(save,session.lastSaveSucceeded ? WackenTheme.SUCCESS_GREEN : WackenTheme.RED);
            taskStatus.addView(save);
        }
    }

    private TextView taskPanelTitle(String title) {
        TextView label = new TextView(this);
        label.setText(title);
        WackenTheme.textColor(label, WackenTheme.AMBER);
        label.setTextSize(18);WackenTheme.heading(label);
        label.setPadding(dp(12), dp(10), dp(12), dp(4));
        label.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 6));
        return label;
    }

    private TextView taskLine(String message) {
        TextView line = new TextView(this);
        line.setText(message);line.setTextSize(14);
        WackenTheme.textColor(line, WackenTheme.TEXT);
        line.setPadding(dp(12), dp(2), dp(12), dp(2));
        WackenTheme.backgroundColor(line, WackenTheme.PANEL);
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
        empty.setText(message);empty.setTextSize(16);
        WackenTheme.textColor(empty, WackenTheme.TEXT);
        empty.setPadding(dp(12), dp(10), dp(12), dp(10));
        empty.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 6));
        return empty;
    }

    private void saveAccepted(String bandName, List<ProposalChoice> choices) {
        List<BandMetadataProposal> accepted = choices.stream()
                .filter(choice -> choice.checkBox().isChecked())
                .map(ProposalChoice::proposal)
                .toList();
        if(session.busy)return;
        ApplyBandMetadataProposalsResult result = session.applyMetadata.apply(bandName, accepted);
        session.lastSaveMessage = result.message();
        session.lastSaveSucceeded = result.success();
        WackenTheme.textColor(status,result.success() ? WackenTheme.SUCCESS_GREEN : WackenTheme.RED);
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
