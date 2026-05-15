package be.wacken.planner.domain;

public record FoodOption(String name) {
    public FoodOption {
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("Food option name must not be blank.");
        }
        name = name.trim();
    }
}
