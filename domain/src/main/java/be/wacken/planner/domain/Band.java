package be.wacken.planner.domain;

public record Band(String name) {
    public Band {
        name = requireName(name, "Band name must not be blank.");
    }

    private static String requireName(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(message);
        }
        return value.trim();
    }
}
