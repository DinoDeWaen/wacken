package be.wacken.planner.domain;

import java.util.List;
import java.util.Optional;

public interface StageRepository {
    void save(Stage stage);

    Optional<Stage> findByName(String name);

    List<Stage> findAll();
}
