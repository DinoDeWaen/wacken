package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.RealRatingRepository;
import be.wacken.planner.domain.SavedRating;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalRepository;
import be.wacken.planner.domain.PersonalBandRatingEvent;
import be.wacken.planner.domain.PersonalBandRatingHistoryRepository;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShowBandDetailUseCaseTest {
    @Test
    void retainsDeletedFestivalNameInPersonalRatingHistory() {
        FakeBandRepository bands = new FakeBandRepository();
        bands.save(new Band("Airbourne"));
        FestivalRepository festivals = new FestivalRepository() {
            @Override public List<Festival> findAll() { return List.of(); }
            @Override public void save(Festival festival) { throw new UnsupportedOperationException(); }
        };
        PersonalBandRatingHistoryRepository history = new PersonalBandRatingHistoryRepository() {
            @Override public void save(PersonalBandRatingEvent event) { throw new UnsupportedOperationException(); }
            @Override public List<PersonalBandRatingEvent> findByUserAndBand(String userName, Band band) {
                return List.of(new PersonalBandRatingEvent("event", userName, band, Optional.empty(),
                        Rating.of(4), Instant.parse("2026-08-01T12:00:00Z"), Optional.of("Deleted festival")));
            }
        };

        BandDetailItem detail = new ShowBandDetailUseCase(bands, new FakeRatingRepository(),
                new FakeRealRatingRepository(), festivals, history)
                .showBand("dino", "Airbourne", MusicLinks.none()).orElseThrow();

        assertEquals("Deleted festival - 4 stars - 2026-08-01T12:00:00Z",
                detail.personalRatingHistory().get(0).displayText());
    }

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

    @Test
    void missingOrInvalidBandDetailNeverCreatesMasterData() {
        BandRepository readOnlyBands = new BandRepository() {
            @Override
            public void save(Band band) {
                throw new AssertionError("Opening band detail must not save a band");
            }

            @Override
            public void replaceAll(List<Band> bands) {
                throw new AssertionError("Opening band detail must not replace bands");
            }

            @Override
            public Optional<Band> findByName(String name) {
                if (name == null || name.isBlank()) {
                    throw new AssertionError("Invalid band names must not reach persistence");
                }
                return Optional.empty();
            }

            @Override
            public List<Band> findAll() {
                return List.of();
            }
        };
        ShowBandDetailUseCase useCase = new ShowBandDetailUseCase(readOnlyBands, new FakeRatingRepository());

        assertEquals(Optional.empty(), useCase.showBand("dino", "Unknown", MusicLinks.none()));
        assertEquals(Optional.empty(), useCase.showBand("dino", " ", MusicLinks.none()));
        assertEquals(Optional.empty(), useCase.showBand("dino", null, MusicLinks.none()));
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
