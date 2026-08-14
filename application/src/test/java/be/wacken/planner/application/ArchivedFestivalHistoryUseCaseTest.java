package be.wacken.planner.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalLineupEntry;
import be.wacken.planner.domain.FestivalLineupRepository;
import be.wacken.planner.domain.FestivalPlanningRatingRepository;
import be.wacken.planner.domain.FestivalRepository;
import be.wacken.planner.domain.PersonalBandRatingEvent;
import be.wacken.planner.domain.PersonalBandRatingHistoryRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.SavedFestivalPlanningRating;

final class ArchivedFestivalHistoryUseCaseTest {
    @Test
    void showsArchivedFestivalBandsAndPersonalRatingEventsReadOnly() {
        ViewArchivedFestivalHistoryUseCase.ArchivedFestivalHistory history = new ViewArchivedFestivalHistoryUseCase(
                new StaticFestivals(),
                new StaticLineup(),
                new EmptyPlanningRatings(),
                new StaticPersonalRatings()
        ).show("dino", "wacken-2026");

        assertEquals("Wacken Open Air 2026", history.festivalName());
        assertEquals(List.of("Airbourne"), history.bandNames());
        assertTrue(history.planningRatings().isEmpty());
        assertEquals("Wacken Open Air 2026 - 4 stars - 2026-08-03T21:15:00Z", history.personalRatings().get(0).displayText());
        assertTrue(history.readOnly());
    }

    @Test
    void fallsBackToPlanningAndPersonalRatingsWhenLineupEntriesAreMissing() {
        ViewArchivedFestivalHistoryUseCase.ArchivedFestivalHistory history = new ViewArchivedFestivalHistoryUseCase(
                new StaticFestivals(),
                new EmptyLineup(),
                new StaticPlanningRatings(),
                new FestivalPersonalRatings()
        ).show("dino", "wacken-2026");

        assertEquals(List.of("Airbourne", "Skyline"), history.bandNames());
        assertEquals(
                List.of(
                        new ViewArchivedFestivalHistoryUseCase.ArchivedPlanningRatingItem("Airbourne", "dino", 5),
                        new ViewArchivedFestivalHistoryUseCase.ArchivedPlanningRatingItem("Skyline", "sofie", 4)
                ),
                history.planningRatings()
        );
        assertEquals(List.of("Airbourne"), history.personalRatings().stream().map(PersonalRatingHistoryItem::bandName).toList());
        assertEquals("Wacken Open Air 2026 - 3 stars - 2026-08-04T19:00:00Z", history.personalRatings().get(0).displayText());
    }

    private static final class StaticFestivals implements FestivalRepository {
        @Override
        public List<Festival> findAll() {
            return List.of(Festival.archived("wacken-2026", "Wacken Open Air 2026"));
        }

        @Override
        public void save(Festival festival) {
        }
    }

    private static final class StaticLineup implements FestivalLineupRepository {
        @Override
        public void saveAllForFestival(String festivalId, List<FestivalLineupEntry> entries) {
        }

        @Override
        public List<FestivalLineupEntry> findByFestival(String festivalId) {
            return List.of(new FestivalLineupEntry(festivalId, new Band("Airbourne"), "Airbourne"));
        }
    }

    private static final class EmptyLineup implements FestivalLineupRepository {
        @Override
        public void saveAllForFestival(String festivalId, List<FestivalLineupEntry> entries) {
        }

        @Override
        public List<FestivalLineupEntry> findByFestival(String festivalId) {
            return List.of();
        }
    }

    private static final class StaticPersonalRatings implements PersonalBandRatingHistoryRepository {
        @Override
        public void save(PersonalBandRatingEvent event) {
        }

        @Override
        public List<PersonalBandRatingEvent> findByUserAndBand(String userName, Band band) {
            return List.of(new PersonalBandRatingEvent(
                    "event",
                    userName,
                    band,
                    Optional.of("wacken-2026"),
                    Rating.of(4),
                    Instant.parse("2026-08-03T21:15:00Z")
            ));
        }
    }

    private static final class FestivalPersonalRatings implements PersonalBandRatingHistoryRepository {
        @Override
        public void save(PersonalBandRatingEvent event) {
        }

        @Override
        public List<PersonalBandRatingEvent> findByUserAndBand(String userName, Band band) {
            return findByUserAndFestival(userName, "wacken-2026").stream()
                    .filter(event -> event.band().equals(band))
                    .toList();
        }

        @Override
        public List<PersonalBandRatingEvent> findByUserAndFestival(String userName, String festivalId) {
            return List.of(new PersonalBandRatingEvent(
                    "event-airbourne",
                    userName,
                    new Band("Airbourne"),
                    Optional.of(festivalId),
                    Rating.of(3),
                    Instant.parse("2026-08-04T19:00:00Z")
            ));
        }
    }

    private static final class StaticPlanningRatings implements FestivalPlanningRatingRepository {
        @Override
        public void save(String groupId, String userName, String festivalId, Band band, Rating rating) {
        }

        @Override
        public Optional<Rating> findByUserFestivalAndBand(String userName, String festivalId, Band band) {
            return Optional.empty();
        }

        @Override
        public List<SavedFestivalPlanningRating> findByFestival(String festivalId) {
            return List.of(
                    new SavedFestivalPlanningRating("group", "dino", festivalId, new Band("Airbourne"), Rating.of(5)),
                    new SavedFestivalPlanningRating("group", "sofie", festivalId, new Band("Skyline"), Rating.of(4))
            );
        }

        @Override
        public List<SavedFestivalPlanningRating> findAll() {
            return List.of();
        }
    }

    private static final class EmptyPlanningRatings implements FestivalPlanningRatingRepository {
        @Override
        public void save(String groupId, String userName, String festivalId, Band band, Rating rating) {
        }

        @Override
        public Optional<Rating> findByUserFestivalAndBand(String userName, String festivalId, Band band) {
            return Optional.empty();
        }

        @Override
        public List<SavedFestivalPlanningRating> findByFestival(String festivalId) {
            return List.of();
        }

        @Override
        public List<SavedFestivalPlanningRating> findAll() {
            return List.of();
        }
    }
}
