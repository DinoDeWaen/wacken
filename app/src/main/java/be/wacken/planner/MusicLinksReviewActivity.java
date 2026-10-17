package be.wacken.planner;

import android.app.Activity;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import be.wacken.planner.application.*;
import be.wacken.planner.domain.Band;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/** Native review adapter. Identity verification and missing-field rules belong to the application. */
public final class MusicLinksReviewActivity extends Activity {
    public static final String EXTRA_BAND_NAME="music_links_band";
    public static final String EXTRA_FESTIVAL_ID="music_links_festival";
    private Session session;
    private LinearLayout rows;
    private TextView status;
    private Button search,cancel;
    private ScrollView scroll;
    private final Handler main=new Handler(Looper.getMainLooper());

    private static final class Session {
        FindBandMusicLinksUseCase app;
        final List<String> bands;
        final String festival;
        final Map<String,String> terms=new ConcurrentHashMap<>();
        final Map<String,FindBandMusicLinksUseCase.Review> reviews=new ConcurrentHashMap<>();
        final Map<String,Integer> selected=new HashMap<>();
        final Set<String> acknowledged=new HashSet<>();
        final ExecutorService worker=Executors.newSingleThreadExecutor();
        final AtomicBoolean cancelled=new AtomicBoolean();
        volatile boolean busy,saving;
        volatile String notice="Search finds proposals only. Check sources and explicitly approve verified missing links.";
        volatile Runnable changed;
        volatile Thread running;
        Session(FindBandMusicLinksUseCase app,List<String> bands,String festival){
            this.app=app;this.bands=List.copyOf(bands);this.festival=festival;
            bands.forEach(b->terms.put(b,b));
        }
        void notifyUi(){Runnable callback=changed;if(callback!=null)callback.run();}
    }

    @Override protected void onCreate(Bundle state){
        super.onCreate(state);
        if(!new AuthSessionStore(this).load().isPresent()){
            startActivity(new Intent(this,LoginActivity.class));finish();return;
        }
        session=(Session)getLastNonConfigurationInstance();
        if(session==null){
            AppRepositories repositories=new AppRepositories(getApplicationContext());
            var app=new FindBandMusicLinksUseCase(repositories.bands(),repositories.festivalLineups(),new VerifiedMusicLinksProvider());
            String band=getIntent().getStringExtra(EXTRA_BAND_NAME);
            String festival=getIntent().getStringExtra(EXTRA_FESTIVAL_ID);
            List<String> scope=band!=null?app.band(band).map(b->List.of(b.name())).orElse(List.of())
                    :app.festivalBands(festival).stream().map(Band::name).toList();
            session=new Session(app,scope,band==null?festival:null);
            if(state!=null)for(String name:scope){
                String term=state.getString("term:"+name);if(term!=null)session.terms.put(name,term);
            }
        }
        session.changed=()->main.post(()->{if(!isFinishing()&&!isDestroyed())render();});
        setContentView(content());render();
        if(state!=null)scroll.post(()->scroll.scrollTo(0,state.getInt("scroll")));
    }
    @Override public Object onRetainNonConfigurationInstance(){return session;}
    @Override protected void onSaveInstanceState(Bundle state){
        super.onSaveInstanceState(state);
        if(session!=null)session.terms.forEach((name,term)->state.putString("term:"+name,term));
        if(scroll!=null)state.putInt("scroll",scroll.getScrollY());
    }
    @Override protected void onDestroy(){
        if(session!=null){
            session.changed=null;
            if(!isChangingConfigurations()){
                session.cancelled.set(true);session.worker.shutdownNow();
            }
        }
        super.onDestroy();
    }
    private ScrollView content(){
        scroll=new ScrollView(this);scroll.setBackgroundColor(WackenTheme.BACKGROUND);
        LinearLayout screen=column();screen.setPadding(dp(16),dp(16),dp(16),dp(16));scroll.addView(screen);
        TextView title=label("Find music links");title.setTextSize(26);title.setTextColor(WackenTheme.AMBER);screen.addView(title);
        screen.addView(label(session.festival==null?"Selected band only":"Selected festival: "+session.festival));
        screen.addView(label("Existing links and ratings are kept. Search terms never rename bands. Unverified links stay empty."));
        status=label("");screen.addView(status);
        search=button("Search missing music links",v->startSearch());screen.addView(search);
        cancel=button("Cancel search",v->{
            session.cancelled.set(true);Thread thread=session.running;if(thread!=null)thread.interrupt();
            session.notice="Cancellation requested. Saved links are kept.";render();
        });screen.addView(cancel);
        rows=column();screen.addView(rows);
        screen.addView(button("Back",v->finish()));return scroll;
    }
    private void render(){
        status.setText(session.notice);
        search.setEnabled(!session.busy&&!session.bands.isEmpty());cancel.setEnabled(session.busy&&!session.saving);
        rows.removeAllViews();
        if(session.bands.isEmpty())rows.addView(label("No missing music links in this selection."));
        for(String name:session.bands)rows.addView(bandRow(name));
    }
    private View bandRow(String name){
        LinearLayout row=column();row.setPadding(dp(12),dp(12),dp(12),dp(12));
        row.setBackground(WackenTheme.panelBackground(this,WackenTheme.PANEL,WackenTheme.GRID,6));
        TextView heading=label(name);heading.setTextSize(20);row.addView(heading);
        session.app.band(name).ifPresent(b->{
            row.addView(label("Known YouTube: "+b.youtubeUrl().orElse("empty")));
            row.addView(label("Known Spotify: "+b.spotifyUrl().orElse("empty")));
        });
        EditText term=new EditText(this);term.setSingleLine(true);term.setTextColor(WackenTheme.TEXT);
        term.setContentDescription("Search term for "+name);term.setText(session.terms.get(name));term.setEnabled(!session.busy);
        term.addTextChangedListener(new TextWatcher(){
            public void beforeTextChanged(CharSequence s,int start,int count,int after){}
            public void onTextChanged(CharSequence s,int start,int before,int count){
                session.terms.put(name,s.toString());
                // Editing invalidates the prior candidate choices; a fresh search is required.
                session.reviews.remove(name);session.selected.remove(name);session.acknowledged.remove(name);
            }
            public void afterTextChanged(Editable e){}
        });row.addView(term);
        var review=session.reviews.get(name);if(review==null)return row;
        row.addView(label(review.state().name()));
        for(String message:review.messages())row.addView(label(message));
        for(int i=0;i<review.candidates().size();i++){
            final int index=i;var candidate=review.candidates().get(i);var evidence=candidate.evidence();
            row.addView(label(evidence.name()+" · "+evidence.artistType()+" · "+evidence.description()));
            row.addView(label("Artist identifier: "+evidence.identityId()));
            row.addView(label(candidate.rationale()));
            source(row,"MusicBrainz identity",evidence.sourceUrl());
            source(row,"Wikidata corroboration",evidence.corroborationUrl());
            for(String url:evidence.officialWebsites())source(row,"Official artist website",url);
            candidate.youtubeUrl().ifPresent(url->source(row,url,url));
            candidate.spotifyUrl().ifPresent(url->source(row,url,url));
            if(!candidate.verified()){
                for(String url:evidence.youtubeUrls())row.addView(label("Unverified YouTube: "+url));
                for(String url:evidence.spotifyUrls())row.addView(label("Unverified Spotify: "+url));
            }
            CheckBox choose=new CheckBox(this);choose.setText("Choose verified identity");choose.setTextColor(WackenTheme.TEXT);
            choose.setMinHeight(dp(48));choose.setChecked(Objects.equals(session.selected.get(name),index));
            choose.setEnabled(!session.busy&&review.state()==FindBandMusicLinksUseCase.State.REVIEW&&candidate.verified());
            choose.setOnCheckedChangeListener((v,checked)->{
                if(checked)session.selected.put(name,index);else session.selected.remove(name);
                session.acknowledged.remove(name);render();
            });row.addView(choose);
            Button refine=button("Refine search to "+evidence.identityId(),v->{
                session.terms.put(name,evidence.identityId());session.reviews.remove(name);
                session.selected.remove(name);session.acknowledged.remove(name);render();
            });refine.setEnabled(!session.busy);row.addView(refine);
        }
        CheckBox checked=new CheckBox(this);checked.setText("I checked the identity evidence for "+name);
        checked.setTextColor(WackenTheme.TEXT);checked.setMinHeight(dp(48));
        checked.setChecked(session.acknowledged.contains(name));checked.setEnabled(!session.busy&&session.selected.containsKey(name));
        Button save=button("Save approved links for "+name,v->save(name,review));
        save.setEnabled(!session.busy&&session.selected.containsKey(name)&&session.acknowledged.contains(name));
        checked.setOnCheckedChangeListener((v,value)->{
            if(value)session.acknowledged.add(name);else session.acknowledged.remove(name);
            save.setEnabled(!session.busy&&value&&session.selected.containsKey(name));
        });row.addView(checked);row.addView(save);
        Button reject=button("Reject all for "+name,v->{
            session.reviews.remove(name);session.selected.remove(name);session.acknowledged.remove(name);
            session.notice="Rejected proposals for "+name+". No links changed.";render();
        });reject.setEnabled(!session.busy);row.addView(reject);return row;
    }
    private void startSearch(){
        if(session.busy)return;
        session.busy=true;session.saving=false;session.cancelled.set(false);
        session.selected.clear();session.acknowledged.clear();session.notice="Checking sources. Missing links stay empty until approval.";
        Map<String,String> terms=Map.copyOf(session.terms);
        Map<String,FindBandMusicLinksUseCase.Review> previous=Map.copyOf(session.reviews);render();
        // Capture retained session, not Activity, for work that can outlive a rotation.
        Session work=session;
        work.worker.execute(()->{
            work.running=Thread.currentThread();
            try{
                if(work.festival!=null)work.app.searchFestival(work.festival,terms,previous,work.cancelled::get,r->{
                    work.reviews.put(r.bandName(),r);work.notifyUi();
                });
                else for(String name:work.bands){
                    work.reviews.put(name,new FindBandMusicLinksUseCase.Review(name,terms.get(name),
                            FindBandMusicLinksUseCase.State.SEARCHING,List.of(),List.of("Checking identity sources…")));work.notifyUi();
                    work.reviews.put(name,work.app.searchBand(name,terms.get(name),work.cancelled::get));
                }
                work.notice=work.cancelled.get()?"Search cancelled. Completed reviews kept; retry unfinished work."
                        :"Search finished. Review evidence before approving links.";
            }catch(RuntimeException error){work.notice="Search could not finish. Saved data kept; retry unfinished work.";}
            finally{Thread.interrupted();work.running=null;work.busy=false;work.notifyUi();}
        });
    }
    private void save(String name,FindBandMusicLinksUseCase.Review review){
        Integer choice=session.selected.get(name);
        if(session.busy||choice==null||!session.acknowledged.contains(name)||session.reviews.get(name)!=review)return;
        session.busy=true;session.saving=true;session.notice="Saving approved missing links…";render();
        Session work=session;
        work.worker.execute(()->{
            try{
                var result=work.app.approve(review,choice);work.notice=result.message();
                if(result.success())work.reviews.remove(name);
            }catch(RuntimeException error){work.notice="Save could not be confirmed. Retry; existing links are kept.";}
            finally{work.selected.remove(name);work.acknowledged.remove(name);work.saving=false;work.busy=false;work.notifyUi();}
        });
    }
    private void source(LinearLayout row,String title,String url){
        if(url==null||!url.startsWith("https://"))return;
        row.addView(button(title,v->{
            try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));}
            catch(ActivityNotFoundException error){session.notice="No app available to open this source.";status.setText(session.notice);}
        }));
    }
    private LinearLayout column(){LinearLayout view=new LinearLayout(this);view.setOrientation(LinearLayout.VERTICAL);return view;}
    private TextView label(String text){
        TextView view=new TextView(this);view.setText(text);view.setTextColor(WackenTheme.TEXT);
        view.setPadding(0,dp(6),0,dp(6));return view;
    }
    private Button button(String text,View.OnClickListener action){
        Button button=WackenTheme.actionButton(this,text,WackenTheme.ButtonStyle.SECONDARY,action);
        button.setMinHeight(dp(48));return button;
    }
    private int dp(int value){return (int)(value*getResources().getDisplayMetrics().density);}
}
