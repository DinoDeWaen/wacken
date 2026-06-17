package be.wacken.planner.application;

import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.SavedRating;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ShowBandDetailUseCase {
    private final BandRepository bands;
    private final RatingRepository ratingRepository;
    private final EffectiveRatingResolver ratings;

    public ShowBandDetailUseCase(BandRepository bands, RatingRepository ratings) {
        this.bands = Objects.requireNonNull(bands, "bands must not be null");
        this.ratingRepository = Objects.requireNonNull(ratings, "ratings must not be null");
        this.ratings = new EffectiveRatingResolver(this.ratingRepository);
    }

    public Optional<BandDetailItem> showBand(String userName, String bandName, MusicLinks musicLinks) {
        Objects.requireNonNull(musicLinks, "musicLinks must not be null");
        return bands.findByName(bandName)
                .map(band -> {
                    EffectiveRating rating = ratings.resolve(userName, band);
                    return new BandDetailItem(
                            band.name(),
                            band.biography(),
                            band.imageUrl(),
                            rating.value(),
                            !rating.explicit(),
                            band.youtubeUrl().or(() -> musicLinks.youtubeUrl()),
                            band.spotifyUrl().or(() -> musicLinks.spotifyUrl()),
                            personRatingsFor(band)
                    );
                });
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
}
