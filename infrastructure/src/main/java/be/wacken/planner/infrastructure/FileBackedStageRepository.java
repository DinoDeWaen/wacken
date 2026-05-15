package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.StageRepository;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class FileBackedStageRepository implements StageRepository {
    private final FileBackedStorage storage;

    public FileBackedStageRepository(Path directory) {
        this.storage = new FileBackedStorage(directory, "stages.tsv");
    }

    @Override
    public void save(Stage stage) {
        Map<String, Stage> stagesByName = loadStagesByName();
        stagesByName.put(stage.name(), stage);
        storage.writeRows(stagesByName.values().stream()
                .map(savedStage -> java.util.Collections.singletonList(savedStage.name()))
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<Stage> findByName(String name) {
        return Optional.ofNullable(loadStagesByName().get(name));
    }

    @Override
    public List<Stage> findAll() {
        return loadStagesByName().values().stream().collect(Collectors.toList());
    }

    private Map<String, Stage> loadStagesByName() {
        Map<String, Stage> stagesByName = new LinkedHashMap<>();
        for (List<String> row : storage.readRows()) {
            Stage stage = new Stage(row.get(0));
            stagesByName.put(stage.name(), stage);
        }
        return stagesByName;
    }
}
