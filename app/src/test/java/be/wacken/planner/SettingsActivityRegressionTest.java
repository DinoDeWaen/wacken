package be.wacken.planner;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

public final class SettingsActivityRegressionTest {
    @Test
    public void initializesRatingAllocationTextBeforeStylingIt() throws IOException {
        String source = new String(Files.readAllBytes(settingsActivitySource()), StandardCharsets.UTF_8);

        int initialization = source.indexOf("ratingAllocation = new TextView(this);");
        int firstStyleUse = source.indexOf("ratingAllocation.setTextColor");
        int addedToSection = source.indexOf("ratingSection.addView(ratingAllocation)");

        assertTrue("SettingsActivity must initialize ratingAllocation before styling it.", initialization >= 0);
        assertTrue("SettingsActivity must style ratingAllocation after initialization.", initialization < firstStyleUse);
        assertTrue("SettingsActivity must add ratingAllocation after initialization.", initialization < addedToSection);
    }

    @Test
    public void exposesRenameActiveFestivalActionInAdminSettings() throws IOException {
        String source = new String(Files.readAllBytes(settingsActivitySource()), StandardCharsets.UTF_8);

        assertTrue("Settings/Admin must expose active festival rename action.", source.contains("\"Rename active festival\""));
        assertTrue("SettingsActivity must call the rename use case.", source.contains("new RenameActiveFestivalUseCase"));
        assertTrue("Rename UI must keep blank-name validation visible to the user.", source.contains("Festival name must not be blank."));
    }

    @Test
    public void exposesBandLinkingAndMetadataActionsInAdminSettings() throws IOException {
        String source = new String(Files.readAllBytes(settingsActivitySource()), StandardCharsets.UTF_8);

        assertTrue("Settings/Admin must expose imported-band linking.", source.contains("\"Link imported bands\""));
        assertTrue("Settings/Admin must open the band-link review screen.", source.contains("BandLinkReviewActivity.class"));
        assertTrue("Settings/Admin must expose missing metadata enrichment.", source.contains("\"Fetch band metadata\""));
        assertTrue("SettingsActivity must use the catalog metadata enrichment use case.", source.contains("new EnrichBandMetadataFromCatalogUseCase"));
    }

    private Path settingsActivitySource() {
        return List.of(
                        Path.of("app/src/main/java/be/wacken/planner/SettingsActivity.java"),
                        Path.of("src/main/java/be/wacken/planner/SettingsActivity.java")
                )
                .stream()
                .filter(Files::exists)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("SettingsActivity source file not found."));
    }
}
