package be.wacken.planner;

/** Shared display vocabulary; rating validity and lifecycle remain domain-owned. */
final class RatingValueText {
    private RatingValueText() { }

    static String describe(int value) {
        return switch(value) {
            case 1 -> "1 · Veto";
            case 2 -> "2 · Low interest";
            case 3 -> "3 · Optional";
            case 4 -> "4 · Strong choice";
            case 5 -> "5 · Must see";
            default -> "Unrated";
        };
    }
}
