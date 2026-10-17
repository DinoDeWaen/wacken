package be.wacken.planner.application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record ScheduleDay(LocalDate date, List<TimelineSlot> slots) {
    public ScheduleDay {
        if (date == null) {
            throw new IllegalArgumentException("Schedule day date must not be null.");
        }
        if (slots == null) {
            throw new IllegalArgumentException("Schedule day slots must not be null.");
        }
        slots = Collections.unmodifiableList(new ArrayList<>(slots));
    }
}
