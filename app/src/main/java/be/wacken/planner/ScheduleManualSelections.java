package be.wacken.planner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import be.wacken.planner.application.ScheduleDecisionCandidate;
import be.wacken.planner.application.TimelineSlot;

final class ScheduleManualSelections {
    private final Map<String, ScheduleDecisionCandidate> selections = new LinkedHashMap<>();

    void select(TimelineSlot slot, ScheduleDecisionCandidate candidate) {
        selections.put(key(slot), manualCandidate(candidate));
    }

    boolean isManual(TimelineSlot slot) {
        return selections.containsKey(key(slot));
    }

    ScheduleDecisionCandidate visibleCandidate(TimelineSlot slot) {
        ScheduleDecisionCandidate selected = selections.get(key(slot));
        if (selected != null) {
            return selected;
        }
        return generatedCandidate(slot);
    }

    List<ScheduleDecisionCandidate> detailCandidates(TimelineSlot slot) {
        List<ScheduleDecisionCandidate> candidates = new ArrayList<>();
        ScheduleDecisionCandidate visible = visibleCandidate(slot);
        candidates.add(visible);
        ScheduleDecisionCandidate generated = generatedCandidate(slot);
        if (!sameBand(visible, generated)) {
            candidates.add(new ScheduleDecisionCandidate(
                    generated.bandName(),
                    generated.rating(),
                    generated.stageName(),
                    generated.start(),
                    generated.end(),
                    "GENERATED CHOICE",
                    false
            ));
        }
        for (ScheduleDecisionCandidate candidate : slot.candidates()) {
            if (!sameBand(candidate, visible) && !sameBand(candidate, generated)) {
                candidates.add(new ScheduleDecisionCandidate(
                        candidate.bandName(),
                        candidate.rating(),
                        candidate.stageName(),
                        candidate.start(),
                        candidate.end(),
                        candidate.status(),
                        false
                ));
            }
        }
        return candidates;
    }

    private ScheduleDecisionCandidate generatedCandidate(TimelineSlot slot) {
        return new ScheduleDecisionCandidate(
                slot.bandName(),
                slot.rating(),
                slot.stageName(),
                slot.start(),
                slot.end(),
                "CHOSEN",
                true
        );
    }

    private ScheduleDecisionCandidate manualCandidate(ScheduleDecisionCandidate candidate) {
        return new ScheduleDecisionCandidate(
                candidate.bandName(),
                candidate.rating(),
                candidate.stageName(),
                candidate.start(),
                candidate.end(),
                "MANUAL CHOICE",
                true
        );
    }

    private boolean sameBand(ScheduleDecisionCandidate first, ScheduleDecisionCandidate second) {
        return first.bandName().equals(second.bandName());
    }

    private String key(TimelineSlot slot) {
        return slot.start() + "|" + slot.end() + "|" + slot.bandName();
    }
}
