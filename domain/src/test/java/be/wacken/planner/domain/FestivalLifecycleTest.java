package be.wacken.planner.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

final class FestivalLifecycleTest {
    @Test
    void findsTheSingleActiveFestival() {
        Festival active = Festival.active("wacken-2026", "Wacken Open Air 2026");
        Festival archived = Festival.archived("wacken-2025", "Wacken Open Air 2025");

        assertEquals(active, FestivalLifecycle.activeFestival(List.of(archived, active)).orElseThrow());
    }

    @Test
    void allowsNoActiveFestival() {
        assertTrue(FestivalLifecycle.activeFestival(List.of(Festival.archived("wacken-2026", "Wacken Open Air 2026"))).isEmpty());
    }

    @Test
    void rejectsMultipleActiveFestivals() {
        List<Festival> festivals = List.of(
                Festival.active("wacken-2026", "Wacken Open Air 2026"),
                Festival.active("rock-am-ring-2027", "Rock am Ring 2027")
        );

        assertThrows(DomainValidationException.class, () -> FestivalLifecycle.activeFestival(festivals));
    }
}
