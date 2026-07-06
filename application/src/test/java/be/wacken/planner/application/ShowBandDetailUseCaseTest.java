package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.RealRatingRepository;
import be.wacken.planner.domain.SavedRating;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShowBandDetailUseCaseTest {
    @Test
    void returnsBandInformationNeededForRating() {
        FakeBandRepository bands = new FakeBandRepository();
        bands.save(new Band("5th Avenue"));
        ShowBandDetailUseCase useCase = new ShowBandDetailUseCase(bands, new FakeRatingRepository());

        Optional<BandDetailItem> detail = useCase.showBand("dino", "5th Avenue", MusicLinks.none());

        assertEquals(Optional.of(new BandDetailItem("5th Avenue", Optional.empty(), Optional.empty(), 0, true, Optional.empty(), Optional.empty(), List.of())), detail);
    }

    @Test
    void displaysStoredRatingAsSelectedStarRating() {
        FakeBandRepository bands = new FakeBandRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Band band = new Band("5th Avenue");
        bands.save(band);
        ratings.save("dino", band, Rating.of(4));
        ShowBandDetailUseCase useCase = new ShowBandDetailUseCase(bands, ratings);

        Optional<BandDetailItem> detail = useCase.showBand("dino", "5th Avenue", MusicLinks.none());

        assertEquals(Optional.of(new BandDetailItem("5th Avenue", Optional.empty(), Optional.empty(), 4, false, Optional.empty(), Optional.empty(), List.of(new PersonRatingStars("dino", 4)))), detail);
    }

    @Test
    void displaysRealPostShowRatingSeparatelyFromPlanningRating() {
        FakeBandRepository bands = new FakeBandRepository();
        FakeRatingRepository planningRatings = new FakeRatingRepository();
        FakeRealRatingRepository realRatings = new FakeRealRatingRepository();
        Band band = new Band("5th Avenue");
        bands.save(band);
        planningRatings.save("dino", band, Rating.of(5));
        realRatings.save("dino", band, Rating.of(3));
        ShowBandDetailUseCase useCase = new ShowBandDetailUseCase(bands, planningRatings, realRatings);

        BandDetailItem detail = useCase.showBand("dino", "5th Avenue", MusicLinks.none()).orElseThrow();

        assertEquals(5, detail.rating());
        assertEquals(false, detail.defaultRating());
        assertEquals(3, detail.realRating());
        assertEquals(false, detail.defaultRealRating());
    }

    @Test
    void includesAvailableMusicLinks() {
        FakeBandRepository bands = new FakeBandRepository();
        bands.save(new Band("5th Avenue"));
        ShowBandDetailUseCase useCase = new ShowBandDetailUseCase(bands, new FakeRatingRepository());

        Optional<BandDetailItem> detail = useCase.showBand(
                "dino",
                "5th Avenue",
                new MusicLinks(Optional.of("https://youtube.example/5th"), Optional.of("https://spotify.example/5th"))
        );

        assertEquals(
                Optional.of(new BandDetailItem(
                        "5th Avenue",
                        Optional.empty(),
                        Optional.empty(),
                        0,
                        true,
                        Optional.of("https://youtube.example/5th"),
                        Optional.of("https://spotify.example/5th"),
                        List.of()
                )),
                detail
        );
    }

    @Test
    void removesBlankMusicLinksFromDetail() {
        FakeBandRepository bands = new FakeBandRepository();
        bands.save(new Band("5th Avenue"));
        ShowBandDetailUseCase useCase = new ShowBandDetailUseCase(bands, new FakeRatingRepository());

        Optional<BandDetailItem> detail = useCase.showBand(
                "dino",
                "5th Avenue",
                new MusicLinks(Optional.of(" "), Optional.empty())
        );

        assertEquals(Optional.of(new BandDetailItem("5th Avenue", Optional.empty(), Optional.empty(), 0, true, Optional.empty(), Optional.empty(), List.of())), detail);
    }

    @Test
    void includesImportedBandBiographyAndStoredLinks() {
        FakeBandRepository bands = new FakeBandRepository();
        bands.save(new Band(
                "5th Avenue",
                Optional.of("Hamburg rock band returning to Wacken."),
                Optional.of("https://images.example/5th.jpg"),
                Optional.of("https://youtube.example/stored"),
                Optional.of("https://spotify.example/stored")
        ));
        ShowBandDetailUseCase useCase = new ShowBandDetailUseCase(bands, new FakeRatingRepository());

        Optional<BandDetailItem> detail = useCase.showBand("dino", "5th Avenue", MusicLinks.none());

        assertEquals(
                Optional.of(new BandDetailItem(
                        "5th Avenue",
                        Optional.of("Hamburg rock band returning to Wacken."),
                        Optional.of("https://images.example/5th.jpg"),
                        0,
                        true,
                        Optional.of("https://youtube.example/stored"),
                        Optional.of("https://spotify.example/stored"),
                        List.of()
                )),
                detail
        );
    }

    @Test
    void rendersHtmlBiographyAsReadableText() {
        FakeBandRepository bands = new FakeBandRepository();
        bands.save(new Band(
                "Thundermother",
                Optional.of("<p>Swedish&nbsp;rock &amp; roll<br />Back at Wacken.</p><p>&quot;Louder&quot; &#39;again&#39; &#x21;</p>"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        ));
        ShowBandDetailUseCase useCase = new ShowBandDetailUseCase(bands, new FakeRatingRepository());

        Optional<BandDetailItem> detail = useCase.showBand("dino", "Thundermother", MusicLinks.none());

        assertEquals(
                Optional.of("Swedish rock & roll\nBack at Wacken.\n\n\"Louder\" 'again' !"),
                detail.orElseThrow().biography()
        );
    }

    @Test
    void includesReadOnlyGroupRatingsForSelectedBand() {
        FakeBandRepository bands = new FakeBandRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Band band = new Band("5th Avenue");
        bands.save(band);
        ratings.save("sofie", band, Rating.of(5));
        ratings.save("dino", band, Rating.of(3));
        ratings.save("alex", band, Rating.of(0));
        ratings.save("sofie", new Band("Other Band"), Rating.of(4));
        ShowBandDetailUseCase useCase = new ShowBandDetailUseCase(bands, ratings);

        Optional<BandDetailItem> detail = useCase.showBand("dino", "5th Avenue", MusicLinks.none());

        assertEquals(
                List.of(
                        new PersonRatingStars("dino", 3),
                        new PersonRatingStars("sofie", 5)
                ),
                detail.orElseThrow().personRatings()
        );
    }

    @Test
    void keepsNoGroupRatingsStateWhenRatingsAreUnavailable() {
        FakeBandRepository bands = new FakeBandRepository();
        bands.save(new Band("5th Avenue"));
        ShowBandDetailUseCase useCase = new ShowBandDetailUseCase(bands, new FakeRatingRepository());

        Optional<BandDetailItem> detail = useCase.showBand("dino", "5th Avenue", MusicLinks.none());

        assertEquals(List.of(), detail.orElseThrow().personRatings());
    }

    @Test
    void returnsEmptyWhenBandDoesNotExist() {
        ShowBandDetailUseCase useCase = new ShowBandDetailUseCase(new FakeBandRepository(), new FakeRatingRepository());

        assertEquals(Optional.empty(), useCase.showBand("dino", "Unknown", MusicLinks.none()));
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

    private static final class FakeRatingRepository implements RatingRepository {
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

        private record Key(String userName, Band band) {
        }
    }

    private static final class FakeRealRatingRepository implements RealRatingRepository {
        private final Map<Key, Rating> ratings = new LinkedHashMap<>();

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
