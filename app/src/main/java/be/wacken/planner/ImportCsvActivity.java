package be.wacken.planner;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import be.wacken.planner.application.FestivalCsvFiles;
import be.wacken.planner.application.ImportFestivalCsvResult;
import be.wacken.planner.application.ImportFestivalCsvUseCase;

public final class ImportCsvActivity extends Activity {
    private static final int REQUEST_BANDS = 100;
    private static final int REQUEST_STAGES = 101;
    private static final int REQUEST_PERFORMANCES = 102;
    private static final int REQUEST_DISTANCES = 103;
    private static final int REQUEST_FOOD = 104;

    private final CsvSelection bandsCsv = new CsvSelection("bands.csv");
    private final CsvSelection stagesCsv = new CsvSelection("stages.csv");
    private final CsvSelection performancesCsv = new CsvSelection("performances.csv");
    private final CsvSelection distancesCsv = new CsvSelection("distances.csv");
    private final CsvSelection foodCsv = new CsvSelection("food.csv");

    private TextView resultMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setBackgroundColor(WackenTheme.BACKGROUND);
        int padding = dp(18);
        form.setPadding(padding, padding, padding, padding);

        TextView title = new TextView(this);
        title.setText("Wacken CSV Import");
        title.setTextColor(WackenTheme.AMBER);
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        form.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Select CSV files. Imported festival data is updated; existing ratings stay untouched.");
        subtitle.setTextColor(WackenTheme.MUTED);
        subtitle.setPadding(0, dp(8), 0, dp(16));
        form.addView(subtitle);

        form.addView(filePicker("Bands", bandsCsv, REQUEST_BANDS));
        form.addView(filePicker("Stages", stagesCsv, REQUEST_STAGES));
        form.addView(filePicker("Performances", performancesCsv, REQUEST_PERFORMANCES));
        form.addView(filePicker("Distances", distancesCsv, REQUEST_DISTANCES));
        form.addView(filePicker("Food", foodCsv, REQUEST_FOOD));

        Button importButton = actionButton("Import selected files");
        importButton.setOnClickListener(view -> importCsv());
        form.addView(importButton);

        Button backButton = secondaryButton("Back to band list");
        backButton.setOnClickListener(view -> finish());
        form.addView(backButton);

        resultMessage = new TextView(this);
        resultMessage.setTextColor(WackenTheme.TEXT);
        resultMessage.setPadding(dp(12), dp(10), dp(12), dp(10));
        resultMessage.setText("Select files to import festival data.");
        resultMessage.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 6));
        LinearLayout.LayoutParams resultLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        resultLayout.setMargins(0, dp(12), 0, 0);
        form.addView(resultMessage, resultLayout);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(WackenTheme.BACKGROUND);
        scrollView.addView(form);
        setContentView(scrollView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null || data.getData() == null) {
            return;
        }

        CsvSelection selection = selectionFor(requestCode);
        if (selection == null) {
            return;
        }

        Uri uri = data.getData();
        try {
            selection.contents = readText(uri);
            selection.fileName = displayName(uri);
            selection.label.setText(selection.fileName);
            selection.label.setTextColor(Color.WHITE);
        } catch (IOException error) {
            resultMessage.setTextColor(WackenTheme.RED);
            resultMessage.setText("Could not read " + selection.expectedName + ": " + error.getMessage());
        }
    }

    private void importCsv() {
        if (bandsCsv.contents.isBlank()) {
            resultMessage.setTextColor(WackenTheme.RED);
            resultMessage.setText("Select at least bands.csv before importing.");
            return;
        }

        AppRepositories repositories = AppRepositories.tsvFallback(this);
        ImportFestivalCsvUseCase importFestivalCsv = new ImportFestivalCsvUseCase(
                repositories.bands(),
                repositories.stages(),
                repositories.performances(),
                repositories.distances(),
                repositories.foodOptions()
        );

        ImportFestivalCsvResult result = importFestivalCsv.importCsv(new FestivalCsvFiles(
                bandsCsv.contents,
                stagesCsv.contents,
                performancesCsv.contents,
                distancesCsv.contents,
                foodCsv.contents
        ));

        if (result.success()) {
            resultMessage.setTextColor(WackenTheme.AMBER);
            resultMessage.setText("Import successful. Existing ratings were preserved.");
        } else {
            resultMessage.setTextColor(WackenTheme.RED);
            resultMessage.setText(String.join("\n", result.errors()));
        }
    }

    private LinearLayout filePicker(String title, CsvSelection selection, int requestCode) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(dp(12), dp(10), dp(12), dp(10));
        row.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 6));
        LinearLayout.LayoutParams rowLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rowLayout.setMargins(0, 0, 0, dp(10));
        row.setLayoutParams(rowLayout);

        Button button = secondaryButton("Choose " + title + " CSV");
        button.setOnClickListener(view -> openCsvPicker(requestCode));
        row.addView(button);

        selection.label = new TextView(this);
        selection.label.setText("No file selected (" + selection.expectedName + ")");
        selection.label.setTextColor(WackenTheme.MUTED);
        selection.label.setPadding(dp(8), dp(6), dp(8), 0);
        row.addView(selection.label);

        return row;
    }

    private void openCsvPicker(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {
                "text/*",
                "text/csv",
                "application/csv",
                "application/vnd.ms-excel"
        });
        startActivityForResult(intent, requestCode);
    }

    private CsvSelection selectionFor(int requestCode) {
        if (requestCode == REQUEST_BANDS) {
            return bandsCsv;
        }
        if (requestCode == REQUEST_STAGES) {
            return stagesCsv;
        }
        if (requestCode == REQUEST_PERFORMANCES) {
            return performancesCsv;
        }
        if (requestCode == REQUEST_DISTANCES) {
            return distancesCsv;
        }
        if (requestCode == REQUEST_FOOD) {
            return foodCsv;
        }
        return null;
    }

    private String readText(Uri uri) throws IOException {
        StringBuilder text = new StringBuilder();
        try (InputStream stream = getContentResolver().openInputStream(uri)) {
            if (stream == null) {
                throw new IOException("No readable stream returned by Android.");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line).append('\n');
            }
        }
        return text.toString();
    }

    private String displayName(Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    return cursor.getString(index);
                }
            }
        }
        return uri.getLastPathSegment();
    }

    private Button actionButton(String text) {
        return WackenTheme.actionButton(this, text, WackenTheme.ButtonStyle.PREMIUM, null);
    }

    private Button secondaryButton(String text) {
        return WackenTheme.actionButton(this, text, WackenTheme.ButtonStyle.SECONDARY, null);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private static final class CsvSelection {
        private final String expectedName;
        private String contents = "";
        private String fileName;
        private TextView label;

        private CsvSelection(String expectedName) {
            this.expectedName = expectedName;
        }
    }
}
