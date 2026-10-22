package be.wacken.planner;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

public final class ArchivedFestivalLayoutRegressionTest {
    @Test
    public void archivedFestivalKeepsReadOnlyRatingsAndKnownMusicActions() throws IOException {
        String source = new String(Files.readAllBytes(source("ArchivedFestivalActivity.java")), StandardCharsets.UTF_8);

        assertTrue(source.contains("new RatingStarsView(ArchivedFestivalActivity.this"));
        assertTrue(source.contains("rowActions(Optional.ofNullable(bandsByName.get(band.bandName())))"));
        assertTrue(source.contains("rating.setEnabled(false);"));
    }

    private Path source(String fileName) {
        return List.of(
                        Path.of("app/src/main/java/be/wacken/planner/" + fileName),
                        Path.of("src/main/java/be/wacken/planner/" + fileName)
                )
                .stream()
                .filter(Files::exists)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(fileName + " source file not found."));
    }
}
