package be.wacken.planner;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

public final class LegacyRealRatingBackfillRegressionTest {
    @Test
    public void appRepositoriesBackfillsLegacyRealRatingsForCurrentSessionUser() throws IOException {
        String source = new String(Files.readAllBytes(source("AppRepositories.java")), StandardCharsets.UTF_8);

        assertTrue(source.contains("this.session = authSessionStore.load();"));
        assertTrue(source.contains("personalRatingCache.backfillLegacyWackenRealRatings("));
        assertTrue(source.contains("session.isPresent() ? Optional.of(session.userId()) : Optional.empty()"));
    }

    @Test
    public void roomBackfillUsesUuidCompatibleIdsAndRemovesStaleLegacyDuplicates() throws IOException {
        String source = new String(
                Files.readAllBytes(source("persistence/RoomPersonalBandRatingHistoryRepository.java")),
                StandardCharsets.UTF_8
        );

        assertTrue(source.contains("UUID.nameUUIDFromBytes"));
        assertTrue(source.contains("deleteStaleLegacyEvents(targetUserName, rating.band());"));
        assertTrue(source.contains("currentUserName.orElse(rating.userName())"));
        assertTrue(source.contains("staleLegacyEventId"));
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
