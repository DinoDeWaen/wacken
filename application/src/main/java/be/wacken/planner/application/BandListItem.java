package be.wacken.planner.application;

public record BandListItem(String bandName, String stageName, String startTime, String endTime, int rating, boolean defaultRating) {
    private static final String TIME_SEPARATOR = "T";

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

    private String clockTime(String dateTime) {
        String time = dateTime.substring(dateTime.indexOf(TIME_SEPARATOR) + 1);
        return time.length() >= 5 ? time.substring(0, 5) : time;
    }

    private boolean hasDateTimeSeparator(String value) {
        return value.indexOf(TIME_SEPARATOR) > 0;
    }
}
