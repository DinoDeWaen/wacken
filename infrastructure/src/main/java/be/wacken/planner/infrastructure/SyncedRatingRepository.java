package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.SavedRating;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class SyncedRatingRepository implements RatingRepository {
    private final RatingRepository cache;
    private final RatingRepository source;

    public SyncedRatingRepository(RatingRepository cache, RatingRepository source) {
        this.cache = Objects.requireNonNull(cache, "cache must not be null");
        this.source = Objects.requireNonNull(source, "source must not be null");
    }

    public void syncSourceToCache() {
        for (SavedRating rating : source.findAll()) {
            cache.save(rating.userName(), rating.band(), rating.rating());
        }
    }

    @Override
    public void save(String userName, Band band, Rating rating) {
        source.save(userName, band, rating);
        cache.save(userName, band, rating);
    }

    @Override
    public Optional<Rating> findByUserAndBand(String userName, Band band) {
        return cache.findByUserAndBand(userName, band);
    }

    @Override
    public List<SavedRating> findAll() {
        return cache.findAll();
    }
}
