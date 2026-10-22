package be.wacken.planner;

import be.wacken.planner.application.BandListItem;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public final class LineupPresentationTest {
    @Test public void nameSearchAndUnratedFilterKeepActualOrderedDataAndDoNotMutateIt() {
        var first=new BandListItem("A Smoke", "Real stage", "2027-08-19T18:00", "2027-08-19T19:00",0,true);
        var second=new BandListItem("B Smoke", "TBA", "TBA", "TBA",4,false);
        var input=List.of(first,second);
        assertEquals(input,LineupPresentation.select(input,"SMOKE",false));
        assertEquals(List.of(first),LineupPresentation.select(input,"smoke",true));
        assertEquals(List.of(second),LineupPresentation.select(input,"b smoke",false));
        assertTrue(LineupPresentation.select(input,"unknown",false).isEmpty());
        assertSame(first,LineupPresentation.select(input,"",false).get(0));
        assertEquals(4,second.rating());
    }

    @Test public void planningValueHasExplicitMeaningIncludingUnratedAndVeto() {
        assertEquals("Planning: Unrated",LineupPresentation.planningLabel(0));
        assertEquals("Planning: 1 · Veto",LineupPresentation.planningLabel(1));
        assertEquals("Planning: 4 · Strong choice",LineupPresentation.planningLabel(4));
        assertEquals("Planning: 5 · Must see",LineupPresentation.planningLabel(5));
    }
}
