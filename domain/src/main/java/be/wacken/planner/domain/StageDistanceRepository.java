package be.wacken.planner.domain;

import java.util.Optional;

public interface StageDistanceRepository {
    void save(StageDistance distance);

    Optional<StageDistance> findBetween(Stage from, Stage to);
}
