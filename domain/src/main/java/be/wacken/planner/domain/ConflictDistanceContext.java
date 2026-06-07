package be.wacken.planner.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record ConflictDistanceContext(Optional<Stage> previousStage, Optional<Stage> nextStage, List<StageDistance> distances) {
    public ConflictDistanceContext {
        previousStage = previousStage == null ? Optional.empty() : previousStage;
        nextStage = nextStage == null ? Optional.empty() : nextStage;
        Objects.requireNonNull(distances, "distances must not be null");
        distances = List.copyOf(distances);
    }

    public static ConflictDistanceContext none() {
        return new ConflictDistanceContext(Optional.empty(), Optional.empty(), List.of());
    }

    public static ConflictDistanceContext fromPreviousStage(Stage previousStage, List<StageDistance> distances) {
        return new ConflictDistanceContext(Optional.of(previousStage), Optional.empty(), distances);
    }

    public static ConflictDistanceContext betweenStages(Stage previousStage, Stage nextStage, List<StageDistance> distances) {
        return new ConflictDistanceContext(Optional.of(previousStage), Optional.of(nextStage), distances);
    }

    int routeScoreTo(Stage stage) {
        int score = 0;
        if (previousStage.isPresent()) {
            score += walkingMinutes(previousStage.orElseThrow(), stage);
        }
        if (nextStage.isPresent()) {
            score += walkingMinutes(stage, nextStage.orElseThrow());
        }
        return score;
    }

    private int walkingMinutes(Stage from, Stage to) {
        if (from.equals(to)) {
            return 0;
        }
        return distances.stream()
                .filter(distance -> connects(distance, from, to))
                .mapToInt(StageDistance::walkingMinutes)
                .findFirst()
                .orElse(1_000_000);
    }

    private boolean connects(StageDistance distance, Stage from, Stage to) {
        return (distance.from().equals(from) && distance.to().equals(to))
                || (distance.from().equals(to) && distance.to().equals(from));
    }
}
