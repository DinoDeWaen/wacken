package be.wacken.planner.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalPlanningRatingRepository;
import be.wacken.planner.domain.FestivalRepository;
import be.wacken.planner.domain.PersonalBandRatingEvent;
import be.wacken.planner.domain.PersonalBandRatingHistoryRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RealRatingRepository;
import be.wacken.planner.domain.SavedFestivalPlanningRating;
import be.wacken.planner.domain.SavedRating;

final class FestivalRatingIndependenceTest {
    @Test
    void planningRatingBelongsToActiveFestivalWithoutCreatingPersonalHistory() {
        FakePlanningRatingRepository planningRatings = new FakePlanningRatingRepository();
        FakePersonalRatings personalRatings = new FakePersonalRatings();

        new ActiveFestivalRatingRepository(
                new StaticActiveFestivalRepository(),
                planningRatings,
                "group"
        ).save("dino", new Band("Airbourne"), Rating.of(5));

        assertEquals(Optional.of(Rating.of(5)), planningRatings.findByUserFestivalAndBand("dino", "wacken-2026", new Band("Airbourne")));
        assertTrue(personalRatings.findByUserAndBand("dino", new Band("Airbourne")).isEmpty());
    }

    @Test
    void personalRealRatingCreatesHistoricalEventWithFestivalAndDate() {
        FakePersonalRatings personalRatings = new FakePersonalRatings();
        FakeRealRatings latestRealRatings = new FakeRealRatings();
        Clock clock = Clock.fixed(Instant.parse("2026-08-03T21:15:00Z"), ZoneOffset.UTC);

        new RecordPersonalBandRatingUseCase(
                new StaticActiveFestivalRepository(),
                personalRatings,
                latestRealRatings,
                clock
        ).rateBand("dino", new Band("Airbourne"), 4);

        List<PersonalBandRatingEvent> events = personalRatings.findByUserAndBand("dino", new Band("Airbourne"));
        assertEquals(1, events.size());
        assertEquals(Optional.of("wacken-2026"), events.get(0).festivalId());
        assertEquals(Rating.of(4), events.get(0).rating());
        assertEquals(Instant.parse("2026-08-03T21:15:00Z"), events.get(0).createdAt());
        assertEquals(Optional.of(Rating.of(4)), latestRealRatings.findByUserAndBand("dino", new Band("Airbourne")));
    }

    private static final class StaticActiveFestivalRepository implements FestivalRepository {
        @Override
        public List<Festival> findAll() {
            return List.of(Festival.active("wacken-2026", "Wacken Open Air 2026"));
        }

        @Override
        public void save(Festival festival) {
        }
    }

    private static final class FakePlanningRatingRepository implements FestivalPlanningRatingRepository {
        private final List<SavedFestivalPlanningRating> ratings = new ArrayList<>();

        @Override
        public void save(String groupId, String userName, String festivalId, Band band, Rating rating) {
            ratings.add(new SavedFestivalPlanningRating(groupId, userName, festivalId, band, rating));
        }

        @Override
        public Optional<Rating> findByUserFestivalAndBand(String userName, String festivalId, Band band) {
            return ratings.stream()
                    .filter(rating -> rating.userName().equals(userName))
                    .filter(rating -> rating.festivalId().equals(festivalId))
                    .filter(rating -> rating.band().equals(band))
                    .map(SavedFestivalPlanningRating::rating)
                    .findFirst();
        }

        @Override
        public List<SavedFestivalPlanningRating> findByFestival(String festivalId) {
            return ratings.stream().filter(rating -> rating.festivalId().equals(festivalId)).toList();
        }

        @Override
        public List<SavedFestivalPlanningRating> findAll() {
            return List.copyOf(ratings);
        }
    }

    private static final class FakePersonalRatings implements PersonalBandRatingHistoryRepository {
        private final List<PersonalBandRatingEvent> events = new ArrayList<>();

        @Override
        public void save(PersonalBandRatingEvent event) {
            events.add(event);
        }

        @Override
        public List<PersonalBandRatingEvent> findByUserAndBand(String userName, Band band) {
            return events.stream()
                    .filter(event -> event.userName().equals(userName))
                    .filter(event -> event.band().equals(band))
                    .toList();
        }
    }

    private static final class FakeRealRatings implements RealRatingRepository {
        private final List<SavedRating> ratings = new ArrayList<>();

        @Override
        public void save(String userName, Band band, Rating rating) {
            ratings.add(new SavedRating(userName, band, rating));
        }

        @Override
        public Optional<Rating> findByUserAndBand(String userName, Band band) {
            return ratings.stream()
                    .filter(rating -> rating.userName().equals(userName))
                    .filter(rating -> rating.band().equals(band))
                    .map(SavedRating::rating)
                    .reduce((left, right) -> right);
        }
    }
}
