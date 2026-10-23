package be.wacken.planner;

import static org.junit.Assert.*;
import org.junit.Test;

/** Independent WCAG relative-luminance checks for the approved native semantic pairs. */
public final class AppearanceContrastTest {
    @Test public void referenceBlackWhiteAndEqualColorsHaveKnownRatios(){
        assertEquals(21.0,contrast(0xff000000,0xffffffff),0.00001);
        assertEquals(1.0,contrast(0xff3b553c,0xff3b553c),0.00001);
    }
    @Test public void pairedTextAndMeaningfulControlBoundariesMeetTheContract(){
        for(var mode:new AppearancePreference[]{AppearancePreference.DARK,AppearancePreference.LIGHT}){
            var palette=mode.palette(false);double minimumText=Double.MAX_VALUE,minimumControl=Double.MAX_VALUE;
            for(int surface:new int[]{palette.background(),palette.panel(),palette.raised()}){
                for(int foreground:new int[]{palette.text(),palette.muted(),palette.accent(),palette.error()}){
                    double ratio=contrast(foreground,surface);minimumText=Math.min(minimumText,ratio);
                    assertTrue(mode+" normal text contrast "+ratio,ratio>=4.5);
                }
                double boundary=contrast(palette.line(),surface);minimumControl=Math.min(minimumControl,boundary);
                assertTrue(mode+" meaningful boundary contrast "+boundary,boundary>=3.0);
            }
            assertTrue(mode+" primary action text",contrast(palette.onAccent(),palette.accent())>=4.5);
            System.out.println(mode+" minimum normal text="+minimumText+"; minimum boundary="+minimumControl
                    +"; primary action="+contrast(palette.onAccent(),palette.accent()));
        }
    }
    private static double contrast(int a,int b){double first=luminance(a),second=luminance(b);return (Math.max(first,second)+0.05)/(Math.min(first,second)+0.05);}
    private static double luminance(int color){return 0.2126*linear((color>>16)&255)+0.7152*linear((color>>8)&255)+0.0722*linear(color&255);}
    private static double linear(int channel){double value=channel/255.0;return value<=0.04045?value/12.92:Math.pow((value+0.055)/1.055,2.4);}
}
