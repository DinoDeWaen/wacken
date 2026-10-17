package be.wacken.planner;

import java.util.List;
import java.util.Objects;

import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalRepository;

public final class SyncingFestivalRepository implements FestivalRepository {
    private final Cache cache;
    private final FestivalRepository source;

    SyncingFestivalRepository(Cache cache, FestivalRepository source) {
        this.cache = Objects.requireNonNull(cache, "cache must not be null");
        this.source = Objects.requireNonNull(source, "source must not be null");
    }

    void syncSourceToCache() {
        cache.replaceAll(source.findAll());
    }

    @Override
    public List<Festival> findAll() {
        return cache.findAll();
    }

    @Override
    public void save(Festival festival) {
        source.save(festival);
        cache.save(festival);
    }

    public interface Cache extends FestivalRepository {
        void replaceAll(List<Festival> festivals);
    }
}
