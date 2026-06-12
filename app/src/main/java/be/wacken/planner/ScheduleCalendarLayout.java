package be.wacken.planner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import be.wacken.planner.application.ScheduleDecisionCandidate;
import be.wacken.planner.application.TimelineSlot;

final class ScheduleCalendarLayout {
    private static final int MIN_BLOCK_MINUTES = 30;
    private static final int FESTIVAL_DAY_END_HOUR = 26;

    private final int startHour;
    private final int endHour;
    private final LocalDate scheduleDate;
    private final List<String> stageColumns;

    private ScheduleCalendarLayout(int startHour, int endHour, LocalDate scheduleDate, List<String> stageColumns) {
        this.startHour = startHour;
        this.endHour = endHour;
        this.scheduleDate = scheduleDate;
        this.stageColumns = Collections.unmodifiableList(new ArrayList<>(stageColumns));
    }

    static ScheduleCalendarLayout forSlots(List<TimelineSlot> slots) {
        if (slots == null || slots.isEmpty()) {
            return new ScheduleCalendarLayout(12, 14, LocalDate.now(), List.of());
        }
        List<ScheduleDecisionCandidate> candidates = new java.util.ArrayList<>();
        for (TimelineSlot slot : slots) {
            candidates.add(new ScheduleDecisionCandidate(
                    slot.bandName(),
                    slot.rating(),
                    slot.stageName(),
                    slot.start(),
                    slot.end(),
                    "CHOSEN",
                    true
            ));
        }
        return forCandidates(candidates);
    }

    static ScheduleCalendarLayout forCandidates(List<ScheduleDecisionCandidate> candidates) {
        LocalDate date = candidates == null || candidates.isEmpty()
                ? LocalDate.now()
                : festivalDay(candidates.get(0).start());
        return forCandidates(candidates, date);
    }

    static ScheduleCalendarLayout forCandidates(List<ScheduleDecisionCandidate> candidates, LocalDate scheduleDate) {
        if (candidates == null || candidates.isEmpty()) {
            return new ScheduleCalendarLayout(12, 14, scheduleDate, List.of());
        }
        int start = FESTIVAL_DAY_END_HOUR;
        int end = 0;
        for (ScheduleDecisionCandidate candidate : candidates) {
            start = Math.min(start, hourFromScheduleStart(candidate.start(), scheduleDate));
            int slotEndHour = hourFromScheduleStart(candidate.end(), scheduleDate);
            slotEndHour += candidate.end().getMinute() == 0 ? 0 : 1;
            end = Math.max(end, slotEndHour);
        }
        end = Math.max(end, FESTIVAL_DAY_END_HOUR);
        if (end <= start) {
            end = start + 1;
        }
        return new ScheduleCalendarLayout(start, end, scheduleDate, stageColumns(candidates));
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

    List<String> stageColumns() {
        return stageColumns;
    }

    int stageColumnCount() {
        return Math.max(1, stageColumns.size());
    }

    int stageColumnIndex(ScheduleDecisionCandidate candidate) {
        int index = stageColumns.indexOf(candidate.stageName());
        return Math.max(0, index);
    }

    int topOffsetMinutes(TimelineSlot slot) {
        return Math.max(0, minutesFromStart(slot.start()));
    }

    int durationMinutes(TimelineSlot slot) {
        int minutes = minutesFromStart(slot.end()) - minutesFromStart(slot.start());
        return Math.max(MIN_BLOCK_MINUTES, minutes);
    }

    int topOffsetMinutes(ScheduleDecisionCandidate candidate) {
        return Math.max(0, minutesFromStart(candidate.start()));
    }

    int durationMinutes(ScheduleDecisionCandidate candidate) {
        int minutes = minutesFromStart(candidate.end()) - minutesFromStart(candidate.start());
        return Math.max(MIN_BLOCK_MINUTES, minutes);
    }

    int endOffsetMinutes(ScheduleDecisionCandidate candidate) {
        return Math.max(0, minutesFromStart(candidate.end()));
    }

    int walkingMarkerOffsetMinutes(ScheduleDecisionCandidate from, ScheduleDecisionCandidate to) {
        int gapStart = endOffsetMinutes(from);
        int gapEnd = topOffsetMinutes(to);
        if (gapEnd <= gapStart) {
            return gapStart;
        }
        return gapStart + ((gapEnd - gapStart) / 2);
    }

    String hourLabel(int hourOffset) {
        int hour = (startHour + hourOffset) % 24;
        return String.format("%02d:00", hour);
    }

    private int minutesFromStart(LocalDateTime time) {
        return hourFromScheduleStart(time, scheduleDate) * 60 + time.getMinute() - startHour * 60;
    }

    private static int hourFromScheduleStart(LocalDateTime time, LocalDate scheduleDate) {
        int dayOffset = time.toLocalDate().isAfter(scheduleDate) ? 24 : 0;
        return dayOffset + time.getHour();
    }

    private static List<String> stageColumns(List<ScheduleDecisionCandidate> candidates) {
        List<String> stages = new ArrayList<>();
        for (ScheduleDecisionCandidate candidate : candidates) {
            if (!stages.contains(candidate.stageName())) {
                stages.add(candidate.stageName());
            }
        }
        stages.sort(Comparator
                .comparingInt(ScheduleCalendarLayout::stagePriority)
                .thenComparing(String::compareToIgnoreCase));
        return stages;
    }

    private static int stagePriority(String stageName) {
        String normalized = stageName.toLowerCase(Locale.ROOT);
        if (normalized.contains("loud")) {
            return 0;
        }
        if (normalized.contains("harder")) {
            return 1;
        }
        return 2;
    }

    private static LocalDate festivalDay(LocalDateTime time) {
        if (time.toLocalTime().isBefore(java.time.LocalTime.of(2, 0))) {
            return time.toLocalDate().minusDays(1);
        }
        return time.toLocalDate();
    }
}
