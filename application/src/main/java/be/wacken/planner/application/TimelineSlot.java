package be.wacken.planner.application;

import be.wacken.planner.domain.GroupDecisionStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.OptionalInt;

public record TimelineSlot(
        String bandName,
        int rating,
        String stageName,
        LocalDateTime start,
        LocalDateTime end,
        GroupDecisionStatus decisionStatus,
        Optional<String> lostAlternativeBandName,
        Optional<Integer> lostAlternativeRating,
        List<ScheduleDecisionCandidate> candidates,
        OptionalInt walkingMinutesToNext
) {
    public TimelineSlot(
            String bandName,
            int rating,
            String stageName,
            LocalDateTime start,
            LocalDateTime end,
            GroupDecisionStatus decisionStatus,
            Optional<String> lostAlternativeBandName,
            Optional<Integer> lostAlternativeRating
    ) {
        this(
                bandName,
                rating,
                stageName,
                start,
                end,
                decisionStatus,
                lostAlternativeBandName,
                lostAlternativeRating,
                Collections.emptyList(),
                OptionalInt.empty()
        );
    }

    public TimelineSlot(
            String bandName,
            int rating,
            String stageName,
            LocalDateTime start,
            LocalDateTime end,
            GroupDecisionStatus decisionStatus,
            Optional<String> lostAlternativeBandName,
            Optional<Integer> lostAlternativeRating,
            List<ScheduleDecisionCandidate> candidates
    ) {
        this(
                bandName,
                rating,
                stageName,
                start,
                end,
                decisionStatus,
                lostAlternativeBandName,
                lostAlternativeRating,
                candidates,
                OptionalInt.empty()
        );
    }

    public TimelineSlot {
        if (bandName == null || bandName.isBlank()) {
            throw new IllegalArgumentException("Timeline slot band name must not be blank.");
        }
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Timeline slot rating must be between 0 and 5.");
        }
        if (stageName == null || stageName.isBlank()) {
            throw new IllegalArgumentException("Timeline slot stage name must not be blank.");
        }
        if (start == null) {
            throw new IllegalArgumentException("Timeline slot start must not be null.");
        }
        if (end == null) {
            throw new IllegalArgumentException("Timeline slot end must not be null.");
        }
        if (decisionStatus == null) {
            throw new IllegalArgumentException("Timeline slot decision status must not be null.");
        }
        lostAlternativeBandName = lostAlternativeBandName == null ? Optional.empty() : lostAlternativeBandName;
        lostAlternativeRating = lostAlternativeRating == null ? Optional.empty() : lostAlternativeRating;
        candidates = candidates == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(candidates));
        walkingMinutesToNext = walkingMinutesToNext == null ? OptionalInt.empty() : walkingMinutesToNext;
        if (lostAlternativeRating.isPresent() && (lostAlternativeRating.get() < 0 || lostAlternativeRating.get() > 5)) {
            throw new IllegalArgumentException("Timeline slot lost alternative rating must be between 0 and 5.");
        }
        if (walkingMinutesToNext.isPresent() && walkingMinutesToNext.getAsInt() < 0) {
            throw new IllegalArgumentException("Timeline slot walking minutes to next must not be negative.");
        }
    }

    public boolean optional() {
        return decisionStatus == GroupDecisionStatus.OPTIONAL;
    }

    public TimelineSlot withWalkingMinutesToNext(OptionalInt walkingMinutes) {
        return new TimelineSlot(
                bandName,
                rating,
                stageName,
                start,
                end,
                decisionStatus,
                lostAlternativeBandName,
                lostAlternativeRating,
                candidates,
                walkingMinutes
        );
    }
}
