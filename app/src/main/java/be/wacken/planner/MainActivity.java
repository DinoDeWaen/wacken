package be.wacken.planner;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import be.wacken.planner.application.BandListItem;
import be.wacken.planner.application.ListBandsUseCase;

public final class MainActivity extends Activity {
    private static final String CURRENT_USER = "my group";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppRepositories repositories = new AppRepositories(this);
        List<BandListItem> bands = new ListBandsUseCase(
                repositories.performances(),
                repositories.ratings(),
                CURRENT_USER
        ).listBands();

        TextView content = new TextView(this);
        content.setText(renderBandList(bands));
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
                    .append(band.rating())
                    .append(band.defaultRating() ? " (default)" : "")
                    .append("\n\n");
        }
        return list.toString().trim();
    }
}
