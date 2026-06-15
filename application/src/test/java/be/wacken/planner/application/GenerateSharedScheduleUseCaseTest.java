package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.SavedRating;
import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.StageDistance;
import be.wacken.planner.domain.StageDistanceRepository;
import be.wacken.planner.domain.GroupDecisionStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

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
    void groupsAfterMidnightSlotsIntoPreviousFestivalDayUntilTwoAm() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Performance lateNight = performance("Sepultura", 31, 1, 0, 2, 0);
        performances.replaceAll(List.of(lateNight));
        ratings.save("sofie", lateNight.band(), Rating.of(5));

        SharedSchedule schedule = new GenerateSharedScheduleUseCase(performances, ratings).generate();

        assertEquals(List.of(LocalDate.of(2026, 7, 30)), schedule.days().stream().map(ScheduleDay::date).toList());
        assertEquals("Sepultura", schedule.days().get(0).slots().get(0).bandName());
    }

    @Test
    void appliesMvpWalkingDefaultsBetweenConsecutiveSelectedActs() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Performance heavy = performance("Sepultura", "Harder", 30, 18, 0, 19, 0);
        Performance louder = performance("Subway to Sally", "Louder", 30, 20, 0, 21, 0);
        Performance other = performance("Alcest", "Headbangers Stage", 30, 22, 0, 23, 0);
        Performance otherLater = performance("Skyline", "W:E:T Stage", 30, 23, 30, 23, 59);
        performances.replaceAll(List.of(heavy, louder, other, otherLater));
        ratings.save("sofie", heavy.band(), Rating.of(5));
        ratings.save("sofie", louder.band(), Rating.of(5));
        ratings.save("sofie", other.band(), Rating.of(5));
        ratings.save("sofie", otherLater.band(), Rating.of(5));

        List<TimelineSlot> slots = new GenerateSharedScheduleUseCase(performances, ratings).generate().days().get(0).slots();

        assertEquals(OptionalInt.of(5), slots.get(0).walkingMinutesToNext());
        assertEquals(OptionalInt.of(15), slots.get(1).walkingMinutesToNext());
        assertEquals(OptionalInt.of(5), slots.get(2).walkingMinutesToNext());
        assertEquals(OptionalInt.empty(), slots.get(3).walkingMinutesToNext());
    }

    @Test
    void usesStoredStageDistanceBeforeMvpDefault() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        FakeStageDistanceRepository distances = new FakeStageDistanceRepository();
        Performance heavy = performance("Sepultura", "Harder", 30, 18, 0, 19, 0);
        Performance louder = performance("Subway to Sally", "Louder", 30, 20, 0, 21, 0);
        performances.replaceAll(List.of(heavy, louder));
        distances.save(StageDistance.between(heavy.stage(), louder.stage(), 9));
        ratings.save("sofie", heavy.band(), Rating.of(5));
        ratings.save("sofie", louder.band(), Rating.of(5));

        TimelineSlot slot = new GenerateSharedScheduleUseCase(performances, ratings, distances)
                .generate()
                .days()
                .get(0)
                .slots()
                .get(0);

        assertEquals(OptionalInt.of(9), slot.walkingMinutesToNext());
    }

    @Test
    void includesLostAlternativeForResolvedConflicts() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Performance selected = performance("5th Avenue", 30, 18, 0, 19, 0);
        Performance lost = performance("Airbourne", 30, 18, 20, 19, 20);
        performances.replaceAll(List.of(selected, lost));
        ratings.save("sofie", selected.band(), Rating.of(5));
        ratings.save("dino", lost.band(), Rating.of(4));

        TimelineSlot slot = new GenerateSharedScheduleUseCase(performances, ratings).generate().days().get(0).slots().get(0);

        assertEquals("5th Avenue", slot.bandName());
        assertEquals(5, slot.rating());
        assertEquals(Optional.of("Airbourne"), slot.lostAlternativeBandName());
        assertEquals(Optional.of(4), slot.lostAlternativeRating());
        assertEquals(GroupDecisionStatus.GO, slot.decisionStatus());
    }

    @Test
    void usesHighestGroupRatingForWinnerAndLostAlternativeStars() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Performance selected = performance("5th Avenue", 30, 18, 0, 19, 0);
        Performance lost = performance("Airbourne", 30, 18, 20, 19, 20);
        performances.replaceAll(List.of(selected, lost));
        ratings.save("sofie", selected.band(), Rating.of(4));
        ratings.save("dino", selected.band(), Rating.of(5));
        ratings.save("sofie", lost.band(), Rating.of(3));
        ratings.save("dino", lost.band(), Rating.of(4));

        TimelineSlot slot = new GenerateSharedScheduleUseCase(performances, ratings).generate().days().get(0).slots().get(0);

        assertEquals(5, slot.rating());
        assertEquals(Optional.of(4), slot.lostAlternativeRating());
    }

    @Test
    void includesDecisionCandidatesForScheduleDetails() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Performance selected = performance("5th Avenue", 30, 18, 0, 19, 0);
        Performance lost = performance("Airbourne", 30, 18, 20, 19, 20);
        performances.replaceAll(List.of(selected, lost));
        ratings.save("sofie", selected.band(), Rating.of(5));
        ratings.save("dino", lost.band(), Rating.of(4));

        TimelineSlot slot = new GenerateSharedScheduleUseCase(performances, ratings).generate().days().get(0).slots().get(0);

        assertEquals(2, slot.candidates().size());
        assertEquals("5th Avenue", slot.candidates().get(0).bandName());
        assertEquals("Stage 5th Avenue", slot.candidates().get(0).stageName());
        assertEquals(5, slot.candidates().get(0).rating());
        assertEquals("CHOSEN", slot.candidates().get(0).status());
        assertEquals(true, slot.candidates().get(0).selected());
        assertEquals("Airbourne", slot.candidates().get(1).bandName());
        assertEquals("Stage Airbourne", slot.candidates().get(1).stageName());
        assertEquals(4, slot.candidates().get(1).rating());
        assertEquals("LOST ALTERNATIVE", slot.candidates().get(1).status());
        assertEquals(false, slot.candidates().get(1).selected());
    }

    @Test
    void marksTiedLostAlternativeAndListsItFirstAfterChosenAct() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Performance selected = performance("Future Palace", "Louder", 31, 13, 45, 14, 45);
        Performance tied = performance("Grand Magus", "Headbangers Stage", 31, 14, 0, 14, 45);
        Performance other = performance("Metaklapa", "Wackinger Stage", 31, 14, 15, 15, 0);
        performances.replaceAll(List.of(selected, tied, other));
        ratings.save("dino", selected.band(), Rating.of(4));
        ratings.save("sofie", selected.band(), Rating.of(3));
        ratings.save("dino", tied.band(), Rating.of(4));
        ratings.save("sofie", tied.band(), Rating.of(3));
        ratings.save("sofie", other.band(), Rating.of(3));

        TimelineSlot slot = new GenerateSharedScheduleUseCase(performances, ratings).generate().days().get(0).slots().get(0);

        assertEquals("Future Palace", slot.bandName());
        assertEquals(Optional.of("Grand Magus"), slot.lostAlternativeBandName());
        assertEquals(List.of("Future Palace", "Grand Magus", "Metaklapa"), slot.candidates().stream().map(ScheduleDecisionCandidate::bandName).toList());
        assertEquals("⚖ TIED ALTERNATIVE", slot.candidates().get(1).status());
        assertEquals("NOT SELECTED", slot.candidates().get(2).status());
    }

    @Test
    void doesNotMarkLostAlternativeAsTieWhenWinnerHasBroaderGroupSupport() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Performance selected = performance("Any given Day", "Headbangers Stage", 31, 16, 0, 16, 45);
        Performance lost = performance("Danko Jones", "Harder", 31, 15, 45, 16, 45);
        performances.replaceAll(List.of(lost, selected));
        ratings.save("sofie", lost.band(), Rating.of(4));
        ratings.save("dino", selected.band(), Rating.of(4));
        ratings.save("sofie", selected.band(), Rating.of(3));

        TimelineSlot slot = new GenerateSharedScheduleUseCase(performances, ratings).generate().days().get(0).slots().get(0);

        assertEquals("Any given Day", slot.bandName());
        assertEquals(Optional.of("Danko Jones"), slot.lostAlternativeBandName());
        assertEquals("LOST ALTERNATIVE", slot.candidates().get(1).status());
    }

    @Test
    void limitsDetailCandidatesToPerformancesThatDirectlyOverlapTheSelectedAct() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Performance selected = performance("Def Leppard", "Harder", 30, 18, 0, 19, 0);
        Performance directAlternative = performance("Direct Alternative", "Louder", 30, 18, 20, 19, 20);
        Performance chainedAlternative = performance("Chained Alternative", "Faster", 30, 19, 15, 20, 15);
        performances.replaceAll(List.of(selected, directAlternative, chainedAlternative));
        ratings.save("sofie", selected.band(), Rating.of(5));
        ratings.save("dino", directAlternative.band(), Rating.of(4));
        ratings.save("jan", chainedAlternative.band(), Rating.of(4));

        TimelineSlot slot = new GenerateSharedScheduleUseCase(performances, ratings).generate().days().get(0).slots().get(0);

        assertEquals(List.of("Def Leppard", "Direct Alternative"), slot.candidates().stream().map(ScheduleDecisionCandidate::bandName).toList());
        assertEquals(Optional.of("Direct Alternative"), slot.lostAlternativeBandName());
    }

    @Test
    void hidesVetoedRejectedActsFromScheduleDecisionCandidates() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Performance selected = performance("Danko Jones", "Harder", 31, 15, 45, 16, 45);
        Performance visibleAlternative = performance("Any given Day", "Headbangers Stage", 31, 16, 0, 16, 45);
        Performance vetoedAlternative = performance("Vetoed Act", "Louder", 31, 16, 0, 16, 45);
        performances.replaceAll(List.of(selected, visibleAlternative, vetoedAlternative));
        ratings.save("sofie", selected.band(), Rating.of(4));
        ratings.save("dino", visibleAlternative.band(), Rating.of(3));
        ratings.save("sofie", vetoedAlternative.band(), Rating.of(1));

        TimelineSlot slot = new GenerateSharedScheduleUseCase(performances, ratings).generate().days().get(0).slots().get(0);

        assertEquals(List.of("Danko Jones", "Any given Day"), slot.candidates().stream().map(ScheduleDecisionCandidate::bandName).toList());
        assertEquals(Optional.of("Any given Day"), slot.lostAlternativeBandName());
    }

    @Test
    void selectsDefLeppardWhenItOnlyOverlapsAnotherSelectedActOutsideTheMiddleThirtyMinutes() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Performance earlierMustSee = performance("Yngwie Malmsteen", "Faster", 31, 18, 0, 19, 0);
        Performance edgeOverlap = performance("Bridge Act", "Louder", 31, 18, 20, 19, 20);
        Performance defLeppard = performance("Def Leppard", "Harder", 31, 18, 45, 19, 45);
        performances.replaceAll(List.of(earlierMustSee, edgeOverlap, defLeppard));
        ratings.save("sofie", earlierMustSee.band(), Rating.of(5));
        ratings.save("dino", earlierMustSee.band(), Rating.of(5));
        ratings.save("sofie", defLeppard.band(), Rating.of(5));
        ratings.save("dino", defLeppard.band(), Rating.of(5));

        SharedSchedule schedule = new GenerateSharedScheduleUseCase(performances, ratings).generate();

        assertEquals(
                List.of("Yngwie Malmsteen", "Def Leppard"),
                schedule.days().get(0).slots().stream().map(TimelineSlot::bandName).toList()
        );
    }

    @Test
    void marksOptionalSlotsWhenConflictResolutionIsOptional() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Performance selected = performance("5th Avenue", 30, 18, 0, 19, 0);
        Performance lost = performance("Airbourne", 30, 18, 20, 19, 20);
        performances.replaceAll(List.of(selected, lost));
        ratings.save("sofie", selected.band(), Rating.of(3));
        ratings.save("dino", selected.band(), Rating.of(3));
        ratings.save("sofie", lost.band(), Rating.of(3));

        TimelineSlot slot = new GenerateSharedScheduleUseCase(performances, ratings).generate().days().get(0).slots().get(0);

        assertEquals("5th Avenue", slot.bandName());
        assertEquals(3, slot.rating());
        assertEquals(GroupDecisionStatus.OPTIONAL, slot.decisionStatus());
        assertEquals(true, slot.optional());
    }

    @Test
    void skipsConflictWhenEveryCandidateIsVetoBlocked() {
        FakePerformanceRepository performances = new FakePerformanceRepository();
        FakeRatingRepository ratings = new FakeRatingRepository();
        Performance first = performance("5th Avenue", 30, 18, 0, 19, 0);
        Performance second = performance("Airbourne", 30, 18, 20, 19, 20);
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
        return performance(bandName, "Stage " + bandName, day, startHour, startMinute, endHour, endMinute);
    }

    private Performance performance(String bandName, String stageName, int day, int startHour, int startMinute, int endHour, int endMinute) {
        return new Performance(
                new Band(bandName),
                new Stage(stageName),
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

    private static final class FakeStageDistanceRepository implements StageDistanceRepository {
        private final Map<Key, StageDistance> distances = new LinkedHashMap<>();

        @Override
        public void save(StageDistance distance) {
            distances.put(new Key(distance.from(), distance.to()), distance);
        }

        @Override
        public void replaceAll(List<StageDistance> replacements) {
            distances.clear();
            for (StageDistance distance : replacements) {
                save(distance);
            }
        }

        @Override
        public Optional<StageDistance> findBetween(Stage from, Stage to) {
            if (from.equals(to)) {
                return Optional.of(StageDistance.between(from, to, 0));
            }
            StageDistance forward = distances.get(new Key(from, to));
            if (forward != null) {
                return Optional.of(forward);
            }
            return Optional.ofNullable(distances.get(new Key(to, from)));
        }

        @Override
        public List<StageDistance> findAll() {
            return List.copyOf(distances.values());
        }

        private record Key(Stage from, Stage to) {
        }
    }
}
