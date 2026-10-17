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
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import org.junit.*;

public final class ArchivedRatingCorrectionAndroidTest {
    private static final String USER="task172",FESTIVAL="task172-archived",OTHER="task172-other",NAME="TASK-172 Native Band";
    private Instrumentation instrumentation;
    private WackenDatabase database;
    private AuthSession previous;
    private ArchivedBandDetailActivity activity;
    private final Instant time=Instant.parse("2026-08-01T12:00:00Z");
    private final Band band=new Band(NAME);
    private boolean failReview,failSave;
    @Before public void setup()throws Exception{
        instrumentation=InstrumentationRegistry.getInstrumentation();database=WackenDatabase.get(instrumentation.getTargetContext());
        previous=new AuthSessionStore(instrumentation.getTargetContext()).load();
        new AuthSessionStore(instrumentation.getTargetContext()).save(new AuthSession("fixture","fixture",USER,"fixture@example.invalid",Long.MAX_VALUE,"task172","member"));
        new RoomBandRepository(database).save(band);
        new RoomFestivalRepository(database).save(Festival.archived(FESTIVAL,"Same Name"));
        new RoomFestivalRepository(database).save(Festival.archived(OTHER,"Same Name"));
        new RoomFestivalLineupRepository(database).saveAllForFestival(FESTIVAL,List.of(new FestivalLineupEntry(FESTIVAL,band,NAME)));
        new RoomFestivalPlanningRatingRepository(database).saveSynced(new SavedFestivalPlanningRating("task172",USER,FESTIVAL,band,Rating.of(3)));
        database.ratingStates().saveAfter(new RoomFestivalAfterRating(USER,FESTIVAL,NAME,UUID.randomUUID().toString(),3,time.toString(),"SYNCED",false));
        database.ratingStates().saveAfter(new RoomFestivalAfterRating(USER,OTHER,NAME,UUID.randomUUID().toString(),5,time.toString(),"SYNCED",false));
        new RoomPersonalBandRatingHistoryRepository(database).saveSynced(new PersonalBandRatingEvent(UUID.randomUUID().toString(),USER,band,Optional.of(OTHER),Rating.of(5),time,Optional.of("Same Name")));
        activity=(ArchivedBandDetailActivity)instrumentation.startActivitySync(new Intent(instrumentation.getTargetContext(),ArchivedBandDetailActivity.class)
            .putExtra(ArchivedBandDetailActivity.EXTRA_FESTIVAL_ID,FESTIVAL).putExtra(ArchivedBandDetailActivity.EXTRA_BAND_NAME,NAME).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        var field=ArchivedBandDetailActivity.class.getDeclaredField("correctionUseCase");field.setAccessible(true);
        field.set(activity,(Supplier<ArchivedRatingCorrectionUseCase>)()->new ArchivedRatingCorrectionUseCase(
            new RoomFestivalRepository(database),new RoomFestivalLineupRepository(database),gateway()));
    }
    @After public void cleanup(){
        if(activity!=null)instrumentation.runOnMainSync(activity::finish);
        new AuthSessionStore(instrumentation.getTargetContext()).save(previous);
        database.ratingStates().deleteAfterFestival(FESTIVAL);database.ratingStates().deleteAfterFestival(OTHER);
        database.festivalLineups().deleteByFestival(FESTIVAL);database.festivals().deleteById(FESTIVAL);database.festivals().deleteById(OTHER);
        database.festivalPlanningRatings().deleteByFestival(FESTIVAL);
        database.getOpenHelper().getWritableDatabase().execSQL("DELETE FROM rating_corrections WHERE userName=?",new Object[]{USER});
        database.getOpenHelper().getWritableDatabase().execSQL("DELETE FROM personal_band_rating_events WHERE userName=?",new Object[]{USER});
        database.ratingStates().clearCurrent(USER);
        database.getOpenHelper().getWritableDatabase().execSQL("DELETE FROM bands WHERE name=?",new Object[]{NAME});
    }
    @Test public void archiveIsReadOnlyUntilExplicitEditAndConfirmation()throws Exception{
        onUi(()->{
            List<RatingStarsView> stars=new ArrayList<>();collectStars(activity.getWindow().getDecorView(),stars);
            assertEquals(2,stars.size());for(var value:stars)assertFalse(value.isEnabled());
            click("Correct my archived rating");
        });
        await(()->findContaining("Current after rating: 3")!=null);
        onUi(()->{((EditText)find("Corrected archived rating")).setText("2");click("Save correction");});
        assertTrue(new RoomRatingCorrectionRepository(database).findByUser(USER).isEmpty());
        onUi(()->click("Confirm correction"));
        await(()->new RoomRatingCorrectionRepository(database).findByUser(USER).size()==1);
        await(()->findContaining("3 → 2")!=null);
        assertEquals(2,database.ratingStates().after(USER,FESTIVAL,NAME).value);
        assertEquals(5,database.ratingStates().after(USER,OTHER,NAME).value);
    }
    @Test public void offlineReviewAndCancelNeverWriteAStagedCorrection()throws Exception{
        failReview=true;onUi(()->click("Correct my archived rating"));
        await(()->findContaining("Could not load review")!=null);
        onUi(()->{
            ((EditText)find("Corrected archived rating")).setText("2");
            assertFalse(find("Save correction").isEnabled());click("Cancel");
        });
        assertTrue(new RoomRatingCorrectionRepository(database).findByUser(USER).isEmpty());
        assertEquals(3,database.ratingStates().after(USER,FESTIVAL,NAME).value);
    }
    @Test public void failedRemoteSaveLeavesArchiveAndAuditUnchanged()throws Exception{
        failSave=true;onUi(()->click("Correct my archived rating"));
        await(()->findContaining("Current after rating: 3")!=null);
        onUi(()->{((EditText)find("Corrected archived rating")).setText("2");click("Save correction");click("Confirm correction");});
        await(()->findContaining("Injected remote failure")!=null);
        assertTrue(new RoomRatingCorrectionRepository(database).findByUser(USER).isEmpty());
        assertEquals(3,database.ratingStates().after(USER,FESTIVAL,NAME).value);
        onUi(()->click("Cancel"));
    }
    @Test public void planningCorrectionCanBecomeUnratedWithoutChangingAfterRating()throws Exception{
        onUi(()->click("Correct my archived rating"));
        await(()->findContaining("Current after rating: 3")!=null);
        onUi(()->((Spinner)find("Archived rating kind")).setSelection(1));
        await(()->findContaining("Current planning rating: 3")!=null);
        onUi(()->{((EditText)find("Corrected archived rating")).setText("0");click("Save correction");click("Confirm correction");});
        await(()->new RoomRatingCorrectionRepository(database).findByUser(USER).size()==1);
        assertEquals(0,database.festivalPlanningRatings().findExact("task172",USER,FESTIVAL,NAME).value);
        assertEquals(3,database.ratingStates().after(USER,FESTIVAL,NAME).value);
    }
    @Test public void missingAfterRatingDoesNotBorrowAnotherFestivalWithTheSameName()throws Exception{
        database.ratingStates().deleteAfterFestival(FESTIVAL);
        onUi(()->{
            try{
                var refresh=ArchivedBandDetailActivity.class.getDeclaredMethod("refresh");refresh.setAccessible(true);refresh.invoke(activity);
                List<RatingStarsView> stars=new ArrayList<>();collectStars(activity.getWindow().getDecorView(),stars);
                var value=RatingStarsView.class.getDeclaredField("savedRating");value.setAccessible(true);
                assertEquals(0,value.getInt(stars.get(1)));
            }catch(ReflectiveOperationException error){throw new AssertionError(error);}
        });
        assertEquals(5,database.ratingStates().after(USER,OTHER,NAME).value);
    }
    private ArchivedRatingCorrectionGateway gateway(){
        return new ArchivedRatingCorrectionGateway(){
            public ArchivedRatingReview review(String user,String festival,Band target,ArchivedRatingKind kind){
                if(failReview)throw new SupabaseSyncException("Offline",null);
                return new ArchivedRatingReview(user,festival,target,kind,3,7,Optional.of(time));
            }
            public RatingCorrection correct(String operation,ArchivedRatingReview expected,int value){
                if(failSave)throw new SupabaseSyncException("Injected remote failure",null);
                var cache=new RoomArchivedRatingCorrectionCache(database,"task172",USER);
                var pending=cache.capture(expected);
                var audit=new RatingCorrection(operation,USER,FESTIVAL,"Same Name",band,expected.kind(),3,value,7,Optional.of(time),time.plusSeconds(60));
                var all=new ArrayList<FestivalAfterRating>(new RoomFestivalAfterRatingRepository(database).findByUser(USER));
                if(expected.kind()==ArchivedRatingKind.AFTER){
                    all.removeIf(a->a.festivalId().equals(FESTIVAL));
                    all.add(new FestivalAfterRating(database.ratingStates().after(USER,FESTIVAL,NAME).operationId,USER,FESTIVAL,band,Rating.of(value),time));
                }
                cache.apply(new SupabaseArchivedRatingCorrectionGateway.Snapshot(
                    new ArchivedRatingReview(USER,FESTIVAL,band,expected.kind(),value,8,Optional.of(time)),audit,pending,all,List.of()));
                return audit;
            }
            public List<RatingCorrection> history(String user,String festival,Band target){return List.of();}
        };
    }
    private void onUi(Runnable action){instrumentation.runOnMainSync(action);}
    private void click(String text){View view=find(text);assertNotNull(text,view);assertTrue(view.isEnabled());view.performClick();}
    private View find(String text){return findRoot(text,false);}
    private View findContaining(String text){return findRoot(text,true);}
    private View findRoot(String text,boolean contains){
        try{
            var owner=ArchivedBandDetailActivity.class.getDeclaredField("correctionDialog");owner.setAccessible(true);
            Object controller=owner.get(activity);
            if(controller!=null)for(String name:List.of("confirmation","dialog")){
                var field=controller.getClass().getDeclaredField(name);field.setAccessible(true);
                AlertDialog dialog=(AlertDialog)field.get(controller);
                if(dialog!=null&&dialog.isShowing()){
                    View value=findView(dialog.getWindow().getDecorView(),text,contains);if(value!=null)return value;
                }
            }
        }catch(ReflectiveOperationException error){throw new AssertionError(error);}
        for(Activity candidate:androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED)){
            View value=findView(candidate.getWindow().getDecorView(),text,contains);if(value!=null)return value;
        }
        return findView(activity.getWindow().getDecorView(),text,contains);
    }
    private View findView(View view,String text,boolean contains){
        if(!contains&&text.contentEquals(view.getContentDescription()==null?"":view.getContentDescription()))return view;
        if(view instanceof TextView label&&(contains?label.getText().toString().contains(text):text.contentEquals(label.getText())
                ||text.contentEquals(label.getContentDescription()==null?"":label.getContentDescription())))return view;
        if(view instanceof ViewGroup group)for(int i=0;i<group.getChildCount();i++){View match=findView(group.getChildAt(i),text,contains);if(match!=null)return match;}
        return null;
    }
    private void collectStars(View view,List<RatingStarsView> stars){
        if(view instanceof RatingStarsView rating){stars.add(rating);return;}
        if(view instanceof ViewGroup group)for(int i=0;i<group.getChildCount();i++)collectStars(group.getChildAt(i),stars);
    }
    private void await(BooleanSupplier condition)throws Exception{
        long deadline=SystemClock.elapsedRealtime()+10000;AtomicBoolean ready=new AtomicBoolean();
        while(SystemClock.elapsedRealtime()<deadline){onUi(()->ready.set(condition.getAsBoolean()));if(ready.get())return;SystemClock.sleep(25);}
        fail("Archived correction did not reach the expected state.");
    }
}
