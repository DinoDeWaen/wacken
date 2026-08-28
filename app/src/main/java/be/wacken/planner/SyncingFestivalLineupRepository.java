package be.wacken.planner;

import java.util.List;
import java.util.Objects;

import be.wacken.planner.domain.FestivalLineupEntry;
import be.wacken.planner.domain.FestivalLineupRepository;

public final class SyncingFestivalLineupRepository implements FestivalLineupRepository {
    private final Cache cache;
    private final Source source;

    SyncingFestivalLineupRepository(Cache cache, Source source) {
        this.cache = Objects.requireNonNull(cache, "cache must not be null");
        this.source = Objects.requireNonNull(source, "source must not be null");
    }

    void syncSourceToCache() {
        cache.replaceAll(source.findAll());
    }

    @Override
    public void saveAllForFestival(String festivalId, List<FestivalLineupEntry> entries) {
        source.saveAllForFestival(festivalId, entries);
        cache.saveAllForFestival(festivalId, entries);
    }

    @Override
    public List<FestivalLineupEntry> findByFestival(String festivalId) {
        return cache.findByFestival(festivalId);
    }

    public interface Cache extends FestivalLineupRepository {
        List<FestivalLineupEntry> findAll();

        void replaceAll(List<FestivalLineupEntry> entries);
    }

    public interface Source extends FestivalLineupRepository {
        List<FestivalLineupEntry> findAll();
    }
}
