package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.RealRatingRepository;
import be.wacken.planner.domain.SavedRating;
import be.wacken.planner.domain.Stage;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExportRatingsCsvUseCaseTest {
    @Test
    void exportsOneRowPerLocallyKnownBandWithRatingsAndScheduleMetadata() {
        FakeBandRepository bands = new FakeBandRepository();
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository planningRatings = new FakeRatingRepository();
        FakeRealRatingRepository realRatings = new FakeRealRatingRepository();
        Band band = new Band("5th Avenue");
        bands.save(band);
        performances.save(new Performance(
                band,
                new Stage("Faster"),
                LocalDateTime.parse("2026-07-30T18:00"),
                LocalDateTime.parse("2026-07-30T19:00")
        ));
        planningRatings.save("dino", band, Rating.of(5));
        planningRatings.save("sofie", band, Rating.of(4));
        realRatings.save("dino", band, Rating.of(3));
        ExportRatingsCsvUseCase useCase = new ExportRatingsCsvUseCase(bands, performances, planningRatings, realRatings);

        String csv = useCase.export("dino");

        assertEquals("""
                band_id,band_name,planning_rating,real_rating,group_ratings,stage,date,time,schedule_status
                5th Avenue,5th Avenue,5,3,dino=5;sofie=4,Faster,2026-07-30,18:00-19:00,SCHEDULED
                """, csv);
    }

    @Test
    void escapesCsvValuesAndLeavesUnratedValuesEmpty() {
        FakeBandRepository bands = new FakeBandRepository();
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository planningRatings = new FakeRatingRepository();
        FakeRealRatingRepository realRatings = new FakeRealRatingRepository();
        Band band = new Band("Comma, \"Quote\"");
        bands.save(band);
        planningRatings.save("dino", band, Rating.of(0));
        realRatings.save("dino", band, Rating.of(0));
        ExportRatingsCsvUseCase useCase = new ExportRatingsCsvUseCase(bands, performances, planningRatings, realRatings);

        String csv = useCase.export("dino");

        assertEquals(
                "band_id,band_name,planning_rating,real_rating,group_ratings,stage,date,time,schedule_status\n"
                        + "\"Comma, \"\"Quote\"\"\",\"Comma, \"\"Quote\"\"\",,,,,,,UNSCHEDULED\n",
                csv
        );
    }

    @Test
    void includesBandsThatExistOnlyThroughCachedPerformances() {
        FakeBandRepository bands = new FakeBandRepository();
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository planningRatings = new FakeRatingRepository();
        FakeRealRatingRepository realRatings = new FakeRealRatingRepository();
        Band band = new Band("Performance Only");
        performances.save(new Performance(
                band,
                new Stage("Harder"),
                LocalDateTime.parse("2026-07-31T20:00"),
                LocalDateTime.parse("2026-07-31T21:00")
        ));
        ExportRatingsCsvUseCase useCase = new ExportRatingsCsvUseCase(bands, performances, planningRatings, realRatings);

        String csv = useCase.export("dino");

        assertEquals("""
                band_id,band_name,planning_rating,real_rating,group_ratings,stage,date,time,schedule_status
                Performance Only,Performance Only,,,,Harder,2026-07-31,20:00-21:00,SCHEDULED
                """, csv);
    }

    private static final class FakeBandRepository implements BandRepository {
        private final Map<String, Band> bands = new LinkedHashMap<>();

        @Override
        public void save(Band band) {
            bands.put(band.name(), band);
        }

        @Override
        public void replaceAll(List<Band> bands) {
            this.bands.clear();
            bands.forEach(this::save);
        }

        @Override
        public Optional<Band> findByName(String name) {
            return Optional.ofNullable(bands.get(name));
        }

        @Override
        public List<Band> findAll() {
            return List.copyOf(bands.values());
        }
    }

    private static final class FakePerformanceRepository implements PerformanceRepository {
        private final List<Performance> performances = new ArrayList<>();

        @Override
        public void save(Performance performance) {
            performances.add(performance);
        }

        @Override
        public void replaceAll(List<Performance> performances) {
            this.performances.clear();
            this.performances.addAll(performances);
        }

        @Override
        public List<Performance> findAll() {
            return List.copyOf(performances);
        }
    }

    private static class FakeRatingRepository implements RatingRepository {
        private final Map<Key, Rating> ratings = new LinkedHashMap<>();

        @Override
        public void save(String userName, Band band, Rating rating) {
            ratings.put(new Key(userName, band), rating);
        }

        @Override
        public Optional<Rating> findByUserAndBand(String userName, Band band) {
            return Optional.ofNullable(ratings.get(new Key(userName, band)));
        }

        @Override
        public List<SavedRating> findAll() {
            return ratings.entrySet()
                    .stream()
                    .map(entry -> new SavedRating(entry.getKey().userName(), entry.getKey().band(), entry.getValue()))
                    .toList();
        }
    }

    private static final class FakeRealRatingRepository extends FakeRatingRepository implements RealRatingRepository {
    }

    private record Key(String userName, Band band) {
    }
}
