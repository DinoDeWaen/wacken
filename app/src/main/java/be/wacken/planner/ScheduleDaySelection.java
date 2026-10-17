package be.wacken.planner;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import be.wacken.planner.application.ScheduleDay;

final class ScheduleDaySelection {
    private ScheduleDaySelection() {
    }

    static Optional<ScheduleDay> selectedDay(List<ScheduleDay> days, LocalDate requestedDate) {
        if (days == null || days.isEmpty()) {
            return Optional.empty();
        }
        if (requestedDate != null) {
            for (ScheduleDay day : days) {
                if (day.date().equals(requestedDate)) {
                    return Optional.of(day);
                }
            }
        }
        return Optional.of(days.get(0));
    }

    static boolean isSelected(ScheduleDay day, ScheduleDay selected) {
        return day != null && selected != null && day.date().equals(selected.date());
    }
}
