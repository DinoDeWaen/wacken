package be.wacken.planner;

import org.junit.Test;
import static org.junit.Assert.*;

public final class RatingValueTextTest {
    @Test public void eachPlanningValueKeepsTheApprovedMeaningAcrossPresentations() {
        String[] expected={"Unrated","1 · Veto","2 · Low interest","3 · Optional","4 · Strong choice","5 · Must see"};
        for(int value=0;value<expected.length;value++) {
            assertEquals(expected[value],RatingValueText.describe(value));
            assertEquals("Planning: "+expected[value],LineupPresentation.planningLabel(value));
        }
    }
}
