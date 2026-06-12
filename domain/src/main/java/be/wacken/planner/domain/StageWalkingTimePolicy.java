package be.wacken.planner.domain;

import java.util.Locale;

public final class StageWalkingTimePolicy {
    private static final String HARDER = "harder";
    private static final String FASTER = "faster";
    private static final String LOUDER = "louder";
    private static final String HEADBANGERS = "headbangers";
    private static final String WET = "wet";

    private StageWalkingTimePolicy() {
    }

    public static int defaultWalkingMinutes(String fromStageName, String toStageName) {
        String from = normalizeStage(fromStageName);
        String to = normalizeStage(toStageName);
        if (from.equals(to)) {
            return 0;
        }
        if (sameNearbyGroup(from, to)) {
            return 5;
        }
        return 15;
    }

    private static boolean sameNearbyGroup(String from, String to) {
        return isMainStageGroup(from) && isMainStageGroup(to)
                || isHeadbangersWetGroup(from) && isHeadbangersWetGroup(to);
    }

    private static boolean isMainStageGroup(String stageName) {
        return HARDER.equals(stageName) || FASTER.equals(stageName) || LOUDER.equals(stageName);
    }

    private static boolean isHeadbangersWetGroup(String stageName) {
        return HEADBANGERS.equals(stageName) || WET.equals(stageName);
    }

    private static String normalizeStage(String stageName) {
        if (stageName == null) {
            return "";
        }
        String normalized = stageName.trim().toLowerCase(Locale.ROOT);
        if (normalized.endsWith(" stage")) {
            normalized = normalized.substring(0, normalized.length() - " stage".length()).trim();
        }
        return normalized.replaceAll("[^a-z0-9]", "");
    }
}
