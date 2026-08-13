package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.RealRatingRepository;
import be.wacken.planner.domain.SavedRating;
import be.wacken.planner.domain.FestivalRepository;
import be.wacken.planner.domain.PersonalBandRatingHistoryRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ShowBandDetailUseCase {
    private final BandRepository bands;
    private final RatingRepository ratingRepository;
    private final RealRatingRepository realRatingRepository;
    private final FestivalRepository festivalRepository;
    private final PersonalBandRatingHistoryRepository personalRatingHistoryRepository;
    private final EffectiveRatingResolver ratings;

    public ShowBandDetailUseCase(BandRepository bands, RatingRepository ratings) {
        this(bands, ratings, new EmptyRealRatingRepository());
    }

    public ShowBandDetailUseCase(BandRepository bands, RatingRepository ratings, RealRatingRepository realRatings) {
        this(bands, ratings, realRatings, new EmptyFestivalRepository(), new EmptyPersonalRatingHistoryRepository());
    }

    public ShowBandDetailUseCase(
            BandRepository bands,
            RatingRepository ratings,
            RealRatingRepository realRatings,
            FestivalRepository festivals,
            PersonalBandRatingHistoryRepository personalRatingHistory
    ) {
        this.bands = Objects.requireNonNull(bands, "bands must not be null");
        this.ratingRepository = Objects.requireNonNull(ratings, "ratings must not be null");
        this.realRatingRepository = Objects.requireNonNull(realRatings, "realRatings must not be null");
        this.festivalRepository = Objects.requireNonNull(festivals, "festivals must not be null");
        this.personalRatingHistoryRepository = Objects.requireNonNull(personalRatingHistory, "personalRatingHistory must not be null");
        this.ratings = new EffectiveRatingResolver(this.ratingRepository);
    }

    public Optional<BandDetailItem> showBand(String userName, String bandName, MusicLinks musicLinks) {
        Objects.requireNonNull(musicLinks, "musicLinks must not be null");
        return bands.findByName(bandName)
                .map(band -> {
                    EffectiveRating rating = ratings.resolve(userName, band);
                    Optional<Rating> realRating = realRatingRepository.findByUserAndBand(userName, band);
                    return new BandDetailItem(
                            band.name(),
                            BiographyText.readable(band.biography()),
                            band.imageUrl(),
                            rating.value(),
                            !rating.explicit(),
                            band.youtubeUrl().or(() -> musicLinks.youtubeUrl()),
                            band.spotifyUrl().or(() -> musicLinks.spotifyUrl()),
                            realRating.map(Rating::value).orElse(0),
                            realRating.map(stored -> stored.value() == 0).orElse(true),
                            personRatingsFor(band),
                            personalRatingHistoryFor(userName, band)
                    );
                });
    }

    private List<PersonalRatingHistoryItem> personalRatingHistoryFor(String userName, Band band) {
        var festivalNames = festivalRepository.findAll()
                .stream()
                .collect(Collectors.toMap(festival -> festival.id(), festival -> festival.name()));
        return personalRatingHistoryRepository.findByUserAndBand(userName, band)
                .stream()
                .map(event -> new PersonalRatingHistoryItem(
                        event.band().name(),
                        event.festivalId().map(festivalNames::get),
                        event.rating().value(),
                        event.createdAt()
                ))
                .collect(Collectors.toList());
    }

    private List<PersonRatingStars> personRatingsFor(be.wacken.planner.domain.Band band) {
        return ratingRepository.findAll()
                .stream()
                .filter(rating -> rating.band().equals(band))
                .filter(rating -> rating.rating().value() > 0)
                .sorted(Comparator.comparing(SavedRating::userName, String.CASE_INSENSITIVE_ORDER))
                .map(rating -> new PersonRatingStars(rating.userName(), rating.rating().value()))
                .collect(Collectors.toList());
    }

    private static final class EmptyRealRatingRepository implements RealRatingRepository {
        @Override
        public void save(String userName, Band band, Rating rating) {
        }

        @Override
        public Optional<Rating> findByUserAndBand(String userName, Band band) {
            return Optional.empty();
        }
    }

    private static final class EmptyFestivalRepository implements FestivalRepository {
        @Override
        public List<be.wacken.planner.domain.Festival> findAll() {
            return List.of();
        }

        @Override
        public void save(be.wacken.planner.domain.Festival festival) {
        }
    }

    private static final class EmptyPersonalRatingHistoryRepository implements PersonalBandRatingHistoryRepository {
        @Override
        public void save(be.wacken.planner.domain.PersonalBandRatingEvent event) {
        }

        @Override
        public List<be.wacken.planner.domain.PersonalBandRatingEvent> findByUserAndBand(String userName, Band band) {
            return List.of();
        }
    }
}
