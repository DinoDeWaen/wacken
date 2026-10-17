package be.wacken.planner;

import android.app.*;
import android.content.Intent;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;
import be.wacken.planner.application.*;
import be.wacken.planner.domain.*;
import be.wacken.planner.infrastructure.*;
import be.wacken.planner.persistence.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import org.junit.*;
import static org.junit.Assert.*;

public final class LocalCsvImportReviewAndroidTest {
    private final Instrumentation ui=InstrumentationRegistry.getInstrumentation();
    private WackenDatabase db;
    private Path directory;
    private ImportCsvActivity activity;
    private ReviewFestivalCsvUseCase useCase;
    private BandRepository catalog;
    private PerformanceRepository performances;
    private final AtomicInteger factories=new AtomicInteger();
    private final Band known=new Band("Known",Optional.of("Golden biography"),Optional.empty(),Optional.of("https://youtube.test/known"),Optional.empty());
    private static final List<String> OPINIONS=List.of("ratings","real_ratings","festival_planning_ratings","personal_band_rating_events","festival_after_ratings","personal_band_ratings","rating_corrections","band_aliases","schedule_locks");
    @Before public void setup() throws Exception {
        db=Room.inMemoryDatabaseBuilder(ui.getTargetContext(),WackenDatabase.class).allowMainThreadQueries().build();
        directory=Files.createTempDirectory(ui.getTargetContext().getCacheDir().toPath(),"review-csv-");
        catalog=new SyncedBandRepository(new RoomBandRepository(db),new FileBackedBandRepository(directory));
        StageRepository stages=new SyncedStageRepository(new RoomStageRepository(db),new FileBackedStageRepository(directory));
        performances=new SyncedPerformanceRepository(new RoomPerformanceRepository(db),new FileBackedPerformanceRepository(directory));
        StageDistanceRepository distances=new SyncedStageDistanceRepository(new RoomStageDistanceRepository(db),new FileBackedStageDistanceRepository(directory));
        FoodOptionRepository food=new SyncedFoodOptionRepository(new RoomFoodOptionRepository(db),new FileBackedFoodOptionRepository(directory));
        catalog.replaceAll(List.of(known,new Band("Other")));
        stages.replaceAll(List.of(new Stage("Main")));
        performances.replaceAll(List.of(new Performance(new Band("Other"),new Stage("Main"),LocalDateTime.parse("2026-08-01T18:00:00"),LocalDateTime.parse("2026-08-01T19:00:00"),"history")));
        distances.replaceAll(List.of());food.replaceAll(List.of(new FoodOption("Original food")));
        new RoomFestivalRepository(db).replaceAll(List.of(Festival.archived("history","Historical"),Festival.active("active","Current")));
        new RoomFestivalLineupRepository(db).saveAllForFestival("active",List.of(new FestivalLineupEntry("active",known,"Original spelling")));
        new RoomFestivalLineupRepository(db).saveAllForFestival("history",List.of(new FestivalLineupEntry("history",new Band("Other"),"Other")));
        db.festivalPlanningRatings().save(new RoomFestivalPlanningRating("g","member","active","Known",5,"PENDING"));
        db.festivalPlanningRatings().save(new RoomFestivalPlanningRating("g","other-member","active","Known",2,"SYNCED"));
        db.ratings().save(new RoomRating("member","Known",4,"g","PENDING"));
        new RoomFestivalAfterRatingRepository(db).save(new FestivalAfterRating(UUID.randomUUID().toString(),"member","active",known,Rating.of(3),Instant.parse("2027-08-01T20:00:00Z")));
        db.ratingStates().saveCurrent(new RoomPersonalBandRating("member","Known",4,"history","Historical",UUID.randomUUID().toString(),"2026-08-01T20:00:00Z"));
        new RoomBandAliasRepository(db).replaceAll(List.of(new ConfirmedBandAlias("Known alias",new CanonicalBand("known-id",known),"member",Instant.EPOCH)));
        useCase=new ReviewFestivalCsvUseCase(catalog,stages,performances,distances,food,new RoomFestivalRepository(db),
            new RoomFestivalLineupRepository(db),new TsvImportTransaction(directory,db::runInTransaction));
    }
    @After public void close() throws Exception {
        if(activity!=null)ui.runOnMainSync(activity::finish);
        ui.waitForIdleSync();db.close();
        try(var paths=Files.walk(directory)) {for(Path path:paths.sorted(Comparator.reverseOrder()).collect(java.util.stream.Collectors.toList()))Files.deleteIfExists(path);}
    }
    @Test public void actualReviewAppliesOnlyCheckedAdditionAndKeepsAllOpinionsMetadataAndArchive() throws Exception {
        Map<String,List<String>> before=rows(OPINIONS);
        open("band_id,name\nnew,New\n");awaitReview();
        assertEquals(2,state().review.changes().size());
        ui.runOnMainSync(()->{
            assertFalse(dialog().getButton(AlertDialog.BUTTON_POSITIVE).isEnabled());
            accept("BAND:New");dialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick();
        });
        await(()->field(activity,"localReview")==null);
        assertEquals(before,rows(OPINIONS));assertEquals(Optional.of(known),catalog.findByName("Known"));
        assertEquals(2,db.festivalLineups().findByFestival("active").size());
        assertEquals("Original spelling",db.festivalLineups().findByFestival("active").stream().filter(r->r.bandName.equals("Known")).findFirst().orElseThrow().uploadedDisplayName);
        assertEquals(1,db.festivalLineups().findByFestival("history").size());
        assertEquals("history",performances.findAll().get(0).festivalId());
        assertEquals(1,factories.get());
    }
    @Test public void cancelReviewDoesNotWriteAnyTsvOrRoomData() throws Exception {
        Map<String,List<String>> before=rows(List.of("bands","festival_lineup_entries","festival_planning_ratings"));
        Map<String,String> files=files();
        open("band_id,name\nnew,New\n");awaitReview();
        ui.runOnMainSync(()->{accept("BAND:New");dialog().getButton(AlertDialog.BUTTON_NEGATIVE).performClick();});
        await(()->field(activity,"localReview")==null);
        assertEquals(before,rows(List.of("bands","festival_lineup_entries","festival_planning_ratings")));assertEquals(files,files());
    }
    @Test public void invalidRowsAreVisibleAndCannotBeAccepted() throws Exception {
        open("band_id,name\nbad,\n");awaitReview();
        assertEquals(1,state().review.changes().size());assertFalse(state().review.changes().get(0).valid());
        ui.runOnMainSync(()->{
            ListView list=(ListView)field(controller(),"list");
            View row=list.getAdapter().getView(0,null,list);
            CheckBox check=(CheckBox)((android.view.ViewGroup)row).getChildAt(0);
            assertFalse(check.isEnabled());assertFalse(dialog().getButton(AlertDialog.BUTTON_POSITIVE).isEnabled());
        });
    }
    @Test public void rotationRetainsTheSameReviewAndAcceptedRowsWithoutRepeatingParsing() throws Exception {
        open("band_id,name\nnew,New\n");awaitReview();var retained=state();
        ui.runOnMainSync(()->accept("BAND:New"));
        Activity old=activity;ui.runOnMainSync(old::recreate);
        AtomicReference<ImportCsvActivity> next=new AtomicReference<>();
        await(()->{
            for(Activity candidate:androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED))
                if(candidate instanceof ImportCsvActivity&&candidate!=old)next.set((ImportCsvActivity)candidate);
            return next.get()!=null;
        });
        activity=next.get();awaitReview();assertSame(retained,state());assertTrue(state().accepted.contains("BAND:New"));assertEquals(1,factories.get());
        ui.runOnMainSync(()->assertTrue(dialog().getButton(AlertDialog.BUTTON_POSITIVE).isEnabled()));
    }
    @Test public void finalLineupFailureRollsBackAllFiveFilesCacheAndPendingData() throws Exception {
        var review=useCase.review(new FestivalCsvFiles("band_id,name\nnew,New\n","","","",""));
        Set<String> accepted=Set.of(review.changes().stream().filter(r->r.identity().equals("New")).findFirst().orElseThrow().key());
        Map<String,List<String>> before=rows(List.of("bands","stages","performances","stage_distances","food_options","festival_lineup_entries","festival_planning_ratings","personal_band_rating_events","festival_after_ratings","personal_band_ratings","band_aliases"));
        Map<String,String> files=files();
        db.getOpenHelper().getWritableDatabase().execSQL("CREATE TRIGGER reject_import BEFORE INSERT ON festival_lineup_entries BEGIN SELECT RAISE(ABORT,'Injected review fault'); END");
        assertFalse(useCase.apply(review,accepted).success());
        assertEquals(before,rows(new ArrayList<>(before.keySet())));assertEquals(files,files());
    }
    @Test public void acceptedLineupRemovalKeepsCatalogAllRatingsAndOtherFestivalData() throws Exception {
        Map<String,List<String>> before=rows(OPINIONS);
        var review=useCase.review(new FestivalCsvFiles("band_id,name\n","","","",""));
        assertTrue(useCase.apply(review,Set.of(review.changes().get(0).key())).success());
        assertTrue(db.festivalLineups().findByFestival("active").isEmpty());assertEquals(Optional.of(known),catalog.findByName("Known"));
        assertEquals(before,rows(OPINIONS));assertEquals(1,db.festivalLineups().findByFestival("history").size());
        assertEquals("history",performances.findAll().get(0).festivalId());
    }
    private void open(String csv) {
        activity=(ImportCsvActivity)ui.startActivitySync(new Intent(ui.getTargetContext(),ImportCsvActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        ui.runOnMainSync(()->{
            set(field(activity,"bandsCsv"),"contents",csv);
            set(activity,"localImportFactory",(Supplier<ReviewFestivalCsvUseCase>)()->{
                assertNotSame(Looper.getMainLooper().getThread(),Thread.currentThread());factories.incrementAndGet();return useCase;
            });
            ((Button)field(activity,"importButton")).performClick();
        });
    }
    private LocalCsvImportReviewDialog controller(){return (LocalCsvImportReviewDialog)field(activity,"localReview");}
    private LocalCsvImportReviewDialog.State state(){return controller().state;}
    private AlertDialog dialog(){return (AlertDialog)field(controller(),"dialog");}
    private void awaitReview() throws Exception {await(()->controller()!=null&&!state().busy&&state().review!=null);}
    private void accept(String key) {
        ListView list=(ListView)field(controller(),"list");
        for(int i=0;i<list.getAdapter().getCount();i++) {
            View row=list.getAdapter().getView(i,null,list);
            CheckBox checkbox=(CheckBox)((ViewGroup)row).getChildAt(0);
            if(("Accept "+key).contentEquals(checkbox.getContentDescription())){checkbox.setChecked(true);return;}
        }
        fail("Missing reviewed row "+key);
    }
    private Map<String,List<String>> rows(List<String> tables) {
        Map<String,List<String>> result=new TreeMap<>();
        for(String table:tables) {
            List<String> data=new ArrayList<>();
            try(var cursor=db.getOpenHelper().getReadableDatabase().query("SELECT * FROM "+table)) {
                while(cursor.moveToNext()) {
                    List<String> cells=new ArrayList<>();
                    for(int i=0;i<cursor.getColumnCount();i++)cells.add(cursor.isNull(i)?"<null>":cursor.getString(i));
                    data.add(cells.toString());
                }
            }
            Collections.sort(data);result.put(table,data);
        }
        return result;
    }
    private Map<String,String> files() throws Exception {
        Map<String,String> result=new TreeMap<>();
        for(String name:List.of("bands.tsv","stages.tsv","performances.tsv","stage-distances.tsv","food-options.tsv"))
            result.put(name,Files.exists(directory.resolve(name))?Base64.getEncoder().encodeToString(Files.readAllBytes(directory.resolve(name))):"<absent>");
        return result;
    }
    private void await(BooleanSupplier condition) throws Exception {
        long until=SystemClock.elapsedRealtime()+15000;AtomicBoolean ready=new AtomicBoolean();
        while(SystemClock.elapsedRealtime()<until){ui.runOnMainSync(()->ready.set(condition.getAsBoolean()));if(ready.get())return;SystemClock.sleep(25);}
        fail("Local import review did not reach expected state");
    }
    private static Object field(Object target,String name){try{var f=target.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(target);}catch(Exception error){throw new AssertionError(error);}}
    private static void set(Object target,String name,Object value){try{var f=target.getClass().getDeclaredField(name);f.setAccessible(true);f.set(target,value);}catch(Exception error){throw new AssertionError(error);}}
}
