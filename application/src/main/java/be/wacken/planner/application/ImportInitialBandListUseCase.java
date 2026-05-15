package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;

import java.util.List;
import java.util.Objects;

public final class ImportInitialBandListUseCase {
    private final BandRepository bands;

    public ImportInitialBandListUseCase(BandRepository bands) {
        this.bands = Objects.requireNonNull(bands, "bands must not be null");
    }

    public ImportInitialBandListResult importBands(List<String> bandNames) {
        int imported = 0;
        int duplicates = 0;

        for (String bandName : bandNames) {
            Band band = new Band(bandName);
            if (bands.findByName(band.name()).isPresent()) {
                duplicates++;
                continue;
            }

            bands.save(band);
            imported++;
        }

        return new ImportInitialBandListResult(imported, duplicates);
    }
}
