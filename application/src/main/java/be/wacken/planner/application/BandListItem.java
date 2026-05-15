package be.wacken.planner.application;

public record BandListItem(String bandName, String stageName, String startTime, String endTime, int rating, boolean defaultRating) {
}
