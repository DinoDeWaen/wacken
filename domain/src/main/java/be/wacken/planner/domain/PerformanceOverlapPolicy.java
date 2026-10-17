package be.wacken.planner.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public final class PerformanceOverlapPolicy {
    private static final Duration MIDDLE_WINDOW = Duration.ofMinutes(30);

    public boolean overlapsForScheduling(Performance first, Performance second) {
        Objects.requireNonNull(first, "first must not be null");
        Objects.requireNonNull(second, "second must not be null");
        TimeWindow firstWindow = middleWindow(first);
        TimeWindow secondWindow = middleWindow(second);
        return firstWindow.start().isBefore(secondWindow.end())
                && secondWindow.start().isBefore(firstWindow.end());
    }

    private TimeWindow middleWindow(Performance performance) {
        Duration duration = Duration.between(performance.start(), performance.end());
        Duration window = duration.compareTo(MIDDLE_WINDOW) < 0 ? duration : MIDDLE_WINDOW;
        LocalDateTime midpoint = performance.start().plus(duration.dividedBy(2));
        Duration halfWindow = window.dividedBy(2);
        return new TimeWindow(midpoint.minus(halfWindow), midpoint.plus(halfWindow));
    }

    private record TimeWindow(LocalDateTime start, LocalDateTime end) {
    }
}
