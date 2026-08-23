package be.wacken.planner;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

public final class BandLinkReviewActivityRegressionTest {
    @Test
    public void keepsReviewedLinkingControlsVisibleInSource() throws IOException {
        String source = new String(Files.readAllBytes(activitySource()), StandardCharsets.UTF_8);

        assertTrue("Review screen must lay each candidate out as a row.", source.contains("row.setOrientation(LinearLayout.HORIZONTAL)"));
        assertTrue("Review screen must show imported names.", source.contains("candidate.uploadedDisplayName()"));
        assertTrue("Review screen must provide editable search.", source.contains("Search own band database"));
        assertTrue("Review screen must use a dropdown for candidate matches.", source.contains("new Spinner(this)"));
        assertTrue("Review screen must include an explicit no-match option.", source.contains("\"No match\""));
        assertTrue("Review screen must require per-row approval.", source.contains("\"Link selected match\""));
        assertTrue("Review screen must call the link use case on approval.", source.contains("linkBand.link"));
    }

    private Path activitySource() {
        return List.of(
                        Path.of("app/src/main/java/be/wacken/planner/BandLinkReviewActivity.java"),
                        Path.of("src/main/java/be/wacken/planner/BandLinkReviewActivity.java")
                )
                .stream()
                .filter(Files::exists)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("BandLinkReviewActivity source file not found."));
    }
}
