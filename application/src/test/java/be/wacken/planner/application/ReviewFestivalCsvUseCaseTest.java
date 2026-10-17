package be.wacken.planner.application;

import be.wacken.planner.domain.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

final class ReviewFestivalCsvUseCaseTest {
    private final BandRepository bands=mock(BandRepository.class);
    private final StageRepository stages=mock(StageRepository.class);
    private final PerformanceRepository performances=mock(PerformanceRepository.class);
    private final StageDistanceRepository distances=mock(StageDistanceRepository.class);
    private final FoodOptionRepository food=mock(FoodOptionRepository.class);
    private final FestivalRepository festivals=mock(FestivalRepository.class);
    private final FestivalLineupRepository lineups=mock(FestivalLineupRepository.class);
    private final Band known=new Band("Known",Optional.of("Golden"),Optional.empty(),Optional.of("https://youtube.test/known"),Optional.empty());
    private ReviewFestivalCsvUseCase useCase;
    @BeforeEach void baseline() {
        when(bands.findAll()).thenReturn(List.of(known,new Band("Other festival")));
        when(stages.findAll()).thenReturn(List.of(new Stage("Main")));
        when(performances.findAll()).thenReturn(List.of());when(distances.findAll()).thenReturn(List.of());when(food.findAll()).thenReturn(List.of());
        when(festivals.findAll()).thenReturn(List.of(Festival.active("active","Active")));
        when(lineups.findByFestival("active")).thenReturn(List.of(new FestivalLineupEntry("active",known,"Known upload")));
        useCase=new ReviewFestivalCsvUseCase(bands,stages,performances,distances,food,festivals,lineups,Runnable::run);
    }
    private FestivalCsvFiles files(String csv) {return new FestivalCsvFiles(csv,"","","","");}
    private Set<String> validKeys(ReviewFestivalCsvUseCase.Review review) {
        return review.changes().stream().filter(ReviewFestivalCsvUseCase.Change::valid).map(ReviewFestivalCsvUseCase.Change::key).collect(java.util.stream.Collectors.toSet());
    }
    @Test void reviewShowsNewRemovedAndInvalidRowsWithoutWriting() {
        var review=useCase.review(files("band_id,name\nnew,New\nbad,\n"));
        assertTrue(review.changes().stream().anyMatch(r->r.identity().equals("New")&&r.operation().equals("ADD")&&r.valid()));
        assertTrue(review.changes().stream().anyMatch(r->!r.valid()));
        assertFalse(review.changes().stream().anyMatch(r->r.operation().equals("REMOVE")),"Invalid rows must not imply removals");
        verify(bands,never()).replaceAll(anyList());verify(lineups,never()).saveAllForFestival(anyString(),anyList());
    }
    @Test void acceptingOneNewBandKeepsRejectedLineupRemovalAndAllCanonicalMetadata() {
        var review=useCase.review(files("band_id,name\nnew,New\n"));
        var addition=review.changes().stream().filter(r->r.identity().equals("New")).findFirst().orElseThrow();
        assertTrue(useCase.apply(review,Set.of(addition.key())).success());
        verify(bands).replaceAll(argThat(rows->rows.contains(known)&&rows.contains(new Band("Other festival"))&&rows.contains(new Band("New"))));
        verify(lineups).saveAllForFestival(eq("active"),argThat(rows->rows.size()==2&&rows.stream().anyMatch(r->r.uploadedDisplayName().equals("Known upload"))));
    }
    @Test void acceptedLineupRemovalNeverDeletesTheCanonicalBand() {
        var review=useCase.review(files("band_id,name\n"));
        assertEquals(1,review.changes().size());var removal=review.changes().get(0);assertEquals("REMOVE",removal.operation());
        assertTrue(useCase.apply(review,Set.of(removal.key())).success());
        verify(bands).replaceAll(List.of(known,new Band("Other festival")));
        verify(lineups).saveAllForFestival("active",List.of());
    }
    @Test void rejectedOrInvalidRowsNeverWriteAndStaleBaselineRequiresFreshReview() {
        var review=useCase.review(files("band_id,name\nx,New\n"));
        assertFalse(useCase.apply(review,Set.of("forged")).success());
        when(stages.findAll()).thenReturn(List.of(new Stage("Changed")));
        assertFalse(useCase.apply(review,Set.of(review.changes().get(0).key())).success());
        verify(bands,never()).replaceAll(anyList());
        var invalid=useCase.review(files("wrong,title\nx,New\n"));
        assertTrue(invalid.changes().stream().noneMatch(ReviewFestivalCsvUseCase.Change::valid));
        assertFalse(useCase.apply(invalid,Set.of(invalid.changes().get(0).key())).success());
    }
    @Test void emptyCompanionSelectionRetainsDataAndSelectedDependencyConflictsBlockAllWrites() {
        var csv=new FestivalCsvFiles("band_id,name\nk,Known\nn,New\n","stage_id,name\ns,Main\n",
            "performance_id,band_id,stage_id,start_at,end_at\np,n,s,2027-08-01T18:00:00,2027-08-01T19:00:00\n","","");
        var review=useCase.review(csv);
        var performance=review.changes().stream().filter(r->r.kind().equals("PERFORMANCE")).findFirst().orElseThrow();
        assertFalse(useCase.apply(review,Set.of(performance.key())).success(),"Rejected band addition leaves a missing reference");
        verify(bands,never()).replaceAll(anyList());
    }

    @Test void acceptedMissingMetadataEnrichmentCannotEraseOrReplaceCanonicalLinks() {
        var review=useCase.review(files("band_id,name,biography,youtube_url,image_url\nk,Known,Overwrite,https://bad.test/replacement,https://images.test/new\n"));
        var change=review.changes().get(0);assertEquals("CHANGE",change.operation());
        assertEquals("Golden",change.after().get("biography"));assertEquals("https://youtube.test/known",change.after().get("youtube"));
        assertTrue(useCase.apply(review,validKeys(review)).success());
        verify(bands).replaceAll(argThat(rows->rows.stream().anyMatch(b->b.name().equals("Known")&&b.biography().equals(known.biography())
            &&b.youtubeUrl().equals(known.youtubeUrl())&&b.imageUrl().equals(Optional.of("https://images.test/new")))));
    }

    @Test void allFiveTypesCanBeReviewedAndOtherFestivalPerformancesArePreserved() {
        var archive=new Performance(new Band("Other festival"),new Stage("Main"),java.time.LocalDateTime.parse("2026-08-01T18:00:00"),java.time.LocalDateTime.parse("2026-08-01T19:00:00"),"archive");
        when(performances.findAll()).thenReturn(List.of(archive));
        var review=useCase.review(new FestivalCsvFiles("band_id,name\nk,Known\n","stage_id,name\ns,Main\nt,Side\n",
            "performance_id,band_id,stage_id,start_at,end_at\np,k,s,2027-08-01T18:00:00,2027-08-01T19:00:00\n",
            "from_stage_id,to_stage_id,walking_minutes\ns,t,7\n","food_id,name,near_stage_id\nf,Pizza,t\n"));
        assertTrue(review.changes().stream().allMatch(ReviewFestivalCsvUseCase.Change::valid));
        assertTrue(useCase.apply(review,validKeys(review)).success());
        verify(performances).replaceAll(argThat(rows->rows.size()==2&&rows.contains(archive)&&rows.stream().anyMatch(p->p.festivalId().equals("active"))));
        verify(distances).replaceAll(List.of(StageDistance.between(new Stage("Main"),new Stage("Side"),7)));
        verify(food).replaceAll(List.of(new FoodOption("Pizza")));
    }

    @Test void invalidDatesNumbersAmbiguousIdsAndDuplicateIdentitiesAreRowsThatCannotBeAccepted() {
        var review=useCase.review(new FestivalCsvFiles("band_id,name\nKnown,First\nKnown,Second\n",
            "stage_id,name\ns,Main\n","performance_id,band_id,stage_id,start_at,end_at\np,Known,s,invalid,also-invalid\n",
            "from_stage_id,to_stage_id,walking_minutes\ns,s,no-number\n","food_id,name,near_stage_id\nf,Pizza,unknown\n"));
        assertTrue(review.changes().stream().anyMatch(c->c.errors().stream().anyMatch(e->e.contains("ambiguous"))));
        assertTrue(review.changes().stream().noneMatch(ReviewFestivalCsvUseCase.Change::valid));
        var badDate=useCase.review(new FestivalCsvFiles("band_id,name\nk,Known\n","",
            "performance_id,band_id,stage_id,start_at,end_at\np,k,Main,invalid,2027-08-01T19:00:00\n","",""));
        assertFalse(badDate.changes().get(0).valid());
        var duplicate=useCase.review(files("band_id,name\na,Known\nb,Known\n"));
        assertEquals(2,duplicate.changes().size());assertTrue(duplicate.changes().stream().noneMatch(ReviewFestivalCsvUseCase.Change::valid));
    }

    @Test void selectedOverlapFailsBeforeWritesButRejectingOneShowAllowsTheOther() {
        var review=useCase.review(new FestivalCsvFiles("band_id,name\nk,Known\n","",
            "performance_id,band_id,stage_id,start_at,end_at\np,k,Main,2027-08-01T18:00:00,2027-08-01T19:00:00\nq,k,Main,2027-08-01T18:30:00,2027-08-01T19:30:00\n","",""));
        assertFalse(useCase.apply(review,validKeys(review)).success());verify(bands,never()).replaceAll(anyList());
        assertTrue(useCase.apply(review,Set.of(review.changes().get(0).key())).success());
    }

    @Test void malformedFileDoesNotInferRemovalWhileIndependentValidStageCanBeAccepted() {
        var review=useCase.review(new FestivalCsvFiles("band_id,name\nx,\"unterminated","stage_id,name\ns,Main\nt,Side\n","","",""));
        assertTrue(review.changes().stream().anyMatch(c->!c.valid()));
        assertTrue(useCase.apply(review,validKeys(review)).success());
        verify(bands).replaceAll(List.of(known,new Band("Other festival")));
        verify(stages).replaceAll(List.of(new Stage("Main"),new Stage("Side")));
    }

    @Test void extraOrMissingCsvCellsCannotBeSilentlyAccepted() {
        for(String csv:List.of("band_id,name\nx,New,unexpected\n","band_id,name\nx\n")) {
            var review=useCase.review(files(csv));
            assertTrue(review.changes().stream().noneMatch(ReviewFestivalCsvUseCase.Change::valid));
            assertTrue(review.changes().stream().noneMatch(r->r.operation().equals("REMOVE")));
        }
    }

    @Test void stageNamesContainingArrowSeparatorsCannotCollapseUnchangedDistances() {
        var first=StageDistance.between(new Stage("A → B"),new Stage("C"),7);
        var second=StageDistance.between(new Stage("A"),new Stage("B → C"),9);
        when(stages.findAll()).thenReturn(List.of(new Stage("A → B"),new Stage("C"),new Stage("A"),new Stage("B → C")));
        when(distances.findAll()).thenReturn(List.of(first,second));
        var review=useCase.review(files("band_id,name\nk,Known\nn,New\n"));
        assertTrue(useCase.apply(review,validKeys(review)).success());
        verify(distances).replaceAll(argThat(rows->rows.size()==2&&rows.containsAll(List.of(first,second))));
    }
}
