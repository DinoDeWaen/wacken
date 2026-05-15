package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .map(savedBand -> java.util.Arrays.asList(
                        savedBand.name(),
                        savedBand.youtubeUrl().orElse(""),
                        savedBand.spotifyUrl().orElse("")
                ))
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<Band> findByName(String name) {
        return Optional.ofNullable(loadBandsByName().get(name));
    }

    @Override
    public List<Band> findAll() {
        return loadBandsByName().values().stream().collect(Collectors.toList());
    }

    private Map<String, Band> loadBandsByName() {
        Map<String, Band> bandsByName = new LinkedHashMap<>();
        for (List<String> row : storage.readRows()) {
            Band band = new Band(
                    row.get(0),
                    optionalColumn(row, 1),
                    optionalColumn(row, 2)
            );
            bandsByName.put(band.name(), band);
        }
        return bandsByName;
    }

    private java.util.Optional<String> optionalColumn(List<String> row, int column) {
        if (column >= row.size() || row.get(column).isBlank()) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(row.get(column));
    }
}
