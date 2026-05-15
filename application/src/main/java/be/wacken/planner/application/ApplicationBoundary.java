package be.wacken.planner.application;

import be.wacken.planner.domain.DomainBoundary;

public final class ApplicationBoundary {
    private ApplicationBoundary() {
    }

    public static String name() {
        return "application -> " + DomainBoundary.name();
    }
}
