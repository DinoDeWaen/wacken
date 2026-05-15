package be.wacken.planner.application;

import java.util.Optional;

public record RateBandResult(boolean success, Optional<String> validationMessage) {
    public static RateBandResult stored() {
        return new RateBandResult(true, Optional.empty());
    }

    public static RateBandResult failure(String validationMessage) {
        return new RateBandResult(false, Optional.of(validationMessage));
    }
}
