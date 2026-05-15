package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.StageRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InMemoryStageRepository implements StageRepository {
    private final Map<String, Stage> stagesByName = new LinkedHashMap<>();

    @Override
    public void save(Stage stage) {
        stagesByName.put(stage.name(), stage);
    }

    @Override
    public Optional<Stage> findByName(String name) {
        return Optional.ofNullable(stagesByName.get(name));
    }

    @Override
    public List<Stage> findAll() {
        return new ArrayList<>(stagesByName.values());
    }
}
