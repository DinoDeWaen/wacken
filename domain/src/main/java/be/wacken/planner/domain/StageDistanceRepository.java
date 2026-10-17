package be.wacken.planner.domain;

import java.util.List;
import java.util.Optional;

public interface StageDistanceRepository {
    void save(StageDistance distance);

    void replaceAll(List<StageDistance> distances);

    Optional<StageDistance> findBetween(Stage from, Stage to);

    List<StageDistance> findAll();
}
