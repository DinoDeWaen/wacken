package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RatingConsistencyUseCaseTest {
    @Test
    void ratingSavedFromOverviewIsReadByDetailFromSharedDatastore() {
        FakeBandRepository bands = new FakeBandRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Band band = new Band("5th Avenue");
        bands.save(band);

        new RateBandUseCase(ratings).rateBand("dino", band, 4);

        Optional<BandDetailItem> detail = new ShowBandDetailUseCase(bands, ratings)
                .showBand("dino", "5th Avenue", MusicLinks.none());

        assertEquals(
                Optional.of(new BandDetailItem("5th Avenue", Optional.empty(), Optional.empty(), 4, false, Optional.empty(), Optional.empty(), List.of())),
                detail
        );
    }

    @Test
    void ratingSavedFromDetailIsReadByOverviewFromSharedDatastore() {
        FakeBandRepository bands = new FakeBandRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Band band = new Band("5th Avenue");
        bands.save(band);
        new RateBandUseCase(ratings).rateBand("dino", band, 4);

        new RateBandUseCase(ratings).rateBand("dino", band, 3);

        List<BandListItem> overview = new ListBandsUseCase(bands, new FakePerformanceRepository(), ratings, "dino")
                .listBands();
        assertEquals(
                List.of(new BandListItem("5th Avenue", "Not scheduled yet", "TBA", "TBA", 3, false)),
                overview
        );
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
            return Optional.ofNullable(bandsByName.get(name));
        }

        @Override
        public List<Band> findAll() {
            return List.copyOf(bandsByName.values());
        }
    }

    private static final class FakePerformanceRepository implements PerformanceRepository {
        @Override
        public void save(Performance performance) {
        }

        @Override
        public void replaceAll(List<Performance> replacements) {
        }

        @Override
        public List<Performance> findAll() {
            return new ArrayList<>();
        }
    }

    private static final class FakeRatingRepository implements RatingRepository {
        private final Map<Key, Rating> ratings = new HashMap<>();

        @Override
        public void save(String userName, Band band, Rating rating) {
            ratings.put(new Key(userName, band), rating);
        }

        @Override
        public Optional<Rating> findByUserAndBand(String userName, Band band) {
            return Optional.ofNullable(ratings.get(new Key(userName, band)));
        }

        private record Key(String userName, Band band) {
        }
    }
}
