package be.wacken.planner.domain;

public record Stage(String name) {
    public Stage {
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("Stage name must not be blank.");
        }
        name = name.trim();
    }
}
