package be.wacken.planner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

public final class BandDetailActivityRegressionTest {
    @Test
    public void openingBandDetailUsesTheReadFlowWithoutCreatingMissingBands() throws IOException {
        String source = new String(Files.readAllBytes(activitySource()), StandardCharsets.UTF_8);

        assertFalse("Opening detail must not save a placeholder band.", source.contains("bands.save("));
        assertTrue("Missing bands must show an explicit state.", source.contains("setContentView(renderMissingBand());"));
        assertTrue("Known bands must still show their detail.", source.contains("setContentView(render(detail));"));
    }

    private Path activitySource() {
        return List.of(
                        Path.of("app/src/main/java/be/wacken/planner/BandDetailActivity.java"),
                        Path.of("src/main/java/be/wacken/planner/BandDetailActivity.java")
                )
                .stream()
                .filter(Files::exists)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("BandDetailActivity source file not found."));
    }
}
