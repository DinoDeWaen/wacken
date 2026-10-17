package be.wacken.planner.application;

public record AddFestivalResult(boolean success, String message, int reusedBands, int createdBands, int prefilledRatings) {
    public static AddFestivalResult added(int reusedBands, int createdBands, int prefilledRatings) {
        return new AddFestivalResult(true, "Festival added.", reusedBands, createdBands, prefilledRatings);
    }

    public static AddFestivalResult failure(String message) {
        return new AddFestivalResult(false, message, 0, 0, 0);
    }
}
