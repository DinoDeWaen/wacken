package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.StageDistance;
import be.wacken.planner.domain.StageDistanceRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class SyncedStageDistanceRepository implements StageDistanceRepository {
    private final StageDistanceRepository cache;
    private final StageDistanceRepository source;

    public SyncedStageDistanceRepository(StageDistanceRepository cache, StageDistanceRepository source) {
        this.cache = Objects.requireNonNull(cache, "cache must not be null");
        this.source = Objects.requireNonNull(source, "source must not be null");
    }

    public void syncSourceToCache() {
        cache.replaceAll(source.findAll());
    }

    @Override
    public void save(StageDistance distance) {
        source.save(distance);
        cache.save(distance);
    }

    @Override
    public void replaceAll(List<StageDistance> distances) {
        source.replaceAll(distances);
        cache.replaceAll(distances);
    }

    @Override
    public Optional<StageDistance> findBetween(Stage from, Stage to) {
        return cache.findBetween(from, to);
    }

    @Override
    public List<StageDistance> findAll() {
        return cache.findAll();
    }
}
