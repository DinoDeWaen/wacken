package be.wacken.planner;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

/** Presentation lifecycle only: restyling never invokes a business operation. */
final class AppearanceController implements Application.ActivityLifecycleCallbacks, ComponentCallbacks {
    static final String PREFERENCES="appearance";
    static final String MODE="mode";
    private static final Map<Activity,WeakReference<AppearanceController>> ATTACHED=new WeakHashMap<>();
    private final Activity activity;
    private final SharedPreferences preferences;
    private final SharedPreferences.OnSharedPreferenceChangeListener listener=(store,key)->{
        if(key==null||MODE.equals(key))refresh();
    };
    private final Map<View,Map<String,BiConsumer<View,AppearancePreference.Palette>>> bindings=new WeakHashMap<>();
    private final Map<GradientDrawable,Panel> panels=new WeakHashMap<>();
    private final Map<android.app.Dialog,Boolean> dialogs=new WeakHashMap<>();
    private boolean closed;
    private boolean deviceDark;

    private AppearanceController(Activity activity) {
        this.activity=activity;
        preferences=store(activity);
        deviceDark=isDark(activity.getResources().getConfiguration());
        preferences.registerOnSharedPreferenceChangeListener(listener);
        activity.getApplication().registerActivityLifecycleCallbacks(this);
        activity.getApplication().registerComponentCallbacks(this);
        refresh();
    }

    static void attach(Activity activity) {
        if(find(activity)!=null)return;
        ATTACHED.put(activity,new WeakReference<>(new AppearanceController(activity)));
    }

    static SharedPreferences store(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFERENCES,Context.MODE_PRIVATE);
    }

    static AppearancePreference preference(Context context) {
        try{return AppearancePreference.parse(store(context).getString(MODE,null));}
        catch(ClassCastException invalid){return AppearancePreference.SYSTEM;}
    }

    static void choose(Context context,AppearancePreference mode) {
        store(context).edit().putString(MODE,mode.storedValue()).apply();
    }

    static AppearancePreference.Palette palette(Context context) {
        AppearanceController controller=find(context);
        boolean dark=controller==null?isDark(context.getResources().getConfiguration()):controller.deviceDark;
        return preference(context).palette(dark);
    }

    static String toggleLabel(Context context) {
        return palette(context).dark()?"Switch to light":"Switch to dark";
    }

    static void toggle(Context context) {
        choose(context,palette(context).dark()?AppearancePreference.LIGHT:AppearancePreference.DARK);
    }

    // Binding callbacks receive their weakly held target; they must not capture it.
    static void bind(View view,String property,BiConsumer<View,AppearancePreference.Palette> update) {
        AppearanceController controller=find(view.getContext());
        if(controller!=null)controller.bindings.computeIfAbsent(view,key->new HashMap<>()).put(property,update);
        update.accept(view,palette(view.getContext()));
    }

    static void ownDialog(android.app.Dialog dialog) {
        AppearanceController controller=find(dialog.getContext());
        if(controller!=null)controller.dialogs.put(dialog,Boolean.TRUE);
    }

    static boolean hasOpenDialogs(Activity activity) {
        AppearanceController controller=find(activity);
        return controller!=null&&controller.dialogs.keySet().stream().anyMatch(android.app.Dialog::isShowing);
    }

    static boolean hasBinding(View view,String property) {
        AppearanceController controller=find(view.getContext());
        return controller!=null&&controller.bindings.getOrDefault(view,Map.of()).containsKey(property);
    }

    static void bindPanel(Context context,GradientDrawable drawable,int fill,int border,int corner) {
        Panel panel=new Panel(fill,border,corner);
        AppearanceController controller=find(context);
        if(controller!=null)controller.panels.put(drawable,panel);
        panel.apply(drawable,palette(context),context);
    }

    private record Panel(int fill,int border,int corner) {
        void apply(GradientDrawable drawable,AppearancePreference.Palette palette,Context context) {
            drawable.setColor(WackenTheme.resolve(fill,palette));
            drawable.setStroke(WackenTheme.dp(context,1),WackenTheme.resolve(border,palette));
            drawable.setCornerRadius(WackenTheme.dp(context,corner));
        }
    }

    private void refresh() {
        if(closed)return;
        AppearancePreference.Palette palette=preference(activity).palette(deviceDark);
        activity.setTheme(palette.dark()?R.style.AppThemeDark:R.style.AppThemeLight);
        // Copy keys strongly for this refresh. A copied WeakHashMap entry still
        // has a weak key that GC can clear between snapshot and callback.
        for(View target:new ArrayList<>(bindings.keySet())) {
            Map<String,BiConsumer<View,AppearancePreference.Palette>> updates=bindings.get(target);
            if(target!=null&&updates!=null)for(var update:new ArrayList<>(updates.values()))update.accept(target,palette);
        }
        for(GradientDrawable target:new ArrayList<>(panels.keySet())) {
            Panel panel=panels.get(target);if(target!=null&&panel!=null)panel.apply(target,palette,activity);
        }
        activity.getWindow().setStatusBarColor(palette.background());
        activity.getWindow().setNavigationBarColor(palette.background());
        View decor=activity.getWindow().getDecorView();
        int flags=decor.getSystemUiVisibility();
        flags=palette.dark()?flags&~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR:flags|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        if(Build.VERSION.SDK_INT>=26)
            flags=palette.dark()?flags&~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR:flags|View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        decor.setSystemUiVisibility(flags);
    }

    private static boolean isDark(Configuration configuration) {
        return (configuration.uiMode&Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES;
    }

    private static AppearanceController find(Context context) {
        while(context instanceof ContextWrapper wrapper) {
            if(context instanceof Activity activity) {
                WeakReference<AppearanceController> reference=ATTACHED.get(activity);
                return reference==null?null:reference.get();
            }
            Context base=wrapper.getBaseContext();if(base==context)break;context=base;
        }
        return null;
    }

    @Override public void onConfigurationChanged(Configuration configuration) {
        deviceDark=isDark(configuration);refresh();
    }
    @Override public void onLowMemory() { }
    @Override public void onActivityResumed(Activity target) {
        if(target==activity){deviceDark=isDark(activity.getResources().getConfiguration());refresh();}
    }
    @Override public void onActivityDestroyed(Activity target) {
        if(target!=activity)return;
        closed=true;
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
        activity.getApplication().unregisterActivityLifecycleCallbacks(this);
        activity.getApplication().unregisterComponentCallbacks(this);
        for(android.app.Dialog dialog:new ArrayList<>(dialogs.keySet()))if(dialog.isShowing())dialog.dismiss();
        dialogs.clear();ATTACHED.remove(activity);bindings.clear();panels.clear();
    }
    @Override public void onActivityCreated(Activity target,Bundle state) { }
    @Override public void onActivityStarted(Activity target) { }
    @Override public void onActivityPaused(Activity target) { }
    @Override public void onActivityStopped(Activity target) { }
    @Override public void onActivitySaveInstanceState(Activity target,Bundle state) { }
}
