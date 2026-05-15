package be.wacken.planner;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import be.wacken.planner.infrastructure.InfrastructureBoundary;

public final class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView content = new TextView(this);
        content.setText(getString(R.string.app_name) + "\n" + InfrastructureBoundary.name());
        setContentView(content);
    }
}
