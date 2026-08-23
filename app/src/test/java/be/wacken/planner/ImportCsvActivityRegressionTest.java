package be.wacken.planner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

public final class ImportCsvActivityRegressionTest {
    @Test
    public void addFestivalNameFieldIsNotPreFilledWithExampleFestival() throws IOException {
        String source = new String(Files.readAllBytes(importCsvActivitySource()), StandardCharsets.UTF_8);

        assertTrue("Add-festival flow must keep a festival name input.", source.contains("festivalName = new EditText(this);"));
        assertTrue("Festival name field must guide with a hint.", source.contains("festivalName.setHint(\"Festival name\")"));
        assertFalse("Add-festival flow must not submit a hard-coded placeholder festival name.", source.contains("festivalName.setText(\"Rock im Park\")"));
    }

    private Path importCsvActivitySource() {
        return List.of(
                        Path.of("app/src/main/java/be/wacken/planner/ImportCsvActivity.java"),
                        Path.of("src/main/java/be/wacken/planner/ImportCsvActivity.java")
                )
                .stream()
                .filter(Files::exists)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("ImportCsvActivity source file not found."));
    }
}
