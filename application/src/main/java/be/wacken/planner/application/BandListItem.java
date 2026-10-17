package be.wacken.planner.application;

import java.util.List;

public record BandListItem(
        String bandName,
        String stageName,
        String startTime,
        String endTime,
        int rating,
        boolean defaultRating,
        List<PersonRatingStars> personRatings
) {
    private static final String TIME_SEPARATOR = "T";

    public BandListItem(String bandName, String stageName, String startTime, String endTime, int rating, boolean defaultRating) {
        this(bandName, stageName, startTime, endTime, rating, defaultRating, List.of());
    }

    public BandListItem {
        personRatings = List.copyOf(personRatings);
    }

    public String displayDate() {
        if (!hasDateTimeSeparator(startTime)) {
            return startTime;
        }
        return startTime.substring(0, startTime.indexOf(TIME_SEPARATOR));
    }

    public String displayTime() {
        if (!hasDateTimeSeparator(startTime) || !hasDateTimeSeparator(endTime)) {
            return startTime;
        }
        return clockTime(startTime) + " - " + clockTime(endTime);
    }

    public boolean explicitRating() {
        return !defaultRating;
    }

    public boolean hasPersonRatings() {
        return !personRatings.isEmpty();
    }

    public String personRatingSummary() {
        return personRatings.stream()
                .map(PersonRatingStars::displayText)
                .reduce((left, right) -> left + "  " + right)
                .orElse("");
    }

    private String clockTime(String dateTime) {
        String time = dateTime.substring(dateTime.indexOf(TIME_SEPARATOR) + 1);
        return time.length() >= 5 ? time.substring(0, 5) : time;
    }

    private boolean hasDateTimeSeparator(String value) {
        return value.indexOf(TIME_SEPARATOR) > 0;
    }
}
