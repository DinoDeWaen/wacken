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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import be.wacken.planner.application.AddFestivalResult;
import be.wacken.planner.application.AddFestivalUseCase;
import be.wacken.planner.application.FestivalCreationGateway;
import be.wacken.planner.application.FestivalCsvFiles;
import be.wacken.planner.application.FestivalCsvReader;
import be.wacken.planner.application.ImportFestivalCsvResult;

import be.wacken.planner.domain.Band;

public final class ImportCsvActivity extends Activity {
    static final String EXTRA_IMPORT_FEEDBACK = "be.wacken.planner.IMPORT_FEEDBACK";
    static final String EXTRA_ADD_FESTIVAL_MODE = "be.wacken.planner.ADD_FESTIVAL_MODE";
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

    private boolean addFestivalMode;
    private EditText festivalName;
    private TextView resultMessage;
    private Button importButton;
    private FestivalCreationGateway festivalCreation;
    private be.wacken.planner.application.FestivalBandCatalog festivalBandCatalog;
    private FestivalBandReviewDialog bandReview;
    private LocalCsvImportReviewDialog localReview;
    private java.util.function.Supplier<be.wacken.planner.application.ReviewFestivalCsvUseCase> localImportFactory;
    private record RetainedImport(FestivalCsvFiles files,java.util.List<String> fileNames,
                                  LocalCsvImportReviewDialog.State review) {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addFestivalMode = getIntent().getBooleanExtra(EXTRA_ADD_FESTIVAL_MODE, false);

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setBackgroundColor(WackenTheme.BACKGROUND);
        int padding = dp(18);
        form.setPadding(padding, padding, padding, padding);

        TextView title = new TextView(this);
        title.setText(addFestivalMode ? "Add Festival" : "Wacken CSV Import");
        title.setTextColor(WackenTheme.AMBER);
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        form.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText(addFestivalMode
                ? "Name the next festival and select bands.csv. Review similar names and aliases before creating the lineup."
                : "Select CSV files, review each proposed change, then apply only accepted rows. This import updates local data; ratings stay untouched.");
        subtitle.setTextColor(WackenTheme.MUTED);
        subtitle.setPadding(0, dp(8), 0, dp(16));
        form.addView(subtitle);

        if (addFestivalMode) {
            festivalName = new EditText(this);
            festivalName.setHint("Festival name");
            festivalName.setTextColor(WackenTheme.TEXT);
            festivalName.setHintTextColor(WackenTheme.MUTED);
            festivalName.setSingleLine(true);
            form.addView(festivalName);
        }

        form.addView(filePicker("Bands", bandsCsv, REQUEST_BANDS));
        if (!addFestivalMode) {
            form.addView(filePicker("Stages", stagesCsv, REQUEST_STAGES));
            form.addView(filePicker("Performances", performancesCsv, REQUEST_PERFORMANCES));
            form.addView(filePicker("Distances", distancesCsv, REQUEST_DISTANCES));
            form.addView(filePicker("Food", foodCsv, REQUEST_FOOD));
        }

        importButton = actionButton(addFestivalMode ? "Add festival from bands.csv" : "Review selected files");
        importButton.setOnClickListener(view -> importCsv());
        form.addView(importButton);

        Button backButton = secondaryButton("Back to band list");
        backButton.setOnClickListener(view -> finish());
        form.addView(backButton);

        resultMessage = new TextView(this);
        resultMessage.setTextColor(WackenTheme.TEXT);
        resultMessage.setPadding(dp(12), dp(10), dp(12), dp(10));
        resultMessage.setText(addFestivalMode ? "Select bands.csv to add the next festival." : "Select files to import festival data.");
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
        if(getLastNonConfigurationInstance() instanceof RetainedImport retained) {
            CsvSelection[] selections={bandsCsv,stagesCsv,performancesCsv,distancesCsv,foodCsv};
            String[] contents={retained.files().bandsCsv(),retained.files().stagesCsv(),retained.files().performancesCsv(),retained.files().distancesCsv(),retained.files().foodCsv()};
            for(int i=0;i<selections.length;i++) {
                CsvSelection selected=selections[i];selected.contents=contents[i];selected.fileName=retained.fileNames().get(i);
                if(selected.label!=null&&selected.fileName!=null)selected.label.setText(selected.fileName);
            }
            if(!addFestivalMode&&retained.review()!=null&&!retained.review().closed)showLocalReview(retained.review());
        } else if(savedInstanceState!=null) {
            resultMessage.setText("Select CSV files again to start a fresh review.");
        }
        if(savedInstanceState!=null&&festivalName!=null)festivalName.setText(savedInstanceState.getString("festivalName",""));
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

        if (addFestivalMode) {
            addFestivalFromBandsCsv();
            return;
        }
        android.content.Context application=getApplicationContext();
        if(localImportFactory==null)localImportFactory=()->AppRepositories.tsvFallback(application).reviewLocalFestivalImport();
        showLocalReview(new LocalCsvImportReviewDialog.State(new FestivalCsvFiles(
                bandsCsv.contents,
                stagesCsv.contents,
                performancesCsv.contents,
                distancesCsv.contents,
                foodCsv.contents
        ),localImportFactory));
    }

    private void showLocalReview(LocalCsvImportReviewDialog.State state) {
        importButton.setEnabled(false);
        localReview=new LocalCsvImportReviewDialog(this,state,result->{
            importButton.setEnabled(true);localReview=null;
            if(result==null) {resultMessage.setText("Review cancelled. No import changes were saved.");return;}
            if (result.success()) {
            resultMessage.setTextColor(WackenTheme.AMBER);
            String feedback = "Accepted local CSV changes saved. Ratings and archived data were kept. Sync may refresh local master data.";
            resultMessage.setText(feedback);
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_IMPORT_FEEDBACK, feedback);
            setResult(RESULT_OK, resultIntent);
            }
        });
        localReview.show();
    }

    private void addFestivalFromBandsCsv() {
        AuthSession session = new AuthSessionStore(this).load();
        if (!session.isPresent()) {
            resultMessage.setTextColor(WackenTheme.RED);
            resultMessage.setText("Sign in before adding a group-wide festival.");
            setResult(RESULT_CANCELED);
            return;
        }
        String name = festivalName.getText().toString().trim();
        try { be.wacken.planner.domain.Festival.active(festivalId(name),name); }
        catch(be.wacken.planner.domain.DomainValidationException error) {
            resultMessage.setTextColor(WackenTheme.RED);resultMessage.setText(error.getMessage());return;
        }
        List<Band> bands;
        try {
            bands = FestivalCsvReader.bandNames(bandsCsv.contents).stream().map(Band::new).collect(Collectors.toList());
        } catch (FestivalCsvReader.FormatException error) {
            resultMessage.setTextColor(WackenTheme.RED);
            resultMessage.setText("Festival was not added. " + error.getMessage());
            setResult(RESULT_CANCELED);
            return;
        }
        importButton.setEnabled(false);
        resultMessage.setTextColor(WackenTheme.AMBER);
        resultMessage.setText("Loading bands for review...");
        new Thread(() -> {
            try {
                if(festivalBandCatalog==null) {
                    AppRepositories repositories=new AppRepositories(getApplicationContext());
                    repositories.requireReviewMember(session.userId());
                    festivalBandCatalog=repositories.festivalBandCatalog();
                    festivalCreation=repositories.festivalCreation();
                }
                var proposals=new be.wacken.planner.application.ReviewFestivalBandsUseCase(festivalBandCatalog).review(bands);
                runOnUiThread(() -> {
                    if(isFinishing()||isDestroyed())return;
                    bandReview=new FestivalBandReviewDialog(this,proposals,rows->createReviewedFestival(session,name,rows),()->{
                        importButton.setEnabled(true);resultMessage.setText("Review cancelled. No festival or aliases were created.");
                    });
                    bandReview.show();
                });
            } catch(RuntimeException error) {
                runOnUiThread(() -> {
                    if(isFinishing()||isDestroyed())return;
                    importButton.setEnabled(true);resultMessage.setTextColor(WackenTheme.RED);
                    resultMessage.setText("Could not review festival bands. "+error.getMessage());
                });
            }
        },"review-festival-bands").start();
    }

    private void createReviewedFestival(AuthSession session,String name,List<be.wacken.planner.domain.ReviewedFestivalBand> bands) {
        resultMessage.setText("Adding festival group-wide...");
        new AddFestivalAction(
                command -> new Thread(command, "add-festival").start(),
                this::runOnUiThread
        ).execute(() -> {
            if (festivalCreation == null) throw new IllegalStateException("Review bands again before creating the festival.");
            return new AddFestivalUseCase(festivalCreation)
                    .addReviewedFestival(session.groupId(), session.userId(), festivalId(name), name, bands);
        }, result -> {
            if(isFinishing()||isDestroyed())return;
            importButton.setEnabled(true);
            showAddFestivalResult(name, result);
        }, message -> {
            if(isFinishing()||isDestroyed())return;
            importButton.setEnabled(true);
            resultMessage.setTextColor(WackenTheme.RED);
            resultMessage.setText("Festival creation could not be confirmed. Retry the same name and CSV.\n"
                    + message);
            setResult(RESULT_CANCELED);
        });
    }

    @Override protected void onDestroy() {
        if(bandReview!=null)bandReview.dismiss();
        if(localReview!=null)localReview.detach(!isChangingConfigurations());
        super.onDestroy();
    }

    @Override public Object onRetainNonConfigurationInstance() {
        return new RetainedImport(new FestivalCsvFiles(bandsCsv.contents,stagesCsv.contents,performancesCsv.contents,distancesCsv.contents,foodCsv.contents),
                java.util.Arrays.asList(bandsCsv.fileName,stagesCsv.fileName,performancesCsv.fileName,distancesCsv.fileName,foodCsv.fileName),
                localReview==null||localReview.state.closed?null:localReview.state);
    }

    @Override protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if(festivalName!=null)state.putString("festivalName",festivalName.getText().toString());
    }

    private void showAddFestivalResult(String name, AddFestivalResult result) {
        if (result.success()) {
            resultMessage.setTextColor(WackenTheme.AMBER);
            String feedback = "Added " + name + ": " + result.reusedBands()
                    + " reused, " + result.createdBands()
                    + " new, " + result.prefilledRatings() + " prefilled.";
            resultMessage.setText(feedback);
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_IMPORT_FEEDBACK, feedback);
            setResult(RESULT_OK, resultIntent);
        } else {
            resultMessage.setTextColor(WackenTheme.RED);
            resultMessage.setText(result.message());
            setResult(RESULT_CANCELED);
        }
    }

    private String festivalId(String name) {
        String slug = name.toLowerCase(java.util.Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        return slug.isEmpty() ? "festival-" + java.util.UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)) : slug;
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
