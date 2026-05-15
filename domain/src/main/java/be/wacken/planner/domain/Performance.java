package be.wacken.planner.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public record Performance(Band band, Stage stage, LocalDateTime start, LocalDateTime end) {
    public Performance {
        Objects.requireNonNull(band, "band must not be null");
        Objects.requireNonNull(stage, "stage must not be null");
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(end, "end must not be null");
        if (!end.isAfter(start)) {
            throw new DomainValidationException("Performance end time must be after start time.");
        }
    }
}
