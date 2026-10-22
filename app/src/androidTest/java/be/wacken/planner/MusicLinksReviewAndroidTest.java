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
        onUi(()->{assertNotNull(find(YT));assertNotNull(findContaining("MusicBrainz identity"));assertNotNull(findContaining("Wikidata corroboration"));click("Reject all for "+NAME);});
        assertTrue(saved.get(NAME).youtubeUrl().isEmpty());
    }
    @Test public void returningFromReviewRefreshesNewlySavedProfileOnBandDetail() throws Exception {
        openFrom(BandDetailActivity.class);
        onUi(()->assertNull(findText(parent.getWindow().getDecorView(),"Open YouTube")));
        new RoomBandRepository(db).save(new Band(NAME,Optional.of(YT),Optional.empty()));
        onUi(activity::finish);
        // finish() schedules a lifecycle transition; an idle queue does not prove
        // the parent has resumed. Observe both resume and the resulting UI.
        await(()->androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                .getLifecycleStageOf(parent)==androidx.test.runner.lifecycle.Stage.RESUMED
                &&findText(parent.getWindow().getDecorView(),"Open YouTube")!=null);
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
            Activity old=activity;Object retained=field(old,"session");
            AtomicReference<Activity> recreated=new AtomicReference<>();
            onUi(old::recreate);
            await(()->{
                for(Activity candidate:androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED))
                    if(candidate instanceof MusicLinksReviewActivity&&candidate!=old)recreated.set(candidate);
                return recreated.get()!=null;
            });
            activity=recreated.get();
            assertSame("Recreation must retain the in-flight review",retained,field(activity,"session"));
            release.set(true);
            await(()->!Boolean.TRUE.equals(field(retained,"busy")));
            assertTrue("Completed retained reviews: "+field(retained,"reviews"),
                ((Map<?,?>)field(retained,"reviews")).containsKey(NAME));
            await(()->find("Choose verified identity")!=null);
            onUi(()->assertEquals("edited identity",((EditText)find("Search term for "+NAME)).getText().toString()));
            assertEquals(1,calls.get());assertTrue(saved.get(NAME).youtubeUrl().isEmpty());
        }finally{release.set(true);}
    }

    @Test public void largeTextEvidenceAndApprovalSurviveBothThemesAndRecreation() throws Exception {
        Object previousMode=AppearanceController.store(instrumentation.getTargetContext()).getAll().get(AppearanceController.MODE);
        activity=instrumentation.startActivitySync(new Intent(instrumentation.getTargetContext(),MusicLinksReviewActivity.class)
            .putExtra(MusicLinksReviewActivity.EXTRA_BAND_NAME,NAME).putExtra(NativeUiTestRunner.FONT_SCALE,2f)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        instrumentation.waitForIdleSync();AtomicInteger calls=new AtomicInteger();
        try {
            inject(term->{calls.incrementAndGet();return new MusicLinkLookupProvider.Lookup(List.of(proof()),List.of());});
            onUi(()->click("Search missing music links"));await(()->find("Choose verified identity")!=null);
            onUi(()->{
                TextView source=(TextView)findContaining("MusicBrainz identity");assertNotNull(source);
                assertTrue("Clickable evidence must expose the real canonical source URL",source.getText().toString().contains("https://musicbrainz.org/artist/"+ID));
                assertNull(source.getEllipsize());assertTrue(source.getMinHeight()>=WackenTheme.dp(activity,48));
                var term=(EditText)find("Search term for "+NAME);
                assertEquals("Search uses16sp body text",android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_SP,16,activity.getResources().getDisplayMetrics()),term.getTextSize(),0.5f);
                click("Choose verified identity");click("I checked the identity evidence for "+NAME);
                AppearanceController.choose(activity,AppearancePreference.LIGHT);
                assertTrue(((CheckBox)find("Choose verified identity")).isChecked());
                assertTrue(find("Save approved links for "+NAME).isEnabled());
            });
            Activity old=activity;Object retained=field(old,"session");onUi(old::recreate);
            await(()->{
                for(Activity candidate:androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                    .getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED))
                    if(candidate instanceof MusicLinksReviewActivity&&candidate!=old){activity=candidate;return true;}
                return false;
            });
            assertSame(retained,field(activity,"session"));
            onUi(()->{
                assertTrue(((CheckBox)find("Choose verified identity")).isChecked());
                assertTrue(((CheckBox)find("I checked the identity evidence for "+NAME)).isChecked());
                assertTrue(find("Save approved links for "+NAME).isEnabled());
                AppearanceController.choose(activity,AppearancePreference.DARK);
                assertTrue(find("Save approved links for "+NAME).isEnabled());
                ((EditText)find("Search term for "+NAME)).setText("new identity");
            });
            assertEquals(1,calls.get());assertTrue(saved.get(NAME).youtubeUrl().isEmpty());
            onUi(()->assertNull("Editing invalidates approval immediately",find("Save approved links for "+NAME)));
        } finally {onUi(()->{
            var editor=AppearanceController.store(instrumentation.getTargetContext()).edit();
            if(previousMode instanceof String mode)editor.putString(AppearanceController.MODE,mode);else editor.remove(AppearanceController.MODE);
            editor.commit();
        });}
    }

    @Test public void metadataSearchAndCheckedProposalSurviveRecreationWithoutAnotherLookupOrSave() throws Exception {
        Object previousMode=AppearanceController.store(instrumentation.getTargetContext()).getAll().get(AppearanceController.MODE);
        saved.put(NAME,new Band(NAME,Optional.of(YT),Optional.empty()));
        activity=instrumentation.startActivitySync(new Intent(instrumentation.getTargetContext(),BandMetadataReviewActivity.class)
            .putExtra(NativeUiTestRunner.FONT_SCALE,2f).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        instrumentation.waitForIdleSync();
        AtomicBoolean started=new AtomicBoolean(),release=new AtomicBoolean();AtomicInteger calls=new AtomicInteger();
        BandMetadataLookupProvider provider=new BandMetadataLookupProvider(){
            public String name(){return "Fixture evidence";}public boolean configured(){return true;}
            public List<BandMetadataProviderCandidate> search(String band){
                calls.incrementAndGet();started.set(true);while(!release.get())SystemClock.sleep(10);
                return List.of(new BandMetadataProviderCandidate(NAME,Optional.of("Reviewed fixture biography"),Optional.empty(),
                    Optional.of("https://www.youtube.com/channel/UC9999999999999999999999"),Optional.empty(),Optional.of("https://band.example/evidence"),100));
            }
        };
        try {
            onUi(()->{
                try {
                    Object old=field(activity,"session");setField(old,"changed",null);setField(old,"cancelled",true);
                    Thread worker=(Thread)field(old,"worker");if(worker!=null)worker.interrupt();
                    Class<?> type=old.getClass();var constructor=type.getDeclaredConstructor(SearchBandMetadataUseCase.class,ApplyBandMetadataProposalsUseCase.class);
                    constructor.setAccessible(true);Object retained=constructor.newInstance(new SearchBandMetadataUseCase(repository(),List.of(provider)),new ApplyBandMetadataProposalsUseCase(repository()));
                    setField(activity,"session",retained);
                    setField(retained,"changed",(Runnable)()->activity.runOnUiThread(()->invoke(activity,"renderState")));
                    invoke(activity,"renderState");click("Refresh metadata proposals");
                }catch(Exception error){throw new AssertionError(error);}
            });
            await(started::get);Object retained=field(activity,"session");
            onUi(()->AppearanceController.choose(activity,AppearancePreference.LIGHT));
            recreate(BandMetadataReviewActivity.class);
            assertSame(retained,field(activity,"session"));release.set(true);
            await(()->findContaining("Reviewed fixture biography")!=null);
            onUi(()->{
                var choice=(CheckBox)findContaining("Biography: Reviewed fixture biography");assertNotNull(choice);
                assertTrue(choice.getMinHeight()>=WackenTheme.dp(activity,48));choice.setChecked(true);
                assertNotNull(findContaining("https://band.example/evidence"));
                assertNull("Existing YouTube must never become a replacement proposal",findContaining("UC9999999999999999999999"));
                AppearanceController.choose(activity,AppearancePreference.DARK);
            });
            recreate(BandMetadataReviewActivity.class);
            onUi(()->assertTrue(((CheckBox)findContaining("Biography: Reviewed fixture biography")).isChecked()));
            assertEquals(1,calls.get());assertTrue(saved.get(NAME).biography().isEmpty());assertEquals(Optional.of(YT),saved.get(NAME).youtubeUrl());
            onUi(()->click("Save accepted metadata"));
            await(()->saved.get(NAME).biography().isPresent());
            assertEquals(Optional.of("Reviewed fixture biography"),saved.get(NAME).biography());assertEquals(Optional.of(YT),saved.get(NAME).youtubeUrl());
        } finally {release.set(true);onUi(()->{
            var editor=AppearanceController.store(instrumentation.getTargetContext()).edit();
            if(previousMode instanceof String mode)editor.putString(AppearanceController.MODE,mode);else editor.remove(AppearanceController.MODE);editor.commit();
        });}
    }

    private void recreate(Class<? extends Activity> type) throws Exception {
        Activity old=activity;onUi(old::recreate);
        await(()->{
            for(Activity candidate:androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED))
                if(type.isInstance(candidate)&&candidate!=old){activity=candidate;return true;}
            return false;
        });
    }
    private static void setField(Object target,String name,Object value){
        try{var f=target.getClass().getDeclaredField(name);f.setAccessible(true);f.set(target,value);}catch(Exception error){throw new AssertionError(error);}
    }
    private static void invoke(Object target,String name){
        try{var method=target.getClass().getDeclaredMethod(name);method.setAccessible(true);method.invoke(target);}catch(Exception error){throw new AssertionError(error);}
    }
    private BandRepository repository(){return new BandRepository(){
        public void save(Band b){saved.put(b.name(),b);}public void replaceAll(List<Band>b){throw new AssertionError();}
        public Optional<Band> findByName(String name){return Optional.ofNullable(saved.get(name));}
        public List<Band> findAll(){return List.copyOf(saved.values());}
    };}

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
    private static Object field(Object target,String name){
        try{var field=target.getClass().getDeclaredField(name);field.setAccessible(true);return field.get(target);}
        catch(Exception error){throw new AssertionError(error);}
    }
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
