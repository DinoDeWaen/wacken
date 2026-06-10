package be.wacken.planner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import be.wacken.planner.application.TimelineSlot;

final class ScheduleCalendarLayout {
    private static final int MIN_BLOCK_MINUTES = 30;

    private final int startHour;
    private final int endHour;

    private ScheduleCalendarLayout(int startHour, int endHour) {
        this.startHour = startHour;
        this.endHour = endHour;
    }

    static ScheduleCalendarLayout forSlots(List<TimelineSlot> slots) {
        if (slots == null || slots.isEmpty()) {
            return new ScheduleCalendarLayout(12, 14);
        }
        int start = 23;
        int end = 0;
        for (TimelineSlot slot : slots) {
            start = Math.min(start, slot.start().getHour());
            int slotEndHour = slot.end().getMinute() == 0 ? slot.end().getHour() : slot.end().getHour() + 1;
            if (slot.end().toLocalDate().isAfter(slot.start().toLocalDate())) {
                slotEndHour += 24;
            }
            end = Math.max(end, slotEndHour);
        }
        if (end <= start) {
            end = start + 1;
        }
        return new ScheduleCalendarLayout(start, end);
    }

    int startHour() {
        return startHour;
    }

    int endHour() {
        return endHour;
    }

    int hourCount() {
        return endHour - startHour;
    }

    int topOffsetMinutes(TimelineSlot slot) {
        return Math.max(0, minutesFromStart(slot.start(), slot.start().toLocalDate()));
    }

    int durationMinutes(TimelineSlot slot) {
        int minutes = minutesFromStart(slot.end(), slot.start().toLocalDate())
                - minutesFromStart(slot.start(), slot.start().toLocalDate());
        return Math.max(MIN_BLOCK_MINUTES, minutes);
    }

    String hourLabel(int hourOffset) {
        int hour = (startHour + hourOffset) % 24;
        return String.format("%02d:00", hour);
    }

    private int minutesFromStart(LocalDateTime time, LocalDate scheduleDate) {
        int dayOffset = time.toLocalDate().isAfter(scheduleDate) ? 24 * 60 : 0;
        return dayOffset + (time.getHour() - startHour) * 60 + time.getMinute();
    }
}
