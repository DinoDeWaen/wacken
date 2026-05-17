package be.wacken.planner.persistence;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.SavedRating;
import be.wacken.planner.RatingSyncLocalStore;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class RoomRatingRepository implements RatingRepository, RatingSyncLocalStore {
    private final RoomRatingDao ratings;

    public RoomRatingRepository(WackenDatabase database) {
        this.ratings = database.ratings();
    }

    @Override
    public void save(String userName, Band band, Rating rating) {
        savePending("local", userName, band, rating);
    }

    @Override
    public void savePending(String groupId, String userName, Band band, Rating rating) {
        ratings.save(new RoomRating(userName, band.name(), rating.value(), groupId, "PENDING"));
    }

    @Override
    public void saveSynced(String groupId, String userName, Band band, Rating rating) {
        ratings.save(new RoomRating(userName, band.name(), rating.value(), groupId, "SYNCED"));
    }

    @Override
    public void saveSyncedGroupRating(String groupId, SavedRating rating) {
        saveSynced(groupId, rating.userName(), rating.band(), rating.rating());
    }

    @Override
    public List<SavedRating> findPending(String groupId, String userName) {
        return ratings.findPending(groupId, userName)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
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
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return ratings.count() == 0;
    }

    private SavedRating toDomain(RoomRating row) {
        return new SavedRating(row.userName, new Band(row.bandName), Rating.of(row.value));
    }
}
