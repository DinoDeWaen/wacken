package be.wacken.planner.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class PerformanceConflictResolver {
    private final GroupDecisionPolicy decisionPolicy;

    public PerformanceConflictResolver() {
        this(new GroupDecisionPolicy());
    }

    PerformanceConflictResolver(GroupDecisionPolicy decisionPolicy) {
        this.decisionPolicy = Objects.requireNonNull(decisionPolicy, "decisionPolicy must not be null");
    }

    public PerformanceConflictResolution resolve(PerformanceConflictSet conflictSet, Map<Band, List<Rating>> ratingsByBand) {
        return resolve(conflictSet, ratingsByBand, ConflictDistanceContext.none());
    }

    public PerformanceConflictResolution resolve(
            PerformanceConflictSet conflictSet,
            Map<Band, List<Rating>> ratingsByBand,
            ConflictDistanceContext distanceContext
    ) {
        Objects.requireNonNull(conflictSet, "conflictSet must not be null");
        Objects.requireNonNull(ratingsByBand, "ratingsByBand must not be null");
        Objects.requireNonNull(distanceContext, "distanceContext must not be null");

        List<Candidate> candidates = candidates(conflictSet, ratingsByBand, distanceContext);
        List<Candidate> selectable = candidates.stream()
                .filter(Candidate::isSelectable)
                .sorted(candidateComparator())
                .toList();

        if (selectable.isEmpty()) {
            GroupDecisionStatus status = candidates.stream().anyMatch(Candidate::isBlocked)
                    ? GroupDecisionStatus.BLOCKED
                    : GroupDecisionStatus.UNRATED;
            String reason = status == GroupDecisionStatus.BLOCKED
                    ? "All overlapping options are blocked by veto rules."
                    : "No overlapping option has been rated yet.";
            return new PerformanceConflictResolution(
                    Optional.empty(),
                    status,
                    Optional.empty(),
                    conflictSet.performances(),
                    reason
            );
        }

        Candidate selected = selectable.get(0);
        Optional<Performance> lostAlternative = selectable.size() > 1
                ? Optional.of(selectable.get(1).performance())
                : Optional.empty();
        List<Performance> rejected = new ArrayList<>();
        for (Candidate candidate : candidates) {
            if (!candidate.performance().equals(selected.performance())) {
                rejected.add(candidate.performance());
            }
        }

        return new PerformanceConflictResolution(
                Optional.of(selected.performance()),
                selected.conflictStatus(),
                lostAlternative,
                rejected,
                selected.reason()
        );
    }

    private List<Candidate> candidates(
            PerformanceConflictSet conflictSet,
            Map<Band, List<Rating>> ratingsByBand,
            ConflictDistanceContext distanceContext
    ) {
        List<Candidate> candidates = new ArrayList<>();
        int index = 0;
        for (Performance performance : conflictSet.performances()) {
            List<Rating> ratings = ratingsByBand.getOrDefault(performance.band(), List.of());
            GroupDecision decision = decisionPolicy.decide(performance, ratings);
            candidates.add(new Candidate(performance, ratings, decision, distanceContext.routeScoreTo(performance.stage()), index));
            index++;
        }
        return List.copyOf(candidates);
    }

    private Comparator<Candidate> candidateComparator() {
        return Comparator
                .comparingInt(Candidate::tier).reversed()
                .thenComparing(Comparator.comparingInt(Candidate::fourCount).reversed())
                .thenComparingInt(Candidate::vetoCount)
                .thenComparing(Comparator.comparingInt(Candidate::threeCount).reversed())
                .thenComparingInt(Candidate::distanceScore)
                .thenComparingInt(Candidate::inputOrder);
    }

    private record Candidate(
            Performance performance,
            List<Rating> ratings,
            GroupDecision decision,
            int distanceScore,
            int inputOrder
    ) {
        Candidate {
            ratings = List.copyOf(ratings);
        }

        boolean isSelectable() {
            return tier() > 0 && decision.status() != GroupDecisionStatus.BLOCKED;
        }

        boolean isBlocked() {
            return decision.status() == GroupDecisionStatus.BLOCKED;
        }

        int tier() {
            int maxRating = decision.maxRating();
            if (maxRating == 5) {
                return 4;
            }
            if (maxRating == 4 && decision.status() == GroupDecisionStatus.GO) {
                return 3;
            }
            if (maxRating == 3 && decision.status() != GroupDecisionStatus.BLOCKED) {
                return 2;
            }
            if (maxRating == 2) {
                return 1;
            }
            return 0;
        }

        GroupDecisionStatus conflictStatus() {
            return tier() >= 3 ? GroupDecisionStatus.GO : GroupDecisionStatus.OPTIONAL;
        }

        String reason() {
            if (decision.maxRating() == 3) {
                return "Optional conflict winner selected from liked-but-missable options.";
            }
            if (decision.maxRating() == 2) {
                return "Optional conflict winner selected from OK or indifferent options.";
            }
            return decision.reason();
        }

        int vetoCount() {
            return decision.vetoCount();
        }

        int fourCount() {
            return ratingCount(4);
        }

        int threeCount() {
            return ratingCount(3);
        }

        private int ratingCount(int value) {
            return (int) ratings.stream()
                    .filter(rating -> rating.value() == value)
                    .count();
        }
    }
}
