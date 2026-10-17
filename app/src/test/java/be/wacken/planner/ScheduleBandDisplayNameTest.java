package be.wacken.planner;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class ScheduleBandDisplayNameTest {
    @Test
    public void removesUuidSuffixFromDisplayName() {
        assertEquals(
                "Def Leppard",
                ScheduleBandDisplayName.clean("Def Leppard - 123e4567-e89b-12d3-a456-426614174000")
        );
    }

    @Test
    public void removesUuidInsideParenthesesFromDisplayName() {
        assertEquals(
                "Future Palace",
                ScheduleBandDisplayName.clean("Future Palace (123e4567-e89b-12d3-a456-426614174000)")
        );
    }

    @Test
    public void leavesNormalBandNameUntouched() {
        assertEquals("Uli Jon Roth", ScheduleBandDisplayName.clean("Uli Jon Roth"));
    }
}
