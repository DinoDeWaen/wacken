package be.wacken.planner.persistence;

import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.StageRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class RoomStageRepository implements StageRepository {
    private final WackenDatabase database;
    private final RoomStageDao stages;

    public RoomStageRepository(WackenDatabase database) {
        this.database = database;
        this.stages = database.stages();
    }

    @Override
    public void save(Stage stage) {
        stages.save(new RoomStage(stage.name()));
    }

    @Override
    public void replaceAll(List<Stage> replacements) {
        database.runInTransaction(() -> {
            stages.deleteAll();
            stages.saveAll(replacements.stream().map(stage -> new RoomStage(stage.name())).collect(Collectors.toList()));
        });
    }

    @Override
    public Optional<Stage> findByName(String name) {
        return Optional.ofNullable(stages.findByName(name)).map(row -> new Stage(row.name));
    }

    @Override
    public List<Stage> findAll() {
        return stages.findAll().stream().map(row -> new Stage(row.name)).collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return stages.count() == 0;
    }
}
