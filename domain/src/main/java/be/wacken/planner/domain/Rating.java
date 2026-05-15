package be.wacken.planner.domain;

public record Rating(int value) {
    public Rating {
        if (value < 0 || value > 4) {
            throw new DomainValidationException("Rating must be between 0 and 4.");
        }
    }

    public static Rating of(int value) {
        return new Rating(value);
    }
}
