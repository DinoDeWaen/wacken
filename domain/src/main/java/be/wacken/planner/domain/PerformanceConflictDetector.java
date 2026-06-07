package be.wacken.planner.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class PerformanceConflictDetector {
    public List<PerformanceConflictSet> detect(List<Performance> performances) {
        Objects.requireNonNull(performances, "performances must not be null");
        List<Performance> sorted = performances.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator
                        .comparing(Performance::start)
                        .thenComparing(Performance::end)
                        .thenComparing(performance -> performance.band().name(), String.CASE_INSENSITIVE_ORDER))
                .toList();

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
        return List.copyOf(sets);
    }

    private boolean overlapsAny(List<Performance> current, Performance candidate) {
        return current.stream().anyMatch(existing -> overlaps(existing, candidate));
    }

    private boolean overlaps(Performance first, Performance second) {
        if (!first.start().toLocalDate().equals(second.start().toLocalDate())) {
            return false;
        }
        return first.start().isBefore(second.end()) && second.start().isBefore(first.end());
    }
}
