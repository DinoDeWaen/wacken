package be.wacken.planner;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import be.wacken.planner.application.BandListItem;

public final class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView content = new TextView(this);
        content.setText(renderBandList(List.of()));
        setContentView(content);
    }

    private String renderBandList(List<BandListItem> bands) {
        if (bands.isEmpty()) {
            return getString(R.string.empty_band_list);
        }

        StringBuilder list = new StringBuilder();
        for (BandListItem band : bands) {
            list.append(band.bandName())
                    .append('\n')
                    .append(band.stageName())
                    .append(" | ")
                    .append(band.startTime())
                    .append(" - ")
                    .append(band.endTime())
                    .append('\n')
                    .append("Rating: ")
                    .append(band.rating().map(String::valueOf).orElse("not rated"))
                    .append("\n\n");
        }
        return list.toString().trim();
    }
}
