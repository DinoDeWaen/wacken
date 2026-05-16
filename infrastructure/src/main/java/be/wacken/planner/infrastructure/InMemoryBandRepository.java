package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InMemoryBandRepository implements BandRepository {
    private final Map<String, Band> bandsByName = new LinkedHashMap<>();

    @Override
    public void save(Band band) {
        bandsByName.put(band.name(), band);
    }

    @Override
    public void replaceAll(List<Band> bands) {
        bandsByName.clear();
        bands.forEach(this::save);
    }

    @Override
    public Optional<Band> findByName(String name) {
        return Optional.ofNullable(bandsByName.get(name));
    }

    @Override
    public List<Band> findAll() {
        return new ArrayList<>(bandsByName.values());
    }
}
