package be.wacken.planner.application;

import java.util.List;

public record SharedSchedule(SharedScheduleStatus status, String message, List<ScheduleDay> days) {
    public SharedSchedule {
        if (status == null) {
            throw new IllegalArgumentException("Shared schedule status must not be null.");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Shared schedule message must not be blank.");
        }
        if (days == null) {
            throw new IllegalArgumentException("Shared schedule days must not be null.");
        }
        days = List.copyOf(days);
    }

    public static SharedSchedule generated(List<ScheduleDay> days) {
        return new SharedSchedule(SharedScheduleStatus.GENERATED, "Shared schedule generated.", days);
    }

    public static SharedSchedule noScheduledPerformances() {
        return new SharedSchedule(SharedScheduleStatus.NO_SCHEDULED_PERFORMANCES, "No scheduled performances are available yet.", List.of());
    }

    public static SharedSchedule noSelections() {
        return new SharedSchedule(SharedScheduleStatus.NO_SELECTIONS, "No performances were selected by the group rules.", List.of());
    }
}
