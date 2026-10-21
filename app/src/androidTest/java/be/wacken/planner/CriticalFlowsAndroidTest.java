package be.wacken.planner;

import static org.junit.Assert.*;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.test.platform.app.InstrumentationRegistry;
import be.wacken.planner.domain.Band;
import be.wacken.planner.persistence.*;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** Native characterization of real Activity wiring; run with scripts/verify-android-smoke.sh. */
public final class CriticalFlowsAndroidTest {
    private static final String BAND="TASK-174 Smoke Band", FESTIVAL="task-174-smoke";
    private static final String STAGE="TASK-174 Smoke Stage", USER="task-174-user", GROUP="task-174-group";
    private Instrumentation instrumentation;
    private WackenDatabase database;
    private AuthSession previousSession;
    private List<RoomFestival> previousFestivals;
    private Optional<Band> previousBand;
    private RoomStage previousStage;
    private int previousMarker;
    private Activity activity;
    private boolean seeded;

    @Before public void seedOfflineFixture() throws Exception {
        instrumentation=InstrumentationRegistry.getInstrumentation();
        assertEquals("Use the smoke runner: Wi-Fi must be disabled", "0", shell("settings get global wifi_on"));
        assertEquals("Use the smoke runner: cellular radio must be disabled", "1", shell("settings get global airplane_mode_on"));
        database=WackenDatabase.get(instrumentation.getTargetContext());
        previousSession=new AuthSessionStore(instrumentation.getTargetContext()).load();
        previousFestivals=database.festivals().findAll();
        previousBand=new RoomBandRepository(database).findByName(BAND);
        previousStage=database.stages().findByName(STAGE);
        previousMarker=database.festivalCacheMarker().count();
        assertTrue("Fixture ID must be unused", previousFestivals.stream().noneMatch(f->f.id.equals(FESTIVAL)));
        database.runInTransaction(()->{
            for(RoomFestival f:previousFestivals)database.festivals().save(new RoomFestival(f.id,f.name,"ARCHIVED"));
            new RoomFestivalRepository(database).save(be.wacken.planner.domain.Festival.active(FESTIVAL,"Native smoke festival"));
            new RoomBandRepository(database).save(new Band(BAND));
            database.stages().save(new RoomStage(STAGE));
            database.festivalLineups().saveAll(List.of(new RoomFestivalLineupEntry(FESTIVAL,BAND,BAND)));
            database.performances().save(new RoomPerformance(FESTIVAL,BAND,STAGE,"2027-08-19T18:00:00","2027-08-19T19:00:00"));
            database.festivalPlanningRatings().save(new RoomFestivalPlanningRating(GROUP,USER,FESTIVAL,BAND,4,"PENDING"));
        });
        seeded=true;
        new AuthSessionStore(instrumentation.getTargetContext()).save(
                new AuthSession("offline-fixture","offline-fixture",USER,"smoke@example.invalid",Long.MAX_VALUE,GROUP,"member"));
    }

    @After public void restoreFixture() {
        if(activity!=null)instrumentation.runOnMainSync(activity::finish);
        if(!seeded)return;
        database.runInTransaction(()->{
            database.festivalLineups().deleteByFestival(FESTIVAL);
            database.performances().deleteByFestival(FESTIVAL);
            database.festivalPlanningRatings().deleteByFestival(FESTIVAL);
            database.ratingStates().deleteAfterFestival(FESTIVAL);
            database.getOpenHelper().getWritableDatabase().execSQL("DELETE FROM personal_band_ratings WHERE userName = ? AND bandName = ?",new Object[]{USER,BAND});
            database.getOpenHelper().getWritableDatabase().execSQL("DELETE FROM personal_band_rating_events WHERE userName = ? AND bandName = ?",new Object[]{USER,BAND});
            database.festivals().deleteById(FESTIVAL);
            database.festivals().saveAll(previousFestivals);
            if(previousBand!=null&&previousBand.isPresent())new RoomBandRepository(database).save(previousBand.get());
            else database.getOpenHelper().getWritableDatabase().execSQL("DELETE FROM bands WHERE name = ?",new Object[]{BAND});
            if(previousStage!=null)database.stages().save(previousStage);
            else database.getOpenHelper().getWritableDatabase().execSQL("DELETE FROM stages WHERE name = ?",new Object[]{STAGE});
            if(previousMarker==0)database.getOpenHelper().getWritableDatabase().execSQL("DELETE FROM festival_cache_marker");
        });
        new AuthSessionStore(instrumentation.getTargetContext()).save(previousSession);
    }

    @Test public void cachedStartupRendersLineupWithoutNetworkOrLoginRedirect() throws Exception {
        open(MainActivity.class);
        await(()->findText(root(),BAND)!=null);
        onUi(()->{
            assertFalse(activity.isFinishing());
            assertNotNull(findText(root(),"Native smoke festival"));
            assertNotNull(findText(root(),BAND));
        });
        // Wait for the real offline background refresh to finish before restoring session/data.
        await(()->!booleanField(activity,"syncInProgress"));
        assertEquals(1,database.festivalPlanningRatings().findPending(GROUP,USER).size());
        assertNotNull(new RoomBandRepository(database).findByName(BAND).orElse(null));
    }

    @Test public void appearanceChangesKeepSelectedFestivalAndPendingRatingWithoutHistoryWrites() throws Exception {
        Object previous=AppearanceController.store(instrumentation.getTargetContext()).getAll().get(AppearanceController.MODE);
        open(BandDetailActivity.class);
        int historyBefore;
        try(var cursor=database.getOpenHelper().getReadableDatabase().query(
                "SELECT COUNT(*) FROM personal_band_rating_events WHERE userName = ? AND bandName = ?",new Object[]{USER,BAND})) {
            assertTrue(cursor.moveToFirst());historyBefore=cursor.getInt(0);
        }
        try {
            onUi(()->{
                View context=findDescription(root(),"Rating context");
                AppearanceController.choose(activity,AppearancePreference.DARK);
                AppearanceController.toggle(activity);
                assertSame(context,findDescription(root(),"Rating context"));
                assertNotNull(findText(root(),BAND));
            });
            var pending=database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND);
            assertEquals(4,pending.value);assertEquals("PENDING",pending.syncStatus);
            try(var cursor=database.getOpenHelper().getReadableDatabase().query(
                    "SELECT COUNT(*) FROM personal_band_rating_events WHERE userName = ? AND bandName = ?",new Object[]{USER,BAND})) {
                assertTrue(cursor.moveToFirst());assertEquals(historyBefore,cursor.getInt(0));
            }
        } finally {
            onUi(()->{
                var editor=AppearanceController.store(activity).edit();
                if(previous instanceof String mode)editor.putString(AppearanceController.MODE,mode);
                else editor.remove(AppearanceController.MODE);
                editor.commit();
            });
        }
    }

    @Test public void settingsChoicesAndQuickToggleShareOnePreference() throws Exception {
        Object previous=AppearanceController.store(instrumentation.getTargetContext()).getAll().get(AppearanceController.MODE);
        open(SettingsActivity.class);
        try {
            onUi(()->{
                AppearanceController.choose(activity,AppearancePreference.SYSTEM);
                assertTrue(((android.widget.RadioButton)findText(root(),"System")).isChecked());
                findText(root(),"Field Notes light").performClick();
                assertEquals(AppearancePreference.LIGHT,AppearanceController.preference(activity));
                assertTrue(findText(root(),"Switch to dark").performClick());
                assertTrue(((android.widget.RadioButton)findText(root(),"Ironline dark")).isChecked());
                assertFalse(((android.widget.RadioButton)findText(root(),"Field Notes light")).isChecked());
                assertEquals(AppearancePreference.DARK,AppearanceController.preference(activity));
            });
        } finally {
            onUi(()->{
                var editor=AppearanceController.store(activity).edit();
                if(previous instanceof String mode)editor.putString(AppearanceController.MODE,mode);
                else editor.remove(AppearanceController.MODE);
                editor.commit();
            });
        }
    }

    @Test public void activeAndArchivedMissingMetadataKeepTheirDistinctAvailableActions() throws Exception {
        open(BandDetailActivity.class);
        onUi(()->{
            assertNotNull(findText(root(),BAND));
            assertNotNull(findText(root(),"Festival planning rating"));
            assertNotNull(findText(root(),"Festival after-rating"));
            assertNotNull(findText(root(),"Running Order"));
            assertNotNull(findText(root(),"Band Links"));
            assertNotNull(findDescription(root(),"Rating context"));
            assertTrue(((TextView)findDescription(root(),"Rating context")).getText().toString().contains("\n"));
            assertFalse(((TextView)findDescription(root(),"Rating context")).getText().toString().contains("\\n"));
            assertNull(findBandImage(root()));
            assertNull(findDescription(root(),"Open YouTube"));
            assertNull(findDescription(root(),"Open Spotify"));
            assertNotNull(findDescription(root(),"Home"));
            assertTrue(findText(root(),"Reset").isEnabled());
            assertTrue(findText(root(),"Find music links").isEnabled());
        });
        onUi(activity::finish);instrumentation.waitForIdleSync();
        new RoomFestivalRepository(database).save(be.wacken.planner.domain.Festival.archived(FESTIVAL,"Native smoke festival"));
        open(ArchivedBandDetailActivity.class);
        onUi(()->{
            assertNotNull(findText(root(),BAND));
            assertNotNull(findText(root(),"Festival planning rating"));
            assertNotNull(findText(root(),"Festival after-rating"));
            assertNotNull(findText(root(),"Correction history"));
            assertNotNull(findDescription(root(),"Rating context"));
            assertTrue(((TextView)findDescription(root(),"Rating context")).getText().toString().contains("\n"));
            assertFalse(((TextView)findDescription(root(),"Rating context")).getText().toString().contains("\\n"));
            assertNull(findBandImage(root()));
            assertNull(findDescription(root(),"Open YouTube"));
            assertNull(findDescription(root(),"Open Spotify"));
            assertNotNull(findDescription(root(),"Back"));
            assertFalse(findText(root(),"Reset").isEnabled());
            assertNull(findText(root(),"Find music links"));
        });
        assertEquals(4,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
        assertEquals("PENDING",database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).syncStatus);
    }

    @Test public void localBandImageSurvivesRecreationAndArchiveWithoutChangingPlanning() throws Exception {
        File image=File.createTempFile("band-image-", ".png", instrumentation.getTargetContext().getCacheDir());
        try {
            Bitmap bitmap=Bitmap.createBitmap(2,2,Bitmap.Config.ARGB_8888);
            try(FileOutputStream output=new FileOutputStream(image)){assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG,100,output));}
            finally{bitmap.recycle();}
            new RoomBandRepository(database).save(new Band(BAND,Optional.empty(),Optional.of(image.toURI().toString()),Optional.empty(),Optional.empty()));
            open(BandDetailActivity.class);
            await(()->findBandImage(root())!=null&&findBandImage(root()).getDrawable()!=null);
            Activity previous=activity;
            onUi(previous::recreate);
            await(()->{
                for(Activity candidate:androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED)){
                    if(candidate instanceof BandDetailActivity&&candidate!=previous){activity=candidate;return true;}
                }
                return false;
            });
            await(()->findBandImage(root())!=null&&findBandImage(root()).getDrawable()!=null);
            onUi(activity::finish);instrumentation.waitForIdleSync();
            new RoomFestivalRepository(database).save(be.wacken.planner.domain.Festival.archived(FESTIVAL,"Native smoke festival"));
            open(ArchivedBandDetailActivity.class);
            await(()->findBandImage(root())!=null&&findBandImage(root()).getDrawable()!=null);
            assertEquals(4,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
            assertEquals("PENDING",database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).syncStatus);
        } finally {assertTrue(image.delete());}
    }

    @Test public void undecodableImagesHideInBothDetailsWithoutChangingPlanning() throws Exception {
        File image=File.createTempFile("invalid-band-image-", ".png", instrumentation.getTargetContext().getCacheDir());
        try {
            try(FileOutputStream output=new FileOutputStream(image)){output.write("not an image".getBytes(StandardCharsets.UTF_8));}
            new RoomBandRepository(database).save(new Band(BAND,Optional.empty(),Optional.of(image.toURI().toString()),Optional.empty(),Optional.empty()));
            open(BandDetailActivity.class);
            await(()->findBandImage(root())!=null&&findBandImage(root()).getVisibility()==View.GONE);
            onUi(activity::finish);instrumentation.waitForIdleSync();
            new RoomFestivalRepository(database).save(be.wacken.planner.domain.Festival.archived(FESTIVAL,"Native smoke festival"));
            open(ArchivedBandDetailActivity.class);
            await(()->findBandImage(root())!=null&&findBandImage(root()).getVisibility()==View.GONE);
            assertEquals(4,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
            assertEquals("PENDING",database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).syncStatus);
        } finally {assertTrue(image.delete());}
    }

    @Test public void detailTouchSavesPlanningRatingLocallyAndResetKeepsZero() throws Exception {
        open(BandDetailActivity.class);
        AtomicReference<RatingStarsView> stars=new AtomicReference<>();
        onUi(()->stars.set(findStars(root())));
        assertNotNull("The actual detail rating control must render",stars.get());
        onUi(()->{
            RatingStarsView control=stars.get();
            assertTrue("Rating control must be laid out",control.getWidth()>0);
            long time=SystemClock.uptimeMillis();
            MotionEvent down=MotionEvent.obtain(time,time,MotionEvent.ACTION_DOWN,control.getWidth()*.9f,control.getHeight()/2f,0);
            MotionEvent up=MotionEvent.obtain(time,time+1,MotionEvent.ACTION_UP,control.getWidth()*.9f,control.getHeight()/2f,0);
            try {control.dispatchTouchEvent(down);control.dispatchTouchEvent(up);}
            finally {down.recycle();up.recycle();}
        });
        assertEquals(5,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
        assertEquals("PENDING",database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).syncStatus);
        onUi(()->{
            Button reset=(Button)findText(root(),"Reset");
            assertNotNull("Planning reset must remain reachable",reset);reset.performClick();
        });
        assertEquals(0,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
    }

    @Test public void scheduleRendersCachedPerformanceInsteadOfNullFailure() throws Exception {
        open(ScheduleActivity.class);
        onUi(()->{
            assertNotNull(findText(root(),"Group Schedule"));
            assertNotNull("A real cached act must render, including its rating stars",findContaining(root(),BAND));
            assertNotNull("Its actual cached time range must render",findText(root(),"18:00-19:00"));
            assertNull(findContaining(root(),"Could not generate"));
            assertNotNull(findText(root(),"Back to bands"));
        });
    }

    @Test public void afterRatingTouchAndResetStayAtFestivalLevelWithoutPromotingBandOpinion() throws Exception {
        database.ratingStates().saveCurrent(new RoomPersonalBandRating(USER,BAND,4,"previous","Previous festival",
                "11111111-1111-4111-8111-111111111111","2026-08-01T12:00:00Z"));
        open(BandDetailActivity.class);
        onUi(()->{
            ViewGroup section=(ViewGroup)findText(root(),"Festival after-rating").getParent();
            RatingStarsView control=findStars(section);assertNotNull(control);assertTrue(control.getWidth()>0);
            long time=SystemClock.uptimeMillis();
            MotionEvent down=MotionEvent.obtain(time,time,MotionEvent.ACTION_DOWN,control.getWidth()*.5f,control.getHeight()/2f,0);
            MotionEvent up=MotionEvent.obtain(time,time+1,MotionEvent.ACTION_UP,control.getWidth()*.5f,control.getHeight()/2f,0);
            try{control.dispatchTouchEvent(down);control.dispatchTouchEvent(up);}finally{down.recycle();up.recycle();}
        });
        var after=new RoomFestivalAfterRatingRepository(database);
        assertEquals(3,after.findByUserFestivalAndBand(USER,FESTIVAL,new be.wacken.planner.domain.Band(BAND)).orElseThrow().rating().value());
        assertEquals(4,database.ratingStates().current(USER,BAND).value);
        assertEquals(4,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
        onUi(()->{
            ViewGroup section=(ViewGroup)findText(root(),"Festival after-rating").getParent();
            findText(section,"Reset").performClick();
        });
        assertEquals(0,after.findByUserFestivalAndBand(USER,FESTIVAL,new be.wacken.planner.domain.Band(BAND)).orElseThrow().rating().value());
        assertEquals(4,database.ratingStates().current(USER,BAND).value);
        assertEquals(1,after.findPending(USER).size());
    }

    @Test public void settingsExposesEnabledExportEntryWithoutNetwork() throws Exception {
        open(SettingsActivity.class);
        onUi(()->{
            View export=findText(root(),"Export ratings CSV");
            assertNotNull(export);assertTrue(export instanceof Button);
            assertTrue(export.isEnabled());assertTrue(export.hasOnClickListeners());
        });
        assertEquals(4,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
    }

    @Test public void detailAndOfflineExportSeparateEstablishedPlanningAndAfterWithCanonicalLinks() throws Exception {
        Band canonical=new Band(BAND,Optional.of("Canonical biography"),Optional.empty(),Optional.empty(),Optional.empty());
        new RoomBandRepository(database).save(canonical);
        database.ratingStates().saveCurrent(new RoomPersonalBandRating(USER,BAND,4,"deleted","Deleted source",
                "source","2026-08-01T12:00:00Z"));
        database.festivalPlanningRatings().save(new RoomFestivalPlanningRating(GROUP,USER,FESTIVAL,BAND,5,"PENDING"));
        database.ratingStates().saveAfter(new RoomFestivalAfterRating(USER,FESTIVAL,BAND,
                "11111111-1111-4111-8111-111111111111",3,"2026-08-02T12:00:00Z","SYNCED",false));
        // A stale navigation extra cannot fabricate a missing canonical channel.
        activity=instrumentation.startActivitySync(new Intent(instrumentation.getTargetContext(),BandDetailActivity.class)
                .putExtra(BandDetailActivity.EXTRA_BAND_NAME,BAND)
                .putExtra(BandDetailActivity.EXTRA_YOUTUBE_URL,"https://youtube.test/stale").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        onUi(()->{
            assertNotNull(findContaining(root(),"Established band rating: 4"));
            assertNotNull(findContaining(root(),"Festival planning rating: 5"));
            assertNotNull(findContaining(root(),"Festival after-rating: 3"));
            assertNotNull(findContaining(root(),"Deleted source [deleted] (deleted festival)"));
            assertNotNull(findContaining(root(),"Group: T ★★★★★"));
        });
        var repositories=new AppRepositories(instrumentation.getTargetContext());
        String csv=new be.wacken.planner.application.ExportRatingLifecycleCsvUseCase(repositories.bands(),repositories.festivals(),
                repositories.festivalLineups(),repositories.festivalPlanningRatings(),repositories.afterRatings(),
                repositories.currentBandRatings(),repositories.personalBandRatings(),repositories.ratingCorrectionHistory()).export(USER);
        assertTrue(csv.contains("ESTABLISHED_BAND,"+USER+","+BAND));
        assertTrue(csv.contains("FESTIVAL_PLANNING,"+USER+","+BAND));
        assertTrue(csv.contains("FESTIVAL_AFTER,"+USER+","+BAND));
        assertFalse(csv.contains("https://youtube.test/stale"));
    }

    @Test public void missingSelectedAfterShowsUnratedWhileDetachedHistoryRemainsExportable() throws Exception {
        new RoomPersonalBandRatingHistoryRepository(database).saveSynced(new be.wacken.planner.domain.PersonalBandRatingEvent(
                "detached",USER,new Band(BAND),Optional.empty(),be.wacken.planner.domain.Rating.of(5),
                java.time.Instant.EPOCH,Optional.of("Deleted festival")));
        open(BandDetailActivity.class);
        onUi(()->{
            assertNotNull(findContaining(root(),"Festival after-rating: Unrated"));
            assertNotNull(findContaining(root(),"Deleted festival - 5 stars - date unknown"));
        });
        assertEquals(1,new AppRepositories(instrumentation.getTargetContext()).personalBandRatings().findByUser(USER).size());
    }

    @Test public void returningAndRotationKeepSelectedFestivalScrollAndRejectArchivedPlanningEdits() throws Exception {
        new RoomBandRepository(database).save(new Band(BAND,Optional.of("Long biography. ".repeat(100)),
                Optional.empty(),Optional.empty(),Optional.empty()));
        open(BandDetailActivity.class);
        Activity original=activity;
        AtomicReference<android.widget.ScrollView> scroll=new AtomicReference<>();
        onUi(()->{scroll.set(findScroll(root()));scroll.get().scrollTo(0,150);});
        AtomicReference<Integer> position=new AtomicReference<>();
        onUi(()->position.set(scroll.get().getScrollY()));
        assertTrue(position.get()>0);
        Activity settings=instrumentation.startActivitySync(new Intent(instrumentation.getTargetContext(),SettingsActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        database.festivalPlanningRatings().save(new RoomFestivalPlanningRating(GROUP,USER,FESTIVAL,BAND,5,"PENDING"));
        onUi(settings::finish);
        await(()->androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED).contains(original));
        onUi(()->{
            assertSame(scroll.get(),findScroll(root()));
            assertEquals(position.get().intValue(),scroll.get().getScrollY());
            assertNotNull(findContaining(root(),"Festival planning rating: 5"));
        });
        new RoomFestivalRepository(database).save(be.wacken.planner.domain.Festival.archived(FESTIVAL,"Renamed opened festival"));
        onUi(original::recreate);
        await(()->{
            for(Activity candidate:androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                    .getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED)){
                if(candidate instanceof BandDetailActivity&&candidate!=original){activity=candidate;return true;}
            }
            return false;
        });
        onUi(()->{
            assertNotNull(findContaining(root(),"Festival: Renamed opened festival ["+FESTIVAL+"]"));
            ViewGroup section=(ViewGroup)findText(root(),"Festival planning rating").getParent();
            findText(section,"Reset").performClick();
        });
        assertEquals(5,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
    }
    private static android.widget.ScrollView findScroll(View view) {
        if(view instanceof android.widget.ScrollView scroll)return scroll;
        if(view instanceof ViewGroup group)for(int i=0;i<group.getChildCount();i++){
            var found=findScroll(group.getChildAt(i));if(found!=null)return found;
        }
        return null;
    }

    private void open(Class<? extends Activity> type) {
        Intent intent=new Intent(instrumentation.getTargetContext(),type).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(type==BandDetailActivity.class)intent.putExtra(BandDetailActivity.EXTRA_BAND_NAME,BAND);
        if(type==ArchivedBandDetailActivity.class)intent.putExtra(ArchivedBandDetailActivity.EXTRA_BAND_NAME,BAND)
                .putExtra(ArchivedBandDetailActivity.EXTRA_FESTIVAL_ID,FESTIVAL);
        activity=instrumentation.startActivitySync(intent);
        instrumentation.waitForIdleSync();
    }
    private View root(){return activity.getWindow().getDecorView();}
    private static View findDescription(View view,String description){
        if(description.equals(view.getContentDescription()))return view;
        if(view instanceof ViewGroup group)for(int i=0;i<group.getChildCount();i++){
            View found=findDescription(group.getChildAt(i),description);if(found!=null)return found;
        }
        return null;
    }
    private static ImageView findBandImage(View view){
        if(view instanceof ImageView image&&"Band image".equals(view.getContentDescription()))return image;
        if(view instanceof ViewGroup group)for(int i=0;i<group.getChildCount();i++){
            ImageView image=findBandImage(group.getChildAt(i));if(image!=null)return image;
        }
        return null;
    }
    private void onUi(Runnable action){instrumentation.runOnMainSync(action);}
    private void await(BooleanSupplier condition) throws Exception {
        long deadline=SystemClock.elapsedRealtime()+15000;
        AtomicReference<Boolean> ready=new AtomicReference<>(false);
        while(SystemClock.elapsedRealtime()<deadline){
            onUi(()->ready.set(condition.getAsBoolean()));
            if(ready.get())return;
            SystemClock.sleep(25);
        }
        fail("Native smoke condition did not become true in 15 seconds");
    }
    private static boolean booleanField(Object target,String name){
        try{java.lang.reflect.Field f=target.getClass().getDeclaredField(name);f.setAccessible(true);return f.getBoolean(target);}
        catch(Exception error){throw new AssertionError(error);}
    }
    private static TextView findText(View view,String text){
        if(view instanceof TextView label&&text.contentEquals(label.getText()))return label;
        if(view instanceof ViewGroup group)for(int i=0;i<group.getChildCount();i++){TextView found=findText(group.getChildAt(i),text);if(found!=null)return found;}
        return null;
    }
    private static TextView findContaining(View view,String text){
        if(view instanceof TextView label&&label.getText().toString().contains(text))return label;
        if(view instanceof ViewGroup group)for(int i=0;i<group.getChildCount();i++){TextView found=findContaining(group.getChildAt(i),text);if(found!=null)return found;}
        return null;
    }
    private static RatingStarsView findStars(View view){
        if(view instanceof RatingStarsView stars)return stars;
        if(view instanceof ViewGroup group)for(int i=0;i<group.getChildCount();i++){RatingStarsView found=findStars(group.getChildAt(i));if(found!=null)return found;}
        return null;
    }
    private String shell(String command) throws Exception {
        try(ParcelFileDescriptor result=instrumentation.getUiAutomation().executeShellCommand(command);
            FileInputStream stream=new FileInputStream(result.getFileDescriptor())){
            java.io.ByteArrayOutputStream bytes=new java.io.ByteArrayOutputStream();
            byte[] buffer=new byte[1024];int count;
            while((count=stream.read(buffer))!=-1)bytes.write(buffer,0,count);
            return new String(bytes.toByteArray(),StandardCharsets.UTF_8).trim();
        }
    }
}
