package be.wacken.planner;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class WackenThemeTest {
    @Test
    public void exposesVisualDesignSystemColorTokens() {
        assertEquals(0xFF121819, WackenTheme.BACKGROUND);
        assertEquals(0xFF20282A, WackenTheme.PANEL);
        assertEquals(0xFF263033, WackenTheme.ELEVATED_PANEL);
        assertEquals(0xFFFFD24A, WackenTheme.GOLD);
        assertEquals(0xFFFF3B6B, WackenTheme.RED);
        assertEquals(0xFFAAB3B7, WackenTheme.STEEL_GREY);
    }

    @Test
    public void definesConsistentButtonStyles() {
        assertEquals(WackenTheme.RED, WackenTheme.ButtonStyle.PRIMARY.fillColor());
        assertEquals(WackenTheme.WHITE, WackenTheme.ButtonStyle.PRIMARY.textColor());
        assertEquals(WackenTheme.GOLD, WackenTheme.ButtonStyle.PREMIUM.fillColor());
        assertEquals(WackenTheme.BLACK, WackenTheme.ButtonStyle.PREMIUM.textColor());
        assertEquals(WackenTheme.GRID, WackenTheme.ButtonStyle.SECONDARY.borderColor());
        assertEquals(WackenTheme.BLOOD_RED, WackenTheme.ButtonStyle.DANGER.borderColor());
    }
}
