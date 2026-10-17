package be.wacken.planner.application;

public record BandLinkResult(boolean success, String message) {
    public static BandLinkResult linked(String uploadedName, String targetBandName) {
        return new BandLinkResult(true, "Linked " + uploadedName + " to " + targetBandName + ".");
    }

    public static BandLinkResult noMatch(String uploadedName) {
        return new BandLinkResult(true, "No match selected for " + uploadedName + ".");
    }

    public static BandLinkResult failure(String message) {
        return new BandLinkResult(false, message);
    }
}
