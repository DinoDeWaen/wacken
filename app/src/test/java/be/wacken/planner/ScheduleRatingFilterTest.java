package be.wacken.planner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;

import be.wacken.planner.application.ScheduleDecisionCandidate;

public final class ScheduleRatingFilterTest {
    @Test
    public void noFilterShowsAllCandidatesWithoutMutatingInput() {
        List<ScheduleDecisionCandidate> candidates = List.of(
                candidate("Two Star", 2),
                candidate("Three Star", 3),
                candidate("Five Star", 5)
        );

        List<ScheduleDecisionCandidate> visible = ScheduleRatingFilter.none().visibleCandidates(candidates);

        assertEquals(List.of("Two Star", "Three Star", "Five Star"), names(visible));
        assertEquals(List.of("Two Star", "Three Star", "Five Star"), names(candidates));
    }

    @Test
    public void hideTwoStarOrLowerFilterShowsOnlyActsAboveTwoStars() {
        ScheduleRatingFilter filter = ScheduleRatingFilter.hideAtOrBelow(2);

        List<ScheduleDecisionCandidate> visible = filter.visibleCandidates(List.of(
                candidate("Unrated", 0),
                candidate("Veto", 1),
                candidate("Two Star", 2),
                candidate("Three Star", 3),
                candidate("Five Star", 5)
        ));

        assertTrue(filter.active());
        assertEquals(List.of("Three Star", "Five Star"), names(visible));
    }

    @Test
    public void thresholdFilterCanBeTurnedOffWithNoFilter() {
        ScheduleDecisionCandidate twoStar = candidate("Two Star", 2);

        assertFalse(ScheduleRatingFilter.hideAtOrBelow(2).shows(twoStar));
        assertTrue(ScheduleRatingFilter.none().shows(twoStar));
    }

    @Test
    public void selectedTwoStarThresholdIsInclusive() {
        List<ScheduleDecisionCandidate> visible = ScheduleRatingFilter.hideAtOrBelow(2).visibleCandidates(List.of(
                candidate("Two Star", 2),
                candidate("Three Star", 3),
                candidate("Four Star", 4)
        ));

        assertEquals(List.of("Three Star", "Four Star"), names(visible));
    }

    @Test
    public void selectedHigherThresholdHidesThatRatingAndBelow() {
        List<ScheduleDecisionCandidate> visible = ScheduleRatingFilter.hideAtOrBelow(3).visibleCandidates(List.of(
                candidate("Two Star", 2),
                candidate("Three Star", 3),
                candidate("Four Star", 4),
                candidate("Five Star", 5)
        ));

        assertEquals(List.of("Four Star", "Five Star"), names(visible));
    }

    @Test
    public void hideBarredFilterKeepsBarredActsVisibleWhenOff() {
        List<ScheduleDecisionCandidate> candidates = overlappingCandidates();

        List<ScheduleDecisionCandidate> visible = ScheduleRatingFilter.none().visibleCandidates(candidates);

        assertEquals(List.of("Higher Rated", "Barred Lower Rated"), names(visible));
        assertTrue(ScheduleBlockStyle.from(candidates.get(1), candidates).scratched());
    }

    @Test
    public void hideBarredFilterHidesScratchedLostOverlappingActs() {
        List<ScheduleDecisionCandidate> candidates = overlappingCandidates();

        List<ScheduleDecisionCandidate> visible = ScheduleRatingFilter.hideBarred(0).visibleCandidates(candidates);

        assertEquals(List.of("Higher Rated"), names(visible));
    }

    @Test
    public void hideBarredFilterCombinesWithSelectedThreshold() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 30, 18, 0);
        List<ScheduleDecisionCandidate> candidates = List.of(
                new ScheduleDecisionCandidate("Higher Rated", 4, "Harder", start, start.plusHours(1), "CHOSEN", true),
                new ScheduleDecisionCandidate("Barred Lower Rated", 3, "Headbangers Stage", start.plusMinutes(15), start.plusMinutes(75), "NOT SELECTED", false),
                new ScheduleDecisionCandidate("Weak But Not Barred", 2, "Louder", start.plusHours(2), start.plusHours(3), "CHOSEN", true)
        );

        List<ScheduleDecisionCandidate> visible = ScheduleRatingFilter.hideBarred(2).visibleCandidates(candidates);

        assertEquals(List.of("Higher Rated"), names(visible));
    }

    private List<String> names(List<ScheduleDecisionCandidate> candidates) {
        return candidates.stream().map(ScheduleDecisionCandidate::bandName).toList();
    }

    private ScheduleDecisionCandidate candidate(String bandName, int rating) {
        LocalDateTime start = LocalDateTime.of(2026, 7, 30, 18, 0);
        return new ScheduleDecisionCandidate(
                bandName,
                rating,
                "Harder",
                start,
                start.plusHours(1),
                "CHOSEN",
                true
        );
    }

    private List<ScheduleDecisionCandidate> overlappingCandidates() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 30, 18, 0);
        return List.of(
                new ScheduleDecisionCandidate(
                        "Higher Rated",
                        4,
                        "Harder",
                        start,
                        start.plusHours(1),
                        "CHOSEN",
                        true
                ),
                new ScheduleDecisionCandidate(
                        "Barred Lower Rated",
                        3,
                        "Headbangers Stage",
                        start.plusMinutes(15),
                        start.plusMinutes(75),
                        "NOT SELECTED",
                        false
                )
        );
    }
}
