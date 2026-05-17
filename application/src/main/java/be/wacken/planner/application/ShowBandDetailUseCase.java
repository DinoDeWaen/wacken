package be.wacken.planner.application;

import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.RatingRepository;

import java.util.Objects;
import java.util.Optional;

public final class ShowBandDetailUseCase {
    private final BandRepository bands;
    private final EffectiveRatingResolver ratings;

    public ShowBandDetailUseCase(BandRepository bands, RatingRepository ratings) {
        this.bands = Objects.requireNonNull(bands, "bands must not be null");
        this.ratings = new EffectiveRatingResolver(ratings);
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
                            band.spotifyUrl().or(() -> musicLinks.spotifyUrl())
                    );
                });
    }
}
