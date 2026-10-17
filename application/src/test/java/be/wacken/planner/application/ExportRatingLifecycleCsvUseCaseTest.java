package be.wacken.planner.application;

import be.wacken.planner.domain.*;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

final class ExportRatingLifecycleCsvUseCaseTest {
    private final BandRepository bands=mock(BandRepository.class);
    private final FestivalRepository festivals=mock(FestivalRepository.class);
    private final FestivalLineupRepository lineups=mock(FestivalLineupRepository.class);
    private final FestivalPlanningRatingRepository planning=mock(FestivalPlanningRatingRepository.class);
    private final FestivalAfterRatingRepository after=mock(FestivalAfterRatingRepository.class);
    private final PersonalBandRatingRepository current=mock(PersonalBandRatingRepository.class);
    private final PersonalBandRatingHistoryRepository history=mock(PersonalBandRatingHistoryRepository.class);
    private final RatingCorrectionRepository corrections=mock(RatingCorrectionRepository.class);
    private final Band band=new Band("Known, \"band\"");
    private String export(){return new ExportRatingLifecycleCsvUseCase(bands,festivals,lineups,planning,after,current,history,corrections).export("member");}
    @BeforeEach void data() {
        when(bands.findAll()).thenReturn(List.of(band));
        when(festivals.findAll()).thenReturn(List.of(Festival.active("new","Renamed festival")));
        when(lineups.findByFestival("new")).thenReturn(List.of(new FestivalLineupEntry("new",band,"Known")));
        when(planning.findByUserFestivalAndBand("member","new",band)).thenReturn(Optional.of(Rating.of(5)));
        when(after.findByUserFestivalAndBand("member","new",band)).thenReturn(Optional.of(new FestivalAfterRating(
                "11111111-1111-4111-8111-111111111111","member","new",band,Rating.of(3),Instant.parse("2026-10-01T12:00:00Z"))));
        when(current.findByUser("member")).thenReturn(List.of(new PersonalBandRating("member",band,Rating.of(4),
                "deleted","Original source","event",Instant.parse("2026-08-02T12:00:00Z"))));
    }
    @Test void exportsSeparateValuesWithExplicitOwnerFestivalSourceAndVersionAndEscaping() {
        String csv=export();
        assertTrue(csv.startsWith("schema_version,record_type,user_id,band_name,"));
        assertTrue(csv.contains("2,ESTABLISHED_BAND,member,\"Known, \"\"band\"\"\""));
        assertTrue(csv.contains(",4,deleted,Original source,event,"));
        assertTrue(csv.contains("FESTIVAL_PLANNING,member,\"Known, \"\"band\"\"\",new,Renamed festival,ACTIVE,,5,"));
        assertTrue(csv.contains("FESTIVAL_AFTER,member,\"Known, \"\"band\"\"\",new,Renamed festival,ACTIVE,,3,"));
        var rows=FestivalCsvReader.reviewRows("lifecycle.csv",csv);
        assertEquals(3,rows.size());
        assertTrue(rows.stream().allMatch(row->row.value("band_name").equals(band.name())));
        assertTrue(csv.contains("2026-08-02T12:00:00Z"));assertTrue(csv.contains("2026-10-01T12:00:00Z"));
    }
    @Test void missingAfterRemainsBlankAndOtherMembersNeverLeak() {
        when(after.findByUserFestivalAndBand("member","new",band)).thenReturn(Optional.empty());
        when(current.findByUser("member")).thenReturn(List.of(new PersonalBandRating("other",band,Rating.of(1),"x","Private","x",Instant.EPOCH)));
        String csv=export();assertFalse(csv.contains("Private"));assertFalse(csv.contains("ESTABLISHED_BAND,"));
        assertTrue(csv.contains("FESTIVAL_AFTER,member,\"Known, \"\"band\"\"\",new,Renamed festival,ACTIVE,,,,"));
        verify(after,never()).findByFestival(anyString());
    }
    @Test void filtersPrivateRowsFromEveryRepositoryAndKeepsExplicitResetDistinctFromMissing() {
        when(history.findByUser("member")).thenReturn(List.of(new PersonalBandRatingEvent("private-history","other",
                new Band("Private"),Optional.of("private"),Rating.of(1),Instant.EPOCH)));
        when(corrections.findByUser("member")).thenReturn(List.of(new RatingCorrection(
                "22222222-2222-4222-8222-222222222222","other","private","Secret",new Band("Private"),
                ArchivedRatingKind.PLANNING,1,2,1,Optional.empty(),Instant.EPOCH)));
        when(after.findByUserFestivalAndBand("member","new",band)).thenReturn(Optional.of(new FestivalAfterRating(
                "11111111-1111-4111-8111-111111111111","member","new",band,Rating.of(0),Instant.EPOCH)));
        var rows=FestivalCsvReader.reviewRows("lifecycle.csv",export());
        assertTrue(rows.stream().noneMatch(row->row.value("user_id").equals("other")));
        var result=rows.stream().filter(row->row.value("record_type").equals("FESTIVAL_AFTER")).findFirst().orElseThrow();
        assertEquals("0",result.value("rating"));assertEquals("",result.value("recorded_at"));
        assertFalse(export().contains("Private"));
        assertThrows(DomainValidationException.class,()->new ExportRatingLifecycleCsvUseCase(bands,festivals,lineups,
                planning,after,current,history,corrections).export(""));
    }
    @Test void historyOnlyBandAndDeletedFestivalKeepOriginalDateAndSnapshotSeparateFromCorrection() {
        Band removed=new Band("Removed");
        when(history.findByUser("member")).thenReturn(List.of(new PersonalBandRatingEvent("history","member",removed,
                Optional.empty(),Rating.of(2),Instant.EPOCH,Optional.of("Deleted festival"))));
        when(corrections.findByUser("member")).thenReturn(List.of(new RatingCorrection(
                "22222222-2222-4222-8222-222222222222","member","deleted","Old name",removed,ArchivedRatingKind.AFTER,
                2,4,1,Optional.of(Instant.parse("2026-07-01T12:00:00Z")),Instant.parse("2026-09-01T12:00:00Z"))));
        String csv=export();
        assertTrue(csv.contains("HISTORY_AFTER,member,Removed,,,UNKNOWN,Deleted festival,2,"));
        assertFalse(csv.contains("1970-01-01"));assertTrue(csv.contains("CORRECTION_AFTER,member,Removed,deleted,,DELETED,Old name"));
        assertTrue(csv.contains("2026-09-01T12:00:00Z,2026-07-01T12:00:00Z,2,4,"));
        assertEquals(csv,export());
    }
}
