package be.wacken.planner.application;

public record RenameActiveFestivalResult(boolean success, String message) {
    public static RenameActiveFestivalResult renamed() {
        return new RenameActiveFestivalResult(true, "Renamed active festival.");
    }

    public static RenameActiveFestivalResult failure(String message) {
        return new RenameActiveFestivalResult(false, message);
    }
}
