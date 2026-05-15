package be.wacken.planner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import be.wacken.planner.application.BandListItem;
import be.wacken.planner.application.ListBandsUseCase;

public final class MainActivity extends Activity {
    private static final String CURRENT_USER = "my group";

    private TextView bandList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        int padding = 32;
        screen.setPadding(padding, padding, padding, padding);

        Button importButton = new Button(this);
        importButton.setText("Import CSV");
        importButton.setOnClickListener(view -> startActivity(new Intent(this, ImportCsvActivity.class)));
        screen.addView(importButton);

        bandList = new TextView(this);
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(bandList);
        screen.addView(scrollView);

        setContentView(screen);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppRepositories repositories = new AppRepositories(this);
        List<BandListItem> bands = new ListBandsUseCase(
                repositories.performances(),
                repositories.ratings(),
                CURRENT_USER
        ).listBands();
        bandList.setText(renderBandList(bands));
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
