package be.wacken.planner;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

public final class SupabaseScheduleLockClientTest {
    @Test
    public void mapsScheduleLockRowsToConflictSelections() throws Exception {
        Map<String, String> locks = SupabaseScheduleLockClient.parseLocks("""
                [
                  {
                    "conflict_key": "Airbourne|Harder|2026-07-30T18:30|2026-07-30T19:30||5th Avenue|Faster|2026-07-30T18:00|2026-07-30T19:00",
                    "selected_candidate_key": "Airbourne|Harder|2026-07-30T18:30|2026-07-30T19:30"
                  }
                ]
                """);

        assertEquals(1, locks.size());
        assertEquals(
                "Airbourne|Harder|2026-07-30T18:30|2026-07-30T19:30",
                locks.get("Airbourne|Harder|2026-07-30T18:30|2026-07-30T19:30||5th Avenue|Faster|2026-07-30T18:00|2026-07-30T19:00")
        );
    }
}
