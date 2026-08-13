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
    private final RoomFestivalPlanningRatingDao ratings;

    public RoomFestivalPlanningRatingRepository(WackenDatabase database) {
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
