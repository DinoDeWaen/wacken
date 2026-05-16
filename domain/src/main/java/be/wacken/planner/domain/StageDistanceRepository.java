package be.wacken.planner.domain;

import java.util.Optional;

public interface StageDistanceRepository {
    void save(StageDistance distance);

    void replaceAll(java.util.List<StageDistance> distances);

    Optional<StageDistance> findBetween(Stage from, Stage to);
}
