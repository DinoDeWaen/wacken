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
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import be.wacken.planner.application.BandListItem;
import be.wacken.planner.application.ListArchivedFestivalBandsUseCase;
import be.wacken.planner.application.ViewArchivedFestivalHistoryUseCase;
import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Festival;

public final class ArchivedFestivalActivity extends Activity {
    public static final String EXTRA_FESTIVAL_ID = "be.wacken.planner.FESTIVAL_ID";

    private String festivalId;
    private String festivalName;
    private String email;
    private List<BandListItem> bands;
    private Map<String, Band> bandsByName;
    private TextView deletionStatus;
    private ListView list;
    private String recoveryNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppearanceController.attach(this);
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
                new be.wacken.planner.application.FestivalAfterRatingView(repositories.afterRatings(), festivalId)
        ).show(session.userId(), festivalId);
        festivalName = history.festivalName();
        bands=readBands(repositories);
        bandsByName = bandsByName(repositories);
        if(savedInstanceState!=null)recoveryNotice=savedInstanceState.getString("recoveryNotice");
        setContentView(render());
        if(savedInstanceState!=null)list.post(()->list.setSelectionFromTop(savedInstanceState.getInt("position"),savedInstanceState.getInt("top")));
    }

    private List<BandListItem> readBands(AppRepositories repositories){
        return new ListArchivedFestivalBandsUseCase(
                repositories.festivalLineups(),
                repositories.festivalPlanningRatings(),
                repositories.personalBandRatings(),
                new be.wacken.planner.application.FestivalAfterRatingView(repositories.afterRatings(), festivalId),
                repositories.performancesForFestival(festivalId)
        ).listBands(session.userId(), festivalId);
    }

    @Override protected void onResume(){
        super.onResume();if(list==null)return;
        AppRepositories repositories=new AppRepositories(this);
        List<BandListItem> current=readBands(repositories);
        if(!current.equals(bands)){
            int position=list.getFirstVisiblePosition(),top=list.getChildCount()==0?0:list.getChildAt(0).getTop();
            bands=current;bandsByName=bandsByName(repositories);list.setAdapter(new ArchivedBandAdapter());list.setSelectionFromTop(position,top);
        }
    }

    @Override protected void onSaveInstanceState(Bundle state){
        if(list!=null){state.putInt("position",list.getFirstVisiblePosition());state.putInt("top",list.getChildCount()==0?0:list.getChildAt(0).getTop());}
        state.putString("recoveryNotice",AppearanceController.hasOpenDialogs(this)?"A previous confirmation or request closed during screen recreation. Check current festival and history before repeating an action; restoring this screen submits nothing.":recoveryNotice);
        super.onSaveInstanceState(state);
    }

    private LinearLayout render() {
        LinearLayout screen=new LinearLayout(this);screen.setOrientation(LinearLayout.VERTICAL);
        WackenTheme.backgroundColor(screen,WackenTheme.BACKGROUND);
        list=new ListView(this);list.setDivider(null);WackenTheme.backgroundColor(list,WackenTheme.BACKGROUND);
        LinearLayout header=column();header.setPadding(dp(16),dp(16),dp(16),dp(16));
        TextView title=text("Bands",28);WackenTheme.heading(title);header.addView(title);
        header.addView(text("Archived · read-only\n"+festivalName+"\n"+email,14));
        if(recoveryNotice!=null)header.addView(text(recoveryNotice,16));
        header.addView(text("Planning and after-ratings keep their festival context. Open a band for history and explicit own-rating corrections.",14));
        if(bands.isEmpty())header.addView(text("No archived bands or ratings available.",16));
        list.addHeaderView(header,null,false);
        LinearLayout footer=column();footer.setPadding(dp(16),dp(12),dp(16),dp(16));
        Button delete=WackenTheme.actionButton(this,"Delete festival",WackenTheme.ButtonStyle.DANGER,
            view->DeleteFestivalDialog.show(this,Festival.archived(festivalId,festivalName),()->{
                Toast.makeText(this,"Festival deleted for the group.",Toast.LENGTH_LONG).show();finish();
            },error->deletionStatus.setText("Delete failed: "+error)));
        delete.setContentDescription("Delete archived festival and schedule");footer.addView(delete);
        deletionStatus=text("",14);deletionStatus.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);footer.addView(deletionStatus);
        footer.addView(WackenTheme.actionButton(this,"Back to bands",WackenTheme.ButtonStyle.SECONDARY,view->finish()));
        list.addFooterView(footer,null,false);list.setAdapter(new ArchivedBandAdapter());
        screen.addView(list,new LinearLayout.LayoutParams(-1,-1));return screen;
    }

    private LinearLayout column(){LinearLayout column=new LinearLayout(this);column.setOrientation(LinearLayout.VERTICAL);return column;}
    private TextView text(String value,int size){
        TextView view=new TextView(this);view.setText(value);view.setTextSize(size);WackenTheme.textColor(view,WackenTheme.TEXT);
        view.setPadding(0,dp(4),0,dp(4));return view;
    }

    private LinearLayout rowActions(Optional<Band> band){
        LinearLayout actions=column();
        band.flatMap(Band::youtubeUrl).ifPresent(url->actions.addView(WackenTheme.actionButton(this,"Open YouTube",WackenTheme.ButtonStyle.SECONDARY,view->ExternalLinks.open(this,url))));
        band.flatMap(Band::spotifyUrl).ifPresent(url->actions.addView(WackenTheme.actionButton(this,"Open Spotify",WackenTheme.ButtonStyle.SECONDARY,view->ExternalLinks.open(this,url))));
        return actions;
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
            LinearLayout row=column();row.setPadding(dp(16),dp(12),dp(16),dp(16));
            row.setBackground(WackenTheme.panelBackground(ArchivedFestivalActivity.this,WackenTheme.PANEL,WackenTheme.GRID,6));
            Button name=WackenTheme.actionButton(ArchivedFestivalActivity.this,band.bandName(),WackenTheme.ButtonStyle.SECONDARY,view->openBand(band));
            name.setTextSize(18);name.setContentDescription("Open archived band "+band.bandName());row.addView(name);
            row.addView(text("Planning: "+RatingValueText.describe(band.rating())+" · read-only",16));
            RatingStarsView rating = new RatingStarsView(ArchivedFestivalActivity.this, band.rating(), band.explicitRating(), WackenTheme.RED);
            rating.setScope("Archived festival planning rating");rating.setEnabled(false);
            rating.setClickable(false);rating.setFocusable(false);rating.showAvailableRating();row.addView(rating);
            row.addView(text(band.stageName()+"\n"+band.displayDate()+"\n"+band.displayTime(),14));
            row.addView(rowActions(Optional.ofNullable(bandsByName.get(band.bandName()))));
            return row;
        }
    }
}
