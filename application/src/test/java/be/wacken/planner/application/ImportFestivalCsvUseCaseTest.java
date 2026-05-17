package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.FoodOption;
import be.wacken.planner.domain.FoodOptionRepository;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.StageDistance;
import be.wacken.planner.domain.StageDistanceRepository;
import be.wacken.planner.domain.StageRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImportFestivalCsvUseCaseTest {
    @Test
    void importsValidFestivalCsvFilesIntoRepositories() {
        Repositories repositories = new Repositories();
        ImportFestivalCsvUseCase useCase = repositories.useCase();

        ImportFestivalCsvResult result = useCase.importCsv(validCsv());

        assertEquals(ImportFestivalCsvResult.imported(), result);
        assertEquals(List.of(new Band("5th Avenue")), repositories.bands.findAll());
        assertEquals(List.of(new Stage("Faster"), new Stage("Harder")), repositories.stages.findAll());
        assertEquals(
                List.of(new Performance(
                        new Band("5th Avenue"),
                        new Stage("Faster"),
                        LocalDateTime.parse("2026-07-30T18:00:00"),
                        LocalDateTime.parse("2026-07-30T19:00:00")
                )),
                repositories.performances.findAll()
        );
        assertEquals(
                Optional.of(StageDistance.between(new Stage("Faster"), new Stage("Harder"), 7)),
                repositories.distances.findBetween(new Stage("Faster"), new Stage("Harder"))
        );
        assertEquals(List.of(new FoodOption("Pizza")), repositories.food.findAll());
    }

    @Test
    void importsBandMusicMetadataWhenAvailable() {
        Repositories repositories = new Repositories();
        ImportFestivalCsvUseCase useCase = repositories.useCase();

        ImportFestivalCsvResult result = useCase.importCsv(new FestivalCsvFiles(
                "band_id,name,biography,biography_html,image_url,youtube_url,spotify_artist_id\n5th-avenue,5th Avenue,\"<p>English &amp; Wacken rock band.</p>\",\"<p>German text.</p>\",https://images.example/5th.jpg,https://youtube.example/5th,spotify-artist-5th\n",
                "stage_id,name\nfaster,Faster\n",
                "performance_id,band_id,stage_id,festival_day_id,start_at,end_at\np1,5th-avenue,faster,thu,2026-07-30T18:00:00,2026-07-30T19:00:00\n",
                "from_stage_id,to_stage_id,walking_minutes\n",
                "food_id,name,near_stage_id\n"
        ));

        assertEquals(ImportFestivalCsvResult.imported(), result);
        assertEquals(
                Optional.of(new Band(
                        "5th Avenue",
                        Optional.of("English & Wacken rock band."),
                        Optional.of("https://images.example/5th.jpg"),
                        Optional.of("https://youtube.example/5th"),
                        Optional.of("https://open.spotify.com/artist/spotify-artist-5th")
                )),
                repositories.bands.findByName("5th Avenue")
        );
    }

    @Test
    void reimportReplacesFestivalMasterDataWithoutTouchingRatings() {
        Repositories repositories = new Repositories();
        repositories.useCase().importCsv(validCsv());
        repositories.ratings.save("dino", new Band("5th Avenue"), Rating.of(4));

        ImportFestivalCsvResult result = repositories.useCase().importCsv(new FestivalCsvFiles(
                "band_id,name\nnew-band,New Band\n",
                "stage_id,name\nharder,Harder\n",
                "performance_id,band_id,stage_id,festival_day_id,start_at,end_at\np-new,new-band,harder,thu,2026-07-30T20:00:00,2026-07-30T21:00:00\n",
                "from_stage_id,to_stage_id,walking_minutes\n",
                "food_id,name,near_stage_id\n"
        ));

        assertEquals(ImportFestivalCsvResult.imported(), result);
        assertEquals(List.of(new Band("New Band")), repositories.bands.findAll());
        assertEquals(Optional.empty(), repositories.bands.findByName("5th Avenue"));
        assertEquals(
                List.of(new Performance(
                        new Band("New Band"),
                        new Stage("Harder"),
                        LocalDateTime.parse("2026-07-30T20:00:00"),
                        LocalDateTime.parse("2026-07-30T21:00:00")
                )),
                repositories.performances.findAll()
        );
        assertEquals(Optional.of(Rating.of(4)), repositories.ratings.findByUserAndBand("dino", new Band("5th Avenue")));
    }

    @Test
    void failsWhenPerformanceReferencesMissingBandAndUnknownStage() {
        Repositories repositories = new Repositories();
        ImportFestivalCsvUseCase useCase = repositories.useCase();

        ImportFestivalCsvResult result = useCase.importCsv(new FestivalCsvFiles(
                "band_id,name\nknown,Known Band\n",
                "stage_id,name\nfaster,Faster\n",
                "performance_id,band_id,stage_id,festival_day_id,start_at,end_at\np1,missing,unknown,thu,2026-07-30T18:00:00,2026-07-30T19:00:00\n",
                "from_stage_id,to_stage_id,walking_minutes\nfaster,unknown,7\n",
                "food_id,name,near_stage_id\nfood-1,Pizza,unknown\n"
        ));

        assertEquals(
                ImportFestivalCsvResult.failure(List.of(
                        "performances.csv row 2 references unknown band_id missing",
                        "performances.csv row 2 references unknown stage_id unknown",
                        "distances.csv row 2 references unknown stage_id unknown",
                        "food.csv row 2 references unknown stage_id unknown"
                )),
                result
        );
        assertEquals(List.of(), repositories.performances.findAll());
    }

    @Test
    void failsWhenPerformancesOverlapOnSameStage() {
        Repositories repositories = new Repositories();
        ImportFestivalCsvUseCase useCase = repositories.useCase();

        ImportFestivalCsvResult result = useCase.importCsv(new FestivalCsvFiles(
                "band_id,name\nfirst,First Band\nsecond,Second Band\n",
                "stage_id,name\nfaster,Faster\n",
                """
                        performance_id,band_id,stage_id,festival_day_id,start_at,end_at
                        p1,first,faster,thu,2026-07-30T18:00:00,2026-07-30T19:00:00
                        p2,second,faster,thu,2026-07-30T18:30:00,2026-07-30T19:30:00
                        """,
                "from_stage_id,to_stage_id,walking_minutes\n",
                "food_id,name,near_stage_id\n"
        ));

        assertEquals(
                ImportFestivalCsvResult.failure(List.of("performances.csv rows 2 and 3 overlap on stage_id faster")),
                result
        );
        assertEquals(List.of(), repositories.performances.findAll());
    }

    private FestivalCsvFiles validCsv() {
        return new FestivalCsvFiles(
                "band_id,name\n5th-avenue,5th Avenue\n",
                "stage_id,name\nfaster,Faster\nharder,Harder\n",
                "performance_id,band_id,stage_id,festival_day_id,start_at,end_at\np1,5th-avenue,faster,thu,2026-07-30T18:00:00,2026-07-30T19:00:00\n",
                "from_stage_id,to_stage_id,walking_minutes\nfaster,harder,7\n",
                "food_id,name,near_stage_id\nfood-1,Pizza,faster\n"
        );
    }

    private static final class Repositories {
        private final FakeBandRepository bands = new FakeBandRepository();
        private final FakeStageRepository stages = new FakeStageRepository();
        private final FakePerformanceRepository performances = new FakePerformanceRepository();
        private final FakeStageDistanceRepository distances = new FakeStageDistanceRepository();
        private final FakeFoodOptionRepository food = new FakeFoodOptionRepository();
        private final FakeRatingRepository ratings = new FakeRatingRepository();

        private ImportFestivalCsvUseCase useCase() {
            return new ImportFestivalCsvUseCase(bands, stages, performances, distances, food);
        }
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

    private static final class FakeStageRepository implements StageRepository {
        private final Map<String, Stage> stagesByName = new LinkedHashMap<>();

        @Override
        public void save(Stage stage) {
            stagesByName.put(stage.name(), stage);
        }

        @Override
        public void replaceAll(List<Stage> stages) {
            stagesByName.clear();
            stages.forEach(this::save);
        }

        @Override
        public Optional<Stage> findByName(String name) {
            return Optional.ofNullable(stagesByName.get(name));
        }

        @Override
        public List<Stage> findAll() {
            return new ArrayList<>(stagesByName.values());
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

    private static final class FakeStageDistanceRepository implements StageDistanceRepository {
        private final Map<Key, StageDistance> distances = new LinkedHashMap<>();

        @Override
        public void save(StageDistance distance) {
            distances.put(new Key(distance.from(), distance.to()), distance);
        }

        @Override
        public void replaceAll(List<StageDistance> replacements) {
            distances.clear();
            replacements.forEach(this::save);
        }

        @Override
        public Optional<StageDistance> findBetween(Stage from, Stage to) {
            return Optional.ofNullable(distances.get(new Key(from, to)));
        }

        @Override
        public List<StageDistance> findAll() {
            return new ArrayList<>(distances.values());
        }

        private record Key(Stage from, Stage to) {
        }
    }

    private static final class FakeFoodOptionRepository implements FoodOptionRepository {
        private final List<FoodOption> foodOptions = new ArrayList<>();

        @Override
        public void save(FoodOption foodOption) {
            foodOptions.add(foodOption);
        }

        @Override
        public void replaceAll(List<FoodOption> replacements) {
            foodOptions.clear();
            foodOptions.addAll(replacements);
        }

        @Override
        public List<FoodOption> findAll() {
            return new ArrayList<>(foodOptions);
        }
    }

    private static final class FakeRatingRepository implements RatingRepository {
        private final Map<Key, Rating> ratings = new LinkedHashMap<>();

        @Override
        public void save(String userName, Band band, Rating rating) {
            ratings.put(new Key(userName, band.name()), rating);
        }

        @Override
        public Optional<Rating> findByUserAndBand(String userName, Band band) {
            return Optional.ofNullable(ratings.get(new Key(userName, band.name())));
        }

        private record Key(String userName, String bandName) {
        }
    }
}
