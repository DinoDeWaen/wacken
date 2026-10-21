package be.wacken.planner;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/** Shared primary destinations; detail/review screens retain their initiating Activity. */
final class MainNavigation {
    enum Destination {
        BANDS("Bands",MainActivity.class),
        SCHEDULE("Schedule",ScheduleActivity.class),
        SETTINGS("Settings",SettingsActivity.class);

        final String label;
        final Class<? extends Activity> activity;
        Destination(String label,Class<? extends Activity> activity) {
            this.label=label;this.activity=activity;
        }
    }

    private MainNavigation() { }

    static View wrap(Activity activity,View content,Destination current) {
        LinearLayout screen=new LinearLayout(activity);
        screen.setOrientation(LinearLayout.VERTICAL);
        WackenTheme.backgroundColor(screen,WackenTheme.BACKGROUND);
        content.setId(R.id.destination_content);
        screen.addView(content,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1));

        LinearLayout destinations=new LinearLayout(activity);
        destinations.setTag("main-navigation");
        destinations.setOrientation(LinearLayout.HORIZONTAL);
        destinations.setPadding(WackenTheme.dp(activity,4),WackenTheme.dp(activity,4),WackenTheme.dp(activity,4),WackenTheme.dp(activity,4));
        destinations.setBackground(WackenTheme.panelBackground(activity,WackenTheme.PANEL,WackenTheme.GRID,0));
        for(Destination destination:Destination.values()) {
            boolean selected=destination==current;
            Button button=WackenTheme.actionButton(activity,destination.label,
                    selected?WackenTheme.ButtonStyle.PRIMARY:WackenTheme.ButtonStyle.SECONDARY,
                    view->open(activity,destination));
            button.setSelected(selected);
            button.setContentDescription(destination.label+(selected?", current destination":""));
            button.setTextSize(14);
            button.setMaxLines(2);
            LinearLayout.LayoutParams layout=new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1);
            layout.setMargins(WackenTheme.dp(activity,2),0,WackenTheme.dp(activity,2),0);
            destinations.addView(button,layout);
        }
        screen.addView(destinations,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        return screen;
    }

    static void open(Activity current,Destination destination) {
        if(destination.activity.isInstance(current))return;
        current.startActivity(new Intent(current,destination.activity).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }
}
