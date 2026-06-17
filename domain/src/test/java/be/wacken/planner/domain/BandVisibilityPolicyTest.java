package be.wacken.planner.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class BandVisibilityPolicyTest {
    @Test
    void hidesGenericMetalBattlePlaceholders() {
        assertTrue(BandVisibilityPolicy.isGenericMetalBattlePlaceholder("Metal Battle"));
        assertTrue(BandVisibilityPolicy.isGenericMetalBattlePlaceholder("Metal Battle tba."));
        assertTrue(BandVisibilityPolicy.isGenericMetalBattlePlaceholder("metal-battle finalist"));
    }

    @Test
    void keepsRealNamedBandsThatOnlyMentionMetalBattle() {
        assertFalse(BandVisibilityPolicy.isGenericMetalBattlePlaceholder("Battle Beast"));
        assertFalse(BandVisibilityPolicy.isGenericMetalBattlePlaceholder("The Metal Battle Alumni"));
        assertFalse(BandVisibilityPolicy.isGenericMetalBattlePlaceholder("Grand Magus"));
    }
}
