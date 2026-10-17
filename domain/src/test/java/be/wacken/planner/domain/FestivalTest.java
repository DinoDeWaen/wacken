package be.wacken.planner.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class FestivalTest {
    @Test
    void archiveMakesFestivalHistoricalAndReadOnly() {
        Festival archived = Festival.active("wacken-2026", "Wacken Open Air 2026").archive();

        assertEquals(FestivalStatus.ARCHIVED, archived.status());
        assertTrue(archived.isArchived());
        assertTrue(archived.isReadOnly());
    }

    @Test
    void requiresIdentityAndName() {
        assertThrows(DomainValidationException.class, () -> Festival.active("", "Wacken Open Air 2026"));
        assertThrows(DomainValidationException.class, () -> Festival.active("wacken-2026", " "));
    }
}
