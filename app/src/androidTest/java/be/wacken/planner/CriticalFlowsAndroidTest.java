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
    private String additionalBand,additionalStage;

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
            database.scheduleLocks().deleteByFestival(FESTIVAL);
            database.ratingStates().deleteAfterFestival(FESTIVAL);
            database.getOpenHelper().getWritableDatabase().execSQL("DELETE FROM personal_band_ratings WHERE userName = ? AND bandName = ?",new Object[]{USER,BAND});
            database.getOpenHelper().getWritableDatabase().execSQL("DELETE FROM personal_band_rating_events WHERE userName = ? AND bandName = ?",new Object[]{USER,BAND});
            database.festivals().deleteById(FESTIVAL);
            database.festivals().saveAll(previousFestivals);
            if(previousBand!=null&&previousBand.isPresent())new RoomBandRepository(database).save(previousBand.get());
            else database.getOpenHelper().getWritableDatabase().execSQL("DELETE FROM bands WHERE name = ?",new Object[]{BAND});
            if(previousStage!=null)database.stages().save(previousStage);
            else database.getOpenHelper().getWritableDatabase().execSQL("DELETE FROM stages WHERE name = ?",new Object[]{STAGE});
            if(additionalBand!=null)database.getOpenHelper().getWritableDatabase().execSQL("DELETE FROM bands WHERE name = ?",new Object[]{additionalBand});
            if(additionalStage!=null)database.getOpenHelper().getWritableDatabase().execSQL("DELETE FROM stages WHERE name = ?",new Object[]{additionalStage});
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
            assertNotNull(findDescription(root(),"Back"));
            assertTrue(findDescription(root(),"Clear planning rating").isEnabled());
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
            RatingStarsView archivedStars=findStars(root());
            for(int index=0;index<archivedStars.getChildCount();index++) {
                View choice=archivedStars.getChildAt(index);
                assertFalse(choice.isEnabled());
                assertFalse(choice.performAccessibilityAction(android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK,null));
            }
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
        await(()->database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value==5);
        assertEquals(5,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
        assertEquals("PENDING",database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).syncStatus);
        onUi(()->{
            Button reset=(Button)findDescription(root(),"Clear planning rating");
            assertNotNull("Planning reset must remain reachable",reset);reset.performClick();
        });
        assertEquals(0,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
    }

    @Test public void planningChoicesSupportAccessibilityAndKeyboardWithUnratedClear() throws Exception {
        open(BandDetailActivity.class);
        await(()->activity.getWindow().getDecorView().hasWindowFocus());
        onUi(()->{
            RatingStarsView stars=findStars(root());
            View fifth=stars.getChildAt(4);
            var node=fifth.createAccessibilityNodeInfo();
            assertEquals("android.widget.Button",node.getClassName().toString());
            assertTrue(node.getContentDescription().toString().contains("5 · Must see"));
            assertTrue(node.getContentDescription().toString().contains("planning"));
            assertTrue(fifth.getWidth()>=WackenTheme.dp(activity,48));
            assertTrue(fifth.getHeight()>=WackenTheme.dp(activity,48));
            assertTrue(fifth.getBackground().isStateful());
            assertTrue(fifth.performAccessibilityAction(android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK,null));
            assertTrue(fifth.isSelected());
        });
        assertEquals(5,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
        AtomicReference<Boolean> previousTouchMode=new AtomicReference<>();
        onUi(()->previousTouchMode.set(root().isInTouchMode()));
        instrumentation.sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_TAB);
        try {
            await(()->!root().isInTouchMode());
            onUi(()->{
                View choice=findStars(root()).getChildAt(0);
                assertTrue("Keyboard choice enabled="+choice.isEnabled()+", focusable="+choice.isFocusable()
                        +", shown="+choice.isShown()+", touchMode="+choice.isInTouchMode(),choice.requestFocus());
            });
            instrumentation.sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_ENTER);
            await(()->database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value==1);
        } finally {instrumentation.setInTouchMode(previousTouchMode.get());}
        assertEquals(1,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
        onUi(()->{
            assertTrue(findDescription(root(),"Clear planning rating").performAccessibilityAction(
                    android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK,null));
            assertNotNull(findContaining(root(),"Planning: Unrated"));
            RatingStarsView stars=findStars(root());
            for(int i=0;i<5;i++)assertFalse(stars.getChildAt(i).isSelected());
        });
        assertEquals(0,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
        assertNull(database.ratingStates().after(USER,FESTIVAL,BAND));
    }

    @Test public void afterDisclosureSurvivesThemeAndRecreationWithoutReplayingTheRating() throws Exception {
        open(BandDetailActivity.class);
        Object previousMode=AppearanceController.store(activity).getAll().get(AppearanceController.MODE);
        try {
            onUi(()->{
                assertNotNull("After-rating must be a deliberate disclosure",findText(root(),"After the show"));
                assertFalse(findText(root(),"Festival after-rating").isShown());
                findText(root(),"After the show").performClick();
            });
            await(()->findStars((View)findText(root(),"Festival after-rating").getParent()).getWidth()>0);
            onUi(()->{
                RatingStarsView stars=findStars((View)findText(root(),"Festival after-rating").getParent());
                assertTrue(stars.getChildAt(2).performAccessibilityAction(
                        android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK,null));
                AppearanceController.choose(activity,AppearancePreference.LIGHT);
                assertTrue(findText(root(),"Festival after-rating").isShown());
            });
            var recorded=database.ratingStates().after(USER,FESTIVAL,BAND);
            assertEquals(3,recorded.value);
            Activity previous=activity;
            onUi(previous::recreate);
            await(()->{
                for(Activity candidate:androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED))
                    if(candidate instanceof BandDetailActivity&&candidate!=previous){activity=candidate;return true;}
                return false;
            });
            onUi(()->{
                assertTrue(findText(root(),"Festival after-rating").isShown());
                assertTrue(findText(root(),"After the show").isSelected());
                AppearanceController.choose(activity,AppearancePreference.DARK);
            });
            var restored=database.ratingStates().after(USER,FESTIVAL,BAND);
            assertEquals(recorded.operationId,restored.operationId);
            assertEquals(recorded.recordedAt,restored.recordedAt);
            assertEquals(4,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
        } finally {
            onUi(()->{
                var editor=AppearanceController.store(instrumentation.getTargetContext()).edit();
                if(previousMode instanceof String mode)editor.putString(AppearanceController.MODE,mode);
                else editor.remove(AppearanceController.MODE);
                editor.commit();
            });
        }
    }

    @Test public void doubleFontDetailStacksAccessibleChoicesWithoutClippingOrWriting() throws Exception {
        activity=instrumentation.startActivitySync(new Intent(instrumentation.getTargetContext(),BandDetailActivity.class)
                .putExtra(BandDetailActivity.EXTRA_BAND_NAME,BAND).putExtra(NativeUiTestRunner.FONT_SCALE,2f)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        instrumentation.waitForIdleSync();
        onUi(()->{
            assertTrue(activity.getResources().getConfiguration().fontScale>=1.99f);
            RatingStarsView stars=findStars(root());
            assertEquals(android.widget.LinearLayout.VERTICAL,stars.getOrientation());
            for(int i=0;i<5;i++) {
                TextView choice=(TextView)stars.getChildAt(i);
                assertTrue(choice.getHeight()>=WackenTheme.dp(activity,48));
                assertTrue(choice.getWidth()>=WackenTheme.dp(activity,48));
                assertNotNull(choice.getLayout());
                assertTrue(choice.getHeight()>=choice.getLayout().getHeight());
                for(int line=0;line<choice.getLineCount();line++)assertEquals(0,choice.getLayout().getEllipsisCount(line));
            }
            assertNotNull(findDescription(root(),"Clear planning rating"));
        });
        assertEquals(4,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
        assertNull(database.ratingStates().after(USER,FESTIVAL,BAND));
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

    @Test public void linkReviewRetainsEditedSearchAndNoMatchAcrossBothThemesAndRecreation() throws Exception {
        String known=BAND.toLowerCase(java.util.Locale.ROOT);
        assertTrue(new RoomBandRepository(database).findByName(known).isEmpty());additionalBand=known;
        new RoomBandRepository(database).save(new Band(known));
        Object previous=AppearanceController.store(instrumentation.getTargetContext()).getAll().get(AppearanceController.MODE);
        open(BandLinkReviewActivity.class);
        try {
            onUi(()->{
                var search=(android.widget.EditText)findDescription(root(),"Search own bands for "+BAND);
                assertNotNull("Link review search must identify its imported band",search);
                search.setText("task-174");
                var matches=(android.widget.Spinner)findDescription(root(),"Match for "+BAND);
                assertNotNull(matches);assertTrue(matches.getMinimumHeight()>=WackenTheme.dp(activity,48));matches.setSelection(0);
                AppearanceController.choose(activity,AppearancePreference.LIGHT);
                assertEquals("task-174",search.getText().toString());
            });
            Activity old=activity;onUi(old::recreate);
            await(()->{
                for(Activity candidate:androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED))
                    if(candidate instanceof BandLinkReviewActivity&&candidate!=old){activity=candidate;return true;}
                return false;
            });
            onUi(()->{
                assertEquals("task-174",((android.widget.EditText)findDescription(root(),"Search own bands for "+BAND)).getText().toString());
                assertEquals("No match",((android.widget.Spinner)findDescription(root(),"Match for "+BAND)).getSelectedItem());
                AppearanceController.choose(activity,AppearancePreference.DARK);
                assertNotNull(findText(root(),"Link selected match"));
            });
            assertEquals(BAND,database.festivalLineups().findByFestival(FESTIVAL).get(0).bandName);
            assertEquals(4,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
        } finally {onUi(()->{
            var editor=AppearanceController.store(instrumentation.getTargetContext()).edit();
            if(previous instanceof String mode)editor.putString(AppearanceController.MODE,mode);else editor.remove(AppearanceController.MODE);editor.commit();
        });}
    }

    @Test public void scheduleFiltersAndViewportSurviveThemeEmptyResultsAndRecreation() throws Exception {
        open(ScheduleActivity.class);
        await(()->booleanField(activity,"scheduleLocksLoaded"));
        AtomicReference<Integer> horizontalPosition=new AtomicReference<>();
        Object previousMode=AppearanceController.store(activity).getAll().get(AppearanceController.MODE);
        try {
            onUi(()->{
                View day=findDescription(root(),"Festival day");
                assertNotNull("Day choice needs a labeled native selector",day);
                assertTrue(day instanceof android.widget.Spinner);
                assertTrue(day.getHeight()>=WackenTheme.dp(activity,48));
                var threshold=(android.widget.Spinner)findDescription(root(),"Hide ratings at or below");
                assertNotNull(threshold);assertTrue(threshold.getHeight()>=WackenTheme.dp(activity,48));
                var calendar=(android.widget.HorizontalScrollView)root().findViewWithTag("schedule-calendar");
                assertNotNull(calendar);calendar.scrollTo(120,0);
                horizontalPosition.set(calendar.getScrollX());assertTrue(horizontalPosition.get()>0);
                AppearanceController.choose(activity,AppearancePreference.LIGHT);
                assertSame(calendar,root().findViewWithTag("schedule-calendar"));
                assertEquals(horizontalPosition.get().intValue(),calendar.getScrollX());
                threshold.setSelection(4);
            });
            await(()->findText(root(),"No acts match the active filters.")!=null);
            onUi(()->((android.widget.Spinner)findDescription(root(),"Hide ratings at or below")).setSelection(0));
            await(()->root().findViewWithTag("schedule-calendar")!=null);
            await(()->((android.widget.HorizontalScrollView)root().findViewWithTag("schedule-calendar")).getScrollX()==horizontalPosition.get());
            onUi(()->{
                ((android.widget.CheckBox)findDescription(root(),"Hide barred overlaps")).setChecked(true);
            });
            await(()->((android.widget.CheckBox)findDescription(root(),"Hide barred overlaps")).isChecked());
            onUi(()->findText(root(),"Show schedule key").performClick());
            Activity old=activity;onUi(old::recreate);
            await(()->{
                for(Activity candidate:androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED))
                    if(candidate instanceof ScheduleActivity&&candidate!=old){activity=candidate;return true;}
                return false;
            });
            await(()->booleanField(activity,"scheduleLocksLoaded")&&root().findViewWithTag("schedule-calendar")!=null);
            await(()->((android.widget.HorizontalScrollView)root().findViewWithTag("schedule-calendar")).getScrollX()==horizontalPosition.get());
            onUi(()->{
                assertTrue(((android.widget.CheckBox)findDescription(root(),"Hide barred overlaps")).isChecked());
                assertEquals(0,((android.widget.Spinner)findDescription(root(),"Hide ratings at or below")).getSelectedItemPosition());
                assertNotNull(findText(root(),"Legend"));
                AppearanceController.choose(activity,AppearancePreference.DARK);
                assertNotNull(findContaining(root(),BAND));
            });
            assertEquals(4,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
        } finally {
            onUi(()->{
                var editor=AppearanceController.store(instrumentation.getTargetContext()).edit();
                if(previousMode instanceof String mode)editor.putString(AppearanceController.MODE,mode);
                else editor.remove(AppearanceController.MODE);
                editor.commit();
            });
        }
    }

    @Test public void doubleFontScheduleDecisionKeepsTheChosenAlternativeAndIndependentRatings() throws Exception {
        String alternative="TASK-209 Alternative with a very long canonical festival band name";
        String stage="TASK-209 Alternative Stage";
        assertTrue(new RoomBandRepository(database).findByName(alternative).isEmpty());
        assertNull(database.stages().findByName(stage));
        additionalBand=alternative;additionalStage=stage;
        new RoomBandRepository(database).save(new Band(alternative));
        database.stages().save(new RoomStage(stage));
        database.festivalLineups().saveAll(List.of(new RoomFestivalLineupEntry(FESTIVAL,alternative,alternative)));
        database.performances().save(new RoomPerformance(FESTIVAL,alternative,stage,"2027-08-19T18:15:00","2027-08-19T19:15:00"));
        database.festivalPlanningRatings().save(new RoomFestivalPlanningRating(GROUP,USER,FESTIVAL,alternative,3,"PENDING"));
        activity=instrumentation.startActivitySync(new Intent(instrumentation.getTargetContext(),ScheduleActivity.class)
                .putExtra(NativeUiTestRunner.FONT_SCALE,2f).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        instrumentation.waitForIdleSync();
        await(()->booleanField(activity,"scheduleLocksLoaded"));
        Object previousMode=AppearanceController.store(activity).getAll().get(AppearanceController.MODE);
        try {
            onUi(()->{
                assertTrue(activity.getResources().getConfiguration().fontScale>=1.99f);
                View slot=findContaining(root(),BAND);
                while(!slot.isClickable()&&slot.getParent() instanceof View parent)slot=parent;
                assertTrue(slot.isFocusable());
                assertEquals("android.widget.Button",slot.createAccessibilityNodeInfo().getClassName().toString());
                assertTrue(slot.getContentDescription().toString().contains("2027-08-19"));
                assertTrue(slot.getContentDescription().toString().contains(STAGE));
                slot.requestRectangleOnScreen(new android.graphics.Rect(0,0,slot.getWidth(),slot.getHeight()),true);
                assertTrue(slot.performAccessibilityAction(android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK,null));
            });
            await(()->decisionRoot()!=null&&findText(decisionRoot(),"Select as act")!=null);
            AtomicReference<android.app.AlertDialog> dialog=new AtomicReference<>();
            onUi(()->{
                dialog.set((android.app.AlertDialog)objectField(activity,"decisionDialog"));
                View content=decisionRoot();
                assertNotNull(findText(content,"Chosen act"));assertNotNull(findText(content,"Alternatives"));
                assertNotNull(findContaining(content,alternative));
                TextView name=findContaining(content,alternative);
                assertTrue(name.getLineCount()>1);
                for(int line=0;line<name.getLineCount();line++)assertEquals(0,name.getLayout().getEllipsisCount(line));
                assertNotNull(findContaining(content,"Walk:"));
                AppearanceController.choose(activity,AppearancePreference.LIGHT);
                assertSame(dialog.get(),objectField(activity,"decisionDialog"));
                TextView select=findText(content,"Select as act");
                select.requestRectangleOnScreen(new android.graphics.Rect(0,0,select.getWidth(),select.getHeight()),true);
            });
            await(()->findText(decisionRoot(),"Select as act").getHeight()>=WackenTheme.dp(activity,48));
            onUi(()->{
                TextView select=findText(decisionRoot(),"Select as act");
                assertTrue(select.getWidth()>=WackenTheme.dp(activity,48));
                assertTrue(select.performAccessibilityAction(android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK,null));
            });
            await(()->objectField(activity,"decisionDialog")==null&&findContaining(root(),alternative)!=null);
            assertEquals(1,database.scheduleLocks().findPendingSelections(GROUP,FESTIVAL).size());
            assertEquals(4,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
            assertEquals(3,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,alternative).value);
            onUi(()->{
                View slot=findContaining(root(),alternative);
                while(!slot.isClickable()&&slot.getParent() instanceof View parent)slot=parent;
                slot.performAccessibilityAction(android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK,null);
            });
            await(()->decisionRoot()!=null&&findText(decisionRoot(),alternative)!=null);
            Activity old=activity;onUi(old::recreate);
            await(()->{
                for(Activity candidate:androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED))
                    if(candidate instanceof ScheduleActivity&&candidate!=old){activity=candidate;return true;}
                return false;
            });
            await(()->decisionRoot()!=null&&findText(decisionRoot(),alternative)!=null);
            onUi(()->{
                assertNotNull(findText(decisionRoot(),"Unlock generated choice"));
                AppearanceController.choose(activity,AppearancePreference.DARK);
                assertNotNull(findText(decisionRoot(),alternative));
                findText(decisionRoot(),"Close").performClick();
            });
            assertEquals(1,database.scheduleLocks().findPendingSelections(GROUP,FESTIVAL).size());
            assertEquals(4,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
            assertNull(database.ratingStates().after(USER,FESTIVAL,BAND));
        } finally {
            onUi(()->{
                var editor=AppearanceController.store(instrumentation.getTargetContext()).edit();
                if(previousMode instanceof String mode)editor.putString(AppearanceController.MODE,mode);
                else editor.remove(AppearanceController.MODE);
                editor.commit();
            });
        }
    }

    private View decisionRoot() {
        android.app.AlertDialog dialog=(android.app.AlertDialog)objectField(activity,"decisionDialog");
        return dialog==null||!dialog.isShowing()?null:dialog.getWindow().getDecorView();
    }

    private static Object objectField(Object target,String name) {
        try{var field=target.getClass().getDeclaredField(name);field.setAccessible(true);return field.get(target);}
        catch(Exception error){throw new AssertionError(error);}
    }

    @Test public void afterRatingTouchAndResetStayAtFestivalLevelWithoutPromotingBandOpinion() throws Exception {
        database.ratingStates().saveCurrent(new RoomPersonalBandRating(USER,BAND,4,"previous","Previous festival",
                "11111111-1111-4111-8111-111111111111","2026-08-01T12:00:00Z"));
        open(BandDetailActivity.class);
        onUi(()->findText(root(),"After the show").performClick());
        await(()->findStars((View)findText(root(),"Festival after-rating").getParent()).getWidth()>0);
        onUi(()->{
            ViewGroup section=(ViewGroup)findText(root(),"Festival after-rating").getParent();
            RatingStarsView control=findStars(section);assertNotNull(control);assertTrue(control.getWidth()>0);
            long time=SystemClock.uptimeMillis();
            MotionEvent down=MotionEvent.obtain(time,time,MotionEvent.ACTION_DOWN,control.getWidth()*.5f,control.getHeight()/2f,0);
            MotionEvent up=MotionEvent.obtain(time,time+1,MotionEvent.ACTION_UP,control.getWidth()*.5f,control.getHeight()/2f,0);
            try{control.dispatchTouchEvent(down);control.dispatchTouchEvent(up);}finally{down.recycle();up.recycle();}
        });
        var after=new RoomFestivalAfterRatingRepository(database);
        await(()->database.ratingStates().after(USER,FESTIVAL,BAND)!=null);
        assertEquals(3,after.findByUserFestivalAndBand(USER,FESTIVAL,new be.wacken.planner.domain.Band(BAND)).orElseThrow().rating().value());
        assertEquals(4,database.ratingStates().current(USER,BAND).value);
        assertEquals(4,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
        onUi(()->{
            ViewGroup section=(ViewGroup)findText(root(),"Festival after-rating").getParent();
            findDescription(section,"Clear after-rating").performClick();
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

    @Test public void sharedDestinationsExposeSelectionAndReuseExistingActivities() throws Exception {
        Object previous=AppearanceController.store(instrumentation.getTargetContext()).getAll().get(AppearanceController.MODE);
        open(MainActivity.class);
        await(()->findText(root(),BAND)!=null);
        Activity bands=activity;
        AtomicReference<Activity> settings=new AtomicReference<>(),schedule=new AtomicReference<>();
        try {
            onUi(()->{
                assertNavigation("Bands");
                for(AppearancePreference mode:new AppearancePreference[]{AppearancePreference.LIGHT,AppearancePreference.DARK}) {
                    AppearanceController.choose(activity,mode);assertNavigation("Bands");
                }
                View content=root().findViewWithTag("main-navigation");
                navigationButton("Bands").performClick();
                assertSame(content,root().findViewWithTag("main-navigation"));
                navigationButton("Settings").performClick();
            });
            awaitActivity(SettingsActivity.class);settings.set(activity);
            onUi(()->{assertNavigation("Settings");navigationButton("Schedule").performClick();});
            awaitActivity(ScheduleActivity.class);schedule.set(activity);
            onUi(()->{assertNavigation("Schedule");navigationButton("Bands").performClick();});
            awaitActivity(MainActivity.class);
            onUi(()->{assertSame(bands,activity);assertNavigation("Bands");});
            await(()->!booleanField(bands,"syncInProgress"));
            assertEquals(4,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
        } finally {
            onUi(()->{
                if(schedule.get()!=null)schedule.get().finish();
                if(settings.get()!=null)settings.get().finish();
                bands.finish();
                var editor=AppearanceController.store(instrumentation.getTargetContext()).edit();
                if(previous instanceof String mode)editor.putString(AppearanceController.MODE,mode);
                else editor.remove(AppearanceController.MODE);
                editor.commit();
            });
        }
    }

    @Test public void reviewBackRecreationAndLauncherReturnKeepTheSettingsDestination() throws Exception {
        Intent initialLaunch=instrumentation.getTargetContext().getPackageManager()
                .getLaunchIntentForPackage(instrumentation.getTargetContext().getPackageName());
        assertNotNull(initialLaunch);
        activity=instrumentation.startActivitySync(initialLaunch);
        instrumentation.waitForIdleSync();
        await(()->findText(root(),BAND)!=null);
        Activity bands=activity;
        AtomicReference<Activity> settings=new AtomicReference<>();
        try {
            onUi(()->navigationButton("Settings").performClick());
            awaitActivity(SettingsActivity.class);settings.set(activity);
            AtomicReference<Integer> scrollPosition=new AtomicReference<>();
            onUi(()->{
                findScroll(root()).scrollTo(0,120);
                scrollPosition.set(findScroll(root()).getScrollY());
                findText(root(),"Link imported bands").performClick();
            });
            awaitActivity(BandLinkReviewActivity.class);
            instrumentation.sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK);
            awaitActivity(SettingsActivity.class);
            onUi(()->{
                assertSame(settings.get(),activity);
                assertNavigation("Settings");
                assertEquals(scrollPosition.get().intValue(),findScroll(root()).getScrollY());
            });
            Activity previous=activity;
            onUi(previous::recreate);
            await(()->{
                for(Activity candidate:androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED))
                    if(candidate instanceof SettingsActivity&&candidate!=previous){activity=candidate;return true;}
                return false;
            });
            settings.set(activity);
            await(()->findScroll(root()).getScrollY()==scrollPosition.get());
            onUi(()->{
                assertNavigation("Settings");
                assertEquals(scrollPosition.get().intValue(),findScroll(root()).getScrollY());
            });
            shell("input keyevent KEYCODE_HOME");
            await(()->androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                    .getActivitiesInStage(androidx.test.runner.lifecycle.Stage.STOPPED).contains(settings.get()));
            Intent launcher=instrumentation.getTargetContext().getPackageManager()
                    .getLaunchIntentForPackage(instrumentation.getTargetContext().getPackageName());
            assertNotNull(launcher);instrumentation.getTargetContext().startActivity(launcher);
            awaitActivity(SettingsActivity.class);
            onUi(()->{assertSame(settings.get(),activity);assertNavigation("Settings");});
        } finally {
            onUi(()->{if(settings.get()!=null)settings.get().finish();bands.finish();});
        }
    }

    private void assertNavigation(String selected) {
        ViewGroup navigation=root().findViewWithTag("main-navigation");
        assertNotNull("Every primary destination needs the shared navigation",navigation);
        assertEquals(3,navigation.getChildCount());
        String[] labels={"Bands","Schedule","Settings"};
        for(int i=0;i<3;i++) {
            TextView button=(TextView)navigation.getChildAt(i);
            assertEquals(labels[i],button.getText().toString());
            assertEquals(labels[i].equals(selected),button.isSelected());
            assertEquals(labels[i]+(labels[i].equals(selected)?", current destination":""),button.getContentDescription());
            assertTrue(button.isEnabled());
        }
    }

    @Test public void detailBackReturnsToTheOriginalBandRowWithSyncAndFestivalActionsReachable() throws Exception {
        open(MainActivity.class);
        await(()->findText(root(),BAND)!=null);
        Activity bands=activity;
        onUi(()->{
            View row=findText(root(),BAND);
            while(!row.isClickable()&&row.getParent() instanceof View parent)row=parent;
            assertTrue("Band row must open detail",row.isClickable());row.performClick();
        });
        awaitActivity(BandDetailActivity.class);
        onUi(()->findDescription(root(),"Back").performClick());
        awaitActivity(MainActivity.class);
        await(()->findText(root(),BAND)!=null&&!booleanField(bands,"syncInProgress"));
        onUi(()->{
            assertSame(bands,activity);assertNavigation("Bands");
            assertNotNull(findText(root(),BAND));
            android.widget.ListView list=root().findViewById(R.id.band_lineup);
            list.setSelection(list.getCount()-1);
        });
        await(()->findDescription(root(),"Sync and exit")!=null);
        onUi(()->{
            assertTrue(findDescription(root(),"Sync and exit").isShown());
            assertTrue(findDescription(root(),"Archive active festival").isShown());
        });
    }
    @Test public void lineupSearchFilterAndDetailReturnRetainContextInBothAppearances() throws Exception {
        open(MainActivity.class);
        await(()->findText(root(),BAND)!=null&&!booleanField(activity,"syncInProgress"));
        Activity bands=activity;
        Object previous=AppearanceController.store(activity).getAll().get(AppearanceController.MODE);
        try {
            onUi(()->{
                TextView search=findDescription(root(),"Find a band")==null?null:(TextView)findDescription(root(),"Find a band");
                assertNotNull("Lineup needs an accessible name search",search);
                search.setText("smoke");
                findText(root(),"Unrated").performClick();
            });
            await(()->findText(root(),"No bands found")!=null);
            onUi(()->{
                assertNull(findText(root(),BAND));
                findText(root(),"All bands").performClick();
            });
            await(()->findText(root(),BAND)!=null);
            onUi(()->{
                assertNotNull(findContaining(root(),"Planning: 4"));
                assertNotNull(findText(root(),"Music links not available"));
                AppearanceController.choose(activity,AppearancePreference.LIGHT);
                assertEquals("smoke",((TextView)findDescription(root(),"Find a band")).getText().toString());
                View row=findText(root(),BAND);
                while(!row.isClickable()&&row.getParent() instanceof View parent)row=parent;
                row.performClick();
            });
            awaitActivity(BandDetailActivity.class);
            onUi(()->findDescription(root(),"Back").performClick());
            awaitActivity(MainActivity.class);
            await(()->findText(root(),BAND)!=null&&!booleanField(activity,"syncInProgress"));
            onUi(()->((android.widget.ListView)root().findViewById(R.id.band_lineup)).setSelection(0));
            await(()->findDescription(root(),"Find a band")!=null);
            onUi(()->{
                assertSame(bands,activity);
                assertEquals("smoke",((TextView)findDescription(root(),"Find a band")).getText().toString());
                AppearanceController.choose(activity,AppearancePreference.DARK);
                ((TextView)findDescription(root(),"Find a band")).setText("no matching act 207");
            });
            await(()->findText(root(),"No bands found")!=null);
            onUi(()->findText(root(),"Clear search and filters").performClick());
            await(()->findText(root(),BAND)!=null);
            onUi(()->{
                ((TextView)findDescription(root(),"Find a band")).setText("smoke");
                findText(root(),"Unrated").performClick();
            });
            Activity previousActivity=activity;
            onUi(previousActivity::recreate);
            await(()->{
                for(Activity candidate:androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED))
                    if(candidate instanceof MainActivity&&candidate!=previousActivity){activity=candidate;return true;}
                return false;
            });
            await(()->findText(root(),"No bands found")!=null&&!booleanField(activity,"syncInProgress"));
            onUi(()->((android.widget.ListView)root().findViewById(R.id.band_lineup)).setSelection(0));
            await(()->findDescription(root(),"Find a band")!=null);
            onUi(()->{
                assertEquals("smoke",((TextView)findDescription(root(),"Find a band")).getText().toString());
                assertTrue(findText(root(),"Unrated").isSelected());
                findText(root(),"All bands").performClick();
            });
            await(()->findText(root(),BAND)!=null);
        } finally {
            onUi(()->{
                var editor=AppearanceController.store(instrumentation.getTargetContext()).edit();
                if(previous instanceof String mode)editor.putString(AppearanceController.MODE,mode);
                else editor.remove(AppearanceController.MODE);
                editor.commit();
            });
        }
        assertEquals(4,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
    }

    @Test public void lineupLongNameAndControlsReflowAtDoubleFontSize() throws Exception {
        String longName="A very long canonical festival band name with a readable final word";
        assertTrue(new RoomBandRepository(database).findByName(longName).isEmpty());
        new RoomBandRepository(database).save(new Band(longName));
        database.festivalLineups().saveAll(List.of(new RoomFestivalLineupEntry(FESTIVAL,longName,longName)));
        try {
            activity=instrumentation.startActivitySync(new Intent(instrumentation.getTargetContext(),MainActivity.class)
                    .putExtra(NativeUiTestRunner.FONT_SCALE,2f).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            instrumentation.waitForIdleSync();
            await(()->root().findViewById(R.id.band_lineup)!=null);
            onUi(()->((android.widget.ListView)root().findViewById(R.id.band_lineup)).setSelection(1));
            await(()->findText(root(),longName)!=null);
            onUi(()->{
                TextView name=findText(root(),longName);
                assertTrue(activity.getResources().getConfiguration().fontScale>=1.99f);
                assertTrue("Essential name must wrap",name.getLineCount()>1);
                assertNotNull(name.getLayout());
                for(int line=0;line<name.getLineCount();line++)assertEquals(0,name.getLayout().getEllipsisCount(line));
                assertTrue(name.getHeight()>=name.getLayout().getHeight());
                RatingStarsView stars=findStars((View)name.getParent());
                assertTrue(stars.getHeight()>=WackenTheme.dp(activity,48));
                assertNotNull(findContaining(root(),"TBA"));
                assertNavigation("Bands");
                android.widget.ListView list=root().findViewById(R.id.band_lineup);
                list.setSelection(list.getCount()-1);
            });
            await(()->findDescription(root(),"Sync and exit")!=null);
            onUi(()->{
                View sync=findDescription(root(),"Sync and exit");
                assertTrue(sync.isShown());
                assertTrue(sync.getHeight()>=WackenTheme.dp(activity,48));
                assertTrue(sync.getWidth()>=WackenTheme.dp(activity,48));
            });
            await(()->!booleanField(activity,"syncInProgress"));
        } finally {
            if(activity!=null)onUi(activity::finish);
            database.getOpenHelper().getWritableDatabase().execSQL("DELETE FROM festival_lineup_entries WHERE festivalId = ? AND bandName = ?",new Object[]{FESTIVAL,longName});
            database.getOpenHelper().getWritableDatabase().execSQL("DELETE FROM bands WHERE name = ?",new Object[]{longName});
        }
    }

    @Test public void lineupKnownMusicLinkHandsOffWithoutInventingTheMissingPlatform() throws Exception {
        new RoomBandRepository(database).save(new Band(BAND,Optional.empty(),Optional.empty(),
                Optional.of("https://www.youtube.com/channel/UC207canonical"),Optional.empty()));
        open(MainActivity.class);
        await(()->findText(root(),BAND)!=null&&!booleanField(activity,"syncInProgress"));
        android.content.IntentFilter filter=new android.content.IntentFilter(Intent.ACTION_VIEW);
        filter.addDataScheme("https");
        Instrumentation.ActivityMonitor monitor=instrumentation.addMonitor(filter,
                new Instrumentation.ActivityResult(Activity.RESULT_CANCELED,null),true);
        try {
            onUi(()->{
                assertNotNull(findDescription(root(),"Open YouTube"));
                assertNull(findDescription(root(),"Open Spotify"));
                findDescription(root(),"Open YouTube").performClick();
            });
            assertEquals(1,monitor.getHits());
            onUi(()->{assertNavigation("Bands");assertNotNull(findText(root(),BAND));});
        } finally {instrumentation.removeMonitor(monitor);}
    }

    @Test public void ratingAnUnratedOverviewRowUpdatesItsScopedValueAndFilter() throws Exception {
        database.festivalPlanningRatings().save(new RoomFestivalPlanningRating(GROUP,USER,FESTIVAL,BAND,0,"PENDING"));
        open(MainActivity.class);
        await(()->findText(root(),BAND)!=null&&!booleanField(activity,"syncInProgress"));
        onUi(()->findText(root(),"Unrated").performClick());
        await(()->findText(root(),BAND)!=null);
        onUi(()->{
            RatingStarsView stars=findStars(root());assertNotNull(stars);
            long now=SystemClock.uptimeMillis();
            MotionEvent down=MotionEvent.obtain(now,now,MotionEvent.ACTION_DOWN,stars.getWidth()*0.7f,stars.getHeight()/2f,0);
            MotionEvent up=MotionEvent.obtain(now,now+10,MotionEvent.ACTION_UP,stars.getWidth()*0.7f,stars.getHeight()/2f,0);
            stars.dispatchTouchEvent(down);stars.dispatchTouchEvent(up);down.recycle();up.recycle();
        });
        await(()->findText(root(),"No bands found")!=null);
        assertEquals(4,database.festivalPlanningRatings().findExact(GROUP,USER,FESTIVAL,BAND).value);
        onUi(()->findText(root(),"All bands").performClick());
        await(()->findContaining(root(),"Planning: 4")!=null);
    }

    private TextView navigationButton(String label) {
        ViewGroup navigation=root().findViewWithTag("main-navigation");
        assertNotNull(navigation);
        return findText(navigation,label);
    }
    private void awaitActivity(Class<? extends Activity> type) throws Exception {
        await(()->{
            for(Activity candidate:androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                    .getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED))
                    if(type.isInstance(candidate)&&!candidate.isFinishing()
                            &&candidate.getWindow().getDecorView().hasWindowFocus()) {activity=candidate;return true;}
            return false;
        });
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
            assertNull(findText(root(),"Find music links"));
            ViewGroup section=(ViewGroup)findText(root(),"Festival planning rating").getParent();
            assertFalse(findDescription(section,"Clear planning rating").isEnabled());
            findDescription(section,"Clear planning rating").performClick();
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
