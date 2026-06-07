package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.SavedRating;
import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.GroupDecisionStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GenerateSharedScheduleUseCaseTest {
    @Test
    void groupsTimelineSlotsByDayAndSortsThemByStartTime() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Performance later = performance("Airbourne", 30, 20, 0, 21, 0);
        Performance earlier = performance("5th Avenue", 30, 18, 0, 19, 0);
        Performance nextDay = performance("Iron Maiden", 31, 19, 0, 21, 0);
        performances.replaceAll(List.of(later, nextDay, earlier));
        ratings.save("sofie", later.band(), Rating.of(5));
        ratings.save("dino", earlier.band(), Rating.of(4));
        ratings.save("sofie", nextDay.band(), Rating.of(5));

        SharedSchedule schedule = new GenerateSharedScheduleUseCase(performances, ratings).generate();

        assertEquals(SharedScheduleStatus.GENERATED, schedule.status());
        assertEquals(List.of(LocalDate.of(2026, 7, 30), LocalDate.of(2026, 7, 31)), schedule.days().stream().map(ScheduleDay::date).toList());
        assertEquals(List.of("5th Avenue", "Airbourne"), schedule.days().get(0).slots().stream().map(TimelineSlot::bandName).toList());
        assertEquals(List.of("Iron Maiden"), schedule.days().get(1).slots().stream().map(TimelineSlot::bandName).toList());
    }

    @Test
    void includesLostAlternativeForResolvedConflicts() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Performance selected = performance("5th Avenue", 30, 18, 0, 19, 0);
        Performance lost = performance("Airbourne", 30, 18, 30, 19, 30);
        performances.replaceAll(List.of(selected, lost));
        ratings.save("sofie", selected.band(), Rating.of(5));
        ratings.save("dino", lost.band(), Rating.of(4));

        TimelineSlot slot = new GenerateSharedScheduleUseCase(performances, ratings).generate().days().get(0).slots().get(0);

        assertEquals("5th Avenue", slot.bandName());
        assertEquals(Optional.of("Airbourne"), slot.lostAlternativeBandName());
        assertEquals(GroupDecisionStatus.GO, slot.decisionStatus());
    }

    @Test
    void marksOptionalSlotsWhenConflictResolutionIsOptional() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Performance selected = performance("5th Avenue", 30, 18, 0, 19, 0);
        Performance lost = performance("Airbourne", 30, 18, 30, 19, 30);
        performances.replaceAll(List.of(selected, lost));
        ratings.save("sofie", selected.band(), Rating.of(3));
        ratings.save("dino", selected.band(), Rating.of(3));
        ratings.save("sofie", lost.band(), Rating.of(3));

        TimelineSlot slot = new GenerateSharedScheduleUseCase(performances, ratings).generate().days().get(0).slots().get(0);

        assertEquals("5th Avenue", slot.bandName());
        assertEquals(GroupDecisionStatus.OPTIONAL, slot.decisionStatus());
        assertEquals(true, slot.optional());
    }

    @Test
    void skipsConflictWhenEveryCandidateIsVetoBlocked() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Performance first = performance("5th Avenue", 30, 18, 0, 19, 0);
        Performance second = performance("Airbourne", 30, 18, 30, 19, 30);
        performances.replaceAll(List.of(first, second));
        ratings.save("sofie", first.band(), Rating.of(3));
        ratings.save("dino", first.band(), Rating.of(1));
        ratings.save("sofie", second.band(), Rating.of(4));
        ratings.save("dino", second.band(), Rating.of(1));
        ratings.save("jan", second.band(), Rating.of(1));

        SharedSchedule schedule = new GenerateSharedScheduleUseCase(performances, ratings).generate();

        assertEquals(SharedScheduleStatus.NO_SELECTIONS, schedule.status());
        assertEquals(List.of(), schedule.days());
    }

    @Test
    void returnsNoScheduledPerformancesWhenOnlyBandDataExists() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();

        SharedSchedule schedule = new GenerateSharedScheduleUseCase(performances, ratings).generate();

        assertEquals(SharedScheduleStatus.NO_SCHEDULED_PERFORMANCES, schedule.status());
        assertEquals("No scheduled performances are available yet.", schedule.message());
        assertEquals(List.of(), schedule.days());
    }

    private Performance performance(String bandName, int day, int startHour, int startMinute, int endHour, int endMinute) {
        return new Performance(
                new Band(bandName),
                new Stage("Stage " + bandName),
                LocalDateTime.of(2026, 7, day, startHour, startMinute),
                LocalDateTime.of(2026, 7, day, endHour, endMinute)
        );
    }

    private static final class FakePerformanceRepository implements PerformanceRepository {
        private final List<Performance> performances = new ArrayList<>();

        @Override
        public void save(Performance performance) {
            performances.add(performance);
        }

        @Override
        public void replaceAll(List<Performance> replacements) {
            performances.clear();
            performances.addAll(replacements);
        }

        @Override
        public List<Performance> findAll() {
            return List.copyOf(performances);
        }
    }

    private static final class FakeRatingRepository implements RatingRepository {
        private final Map<Key, Rating> ratings = new LinkedHashMap<>();

        @Override
        public void save(String userName, Band band, Rating rating) {
            ratings.put(new Key(userName, band), rating);
        }

        @Override
        public Optional<Rating> findByUserAndBand(String userName, Band band) {
            return Optional.ofNullable(ratings.get(new Key(userName, band)));
        }

        @Override
        public List<SavedRating> findAll() {
            return ratings.entrySet()
                    .stream()
                    .map(entry -> new SavedRating(entry.getKey().userName(), entry.getKey().band(), entry.getValue()))
                    .toList();
        }

        private record Key(String userName, Band band) {
        }
    }
}
