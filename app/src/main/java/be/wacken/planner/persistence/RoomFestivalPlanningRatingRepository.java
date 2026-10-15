package be.wacken.planner.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.FestivalPlanningRatingRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.SavedFestivalPlanningRating;
import be.wacken.planner.FestivalPlanningRatingSyncLocalStore;

public final class RoomFestivalPlanningRatingRepository implements FestivalPlanningRatingSyncLocalStore {
    private final WackenDatabase database;
    private final RoomFestivalPlanningRatingDao ratings;

    public RoomFestivalPlanningRatingRepository(WackenDatabase database) {
        this.database = database;
        this.ratings = database.festivalPlanningRatings();
    }

    @Override
    public void save(String groupId, String userName, String festivalId, Band band, Rating rating) {
        ratings.save(new RoomFestivalPlanningRating(groupId, userName, festivalId, band.name(), rating.value(), "PENDING"));
    }

    public void saveSynced(SavedFestivalPlanningRating rating) {
        ratings.save(new RoomFestivalPlanningRating(rating.groupId(), rating.userName(), rating.festivalId(), rating.band().name(), rating.rating().value(), "SYNCED"));
    }

    public List<SavedFestivalPlanningRating> findPending(String groupId, String userName) {
        return ratings.findPending(groupId, userName).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void movePendingRating(SavedFestivalPlanningRating expected, String targetFestivalId) {
        database.runInTransaction(() -> {
            RoomFestivalPlanningRating source = ratings.findExact(expected.groupId(), expected.userName(),
                    expected.festivalId(), expected.band().name());
            if (source == null || !"PENDING".equals(source.syncStatus) || source.value != expected.rating().value()) {
                throw new IllegalStateException("Pending rating changed. Review it again before recovery.");
            }
            if (ratings.findExact(expected.groupId(), expected.userName(), targetFestivalId, expected.band().name()) != null) {
                throw new IllegalStateException("A Wacken rating already exists for this band. The pending rating was kept.");
            }
            if (ratings.deletePendingExact(expected.groupId(), expected.userName(), expected.festivalId(),
                    expected.band().name(), expected.rating().value()) != 1) {
                throw new IllegalStateException("Pending rating changed. Review it again before recovery.");
            }
            ratings.save(new RoomFestivalPlanningRating(expected.groupId(), expected.userName(), targetFestivalId,
                    expected.band().name(), expected.rating().value(), "PENDING"));
        });
    }

    @Override
    public Optional<Rating> findByUserFestivalAndBand(String userName, String festivalId, Band band) {
        return Optional.ofNullable(ratings.findByUserFestivalAndBand(userName, festivalId, band.name()))
                .map(row -> Rating.of(row.value));
    }

    @Override
    public List<SavedFestivalPlanningRating> findByFestival(String festivalId) {
        return ratings.findByFestival(festivalId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<SavedFestivalPlanningRating> findAll() {
        return ratings.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private SavedFestivalPlanningRating toDomain(RoomFestivalPlanningRating row) {
        return new SavedFestivalPlanningRating(row.groupId, row.userName, row.festivalId, new Band(row.bandName), Rating.of(row.value));
    }
}
