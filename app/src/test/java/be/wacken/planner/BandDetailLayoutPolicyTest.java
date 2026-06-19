package be.wacken.planner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class BandDetailLayoutPolicyTest {
    @Test
    public void stacksDetailSectionsOnNarrowPhoneScreens() {
        assertTrue(BandDetailLayoutPolicy.stacksSections(480));
    }

    @Test
    public void keepsImageAndDetailSectionsSideBySideOnLargerScreens() {
        assertFalse(BandDetailLayoutPolicy.stacksSections(520));
    }
}
