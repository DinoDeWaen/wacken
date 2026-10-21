package be.wacken.planner;

import org.junit.Test;
import static org.junit.Assert.*;

public final class AppearancePreferenceTest {
    @Test public void invalidAndMissingPreferencesFollowTheDevice() {
        for (String stored : new String[]{null,"","invalid","DARK"}) {
            assertEquals(AppearancePreference.SYSTEM, AppearancePreference.parse(stored));
            assertEquals(AppearancePreference.DARK, AppearancePreference.parse(stored).resolve(true));
            assertEquals(AppearancePreference.LIGHT, AppearancePreference.parse(stored).resolve(false));
        }
    }
    @Test public void explicitPreferencesSurviveOppositeDeviceChanges() {
        for (boolean dark : new boolean[]{true,false}) {
            assertEquals(AppearancePreference.DARK, AppearancePreference.parse("dark").resolve(dark));
            assertEquals(AppearancePreference.LIGHT, AppearancePreference.parse("light").resolve(dark));
        }
        for (AppearancePreference value : AppearancePreference.values())
            assertEquals(value, AppearancePreference.parse(value.storedValue()));
    }
    @Test public void toggleAlwaysSelectsTheOppositeExplicitMode() {
        assertEquals(AppearancePreference.LIGHT, AppearancePreference.SYSTEM.toggle(true));
        assertEquals(AppearancePreference.DARK, AppearancePreference.SYSTEM.toggle(false));
        assertEquals(AppearancePreference.DARK, AppearancePreference.LIGHT.toggle(true));
        assertEquals("Switch to light", AppearancePreference.DARK.toggleLabel(false));
        assertEquals("Switch to dark", AppearancePreference.LIGHT.toggleLabel(true));
    }
    @Test public void pairedPalettesMatchTheApprovedContractAndEnabledContrast() {
        var dark=AppearancePreference.DARK.palette(false);
        var light=AppearancePreference.LIGHT.palette(true);
        assertEquals(0xff101418,dark.background());
        assertEquals(0xffffd25a,dark.accent());
        assertEquals(0xfff4efe3,light.background());
        assertEquals(0xff3b553c,light.accent());
        for(var palette:new AppearancePreference.Palette[]{dark,light}) {
            for(int surface:new int[]{palette.background(),palette.panel(),palette.raised()}) {
                for(int text:new int[]{palette.text(),palette.muted(),palette.accent(),palette.error()})
                    assertTrue("Enabled text contrast",contrast(text,surface)>=4.5);
                assertTrue("Control boundary contrast",contrast(palette.line(),surface)>=3);
            }
            assertTrue("Primary label contrast",contrast(palette.onAccent(),palette.accent())>=4.5);
        }
    }
    private static double contrast(int a,int b) {
        double x=luminance(a),y=luminance(b);
        return (Math.max(x,y)+0.05)/(Math.min(x,y)+0.05);
    }
    private static double luminance(int color) {
        double[] c=new double[3];
        for(int i=0;i<3;i++) {double s=((color>>(16-i*8))&255)/255.0;c[i]=s<=0.04045?s/12.92:Math.pow((s+0.055)/1.055,2.4);}
        return c[0]*0.2126+c[1]*0.7152+c[2]*0.0722;
    }
}
