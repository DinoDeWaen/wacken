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
    public void archivedFestivalListUsesActiveBandListTableShape() throws IOException {
        String source = new String(Files.readAllBytes(source("ArchivedFestivalActivity.java")), StandardCharsets.UTF_8);

        assertTrue(source.contains("headerCell(\"Band\", 2.45f)"));
        assertTrue(source.contains("headerCell(\"Rating\", 1.75f)"));
        assertTrue(source.contains("headerCell(\"\", 1.45f)"));
        assertTrue(source.contains("headerCell(\"Stage\", 1.25f)"));
        assertTrue(source.contains("headerCell(\"Date\", 0.95f)"));
        assertTrue(source.contains("headerCell(\"Time\", 1.15f)"));
        assertTrue(source.contains("new RatingStarsView(ArchivedFestivalActivity.this"));
        assertTrue(source.contains("rowActions(Optional.ofNullable(bandsByName.get(band.bandName())))"));
        assertTrue(source.contains("rating.setEnabled(false);"));
    }

    @Test
    public void archivedBandDetailUsesActiveDetailSectionsReadOnly() throws IOException {
        String source = new String(Files.readAllBytes(source("ArchivedBandDetailActivity.java")), StandardCharsets.UTF_8);

        assertTrue(source.contains("sectionTitle(\"Your Rating\")"));
        assertTrue(source.contains("sectionTitle(\"Real Rating\")"));
        assertTrue(source.contains("sectionTitle(\"Running Order\")"));
        assertTrue(source.contains("sectionTitle(\"Band Links\")"));
        assertTrue(source.contains("imagePanel(url)"));
        assertTrue(source.contains("disabledStars(detail.realRating()"));
        assertTrue(source.contains("reset.setEnabled(false);"));
        assertTrue(source.contains("PersonalRatingHistoryItem::rating"));
        assertTrue(source.contains("item.festivalName().filter(history.festivalName()::equals).isPresent()"));
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
