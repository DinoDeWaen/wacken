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
        writeBands(bandsByName.values().stream().collect(Collectors.toList()));
    }

    @Override
    public void replaceAll(List<Band> bands) {
        writeBands(bands);
    }

    private void writeBands(List<Band> bands) {
        storage.writeRows(bands.stream()
                .map(savedBand -> java.util.Arrays.asList(
                        savedBand.name(),
                        savedBand.biography().orElse(""),
                        savedBand.imageUrl().orElse(""),
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
            Band band = bandFromRow(row);
            bandsByName.put(band.name(), band);
        }
        return bandsByName;
    }

    private Band bandFromRow(List<String> row) {
        if (row.size() <= 3) {
            return new Band(row.get(0), optionalColumn(row, 1), optionalColumn(row, 2));
        }
        if (row.size() == 4) {
            return new Band(row.get(0), optionalColumn(row, 1), optionalColumn(row, 2), optionalColumn(row, 3));
        }
        return new Band(row.get(0), optionalColumn(row, 1), optionalColumn(row, 2), optionalColumn(row, 3), optionalColumn(row, 4));
    }

    private java.util.Optional<String> optionalColumn(List<String> row, int column) {
        if (column >= row.size() || row.get(column).isBlank()) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(row.get(column));
    }
}
