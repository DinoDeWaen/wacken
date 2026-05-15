package be.wacken.planner.infrastructure;

import be.wacken.planner.application.ApplicationBoundary;

public final class InfrastructureBoundary {
    private InfrastructureBoundary() {
    }

    public static String name() {
        return "infrastructure -> " + ApplicationBoundary.name();
    }
}
