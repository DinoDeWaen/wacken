package be.wacken.planner.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class PerformanceConflictDetector {
    private final PerformanceOverlapPolicy overlapPolicy = new PerformanceOverlapPolicy();

    public List<PerformanceConflictSet> detect(List<Performance> performances) {
        Objects.requireNonNull(performances, "performances must not be null");
        List<Performance> sorted = performances.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator
                        .comparing(Performance::start)
                        .thenComparing(Performance::end)
                        .thenComparing(performance -> performance.band().name(), String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        List<PerformanceConflictSet> sets = new ArrayList<>();
        List<Performance> current = new ArrayList<>();
        for (Performance performance : sorted) {
            if (current.isEmpty() || overlapsAny(current, performance)) {
                current.add(performance);
                continue;
            }
            sets.add(new PerformanceConflictSet(current));
            current = new ArrayList<>();
            current.add(performance);
        }
        if (!current.isEmpty()) {
            sets.add(new PerformanceConflictSet(current));
        }
        return Collections.unmodifiableList(new ArrayList<>(sets));
    }

    private boolean overlapsAny(List<Performance> current, Performance candidate) {
        return current.stream().anyMatch(existing -> overlaps(existing, candidate));
    }

    private boolean overlaps(Performance first, Performance second) {
        return overlapPolicy.overlapsForScheduling(first, second);
    }
}
