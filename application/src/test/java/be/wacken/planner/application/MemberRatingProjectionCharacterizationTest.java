package be.wacken.planner.application;

import be.wacken.planner.domain.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

final class MemberRatingProjectionCharacterizationTest {
    private static final Band BAND = new Band("Band");

    @Test
    void listDetailAndCsvKeepPositiveIncludingVetoExactNamesAndStableMemberOrder() {
        RatingRepository ratings = mock(RatingRepository.class);
        when(ratings.findAll()).thenReturn(List.of(
                new SavedRating("zoe", BAND, Rating.of(5)),
                new SavedRating("Alice", BAND, Rating.of(4)),
                new SavedRating("alice", BAND, Rating.of(3)),
                new SavedRating("zero", BAND, Rating.of(0)),
                new SavedRating("one", BAND, Rating.of(1)),
                new SavedRating("wrong-case", new Band("band"), Rating.of(5))));
        var expected = List.of(new PersonRatingStars("Alice", 4), new PersonRatingStars("alice", 3),
                new PersonRatingStars("one", 1), new PersonRatingStars("zoe", 5));
        BandRepository bands = bands();
        PerformanceRepository performances = performances();

        assertEquals(expected, new ListBandsUseCase(bands, performances, ratings, "Alice").listBands().get(0).personRatings());
        assertEquals(expected, new ShowBandDetailUseCase(bands, ratings).showBand("Alice", "Band", MusicLinks.none()).orElseThrow().personRatings());
        String csv = new ExportRatingsCsvUseCase(bands, performances, ratings, mock(RealRatingRepository.class)).export("Alice");
        assertEquals("Alice=4;alice=3;one=1;zoe=5", csv.lines().skip(1).findFirst().orElseThrow().split(",", -1)[4]);
        assertEquals(9, csv.lines().findFirst().orElseThrow().split(",", -1).length);
    }

    @Test
    void scheduleCandidateKeepsPositiveMembersAndExactCanonicalName() {
        RatingRepository ratings = mock(RatingRepository.class);
        when(ratings.findAll()).thenReturn(List.of(new SavedRating("zoe", BAND, Rating.of(5)),
                new SavedRating("Alice", BAND, Rating.of(4)), new SavedRating("alice", BAND, Rating.of(3)),
                new SavedRating("zero", BAND, Rating.of(0)), new SavedRating("wrong-case", new Band("band"), Rating.of(5))));
        var candidate = new GenerateSharedScheduleUseCase(performances(), ratings).generate().days().get(0).slots().get(0).candidates().get(0);
        assertEquals(List.of(new PersonRatingStars("Alice", 4), new PersonRatingStars("alice", 3), new PersonRatingStars("zoe", 5)), candidate.personRatings());
    }

    @Test
    void everyConsumerKeepsExplicitFestivalAndMemberValuesWithoutOtherRatingRolesOrWrites() {
        FestivalPlanningRatingRepository planning = mock(FestivalPlanningRatingRepository.class);
        FestivalLineupRepository lineups = mock(FestivalLineupRepository.class);
        FestivalRepository festivals = mock(FestivalRepository.class);
        when(planning.findByFestival("first")).thenReturn(List.of(
                new SavedFestivalPlanningRating("group", "Alice", "first", BAND, Rating.of(4)),
                new SavedFestivalPlanningRating("group", "Bob", "first", BAND, Rating.of(3))));
        when(planning.findByFestival("next")).thenReturn(List.of(
                new SavedFestivalPlanningRating("group", "Alice", "next", BAND, Rating.of(5)),
                new SavedFestivalPlanningRating("group", "Bob", "next", BAND, Rating.of(0))));
        for (String festival : List.of("first", "next")) {
            when(lineups.findByFestival(festival)).thenReturn(List.of(new FestivalLineupEntry(festival, BAND, "Band")));
            RatingRepository scoped = new FestivalPlanningRatingView(festivals, lineups, planning, "group", festival);
            List<PersonRatingStars> expected = festival.equals("first")
                    ? List.of(new PersonRatingStars("Alice", 4), new PersonRatingStars("Bob", 3))
                    : List.of(new PersonRatingStars("Alice", 5));
            assertEquals(expected, new ListBandsUseCase(bands(), performances(), scoped, "Alice").listBands().get(0).personRatings());
            assertEquals(expected, new ShowBandDetailUseCase(bands(), scoped).showBand("Alice", "Band", MusicLinks.none()).orElseThrow().personRatings());
            assertEquals(expected, new GenerateSharedScheduleUseCase(performances(), scoped).generate().days().get(0).slots().get(0).candidates().get(0).personRatings());
            String groupCsv = new ExportRatingsCsvUseCase(bands(), performances(), scoped, mock(RealRatingRepository.class))
                    .export("Alice").lines().skip(1).findFirst().orElseThrow().split(",", -1)[4];
            assertEquals(festival.equals("first") ? "Alice=4;Bob=3" : "Alice=5", groupCsv);
            assertEquals(expected, new ListArchivedFestivalBandsUseCase(lineups, planning,
                    mock(PersonalBandRatingHistoryRepository.class), mock(RealRatingRepository.class), performances())
                    .listBands("Alice", festival).get(0).personRatings());
        }
        verify(planning, never()).findAll();
        verify(planning, never()).save(anyString(), anyString(), anyString(), any(), any());
        verify(lineups, never()).saveAllForFestival(anyString(), anyList());
    }

    private static BandRepository bands() {
        BandRepository bands = mock(BandRepository.class);
        when(bands.findAll()).thenReturn(List.of(BAND));
        when(bands.findByName("Band")).thenReturn(Optional.of(BAND));
        return bands;
    }

    private static PerformanceRepository performances() {
        PerformanceRepository performances = mock(PerformanceRepository.class);
        when(performances.findAll()).thenReturn(List.of(new Performance(BAND, new Stage("Stage"),
                LocalDateTime.parse("2026-07-30T18:00"), LocalDateTime.parse("2026-07-30T19:00"))));
        return performances;
    }
}
