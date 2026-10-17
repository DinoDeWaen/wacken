package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.Stage;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListingAndRatingQaScenarioTest {
    @Test
    void attendeeListsBandsSetsRatingAndSeesItWhenReopeningList() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeBandRepository bands = new FakeBandRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Band band = new Band("5th Avenue");
        bands.save(band);
        performances.save(new Performance(
                band,
                new Stage("Faster"),
                LocalDateTime.parse("2026-07-30T18:00:00"),
                LocalDateTime.parse("2026-07-30T19:00:00")
        ));

        ListBandsUseCase firstOpen = new ListBandsUseCase(bands, performances, ratings, "dino");
        assertEquals(
                List.of(new BandListItem("5th Avenue", "Faster", "2026-07-30T18:00", "2026-07-30T19:00", 0, true)),
                firstOpen.listBands()
        );

        RateBandResult ratingResult = new RateBandUseCase(ratings).rateBand("dino", band, 5);
        assertEquals(RateBandResult.stored(), ratingResult);

        ListBandsUseCase reopened = new ListBandsUseCase(bands, performances, ratings, "dino");
        assertEquals(
                List.of(new BandListItem("5th Avenue", "Faster", "2026-07-30T18:00", "2026-07-30T19:00", 5, false)),
                reopened.listBands()
        );
    }

    @Test
    void invalidRatingInputIsRejectedAndExistingListRatingRemainsDefault() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeBandRepository bands = new FakeBandRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Band band = new Band("5th Avenue");
        bands.save(band);
        performances.save(new Performance(
                band,
                new Stage("Faster"),
                LocalDateTime.parse("2026-07-30T18:00:00"),
                LocalDateTime.parse("2026-07-30T19:00:00")
        ));

        RateBandResult ratingResult = new RateBandUseCase(ratings).rateBand("dino", band, 6);

        assertEquals(RateBandResult.failure("Rating must be between 0 and 5."), ratingResult);
        assertEquals(
                List.of(new BandListItem("5th Avenue", "Faster", "2026-07-30T18:00", "2026-07-30T19:00", 0, true)),
                new ListBandsUseCase(bands, performances, ratings, "dino").listBands()
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
            return new ArrayList<>(bandsByName.values());
        }
    }

    private static final class FakePerformanceRepository implements PerformanceRepository {
        private final List<Performance> performances = new ArrayList<>();

        @Override
        public void save(Performance performance) {
            performances.add(performance);
        }

        @Override
        public void replaceAll(List<Performance> replacements) {
            performances.clear();
            performances.addAll(replacements);
        }

        @Override
        public List<Performance> findAll() {
            return new ArrayList<>(performances);
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
