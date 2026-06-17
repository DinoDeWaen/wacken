package be.wacken.planner.domain;

import java.util.Locale;

public final class BandVisibilityPolicy {
    private BandVisibilityPolicy() {
    }

    public static boolean isVisibleInRatingLists(Band band) {
        return band != null && !isGenericMetalBattlePlaceholder(band.name());
    }

    public static boolean isGenericMetalBattlePlaceholder(String bandName) {
        if (bandName == null) {
            return false;
        }
        String normalized = bandName
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[\\s._-]+", " ");
        return normalized.equals("metal battle") || normalized.startsWith("metal battle ");
    }
}
