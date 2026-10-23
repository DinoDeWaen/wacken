package be.wacken.planner;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import androidx.test.runner.AndroidJUnitRunner;

/** Applies explicit test-only device configurations before real Activity view creation. */
public final class NativeUiTestRunner extends AndroidJUnitRunner {
    static final String FONT_SCALE="be.wacken.planner.test.FONT_SCALE";

    @Override public void onStart() {
        String appearance=androidx.test.platform.app.InstrumentationRegistry.getArguments().getString("initialAppearance");
        if(appearance!=null) {
            if(!appearance.equals("dark")&&!appearance.equals("light"))throw new IllegalArgumentException("initialAppearance must be dark or light");
            AppearanceController.store(getTargetContext()).edit().putString(AppearanceController.MODE,appearance).commit();
        }
        super.onStart();
    }

    @Override public Activity newActivity(ClassLoader loader,String className,Intent intent)
            throws InstantiationException,IllegalAccessException,ClassNotFoundException {
        Activity activity=super.newActivity(loader,className,intent);
        if(intent.hasExtra(FONT_SCALE)) {
            Configuration override=new Configuration();
            override.fontScale=intent.getFloatExtra(FONT_SCALE,1f);
            activity.applyOverrideConfiguration(override);
        }
        return activity;
    }
}
