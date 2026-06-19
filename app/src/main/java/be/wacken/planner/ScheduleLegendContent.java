package be.wacken.planner;

final class ScheduleLegendContent {
    private ScheduleLegendContent() {
    }

    static String hideBarredLabel() {
        return "Hide barred overlaps";
    }

    static String ratingThresholdLabel() {
        return "Hide ratings at/below";
    }

    static String description() {
        return "Gold border: 5-star must see\n"
                + "Red border: 4-star strong choice\n"
                + "Grey border: 2-3-star optional\n"
                + "Scratched: lower-rated visible overlap to skip\n"
                + "Lock: locked group choice\n"
                + "Tie: shown first in alternatives\n"
                + "Filters: hide barred overlaps or ratings at/below the selected stars";
    }
}
