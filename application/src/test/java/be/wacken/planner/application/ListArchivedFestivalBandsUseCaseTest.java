package be.wacken.planner.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.FestivalLineupEntry;
import be.wacken.planner.domain.FestivalLineupRepository;
import be.wacken.planner.domain.FestivalPlanningRatingRepository;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.PersonalBandRatingEvent;
import be.wacken.planner.domain.PersonalBandRatingHistoryRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RealRatingRepository;
import be.wacken.planner.domain.SavedFestivalPlanningRating;
import be.wacken.planner.domain.SavedRating;

final class ListArchivedFestivalBandsUseCaseTest {
    @Test
    void listsArchivedBandsFromPlanningRatingsWhenLineupEntriesAreMissing() {
        List<BandListItem> bands = new ListArchivedFestivalBandsUseCase(
                new EmptyLineup(),
                new StaticPlanningRatings(),
                new EmptyPersonalRatings(),
                new EmptyRealRatings(),
                new EmptyPerformances()
        ).listBands("dino", "wacken-2026");

        assertEquals(
                List.of(new BandListItem("Airbourne", "Not scheduled yet", "TBA", "TBA", 5, false, List.of(
                        new PersonRatingStars("dino", 5),
                        new PersonRatingStars("sofie", 4)
                ))),
                bands
        );
    }

    @Test
    void listsArchivedBandsFromLegacyRealRatingsWhenOnlyRealRatingsExist() {
        List<BandListItem> bands = new ListArchivedFestivalBandsUseCase(
                new EmptyLineup(),
                new EmptyPlanningRatings(),
                new EmptyPersonalRatings(),
                new LegacyRealRatings(),
                new EmptyPerformances()
        ).listBands("dino", "wacken-2026");

        assertEquals(List.of(new BandListItem("Airbourne", "Not scheduled yet", "TBA", "TBA", 0, true, List.of())), bands);
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

    private static final class StaticPlanningRatings implements FestivalPlanningRatingRepository {
        @Override
        public void save(String groupId, String userName, String festivalId, Band band, Rating rating) {
        }

        @Override
        public Optional<Rating> findByUserFestivalAndBand(String userName, String festivalId, Band band) {
            if ("dino".equals(userName) && "Airbourne".equals(band.name())) {
                return Optional.of(Rating.of(5));
            }
            return Optional.empty();
        }

        @Override
        public List<SavedFestivalPlanningRating> findByFestival(String festivalId) {
            return List.of(
                    new SavedFestivalPlanningRating("group", "dino", festivalId, new Band("Airbourne"), Rating.of(5)),
                    new SavedFestivalPlanningRating("group", "sofie", festivalId, new Band("Airbourne"), Rating.of(4))
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

    private static final class EmptyPersonalRatings implements PersonalBandRatingHistoryRepository {
        @Override
        public void save(PersonalBandRatingEvent event) {
        }

        @Override
        public List<PersonalBandRatingEvent> findByUserAndBand(String userName, Band band) {
            return List.of();
        }

        @Override
        public List<PersonalBandRatingEvent> findByUserAndFestival(String userName, String festivalId) {
            return List.of();
        }
    }

    private static final class LegacyRealRatings implements RealRatingRepository {
        @Override
        public void save(String userName, Band band, Rating rating) {
        }

        @Override
        public Optional<Rating> findByUserAndBand(String userName, Band band) {
            return Optional.empty();
        }

        @Override
        public List<SavedRating> findAll() {
            return List.of(new SavedRating("dino", new Band("Airbourne"), Rating.of(4)));
        }
    }

    private static final class EmptyRealRatings implements RealRatingRepository {
        @Override
        public void save(String userName, Band band, Rating rating) {
        }

        @Override
        public Optional<Rating> findByUserAndBand(String userName, Band band) {
            return Optional.empty();
        }
    }

    private static final class EmptyPerformances implements PerformanceRepository {
        @Override
        public void save(Performance performance) {
        }

        @Override
        public void replaceAll(List<Performance> replacements) {
        }

        @Override
        public List<Performance> findAll() {
            return List.of();
        }
    }
}
