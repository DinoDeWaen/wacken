package be.wacken.planner;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import be.wacken.planner.domain.BandVisibilityPolicy;
import be.wacken.planner.domain.SavedRating;

final class RatingAllocationSummary {
    private RatingAllocationSummary() {
    }

    static Map<Integer, Integer> countForUser(String userName, List<SavedRating> ratings) {
        Map<Integer, Integer> counts = emptyCounts();
        for (SavedRating rating : ratings) {
            int value = rating.rating().value();
            if (rating.userName().equals(userName)
                    && counts.containsKey(value)
                    && BandVisibilityPolicy.isVisibleInRatingLists(rating.band())) {
                counts.put(value, counts.get(value) + 1);
            }
        }
        return counts;
    }

    static String format(Map<Integer, Integer> counts) {
        StringBuilder summary = new StringBuilder("Rating allocation");
        for (int stars = 5; stars >= 1; stars--) {
            summary.append('\n')
                    .append(stars)
                    .append(stars == 1 ? " star: " : " stars: ")
                    .append(counts.getOrDefault(stars, 0));
        }
        return summary.toString();
    }

    private static Map<Integer, Integer> emptyCounts() {
        Map<Integer, Integer> counts = new LinkedHashMap<>();
        for (int stars = 5; stars >= 1; stars--) {
            counts.put(stars, 0);
        }
        return counts;
    }
}
