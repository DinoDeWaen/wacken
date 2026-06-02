package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
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

        assertEquals(Optional.of(new BandDetailItem("5th Avenue", Optional.empty(), Optional.empty(), 0, true, Optional.empty(), Optional.empty())), detail);
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

        assertEquals(Optional.of(new BandDetailItem("5th Avenue", Optional.empty(), Optional.empty(), 4, false, Optional.empty(), Optional.empty())), detail);
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
                        Optional.of("https://spotify.example/5th")
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

        assertEquals(Optional.of(new BandDetailItem("5th Avenue", Optional.empty(), Optional.empty(), 0, true, Optional.empty(), Optional.empty())), detail);
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
                        Optional.of("https://spotify.example/stored")
                )),
                detail
        );
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

        private record Key(String userName, Band band) {
        }
    }
}
