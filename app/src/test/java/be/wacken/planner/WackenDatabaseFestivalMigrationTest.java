package be.wacken.planner;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

public final class WackenDatabaseFestivalMigrationTest {
    @Test
    public void migratesExistingDatabasesToSeedActiveWackenFestival() throws IOException {
        String source = new String(Files.readAllBytes(databaseSource()), StandardCharsets.UTF_8);

        assertTrue(source.contains("version = 8"));
        assertTrue(source.contains("CREATE TABLE IF NOT EXISTS festivals"));
        assertTrue(!source.contains("CREATE UNIQUE INDEX IF NOT EXISTS idx_festivals_one_active"));
        assertTrue(source.contains("VALUES ('wacken-2026', 'Wacken Open Air 2026', 'ACTIVE')"));
        assertTrue(source.contains("MIGRATION_5_6"));
        assertTrue(source.contains("CREATE TABLE IF NOT EXISTS festival_lineup_entries"));
        assertTrue(source.contains("CREATE TABLE IF NOT EXISTS festival_planning_ratings"));
        assertTrue(source.contains("CREATE TABLE IF NOT EXISTS personal_band_rating_events"));
        assertTrue(source.contains("SELECT 'wacken-2026', name, name FROM bands"));
        assertTrue(source.contains("'1970-01-01T00:00:00Z'"));
        assertTrue(source.contains("MIGRATION_6_7"));
        assertTrue(source.contains("MIGRATION_7_8"));
        assertTrue(source.contains("DROP INDEX IF EXISTS idx_festivals_one_active"));
    }

    private Path databaseSource() {
        return List.of(
                        Path.of("app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java"),
                        Path.of("src/main/java/be/wacken/planner/persistence/WackenDatabase.java")
                )
                .stream()
                .filter(Files::exists)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("WackenDatabase source file not found."));
    }
}
