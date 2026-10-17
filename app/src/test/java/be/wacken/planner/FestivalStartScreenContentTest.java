package be.wacken.planner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import be.wacken.planner.application.FestivalStartState;
import be.wacken.planner.domain.Festival;

public final class FestivalStartScreenContentTest {
    @Test
    public void activeFestivalShowsEditableBandListAndArchiveAction() {
        FestivalStartScreenContent content = FestivalStartScreenContent.from(
                new FestivalStartState(Optional.of(Festival.active("wacken-2026", "Wacken Open Air 2026")), List.of()),
                "dino@example.com"
        );

        assertEquals("Wacken Open Air 2026", content.title());
        assertEquals("Line-up ratings for dino@example.com", content.subtitle());
        assertTrue(content.showBandList());
        assertTrue(content.showArchiveAction());
        assertFalse(content.showAddFestivalAction());
    }

    @Test
    public void noActiveFestivalShowsArchiveStateAndAddPath() {
        FestivalStartScreenContent content = FestivalStartScreenContent.from(
                new FestivalStartState(Optional.empty(), List.of(Festival.archived("wacken-2026", "Wacken Open Air 2026"))),
                "dino@example.com"
        );

        assertEquals("Festival archive", content.title());
        assertEquals("No active festival", content.subtitle());
        assertFalse(content.showBandList());
        assertFalse(content.showArchiveAction());
        assertTrue(content.showAddFestivalAction());
        assertEquals("Archived festivals (read-only): Wacken Open Air 2026", content.statusText());
    }
}
