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
    public void appRepositoriesUsesFestivalScopedRatingsWithoutInventingLegacyFestivalOwnership() throws IOException {
        String source = new String(Files.readAllBytes(source("AppRepositories.java")), StandardCharsets.UTF_8);

        assertTrue(source.contains("this.session = authSessionStore.load();"));
        assertTrue(source.contains("new ActiveFestivalAfterRatingRepository(festivals, afterRatings)"));
        assertTrue("Unknown legacy ownership must not be reassigned at startup", !source.contains("personalRatingCache.backfillLegacyWackenRealRatings("));
        assertTrue(source.contains("sourceMode == SourceMode.SUPABASE && session.isPresent()"));
    }

    @Test
    public void roomBackfillUsesAndroidCompatibleCollectionOperationsAndUuidCompatibleIds() throws IOException {
        String source = new String(
                Files.readAllBytes(source("persistence/RoomPersonalBandRatingHistoryRepository.java")),
                StandardCharsets.UTF_8
        );

        assertTrue("Startup backfill must not use Stream.toList on Android.", !source.contains("stream().toList()"));
        assertTrue("Startup backfill must not use Stream.toList on Android.", !source.contains(".stream()\n                .toList()"));
        assertTrue(source.contains(".collect(Collectors.toList())"));
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
