package be.wacken.planner;

import static org.junit.Assert.*;
import android.app.*;
import android.content.Intent;
import android.os.SystemClock;
import android.view.*;
import android.widget.*;
import androidx.test.platform.app.InstrumentationRegistry;
import be.wacken.planner.application.*;
import be.wacken.planner.domain.*;
import be.wacken.planner.persistence.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.BooleanSupplier;
import org.junit.*;

public final class MusicLinksReviewAndroidTest {
    private static final String NAME="TASK-202 Native Band", ID="11111111-1111-4111-8111-111111111111";
    private static final String YT="https://www.youtube.com/channel/UC1234567890123456789012";
    private Instrumentation instrumentation;
    private Activity activity, parent;
    private AuthSession previous;
    private Optional<Band> previousBand;
    private WackenDatabase db;
    private final Map<String,Band> saved=new HashMap<>();
    private final List<String> terms=new ArrayList<>();

    @Before public void setup(){
        instrumentation=InstrumentationRegistry.getInstrumentation();
        db=WackenDatabase.get(instrumentation.getTargetContext());
        previous=new AuthSessionStore(instrumentation.getTargetContext()).load();
        previousBand=new RoomBandRepository(db).findByName(NAME);
        new RoomBandRepository(db).save(new Band(NAME));
        new AuthSessionStore(instrumentation.getTargetContext()).save(
            new AuthSession("offline-fixture","offline-fixture","task202","native@example.invalid",Long.MAX_VALUE,"task202","member"));
        saved.put(NAME,new Band(NAME));
    }
    @After public void cleanup(){
        onUi(()->{if(activity!=null)activity.finish();if(parent!=null)parent.finish();});
        new AuthSessionStore(instrumentation.getTargetContext()).save(previous);
        if(previousBand.isPresent())new RoomBandRepository(db).save(previousBand.get());
        else db.getOpenHelper().getWritableDatabase().execSQL("DELETE FROM bands WHERE name = ?",new Object[]{NAME});
    }
    @Test public void bandEntrySearchesEditedTermWithoutRenamingOrSavingThenRejects() throws Exception {
        openFrom(BandDetailActivity.class);
        inject(term->{terms.add(term);return new MusicLinkLookupProvider.Lookup(List.of(proof()),List.of());});
        onUi(()->{((EditText)find("Search term for "+NAME)).setText("refined artist");click("Search missing music links");});
        await(()->find("Choose verified identity")!=null);
        assertEquals(List.of("refined artist"),terms);assertTrue(saved.get(NAME).youtubeUrl().isEmpty());
        onUi(()->{assertNotNull(find(YT));assertNotNull(find("MusicBrainz identity"));assertNotNull(find("Wikidata corroboration"));click("Reject all for "+NAME);});
        assertTrue(saved.get(NAME).youtubeUrl().isEmpty());
    }
    @Test public void returningFromReviewRefreshesNewlySavedProfileOnBandDetail() throws Exception {
        openFrom(BandDetailActivity.class);
        new RoomBandRepository(db).save(new Band(NAME,Optional.of(YT),Optional.empty()));
        onUi(activity::finish);instrumentation.waitForIdleSync();
        onUi(()->assertNotNull("Newly approved link must be immediately available on return",
                findText(parent.getWindow().getDecorView(),"Open YouTube")));
    }
    @Test public void verifiedIdentityRequiresExplicitSelectionAndApproval() throws Exception {
        openDirect();inject(term->new MusicLinkLookupProvider.Lookup(List.of(proof()),List.of()));
        onUi(()->click("Search missing music links"));await(()->find("Choose verified identity")!=null);
        onUi(()->{
            assertFalse(find("Save approved links for "+NAME).isEnabled());
            click("Choose verified identity");click("I checked the identity evidence for "+NAME);
            assertTrue(find("Save approved links for "+NAME).isEnabled());click("Save approved links for "+NAME);
        });
        await(()->saved.get(NAME).youtubeUrl().isPresent());
        assertEquals(Optional.of(YT),saved.get(NAME).youtubeUrl());
        assertEquals(NAME,saved.get(NAME).name());
    }
    @Test public void errorThenRetryAndNoMatchNeverSave() throws Exception {
        openDirect();AtomicInteger calls=new AtomicInteger();
        inject(term->{if(calls.getAndIncrement()==0)throw new BandMetadataLookupException("Rate limited. Retry later.",null);
            return new MusicLinkLookupProvider.Lookup(List.of(),List.of("No matching artist."));});
        onUi(()->click("Search missing music links"));await(()->findContaining("ERROR")!=null);
        onUi(()->click("Search missing music links"));await(()->findContaining("NO_MATCH")!=null);
        assertEquals(2,calls.get());assertTrue(saved.get(NAME).youtubeUrl().isEmpty());
    }
    @Test public void cancellationRemainsResponsiveAndUnverifiedIdentityCannotSave() throws Exception {
        openDirect();AtomicBoolean started=new AtomicBoolean(),release=new AtomicBoolean();
        inject(term->{started.set(true);while(!release.get())SystemClock.sleep(10);
            return new MusicLinkLookupProvider.Lookup(List.of(proof()),List.of());});
        onUi(()->click("Search missing music links"));await(started::get);
        onUi(()->click("Cancel search"));release.set(true);
        await(()->findContaining("CANCELLED")!=null);
        assertTrue(saved.get(NAME).youtubeUrl().isEmpty());
        inject(term->new MusicLinkLookupProvider.Lookup(List.of(new MusicLinkEvidence(ID,NAME,"Group","",
                List.of(),"","","","",List.of(),List.of(),List.of(YT),List.of(),List.of())),List.of()));
        onUi(()->click("Search missing music links"));await(()->findContaining("Unverified identity")!=null);
        onUi(()->assertFalse(find("Save approved links for "+NAME).isEnabled()));
    }
    @Test public void settingsEntryPassesSelectedFestivalAndAndroidParserReadsOnlyAnchors() throws Exception {
        List<RoomFestival> old=db.festivals().findAll();
        try{
            for(RoomFestival f:old)db.festivals().save(new RoomFestival(f.id,f.name,"ARCHIVED"));
            new RoomFestivalRepository(db).save(Festival.active("task202-native","Music lookup festival"));
            db.festivalLineups().saveAll(List.of(new RoomFestivalLineupEntry("task202-native",NAME,NAME)));
            openFrom(SettingsActivity.class);
            assertEquals("task202-native",activity.getIntent().getStringExtra(MusicLinksReviewActivity.EXTRA_FESTIVAL_ID));
            onUi(()->assertNotNull(find("Search term for "+NAME)));
            List<String> links=OfficialMusicPageClient.anchorLinks(
                "<p>https://fake.example</p><a href='/music'>Music</a><a href='"+YT+"'>Channel</a>",java.net.URI.create("https://band.example/"));
            assertEquals(List.of("https://band.example/music",YT),links);
        }finally{
            db.festivalLineups().deleteByFestival("task202-native");db.festivals().deleteById("task202-native");
            db.festivals().saveAll(old);
        }
    }

    @Test public void rotationRetainsEditedTermAndRunningReviewWithoutRepeatingLookup() throws Exception {
        openDirect();AtomicBoolean started=new AtomicBoolean(),release=new AtomicBoolean();AtomicInteger calls=new AtomicInteger();
        inject(term->{calls.incrementAndGet();started.set(true);while(!release.get())SystemClock.sleep(10);
            return new MusicLinkLookupProvider.Lookup(List.of(proof()),List.of());});
        try{
            onUi(()->{((EditText)find("Search term for "+NAME)).setText("edited identity");click("Search missing music links");});
            await(started::get);
            Instrumentation.ActivityMonitor monitor=instrumentation.addMonitor(MusicLinksReviewActivity.class.getName(),null,false);
            onUi(activity::recreate);Activity recreated=monitor.waitForActivityWithTimeout(5000);instrumentation.removeMonitor(monitor);
            assertNotNull(recreated);activity=recreated;release.set(true);
            await(()->find("Choose verified identity")!=null);
            onUi(()->assertEquals("edited identity",((EditText)find("Search term for "+NAME)).getText().toString()));
            assertEquals(1,calls.get());assertTrue(saved.get(NAME).youtubeUrl().isEmpty());
        }finally{release.set(true);}
    }

    private void openDirect(){
        activity=instrumentation.startActivitySync(new Intent(instrumentation.getTargetContext(),MusicLinksReviewActivity.class)
            .putExtra(MusicLinksReviewActivity.EXTRA_BAND_NAME,NAME).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        instrumentation.waitForIdleSync();
    }
    private void openFrom(Class<? extends Activity> type){
        parent=instrumentation.startActivitySync(new Intent(instrumentation.getTargetContext(),type)
            .putExtra(BandDetailActivity.EXTRA_BAND_NAME,NAME).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        Instrumentation.ActivityMonitor monitor=instrumentation.addMonitor(MusicLinksReviewActivity.class.getName(),null,false);
        onUi(()->{View v=findText(parent.getWindow().getDecorView(),"Find music links");assertNotNull(v);v.performClick();});
        activity=monitor.waitForActivityWithTimeout(5000);instrumentation.removeMonitor(monitor);
        assertNotNull("Actual entry must open review",activity);instrumentation.waitForIdleSync();
    }
    private void inject(MusicLinkLookupProvider provider) throws Exception {
        BandRepository repo=new BandRepository(){
            public void save(Band b){saved.put(b.name(),b);}
            public void replaceAll(List<Band>b){throw new AssertionError();}
            public Optional<Band> findByName(String n){return Optional.ofNullable(saved.get(n));}
            public List<Band> findAll(){return List.copyOf(saved.values());}
        };
        FestivalLineupRepository lineup=new FestivalLineupRepository(){
            public void saveAllForFestival(String f,List<FestivalLineupEntry> e){throw new AssertionError();}
            public List<FestivalLineupEntry> findByFestival(String f){return List.of();}
        };
        var field=MusicLinksReviewActivity.class.getDeclaredField("session");field.setAccessible(true);
        Object session=field.get(activity);var app=session.getClass().getDeclaredField("app");app.setAccessible(true);
        app.set(session,new FindBandMusicLinksUseCase(repo,lineup,provider));
    }
    private MusicLinkEvidence proof(){
        String site="https://band.example/";
        return new MusicLinkEvidence(ID,NAME,"Group","Distinct fixture identity",List.of(),
            "https://musicbrainz.org/artist/"+ID,"https://www.wikidata.org/wiki/Q123",ID,NAME,
            List.of(site),List.of(site),List.of(YT),List.of(),List.of(new MusicLinkEvidence.Page(site,List.of(YT))));
    }
    private void onUi(Runnable r){instrumentation.runOnMainSync(r);}
    private View find(String s){return findText(activity.getWindow().getDecorView(),s);}
    private void click(String s){View v=find(s);assertNotNull(s,v);assertTrue(s,v.isEnabled());v.performClick();}
    private View findContaining(String s){return findContaining(activity.getWindow().getDecorView(),s);}
    private static View findText(View v,String s){
        if(v instanceof TextView t&&(s.contentEquals(t.getText())||s.contentEquals(t.getContentDescription()==null?"":t.getContentDescription())))return v;
        if(v instanceof ViewGroup g)for(int i=0;i<g.getChildCount();i++){View f=findText(g.getChildAt(i),s);if(f!=null)return f;}return null;
    }
    private static View findContaining(View v,String s){
        if(v instanceof TextView t&&t.getText().toString().contains(s))return v;
        if(v instanceof ViewGroup g)for(int i=0;i<g.getChildCount();i++){View f=findContaining(g.getChildAt(i),s);if(f!=null)return f;}return null;
    }
    private void await(BooleanSupplier condition) throws Exception{
        long until=SystemClock.elapsedRealtime()+10000;AtomicBoolean ready=new AtomicBoolean();
        while(SystemClock.elapsedRealtime()<until){onUi(()->ready.set(condition.getAsBoolean()));if(ready.get())return;SystemClock.sleep(25);}
        fail("Review did not reach expected state.");
    }
}
