package be.wacken.planner;

import static org.junit.Assert.*;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import be.wacken.planner.application.*;
import be.wacken.planner.domain.*;
import be.wacken.planner.persistence.*;

public final class FestivalCreationAndroidTest {
    private static final String GROUP="fixture-group", USER="fixture-user";
    private Instrumentation instrumentation;
    private WackenDatabase database;
    private ImportCsvActivity activity;
    private AuthSession previousSession;

    @Before public void setup() {
        instrumentation=InstrumentationRegistry.getInstrumentation();
        database=Room.inMemoryDatabaseBuilder(instrumentation.getTargetContext(),WackenDatabase.class).allowMainThreadQueries().build();
        previousSession=new AuthSessionStore(instrumentation.getTargetContext()).load();
        new RoomFestivalRepository(database).save(Festival.active("wacken-2026","Wacken history"));
        new RoomBandRepository(database).save(new Band("Any given Day",Optional.of("Golden biography"),Optional.empty(),Optional.empty(),Optional.empty()));
    }
    @After public void cleanup() {
        if(activity!=null)instrumentation.runOnMainSync(activity::finish);
        new AuthSessionStore(instrumentation.getTargetContext()).save(previousSession);
        database.close();
    }

    @Test public void completeSnapshotReconcilesStaleCacheWithoutLosingHistoryOrPendingEdits() throws Exception {
        RoomFestivalPlanningRatingRepository ratings=new RoomFestivalPlanningRatingRepository(database);
        ratings.save(GROUP,USER,"new-festival",new Band("Airbourne"),Rating.of(2));
        database.personalBandRatingEvents().save(new RoomPersonalBandRatingEvent("history",USER,"Airbourne","wacken-2026","Wacken history",5,"2026-01-01T00:00:00Z","SYNCED"));
        new RoomFestivalCreationCache(database).accept(SupabaseFestivalCreationGateway.parse(response(List.of("Airbourne")),GROUP,USER));
        assertEquals("ARCHIVED",database.festivals().findAll().stream().filter(f->f.id.equals("wacken-2026")).findFirst().orElseThrow().status);
        assertEquals(1,database.festivalLineups().findByFestival("new-festival").size());
        assertEquals(Optional.of(Rating.of(2)),ratings.findByUserFestivalAndBand(USER,"new-festival",new Band("Airbourne")));
        assertEquals(1,ratings.findPending(GROUP,USER).size());
        assertEquals(1,database.personalBandRatingEvents().findByUserAndBand(USER,"Airbourne").size());
        assertEquals("Golden biography",new RoomBandRepository(database).findByName("Any given Day").orElseThrow().biography().orElseThrow());
    }

    @Test public void creationPublishesOnlyPlanningAndReplayPreservesAfterAndEstablishedOpinions() throws Exception {
        database.ratingStates().saveCurrent(new RoomPersonalBandRating(USER,"Airbourne",4,"wacken-2026","Wacken history","source-event","2026-01-01T00:00:00Z"));
        SupabaseFestivalCreationGateway.Snapshot snapshot=SupabaseFestivalCreationGateway.parse(response(List.of("Airbourne")),GROUP,USER);
        RoomFestivalCreationCache cache=new RoomFestivalCreationCache(database);
        cache.accept(snapshot);
        RoomFestivalPlanningRatingRepository planning=new RoomFestivalPlanningRatingRepository(database);
        assertEquals(Optional.of(Rating.of(4)),planning.findByUserFestivalAndBand(USER,"new-festival",new Band("Airbourne")));
        assertTrue(database.ratingStates().afterByUser(USER).isEmpty());
        RoomFestivalAfterRatingRepository after=new RoomFestivalAfterRatingRepository(database);
        after.save(new FestivalAfterRating(UUID.randomUUID().toString(),USER,"new-festival",new Band("Airbourne"),Rating.of(3),java.time.Instant.parse("2026-10-01T00:00:00Z")));
        planning.save(GROUP,USER,"new-festival",new Band("Airbourne"),Rating.of(5));
        cache.accept(snapshot);
        assertEquals(Optional.of(Rating.of(5)),planning.findByUserFestivalAndBand(USER,"new-festival",new Band("Airbourne")));
        assertEquals(3,after.findByUserFestivalAndBand(USER,"new-festival",new Band("Airbourne")).orElseThrow().rating().value());
        assertEquals(4,new RoomPersonalBandRatingRepository(database).findByUserAndBand(USER,new Band("Airbourne")).orElseThrow().rating().value());
    }

    @Test public void cacheFailureRollsBackAllTablesAndRetryUsesSameRemoteCommand() throws Exception {
        List<String> requests=new ArrayList<>();
        String result=response(List.of("Airbourne"));
        SupabaseFestivalCreationGateway gateway=new SupabaseFestivalCreationGateway(new SupabaseMasterDataClient((m,p,b,u)->{requests.add(b);return result;}),new RoomFestivalCreationCache(database));
        database.getOpenHelper().getWritableDatabase().execSQL("CREATE TRIGGER reject_creation BEFORE INSERT ON festival_lineup_entries BEGIN SELECT RAISE(ABORT,'Injected cache failure'); END");
        try {
            gateway.create(GROUP,USER,Festival.active("new-festival","New festival"),List.of(new Band("Airbourne")));
            fail("Expected cache failure");
        } catch(SupabaseSyncException expected) {assertTrue(expected.getMessage().contains("saved group-wide"));}
        assertEquals(1,database.festivals().findAll().size());
        assertEquals("ACTIVE",database.festivals().findAll().get(0).status);
        assertEquals(1,database.bands().findAll().size());
        assertTrue(database.festivalPlanningRatings().findAll().isEmpty());
        database.getOpenHelper().getWritableDatabase().execSQL("DROP TRIGGER reject_creation");
        assertTrue(gateway.create(GROUP,USER,Festival.active("new-festival","New festival"),List.of(new Band("Airbourne"))).success());
        assertEquals(new JSONObject(requests.get(0)).getString("p_request_id"),new JSONObject(requests.get(1)).getString("p_request_id"));
        assertEquals(1,database.festivalLineups().findByFestival("new-festival").size());
    }

    @Test public void aliasCacheFailureRollsBackFestivalCatalogAndPlanningPublication() throws Exception {
        JSONObject reply=new JSONObject(response(List.of("Airbourne")));
        reply.put("aliases",new JSONArray().put(new JSONObject().put("alias_name","Airborn").put("band_id","airbourne")
            .put("band_name","Airbourne").put("confirmed_by",USER).put("confirmed_at","2026-10-17T12:00:00Z")));
        database.getOpenHelper().getWritableDatabase().execSQL("CREATE TRIGGER reject_alias BEFORE INSERT ON band_aliases BEGIN SELECT RAISE(ABORT,'Alias cache fault'); END");
        try {new RoomFestivalCreationCache(database).accept(SupabaseFestivalCreationGateway.parse(reply.toString(),GROUP,USER));fail("Expected rollback");}
        catch(RuntimeException expected) {}
        assertEquals(1,database.festivals().findAll().size());assertEquals("ACTIVE",database.festivals().findAll().get(0).status);
        assertEquals(1,database.bands().findAll().size());assertTrue(database.festivalLineups().findAll().isEmpty());
        assertTrue(database.festivalPlanningRatings().findAll().isEmpty());assertTrue(database.bandAliases().findAll().isEmpty());
    }

    @Test public void actualAddScreenStaysResponsiveAndPublishesTheEntireCsv() throws Exception {
        String csv=read(instrumentation.getContext().getAssets().open("bands.csv"));
        List<String> names=FestivalCsvReader.bandNames(csv);
        assertEquals(63,names.size());
        CountDownLatch started=new CountDownLatch(1),release=new CountDownLatch(1);
        AtomicReference<Thread> worker=new AtomicReference<>();
        String serverResult=response(names);
        FestivalCreationGateway gateway=new SupabaseFestivalCreationGateway(new SupabaseMasterDataClient((m,p,b,u)->{
            worker.set(Thread.currentThread());started.countDown();
            try {if(!release.await(10,TimeUnit.SECONDS))throw new IOException("Test worker timeout");}
            catch(InterruptedException error){Thread.currentThread().interrupt();throw new IOException(error);}
            return serverResult;
        }),new RoomFestivalCreationCache(database));
        openScreen(csv,gateway);
        Button button=(Button)field(activity,"importButton");
        try {
            instrumentation.runOnMainSync(button::performClick);
            confirmReviewSummary();
            assertTrue(started.await(10,TimeUnit.SECONDS));
            instrumentation.runOnMainSync(()->{
                assertFalse(button.isEnabled());
                assertTrue(((TextView)fieldUnchecked(activity,"resultMessage")).getText().toString().contains("Adding festival"));
            });
            assertNotSame(Looper.getMainLooper().getThread(),worker.get());
        } finally {release.countDown();}
        awaitFeedback("Added ");
        assertEquals(63,database.festivalLineups().findByFestival("new-festival").size());
        assertEquals("Golden biography",new RoomBandRepository(database).findByName("Any given Day").orElseThrow().biography().orElseThrow());
        assertTrue(new RoomBandRepository(database).findByName("Any Given Day").isPresent());
    }

    @Test public void screenFailureRestoresButtonAndLeavesCacheUntouched() throws Exception {
        openScreen("name\nAirbourne\n",new SupabaseFestivalCreationGateway(
                new SupabaseMasterDataClient((m,p,b,u)->{throw new IOException("Network unavailable");}),new RoomFestivalCreationCache(database)));
        instrumentation.runOnMainSync(()->((Button)fieldUnchecked(activity,"importButton")).performClick());
        confirmReviewSummary();
        awaitFeedback("Festival creation could not be confirmed");
        instrumentation.runOnMainSync(()->assertTrue(((Button)fieldUnchecked(activity,"importButton")).isEnabled()));
        assertEquals(1,database.festivals().findAll().size());
        assertEquals(1,database.bands().findAll().size());
        assertTrue(database.festivalLineups().findAll().isEmpty());
    }

    @Test public void malformedResponseCannotPublishAnotherUsersData() throws Exception {
        String invalid=new JSONObject(response(List.of("Airbourne"))).put("user_id","someone-else").toString();
        SupabaseFestivalCreationGateway gateway=new SupabaseFestivalCreationGateway(new SupabaseMasterDataClient((m,p,b,u)->invalid),new RoomFestivalCreationCache(database));
        try {gateway.create(GROUP,USER,Festival.active("new-festival","New festival"),List.of(new Band("Airbourne")));fail("Expected session mismatch");}
        catch(SupabaseSyncException expected){assertTrue(expected.getMessage().contains("Retry"));}
        assertEquals(1,database.festivals().findAll().size());
    }

    @Test public void malformedCsvIsRejectedOnScreenBeforeAnyRemoteCall() throws Exception {
        openScreen("band_id,title\nfirst,Airbourne\n",(g,u,f,b)->{throw new AssertionError("Malformed CSV reached persistence");});
        instrumentation.runOnMainSync(()->((Button)fieldUnchecked(activity,"importButton")).performClick());
        awaitFeedback("Festival was not added.");
        instrumentation.runOnMainSync(()->assertTrue(((Button)fieldUnchecked(activity,"importButton")).isEnabled()));
        assertEquals(1,database.festivals().findAll().size());
        assertTrue(database.festivalLineups().findAll().isEmpty());
    }

    private void openScreen(String csv,FestivalCreationGateway gateway) throws Exception {
        new AuthSessionStore(instrumentation.getTargetContext()).save(new AuthSession("fixture","fixture",USER,"fixture@example.invalid",Long.MAX_VALUE,GROUP,"member"));
        activity=(ImportCsvActivity)instrumentation.startActivitySync(new Intent(instrumentation.getTargetContext(),ImportCsvActivity.class)
                .putExtra(ImportCsvActivity.EXTRA_ADD_FESTIVAL_MODE,true).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        Object selection=field(activity,"bandsCsv");
        Field contents=selection.getClass().getDeclaredField("contents");contents.setAccessible(true);contents.set(selection,csv);
        Field dependency=ImportCsvActivity.class.getDeclaredField("festivalCreation");dependency.setAccessible(true);dependency.set(activity,gateway);
        try {
            List<CanonicalBand> catalog=FestivalCsvReader.bandNames(csv).stream().map(name->new CanonicalBand("fixture-"+name,new Band(name))).collect(Collectors.toList());
            Field review=ImportCsvActivity.class.getDeclaredField("festivalBandCatalog");review.setAccessible(true);
            review.set(activity,(FestivalBandCatalog)()->new FestivalBandCatalog.Snapshot(catalog,List.of()));
        } catch(FestivalCsvReader.FormatException malformedFixture) { /* Rejected before review. */ }
        instrumentation.runOnMainSync(()->((EditText)fieldUnchecked(activity,"festivalName")).setText("New festival"));
    }
    private void confirmReviewSummary() throws Exception {
        long deadline=SystemClock.elapsedRealtime()+20000;
        while(SystemClock.elapsedRealtime()<deadline) {
            AtomicReference<android.app.AlertDialog> dialog=new AtomicReference<>();
            instrumentation.runOnMainSync(()->{
                Object review=fieldUnchecked(activity,"bandReview");
                if(review!=null)dialog.set((android.app.AlertDialog)fieldUnchecked(review,"dialog"));
            });
            if(dialog.get()!=null&&dialog.get().isShowing()) {
                instrumentation.runOnMainSync(()->dialog.get().getButton(android.app.AlertDialog.BUTTON_POSITIVE).performClick());return;
            }
            SystemClock.sleep(25);
        }
        AtomicReference<String> feedback=new AtomicReference<>();
        instrumentation.runOnMainSync(()->feedback.set(((TextView)fieldUnchecked(activity,"resultMessage")).getText().toString()));
        fail("Review confirmation did not appear: "+feedback.get());
    }
    private void awaitFeedback(String prefix) throws Exception {
        long deadline=SystemClock.elapsedRealtime()+10000;
        AtomicReference<String> feedback=new AtomicReference<>("");
        while(SystemClock.elapsedRealtime()<deadline) {
            instrumentation.runOnMainSync(()->feedback.set(((TextView)fieldUnchecked(activity,"resultMessage")).getText().toString()));
            if(feedback.get().startsWith(prefix))return;
            SystemClock.sleep(25);
        }
        fail("Expected "+prefix+" but saw "+feedback.get());
    }
    private static Object field(Object target,String name) throws Exception {
        Field field=target.getClass().getDeclaredField(name);field.setAccessible(true);return field.get(target);
    }
    private static Object fieldUnchecked(Object target,String name) {
        try{return field(target,name);}catch(Exception error){throw new AssertionError(error);}
    }
    private String read(InputStream stream) throws IOException {
        try(BufferedReader reader=new BufferedReader(new InputStreamReader(stream,StandardCharsets.UTF_8))){return reader.lines().collect(Collectors.joining("\n"));}
    }
    private String response(List<String> names) throws Exception {
        JSONObject festival=new JSONObject().put("id","new-festival").put("name","New festival").put("status","ACTIVE");
        JSONArray bands=new JSONArray(),lineup=new JSONArray(),ratings=new JSONArray();
        for(String name:names) {
            bands.put(new JSONObject().put("name",name).put("biography_html","Server biography"));
            lineup.put(new JSONObject().put("festival_id","new-festival").put("band_name",name).put("uploaded_display_name",name));
        }
        if(names.contains("Airbourne"))ratings.put(new JSONObject().put("band_name","Airbourne").put("rating",4));
        return new JSONObject().put("group_id",GROUP).put("user_id",USER).put("festival",festival)
                .put("festivals",new JSONArray().put(new JSONObject().put("id","wacken-2026").put("name","Wacken history").put("status","ARCHIVED")).put(festival))
                .put("bands",bands).put("lineup",lineup).put("planning_ratings",ratings)
                .put("reused_bands",0).put("created_bands",names.size()).put("prefilled_ratings",ratings.length()).put("aliases",new JSONArray()).toString();
    }
}
