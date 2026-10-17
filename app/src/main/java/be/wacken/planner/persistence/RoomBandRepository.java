package be.wacken.planner.persistence;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class RoomBandRepository implements BandRepository {
    private final WackenDatabase database;
    private final RoomBandDao bands;

    public RoomBandRepository(WackenDatabase database) {
        this.database = database;
        this.bands = database.bands();
    }

    @Override
    public void save(Band band) {
        bands.save(toRow(band));
    }

    @Override
    public void replaceAll(List<Band> replacements) {
        database.runInTransaction(() -> {
            bands.deleteAll();
            bands.saveAll(replacements.stream().map(this::toRow).collect(Collectors.toList()));
        });
    }

    @Override
    public Optional<Band> findByName(String name) {
        return Optional.ofNullable(bands.findByName(name)).map(this::toDomain);
    }

    @Override
    public List<Band> findAll() {
        return bands.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return bands.count() == 0;
    }

    private RoomBand toRow(Band band) {
        return new RoomBand(
                band.name(),
                band.biography().orElse(""),
                band.imageUrl().orElse(""),
                band.youtubeUrl().orElse(""),
                band.spotifyUrl().orElse("")
        );
    }

    private Band toDomain(RoomBand row) {
        return new Band(
                row.name,
                optional(row.biography),
                optional(row.imageUrl),
                optional(row.youtubeUrl),
                optional(row.spotifyUrl)
        );
    }

    private Optional<String> optional(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(value);
    }
}
