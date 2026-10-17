package be.wacken.planner.domain;

import java.util.Objects;

public record Festival(String id, String name, FestivalStatus status) {
    public Festival {
        id = requireText(id, "Festival id must not be blank.");
        name = requireText(name, "Festival name must not be blank.");
        Objects.requireNonNull(status, "status must not be null");
    }

    public static Festival active(String id, String name) {
        return new Festival(id, name, FestivalStatus.ACTIVE);
    }

    public static Festival archived(String id, String name) {
        return new Festival(id, name, FestivalStatus.ARCHIVED);
    }

    public Festival archive() {
        return new Festival(id, name, FestivalStatus.ARCHIVED);
    }

    public Festival rename(String name) {
        return new Festival(id, name, status);
    }

    public boolean isActive() {
        return status == FestivalStatus.ACTIVE;
    }

    public boolean isArchived() {
        return status == FestivalStatus.ARCHIVED;
    }

    public boolean isReadOnly() {
        return isArchived();
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(message);
        }
        return value.trim();
    }
}
