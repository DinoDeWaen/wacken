package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.StageRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class SyncedStageRepository implements StageRepository {
    private final StageRepository cache;
    private final StageRepository source;

    public SyncedStageRepository(StageRepository cache, StageRepository source) {
        this.cache = Objects.requireNonNull(cache, "cache must not be null");
        this.source = Objects.requireNonNull(source, "source must not be null");
    }

    public void syncSourceToCache() {
        cache.replaceAll(source.findAll());
    }

    @Override
    public void save(Stage stage) {
        source.save(stage);
        cache.save(stage);
    }

    @Override
    public void replaceAll(List<Stage> stages) {
        source.replaceAll(stages);
        cache.replaceAll(stages);
    }

    @Override
    public Optional<Stage> findByName(String name) {
        return cache.findByName(name);
    }

    @Override
    public List<Stage> findAll() {
        return cache.findAll();
    }
}
