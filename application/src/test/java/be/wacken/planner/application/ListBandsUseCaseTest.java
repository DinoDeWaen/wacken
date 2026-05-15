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

class ListBandsUseCaseTest {
    @Test
    void returnsBandsWithStageAndTimeSortedByStartTime() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeBandRepository bands = new FakeBandRepository();
        performances.save(performance("Later Band", "Harder Stage", 21, 0, 22, 0));
        performances.save(performance("Earlier Band", "Faster Stage", 18, 0, 19, 0));

        ListBandsUseCase useCase = new ListBandsUseCase(bands, performances, new FakeRatingRepository(), "dino");

        assertEquals(
                List.of(
                        new BandListItem("Earlier Band", "Faster Stage", "2026-07-30T18:00", "2026-07-30T19:00", 1, true),
                        new BandListItem("Later Band", "Harder Stage", "2026-07-30T21:00", "2026-07-30T22:00", 1, true)
                ),
                useCase.listBands()
        );
    }

    @Test
    void returnsEmptyListWhenNoBandsOrPerformancesAreImported() {
        ListBandsUseCase useCase = new ListBandsUseCase(
                new FakeBandRepository(),
                new FakePerformanceRepository(),
                new FakeRatingRepository(),
                "dino"
        );

        assertEquals(List.of(), useCase.listBands());
    }

    @Test
    void returnsBandsWithoutPerformancesAsUnscheduled() {
        FakeBandRepository bands = new FakeBandRepository();
        bands.save(new Band("5th Avenue"));
        bands.save(new Band("Midnight Skyline"));
        ListBandsUseCase useCase = new ListBandsUseCase(bands, new FakePerformanceRepository(), new FakeRatingRepository(), "dino");

        assertEquals(
                List.of(
                        new BandListItem("5th Avenue", "Not scheduled yet", "TBA", "TBA", 1, true),
                        new BandListItem("Midnight Skyline", "Not scheduled yet", "TBA", "TBA", 1, true)
                ),
                useCase.listBands()
        );
    }

    @Test
    void includesStoredRatingForCurrentUserAsExplicit() {
        FakeBandRepository bands = new FakeBandRepository();
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Performance performance = performance("5th Avenue", "Faster Stage", 18, 0, 19, 0);
        performances.save(performance);
        ratings.save("dino", performance.band(), Rating.of(3));
        ListBandsUseCase useCase = new ListBandsUseCase(bands, performances, ratings, "dino");

        assertEquals(
                List.of(new BandListItem("5th Avenue", "Faster Stage", "2026-07-30T18:00", "2026-07-30T19:00", 3, false)),
                useCase.listBands()
        );
    }

    @Test
    void savedRatingReplacesDefaultForFutureBandReads() {
        FakeBandRepository bands = new FakeBandRepository();
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Performance performance = performance("5th Avenue", "Faster Stage", 18, 0, 19, 0);
        performances.save(performance);
        ListBandsUseCase useCase = new ListBandsUseCase(bands, performances, ratings, "dino");

        assertEquals(
                List.of(new BandListItem("5th Avenue", "Faster Stage", "2026-07-30T18:00", "2026-07-30T19:00", 1, true)),
                useCase.listBands()
        );

        new RateBandUseCase(ratings).rateBand("dino", performance.band(), 2);

        assertEquals(
                List.of(new BandListItem("5th Avenue", "Faster Stage", "2026-07-30T18:00", "2026-07-30T19:00", 2, false)),
                useCase.listBands()
        );
    }

    @Test
    void includesStoredRatingForUnscheduledBand() {
        FakeBandRepository bands = new FakeBandRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Band band = new Band("5th Avenue");
        bands.save(band);
        ratings.save("dino", band, Rating.of(4));
        ListBandsUseCase useCase = new ListBandsUseCase(bands, new FakePerformanceRepository(), ratings, "dino");

        assertEquals(
                List.of(new BandListItem("5th Avenue", "Not scheduled yet", "TBA", "TBA", 4, false)),
                useCase.listBands()
        );
    }

    private static Performance performance(String bandName, String stageName, int startHour, int startMinute, int endHour, int endMinute) {
        return new Performance(
                new Band(bandName),
                new Stage(stageName),
                LocalDateTime.of(2026, 7, 30, startHour, startMinute),
                LocalDateTime.of(2026, 7, 30, endHour, endMinute)
        );
    }

    private static final class FakeBandRepository implements BandRepository {
        private final Map<String, Band> bandsByName = new LinkedHashMap<>();

        @Override
        public void save(Band band) {
            bandsByName.put(band.name(), band);
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
