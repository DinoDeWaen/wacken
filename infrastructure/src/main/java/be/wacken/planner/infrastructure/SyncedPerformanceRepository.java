package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;

import java.util.List;
import java.util.Objects;

public final class SyncedPerformanceRepository implements PerformanceRepository {
    private final PerformanceRepository cache;
    private final PerformanceRepository source;

    public SyncedPerformanceRepository(PerformanceRepository cache, PerformanceRepository source) {
        this.cache = Objects.requireNonNull(cache, "cache must not be null");
        this.source = Objects.requireNonNull(source, "source must not be null");
    }

    public void syncSourceToCache() {
        cache.replaceAll(source.findAll());
    }

    @Override
    public void save(Performance performance) {
        source.save(performance);
        cache.save(performance);
    }

    @Override
    public void replaceAll(List<Performance> performances) {
        source.replaceAll(performances);
        cache.replaceAll(performances);
    }

    @Override
    public List<Performance> findAll() {
        return cache.findAll();
    }
}
