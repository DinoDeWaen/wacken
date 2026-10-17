package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImportInitialBandListUseCaseTest {
    @Test
    void importsBandNamesWithoutPerformanceScheduleData() {
        FakeBandRepository bands = new FakeBandRepository();
        ImportInitialBandListUseCase useCase = new ImportInitialBandListUseCase(bands);

        ImportInitialBandListResult result = useCase.importBands(List.of("5th Avenue", "Iron Maiden"));

        assertEquals(2, result.importedCount());
        assertEquals(0, result.duplicateCount());
        assertEquals(List.of(new Band("5th Avenue"), new Band("Iron Maiden")), bands.findAll());
    }

    @Test
    void ignoresDuplicateBandNames() {
        FakeBandRepository bands = new FakeBandRepository();
        ImportInitialBandListUseCase useCase = new ImportInitialBandListUseCase(bands);

        ImportInitialBandListResult result = useCase.importBands(List.of("5th Avenue", "5th Avenue", " 5th Avenue "));

        assertEquals(1, result.importedCount());
        assertEquals(2, result.duplicateCount());
        assertEquals(List.of(new Band("5th Avenue")), bands.findAll());
    }

    @Test
    void importedBandsAreAvailableForListingAndRatingFlows() {
        FakeBandRepository bands = new FakeBandRepository();
        ImportInitialBandListUseCase useCase = new ImportInitialBandListUseCase(bands);

        useCase.importBands(List.of("5th Avenue"));

        assertEquals(Optional.of(new Band("5th Avenue")), bands.findByName("5th Avenue"));
    }

    private static final class FakeBandRepository implements BandRepository {
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
            return Optional.ofNullable(bandsByName.get(name.trim()));
        }

        @Override
        public List<Band> findAll() {
            return new ArrayList<>(bandsByName.values());
        }
    }
}
