package be.wacken.planner;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.*;

/** Exercises actual Activity/view/listener wiring without signing in or touching user data. */
public final class AppearanceAndroidTest {
    private Instrumentation instrumentation;
    private Activity activity;
    private Object previousPreference;
    private Configuration originalConfiguration;

    @Before public void setup() {
        instrumentation=InstrumentationRegistry.getInstrumentation();
        var context=instrumentation.getTargetContext();
        previousPreference=AppearanceController.store(context).getAll().get(AppearanceController.MODE);
        originalConfiguration=new Configuration(context.getResources().getConfiguration());
        instrumentation.runOnMainSync(()->AppearanceController.store(context).edit().remove(AppearanceController.MODE).commit());
    }

    @After public void restore() {
        instrumentation.runOnMainSync(()->{
            if(activity!=null){activity.getApplication().onConfigurationChanged(originalConfiguration);activity.finish();}
            var editor=AppearanceController.store(instrumentation.getTargetContext()).edit();
            if(previousPreference instanceof String value)editor.putString(AppearanceController.MODE,value);
            else if(previousPreference instanceof Integer value)editor.putInt(AppearanceController.MODE,value);
            else editor.remove(AppearanceController.MODE);
            editor.commit();
        });
        instrumentation.waitForIdleSync();
    }

    @Test public void toggleUpdatesExistingViewsWithoutLosingDrafts() {
        open();
        instrumentation.runOnMainSync(()->{
            AppearanceController.choose(activity,AppearancePreference.DARK);
            EditText email=activity.findViewById(R.id.login_email);
            EditText password=activity.findViewById(R.id.login_password);
            email.setText("appearance@example.invalid");password.setText("unsent-draft");
            TextView title=findText(root(),"Wacken Planner");
            assertEquals(0xffffd25a,title.getCurrentTextColor());
            assertTrue(findText(root(),"Switch to light").performClick());
            assertSame(email,activity.findViewById(R.id.login_email));
            assertEquals("appearance@example.invalid",email.getText().toString());
            assertEquals("unsent-draft",password.getText().toString());
            assertEquals(AppearancePreference.LIGHT,AppearanceController.preference(activity));
            assertEquals(0xff3b553c,title.getCurrentTextColor());
            assertEquals(0xff20241f,email.getCurrentTextColor());
            assertNotNull(findText(root(),"Switch to dark"));
        });
    }

    @Test public void systemCallbacksFollowDeviceModeButExplicitChoiceOverrides() {
        open();
        instrumentation.runOnMainSync(()->{
            Configuration change=new Configuration(originalConfiguration);
            change.uiMode=(change.uiMode&~Configuration.UI_MODE_NIGHT_MASK)|Configuration.UI_MODE_NIGHT_YES;
            activity.getApplication().onConfigurationChanged(change);
            assertNotNull(findText(root(),"Switch to light"));
            change.uiMode=(change.uiMode&~Configuration.UI_MODE_NIGHT_MASK)|Configuration.UI_MODE_NIGHT_NO;
            activity.getApplication().onConfigurationChanged(change);
            assertNotNull(findText(root(),"Switch to dark"));
            AppearanceController.choose(activity,AppearancePreference.DARK);
            activity.getApplication().onConfigurationChanged(change);
            assertNotNull(findText(root(),"Switch to light"));
            AppearanceController.store(activity).edit().putInt(AppearanceController.MODE,42).commit();
            assertEquals(AppearancePreference.SYSTEM,AppearanceController.preference(activity));
            assertNotNull(findText(root(),"Switch to dark"));
        });
    }

    @Test public void preferenceAndDraftSurviveIndependentActivityRecreation() {
        open();
        Activity previous=activity;
        instrumentation.runOnMainSync(()->{
            AppearanceController.choose(activity,AppearancePreference.LIGHT);
            ((EditText)activity.findViewById(R.id.login_email)).setText("retained@example.invalid");
            ((EditText)activity.findViewById(R.id.login_password)).setText("memory-only");
            activity.recreate();
        });
        long limit=SystemClock.uptimeMillis()+10000;
        AtomicReference<Activity> replacement=new AtomicReference<>();
        while(SystemClock.uptimeMillis()<limit&&replacement.get()==null) {
            instrumentation.runOnMainSync(()->{
                for(Activity candidate:ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED))
                    if(candidate instanceof LoginActivity&&candidate!=previous)replacement.set(candidate);
            });
            SystemClock.sleep(40);
        }
        assertNotNull("Recreated login must resume",replacement.get());activity=replacement.get();
        instrumentation.runOnMainSync(()->{
            assertEquals(AppearancePreference.LIGHT,AppearanceController.preference(activity));
            assertEquals("retained@example.invalid",((EditText)activity.findViewById(R.id.login_email)).getText().toString());
            assertEquals("memory-only",((EditText)activity.findViewById(R.id.login_password)).getText().toString());
            assertNotNull(findText(root(),"Switch to dark"));
        });
    }

    @Test public void savedChoiceIsReadByANewActivityAndChangesPropagateToOpenScreens() {
        open();
        Activity first=activity;
        instrumentation.runOnMainSync(()->AppearanceController.choose(first,AppearancePreference.DARK));
        open();
        instrumentation.runOnMainSync(()->{
            assertNotSame(first,activity);
            assertEquals(AppearancePreference.DARK,AppearanceController.preference(activity));
            assertNotNull(findText(root(),"Switch to light"));
            AppearanceController.choose(activity,AppearancePreference.LIGHT);
            assertNotNull(findText(first.findViewById(android.R.id.content),"Switch to dark"));
            first.finish();
        });
    }

    @Test public void retainedSignInProgressBlocksASecondSubmitAndKeepsFailureRecovery() throws Exception {
        open();Activity old=activity;AtomicReference<Object> work=new AtomicReference<>();
        instrumentation.runOnMainSync(()->{
            ((EditText)activity.findViewById(R.id.login_email)).setText("retained@example.invalid");
            ((EditText)activity.findViewById(R.id.login_password)).setText("memory-only");
            try{
                var field=LoginActivity.class.getDeclaredField("state");field.setAccessible(true);work.set(field.get(activity));
                var busy=work.get().getClass().getDeclaredField("busy");busy.setAccessible(true);busy.setBoolean(work.get(),true);
                var render=LoginActivity.class.getDeclaredMethod("renderSignIn");render.setAccessible(true);render.invoke(activity);
            }catch(Exception error){throw new AssertionError(error);}
            assertFalse(findText(root(),"Sign in").isEnabled());activity.recreate();
        });
        long limit=SystemClock.uptimeMillis()+10000;
        while(activity==old&&SystemClock.uptimeMillis()<limit){
            instrumentation.runOnMainSync(()->{for(Activity candidate:ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED))
                if(candidate instanceof LoginActivity&&candidate!=old)activity=candidate;});SystemClock.sleep(25);
        }
        assertNotSame(old,activity);
        instrumentation.runOnMainSync(()->{
            try{
                var field=LoginActivity.class.getDeclaredField("state");field.setAccessible(true);assertSame(work.get(),field.get(activity));
                assertFalse(findText(root(),"Sign in").isEnabled());assertNotNull(findText(root(),"Signing in..."));
                var busy=work.get().getClass().getDeclaredField("busy");busy.setAccessible(true);busy.setBoolean(work.get(),false);
                var error=work.get().getClass().getDeclaredField("error");error.setAccessible(true);error.set(work.get(),"Fixture sign-in failed. Retry when connected.");
                var render=LoginActivity.class.getDeclaredMethod("renderSignIn");render.setAccessible(true);render.invoke(activity);
                assertTrue(findText(root(),"Sign in").isEnabled());assertNotNull(findText(root(),"Fixture sign-in failed. Retry when connected."));
                assertEquals("memory-only",((EditText)activity.findViewById(R.id.login_password)).getText().toString());
            }catch(Exception error){throw new AssertionError(error);}
        });
    }

    @Test public void doubleFontSignInScrollsWithReadableInputsAndMemoryOnlyPassword(){
        activity=instrumentation.startActivitySync(new Intent(instrumentation.getTargetContext(),LoginActivity.class)
                .putExtra(NativeUiTestRunner.FONT_SCALE,2f).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_MULTIPLE_TASK));
        instrumentation.waitForIdleSync();
        instrumentation.runOnMainSync(()->{
            android.widget.ScrollView scroll=findScroll(root());assertNotNull("Sign-in form must scroll at large text",scroll);
            EditText email=activity.findViewById(R.id.login_email),password=activity.findViewById(R.id.login_password);
            email.setText("large-text@example.invalid");password.setText("memory-only-draft");
            assertFalse("Password must never enter saved hierarchy state",password.isSaveEnabled());
            assertTrue("Input must grow to its actual font line",email.getHeight()>=email.getLineHeight()+email.getCompoundPaddingTop()+email.getCompoundPaddingBottom());
            AppearanceController.choose(activity,AppearancePreference.LIGHT);assertSame(email,activity.findViewById(R.id.login_email));
            scroll.fullScroll(View.FOCUS_DOWN);assertNotNull(findText(root(),"Sign in"));
            AppearanceController.choose(activity,AppearancePreference.DARK);assertEquals("memory-only-draft",password.getText().toString());
        });
    }
    private android.widget.ScrollView findScroll(View view){
        if(view instanceof android.widget.ScrollView scroll)return scroll;
        if(view instanceof ViewGroup group)for(int i=0;i<group.getChildCount();i++){var found=findScroll(group.getChildAt(i));if(found!=null)return found;}
        return null;
    }

    private void open() {
        activity=instrumentation.startActivitySync(new Intent(instrumentation.getTargetContext(),LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_MULTIPLE_TASK));
        instrumentation.waitForIdleSync();
    }
    private View root(){return activity.findViewById(android.R.id.content);}
    private TextView findText(View view,String expected) {
        if(view instanceof TextView text&&expected.contentEquals(text.getText()))return text;
        if(view instanceof ViewGroup group)for(int i=0;i<group.getChildCount();i++) {
            TextView found=findText(group.getChildAt(i),expected);if(found!=null)return found;
        }
        return null;
    }
}
