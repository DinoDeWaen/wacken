package be.wacken.planner;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.SavedRating;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class RatingAllocationSummaryTest {
    @Test
    public void countsOneToFiveStarRatingsForTheSelectedUser() {
        Map<Integer, Integer> counts = RatingAllocationSummary.countForUser("dino", List.of(
                rating("dino", "Airbourne", 5),
                rating("dino", "Iron Maiden", 5),
                rating("dino", "Skyline", 4),
                rating("dino", "Alcest", 3),
                rating("dino", "Rose Tattoo", 2),
                rating("dino", "Europe", 1),
                rating("dino", "Unrated", 0),
                rating("sofie", "Airbourne", 5)
        ));

        assertEquals(Map.of(
                5, 2,
                4, 1,
                3, 1,
                2, 1,
                1, 1
        ), counts);
    }

    @Test
    public void keepsZeroBucketsVisibleWhenNoRatingsExist() {
        Map<Integer, Integer> counts = RatingAllocationSummary.countForUser("dino", List.of());

        assertEquals(Map.of(
                5, 0,
                4, 0,
                3, 0,
                2, 0,
                1, 0
        ), counts);
    }

    @Test
    public void formatsCountsForSettingsDisplay() {
        String summary = RatingAllocationSummary.format(Map.of(
                5, 2,
                4, 0,
                3, 1,
                2, 0,
                1, 4
        ));

        assertTrue(summary.contains("Rating allocation"));
        assertTrue(summary.contains("5 stars: 2"));
        assertTrue(summary.contains("4 stars: 0"));
        assertTrue(summary.contains("3 stars: 1"));
        assertTrue(summary.contains("2 stars: 0"));
        assertTrue(summary.contains("1 star: 4"));
    }

    private SavedRating rating(String userName, String bandName, int value) {
        return new SavedRating(userName, new Band(bandName), Rating.of(value));
    }
}
