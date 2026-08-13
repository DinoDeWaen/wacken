package be.wacken.planner.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.FestivalLifecycle;
import be.wacken.planner.domain.FestivalPlanningRatingRepository;
import be.wacken.planner.domain.FestivalRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.SavedRating;

public final class ActiveFestivalRatingRepository implements RatingRepository {
    private final FestivalRepository festivals;
    private final FestivalPlanningRatingRepository planningRatings;
    private final String groupId;

    public ActiveFestivalRatingRepository(FestivalRepository festivals, FestivalPlanningRatingRepository planningRatings, String groupId) {
        this.festivals = festivals;
        this.planningRatings = planningRatings;
        this.groupId = groupId;
    }

    @Override
    public void save(String userName, Band band, Rating rating) {
        FestivalLifecycle.activeFestival(festivals.findAll())
                .ifPresent(active -> planningRatings.save(groupId, userName, active.id(), band, rating));
    }

    @Override
    public Optional<Rating> findByUserAndBand(String userName, Band band) {
        return FestivalLifecycle.activeFestival(festivals.findAll())
                .flatMap(active -> planningRatings.findByUserFestivalAndBand(userName, active.id(), band));
    }

    @Override
    public List<SavedRating> findAll() {
        return FestivalLifecycle.activeFestival(festivals.findAll())
                .map(active -> planningRatings.findByFestival(active.id())
                        .stream()
                        .map(rating -> new SavedRating(rating.userName(), rating.band(), rating.rating()))
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }
}
