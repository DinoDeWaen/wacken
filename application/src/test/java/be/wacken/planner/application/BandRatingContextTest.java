package be.wacken.planner.application;

import be.wacken.planner.domain.*;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

final class BandRatingContextTest {
    private final Band band=new Band("Known",Optional.of("Canonical biography"),Optional.empty(),Optional.of("https://youtube.test/known"),Optional.empty());
    private final BandRepository bands=mock(BandRepository.class);
    private final FestivalRepository festivals=mock(FestivalRepository.class);
    private final FestivalLineupRepository lineups=mock(FestivalLineupRepository.class);
    private final FestivalPlanningRatingRepository planning=mock(FestivalPlanningRatingRepository.class);
    private final FestivalAfterRatingRepository after=mock(FestivalAfterRatingRepository.class);
    private final PersonalBandRatingRepository current=mock(PersonalBandRatingRepository.class);
    @BeforeEach void data() {
        when(bands.findByName("Known")).thenReturn(Optional.of(band));
        when(festivals.findAll()).thenReturn(List.of(Festival.active("new","Renamed Current"),Festival.archived("old","Archive")));
        when(lineups.findByFestival("new")).thenReturn(List.of(new FestivalLineupEntry("new",new Band("Known"),"Upload")));
        when(planning.findByUserFestivalAndBand("member","new",band)).thenReturn(Optional.of(Rating.of(5)));
        when(after.findByUserFestivalAndBand("member","new",band)).thenReturn(Optional.of(new FestivalAfterRating("11111111-1111-4111-8111-111111111111","member","new",band,Rating.of(3),Instant.parse("2026-10-01T12:00:00Z"))));
        when(current.findByUserAndBand("member",band)).thenReturn(Optional.of(new PersonalBandRating("member",band,Rating.of(4),"old","Archive snapshot","source",Instant.parse("2026-08-01T12:00:00Z"))));
    }
    private ViewBandRatingContextUseCase useCase(){return new ViewBandRatingContextUseCase(bands,festivals,lineups,planning,after,current);}
    @Test void separatesEstablishedPlanningAndAfterWithOwnerFestivalSourceAndDates() {
        var context=useCase().show("member","new","Known").orElseThrow();
        assertEquals(4,context.current().orElseThrow().rating().value());
        assertEquals(5,context.planning().orElseThrow().value());assertEquals(3,context.after().orElseThrow().rating().value());
        assertEquals(band,context.band());assertTrue(String.join("\n",context.lines()).contains("Renamed Current"));
        assertTrue(String.join("\n",context.lines()).contains("Archive snapshot"));
    }
    @Test void missingSelectedAfterNeverBorrowsAnotherFestivalOrAnotherMember() {
        when(after.findByUserFestivalAndBand("member","new",band)).thenReturn(Optional.empty());
        var context=useCase().show("member","new","Known").orElseThrow();
        assertTrue(context.after().isEmpty());assertEquals(4,context.current().orElseThrow().rating().value());
        verify(after,never()).findByUser(anyString());verify(after,never()).findByFestival(anyString());
        assertTrue(useCase().show("other","new","Known").orElseThrow().current().isEmpty());
    }
    @Test void retainedSourceSurvivesDeletionAndRemovedLineupDoesNotExposeStaleFestivalValues() {
        when(festivals.findAll()).thenReturn(List.of(Festival.active("new","Current")));
        assertTrue(String.join("\n",useCase().show("member","new","Known").orElseThrow().lines()).contains("deleted"));
        when(lineups.findByFestival("new")).thenReturn(List.of());
        var context=useCase().show("member","new","Known").orElseThrow();
        assertTrue(context.planning().isEmpty());assertTrue(context.after().isEmpty());assertTrue(context.current().isPresent());
    }
    @Test void archivedHistoryKeepsCaseDistinctCanonicalIdentities() {
        var history=mock(PersonalBandRatingHistoryRepository.class);
        when(lineups.findByFestival("old")).thenReturn(List.of(
                new FestivalLineupEntry("old",new Band("Known"),"Known"),
                new FestivalLineupEntry("old",new Band("known"),"known")));
        var result=new ViewArchivedFestivalHistoryUseCase(festivals,lineups,planning,history).show("member","old");
        assertEquals(2,result.bandNames().size());
    }
    @Test void scheduleUsesOnlyBoundFestivalPlanningRegardlessOfMetadataAndOtherRatings() {
        var performances=mock(PerformanceRepository.class);
        var time=java.time.LocalDateTime.parse("2026-08-01T18:00:00");
        when(performances.findAll()).thenReturn(List.of(new Performance(band,new Stage("Main"),time,time.plusHours(1),"new")));
        when(planning.findByFestival("new")).thenReturn(List.of(new SavedFestivalPlanningRating("group","member","new",new Band("Known"),Rating.of(5))));
        var view=new FestivalPlanningRatingView(festivals,lineups,planning,"group","new");
        var schedule=new GenerateSharedScheduleUseCase(performances,view).generate();
        assertEquals(SharedScheduleStatus.GENERATED,schedule.status());
        verifyNoInteractions(after,current);
    }
    @Test void canonicalMetadataCannotHidePlanningGroupVotesOrPerformanceContext() {
        RatingRepository ratings=mock(RatingRepository.class);
        when(ratings.findAll()).thenReturn(List.of(new SavedRating("member",new Band("Known"),Rating.of(5))));
        var detail=new ShowBandDetailUseCase(bands,ratings).showBand("member","Known",new MusicLinks(Optional.empty(),Optional.empty())).orElseThrow();
        assertEquals(1,detail.personRatings().size());
    }
}
