package be.wacken.planner;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import be.wacken.planner.application.FestivalCsvFiles;
import be.wacken.planner.application.ImportFestivalCsvResult;
import be.wacken.planner.application.ImportFestivalCsvUseCase;

public final class ImportCsvActivity extends Activity {
    private EditText bandsCsv;
    private EditText stagesCsv;
    private EditText performancesCsv;
    private EditText distancesCsv;
    private EditText foodCsv;
    private TextView resultMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        int padding = 32;
        form.setPadding(padding, padding, padding, padding);

        bandsCsv = csvInput("bands.csv");
        stagesCsv = csvInput("stages.csv");
        performancesCsv = csvInput("performances.csv");
        distancesCsv = csvInput("distances.csv");
        foodCsv = csvInput("food.csv");

        form.addView(label("bands.csv"));
        form.addView(bandsCsv);
        form.addView(label("stages.csv"));
        form.addView(stagesCsv);
        form.addView(label("performances.csv"));
        form.addView(performancesCsv);
        form.addView(label("distances.csv"));
        form.addView(distancesCsv);
        form.addView(label("food.csv"));
        form.addView(foodCsv);

        Button importButton = new Button(this);
        importButton.setText("Import");
        importButton.setOnClickListener(view -> importCsv());
        form.addView(importButton);

        Button backButton = new Button(this);
        backButton.setText("Back to band list");
        backButton.setOnClickListener(view -> finish());
        form.addView(backButton);

        resultMessage = new TextView(this);
        form.addView(resultMessage);

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(form);
        setContentView(scrollView);
    }

    private void importCsv() {
        AppRepositories repositories = new AppRepositories(this);
        ImportFestivalCsvUseCase importFestivalCsv = new ImportFestivalCsvUseCase(
                repositories.bands(),
                repositories.stages(),
                repositories.performances(),
                repositories.distances(),
                repositories.foodOptions()
        );

        ImportFestivalCsvResult result = importFestivalCsv.importCsv(new FestivalCsvFiles(
                bandsCsv.getText().toString(),
                stagesCsv.getText().toString(),
                performancesCsv.getText().toString(),
                distancesCsv.getText().toString(),
                foodCsv.getText().toString()
        ));

        if (result.success()) {
            resultMessage.setText("Import successful.");
        } else {
            resultMessage.setText(String.join("\n", result.errors()));
        }
    }

    private TextView label(String text) {
        TextView label = new TextView(this);
        label.setText(text);
        return label;
    }

    private EditText csvInput(String hint) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setMinLines(4);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        return input;
    }
}
