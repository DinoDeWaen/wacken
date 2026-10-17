package be.wacken.planner;

import java.util.regex.Pattern;

final class ScheduleBandDisplayName {
    private static final Pattern UUID = Pattern.compile(
            "(?i)[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"
    );
    private static final Pattern TRAILING_SEPARATORS = Pattern.compile("[\\s|:_/()\\[\\]-]+$");
    private static final Pattern DUPLICATE_SPACES = Pattern.compile("\\s{2,}");

    private ScheduleBandDisplayName() {
    }

    static String clean(String rawName) {
        if (rawName == null || rawName.isBlank()) {
            return "";
        }
        String withoutUuid = UUID.matcher(rawName).replaceAll("");
        String normalized = DUPLICATE_SPACES.matcher(withoutUuid).replaceAll(" ").trim();
        return TRAILING_SEPARATORS.matcher(normalized).replaceAll("").trim();
    }
}
