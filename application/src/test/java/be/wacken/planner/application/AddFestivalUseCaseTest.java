package be.wacken.planner.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalLineupEntry;
import be.wacken.planner.domain.FestivalLineupRepository;
import be.wacken.planner.domain.FestivalPlanningRatingRepository;
import be.wacken.planner.domain.FestivalRepository;
import be.wacken.planner.domain.PersonalBandRatingEvent;
import be.wacken.planner.domain.PersonalBandRatingHistoryRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.SavedFestivalPlanningRating;

final class AddFestivalUseCaseTest {
    @Test
    void addsFestivalAfterArchiveAndReusesExactBandNames() {
        FakeFestivalRepository festivals = new FakeFestivalRepository(List.of(Festival.archived("wacken-2026", "Wacken Open Air 2026")));
        FakeBandRepository bands = new FakeBandRepository(List.of(new Band("Airbourne")));
        FakeLineupRepository lineups = new FakeLineupRepository();
        FakePlanningRatingRepository planningRatings = new FakePlanningRatingRepository();

        AddFestivalResult result = new AddFestivalUseCase(
                festivals,
                bands,
                lineups,
                planningRatings,
                new FakePersonalRatingHistoryRepository()
        ).addFestival("group", "dino", "rock-im-park-2027", "Rock im Park 2027", List.of(new Band("Airbourne"), new Band("New Act")));

        assertTrue(result.success());
        assertEquals(1, result.reusedBands());
        assertEquals(1, result.createdBands());
        assertEquals(Festival.active("rock-im-park-2027", "Rock im Park 2027"), festivals.findAll().get(1));
        assertEquals(List.of("Airbourne", "New Act"), lineups.findByFestival("rock-im-park-2027").stream().map(entry -> entry.band().name()).toList());
    }

    @Test
    void preventsAddingFestivalWhenOneIsAlreadyActive() {
        AddFestivalResult result = new AddFestivalUseCase(
                new FakeFestivalRepository(List.of(Festival.active("wacken-2026", "Wacken Open Air 2026"))),
                new FakeBandRepository(List.of()),
                new FakeLineupRepository(),
                new FakePlanningRatingRepository(),
                new FakePersonalRatingHistoryRepository()
        ).addFestival("group", "dino", "rock-im-park-2027", "Rock im Park 2027", List.of(new Band("Airbourne")));

        assertFalse(result.success());
        assertEquals("Archive the active festival before adding the next one.", result.message());
    }

    @Test
    void prefillUsesLatestPersonalRatingOnly() {
        FakePersonalRatingHistoryRepository personalRatings = new FakePersonalRatingHistoryRepository();
        personalRatings.save(new PersonalBandRatingEvent("old", "dino", new Band("Airbourne"), Optional.of("wacken-2026"), Rating.of(2), Instant.parse("2026-08-01T10:00:00Z")));
        personalRatings.save(new PersonalBandRatingEvent("latest", "dino", new Band("Airbourne"), Optional.of("rock-am-ring-2027"), Rating.of(4), Instant.parse("2027-06-01T10:00:00Z")));
        FakePlanningRatingRepository planningRatings = new FakePlanningRatingRepository();

        AddFestivalResult result = new AddFestivalUseCase(
                new FakeFestivalRepository(List.of(Festival.archived("wacken-2026", "Wacken Open Air 2026"))),
                new FakeBandRepository(List.of(new Band("Airbourne"))),
                new FakeLineupRepository(),
                planningRatings,
                personalRatings
        ).addFestival("group", "dino", "rock-im-park-2028", "Rock im Park 2028", List.of(new Band("Airbourne"), new Band("Unknown Act")));

        assertEquals(1, result.prefilledRatings());
        assertEquals(Optional.of(Rating.of(4)), planningRatings.findByUserFestivalAndBand("dino", "rock-im-park-2028", new Band("Airbourne")));
        assertTrue(planningRatings.findByUserFestivalAndBand("dino", "rock-im-park-2028", new Band("Unknown Act")).isEmpty());
    }

    private static final class FakeFestivalRepository implements FestivalRepository {
        private final List<Festival> festivals = new ArrayList<>();

        FakeFestivalRepository(List<Festival> festivals) {
            this.festivals.addAll(festivals);
        }

        @Override
        public List<Festival> findAll() {
            return List.copyOf(festivals);
        }

        @Override
        public void save(Festival festival) {
            festivals.removeIf(existing -> existing.id().equals(festival.id()));
            festivals.add(festival);
        }
    }

    private static final class FakeBandRepository implements BandRepository {
        private final List<Band> bands = new ArrayList<>();

        FakeBandRepository(List<Band> bands) {
            this.bands.addAll(bands);
        }

        @Override
        public void save(Band band) {
            if (findByName(band.name()).isEmpty()) {
                bands.add(band);
            }
        }

        @Override
        public void replaceAll(List<Band> replacements) {
            bands.clear();
            bands.addAll(replacements);
        }

        @Override
        public Optional<Band> findByName(String name) {
            return bands.stream().filter(band -> band.name().equals(name)).findFirst();
        }

        @Override
        public List<Band> findAll() {
            return List.copyOf(bands);
        }
    }

    private static final class FakeLineupRepository implements FestivalLineupRepository {
        private final List<FestivalLineupEntry> entries = new ArrayList<>();

        @Override
        public void saveAllForFestival(String festivalId, List<FestivalLineupEntry> entries) {
            this.entries.removeIf(entry -> entry.festivalId().equals(festivalId));
            this.entries.addAll(entries);
        }

        @Override
        public List<FestivalLineupEntry> findByFestival(String festivalId) {
            return entries.stream().filter(entry -> entry.festivalId().equals(festivalId)).toList();
        }
    }

    private static final class FakePlanningRatingRepository implements FestivalPlanningRatingRepository {
        private final List<SavedFestivalPlanningRating> ratings = new ArrayList<>();

        @Override
        public void save(String groupId, String userName, String festivalId, Band band, Rating rating) {
            ratings.removeIf(existing -> existing.groupId().equals(groupId)
                    && existing.userName().equals(userName)
                    && existing.festivalId().equals(festivalId)
                    && existing.band().equals(band));
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

    private static final class FakePersonalRatingHistoryRepository implements PersonalBandRatingHistoryRepository {
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
}
