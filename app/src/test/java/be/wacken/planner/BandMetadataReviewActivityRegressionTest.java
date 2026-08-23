package be.wacken.planner;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

public final class BandMetadataReviewActivityRegressionTest {
    @Test
    public void keepsReviewedMetadataControlsVisibleInSource() throws IOException {
        String source = new String(Files.readAllBytes(activitySource()), StandardCharsets.UTF_8);

        assertTrue("Review screen must fetch proposals through the application use case.", source.contains("new SearchBandMetadataUseCase"));
        assertTrue("Review screen must register MusicBrainz as the first metadata provider.", source.contains("new MusicBrainzMetadataProvider"));
        assertTrue("Review screen must require selectable proposal approval.", source.contains("new CheckBox(this)"));
        assertTrue("Review screen must save only accepted metadata proposals.", source.contains("\"Save accepted metadata\""));
        assertTrue("Review screen must call the approval use case.", source.contains("applyMetadata.apply"));
        assertTrue("Review screen must show missing-field proposal source context.", source.contains("proposal.sourceName()"));
    }

    private Path activitySource() {
        return List.of(
                        Path.of("app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java"),
                        Path.of("src/main/java/be/wacken/planner/BandMetadataReviewActivity.java")
                )
                .stream()
                .filter(Files::exists)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("BandMetadataReviewActivity source file not found."));
    }
}
