package be.wacken.planner.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class FestivalLifecycle {
    private FestivalLifecycle() {
    }

    public static Optional<Festival> activeFestival(List<Festival> festivals) {
        Objects.requireNonNull(festivals, "festivals must not be null");
        List<Festival> activeFestivals = festivals.stream()
                .filter(Festival::isActive)
                .toList();
        if (activeFestivals.size() > 1) {
            throw new DomainValidationException("Only one festival can be active at a time.");
        }
        return activeFestivals.stream().findFirst();
    }

    public static List<Festival> archivedFestivals(List<Festival> festivals) {
        Objects.requireNonNull(festivals, "festivals must not be null");
        return festivals.stream()
                .filter(Festival::isArchived)
                .toList();
    }
}
