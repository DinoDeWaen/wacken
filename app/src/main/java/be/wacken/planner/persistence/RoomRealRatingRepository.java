package be.wacken.planner.persistence;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RealRatingRepository;
import be.wacken.planner.domain.SavedRating;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class RoomRealRatingRepository implements RealRatingRepository {
    private final RoomRealRatingDao ratings;

    public RoomRealRatingRepository(WackenDatabase database) {
        this.ratings = database.realRatings();
    }

    @Override
    public void save(String userName, Band band, Rating rating) {
        ratings.save(new RoomRealRating(userName, band.name(), rating.value()));
    }

    @Override
    public Optional<Rating> findByUserAndBand(String userName, Band band) {
        return Optional.ofNullable(ratings.findByUserAndBand(userName, band.name()))
                .map(row -> Rating.of(row.value));
    }

    @Override
    public List<SavedRating> findAll() {
        return ratings.findAll()
                .stream()
                .map(row -> new SavedRating(row.userName, new Band(row.bandName), Rating.of(row.value)))
                .collect(Collectors.toList());
    }
}
