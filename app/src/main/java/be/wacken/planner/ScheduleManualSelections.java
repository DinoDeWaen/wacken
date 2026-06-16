package be.wacken.planner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import be.wacken.planner.application.ScheduleDecisionCandidate;
import be.wacken.planner.application.TimelineSlot;

final class ScheduleManualSelections {
    private static final String LOCKED_STATUS = "🔒 LOCKED CHOICE";
    private final Map<String, String> selectedCandidateKeysByConflict;

    ScheduleManualSelections() {
        this(Collections.emptyMap());
    }

    ScheduleManualSelections(Map<String, String> selectedCandidateKeysByConflict) {
        this.selectedCandidateKeysByConflict = new LinkedHashMap<>(
                Objects.requireNonNull(selectedCandidateKeysByConflict, "selectedCandidateKeysByConflict must not be null")
        );
    }

    void select(TimelineSlot slot, ScheduleDecisionCandidate candidate) {
        selectedCandidateKeysByConflict.put(conflictKey(slot), candidateKey(candidate));
    }

    void clear(TimelineSlot slot) {
        selectedCandidateKeysByConflict.remove(conflictKey(slot));
    }

    boolean isManual(TimelineSlot slot) {
        return lockedCandidate(slot) != null;
    }

    ScheduleDecisionCandidate visibleCandidate(TimelineSlot slot) {
        ScheduleDecisionCandidate selected = lockedCandidate(slot);
        if (selected != null) {
            return lockedCandidate(selected);
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

    Map<String, String> locks() {
        return Collections.unmodifiableMap(selectedCandidateKeysByConflict);
    }

    static String conflictKey(TimelineSlot slot) {
        List<ScheduleDecisionCandidate> candidates = slot.candidates();
        if (candidates.isEmpty()) {
            candidates = List.of(generatedCandidateFor(slot));
        }
        return candidates.stream()
                .map(ScheduleManualSelections::candidateKey)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.joining("||"));
    }

    static String candidateKey(ScheduleDecisionCandidate candidate) {
        return candidate.bandName()
                + "|" + candidate.stageName()
                + "|" + candidate.start()
                + "|" + candidate.end();
    }

    private ScheduleDecisionCandidate generatedCandidate(TimelineSlot slot) {
        return generatedCandidateFor(slot);
    }

    private static ScheduleDecisionCandidate generatedCandidateFor(TimelineSlot slot) {
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

    private ScheduleDecisionCandidate lockedCandidate(ScheduleDecisionCandidate candidate) {
        return new ScheduleDecisionCandidate(
                candidate.bandName(),
                candidate.rating(),
                candidate.stageName(),
                candidate.start(),
                candidate.end(),
                LOCKED_STATUS,
                true
        );
    }

    private ScheduleDecisionCandidate lockedCandidate(TimelineSlot slot) {
        String selectedCandidateKey = selectedCandidateKeysByConflict.get(conflictKey(slot));
        if (selectedCandidateKey == null) {
            return null;
        }
        for (ScheduleDecisionCandidate candidate : slot.candidates()) {
            if (selectedCandidateKey.equals(candidateKey(candidate))) {
                return candidate;
            }
        }
        ScheduleDecisionCandidate generated = generatedCandidate(slot);
        if (selectedCandidateKey.equals(candidateKey(generated))) {
            return generated;
        }
        return null;
    }

    private boolean sameBand(ScheduleDecisionCandidate first, ScheduleDecisionCandidate second) {
        return first.bandName().equals(second.bandName());
    }

}
