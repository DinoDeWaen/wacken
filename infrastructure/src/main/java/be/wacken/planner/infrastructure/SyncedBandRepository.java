package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class SyncedBandRepository implements BandRepository {
    private final BandRepository cache;
    private final BandRepository source;

    public SyncedBandRepository(BandRepository cache, BandRepository source) {
        this.cache = Objects.requireNonNull(cache, "cache must not be null");
        this.source = Objects.requireNonNull(source, "source must not be null");
    }

    public void syncSourceToCache() {
        cache.replaceAll(source.findAll());
    }

    @Override
    public void save(Band band) {
        source.save(band);
        cache.save(band);
    }

    @Override
    public void replaceAll(List<Band> bands) {
        source.replaceAll(bands);
        cache.replaceAll(bands);
    }

    @Override
    public Optional<Band> findByName(String name) {
        return cache.findByName(name);
    }

    @Override
    public List<Band> findAll() {
        return cache.findAll();
    }
}
