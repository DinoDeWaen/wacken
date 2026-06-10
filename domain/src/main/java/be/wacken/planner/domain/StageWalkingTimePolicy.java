package be.wacken.planner.domain;

import java.util.Locale;

public final class StageWalkingTimePolicy {
    private static final String HEAVY = "heavy";
    private static final String LOUDER = "louder";

    private StageWalkingTimePolicy() {
    }

    public static int defaultWalkingMinutes(String fromStageName, String toStageName) {
        String from = normalizeStage(fromStageName);
        String to = normalizeStage(toStageName);
        if (from.equals(to)) {
            return 0;
        }
        if (isHeavyOrLouder(from) && isHeavyOrLouder(to)) {
            return 5;
        }
        if (isHeavyOrLouder(from) || isHeavyOrLouder(to)) {
            return 15;
        }
        return 5;
    }

    private static boolean isHeavyOrLouder(String stageName) {
        return HEAVY.equals(stageName) || LOUDER.equals(stageName);
    }

    private static String normalizeStage(String stageName) {
        return stageName == null ? "" : stageName.trim().toLowerCase(Locale.ROOT);
    }
}
