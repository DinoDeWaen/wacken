package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class FileBackedBandRepository implements BandRepository {
    private final FileBackedStorage storage;

    public FileBackedBandRepository(Path directory) {
        this.storage = new FileBackedStorage(directory, "bands.tsv");
    }

    @Override
    public void save(Band band) {
        Map<String, Band> bandsByName = loadBandsByName();
        bandsByName.put(band.name(), band);
        storage.writeRows(bandsByName.values().stream()
                .map(savedBand -> List.of(savedBand.name()))
                .toList());
    }

    @Override
    public Optional<Band> findByName(String name) {
        return Optional.ofNullable(loadBandsByName().get(name));
    }

    @Override
    public List<Band> findAll() {
        return loadBandsByName().values().stream().toList();
    }

    private Map<String, Band> loadBandsByName() {
        Map<String, Band> bandsByName = new LinkedHashMap<>();
        for (List<String> row : storage.readRows()) {
            Band band = new Band(row.get(0));
            bandsByName.put(band.name(), band);
        }
        return bandsByName;
    }
}
