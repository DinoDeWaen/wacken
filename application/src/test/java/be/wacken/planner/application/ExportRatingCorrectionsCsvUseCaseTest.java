package be.wacken.planner.application;

import static org.junit.jupiter.api.Assertions.*;
import be.wacken.planner.domain.*;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;

final class ExportRatingCorrectionsCsvUseCaseTest {
    @Test void exportsOwnBeforeAfterActorAndSeparateOriginalCorrectionDatesWithCsvEscaping(){
        var mine=audit("member",Optional.of(Instant.parse("2026-08-01T12:00:00Z")));
        var other=audit("other",Optional.empty());
        String csv=new ExportRatingCorrectionsCsvUseCase(repository(List.of(mine,other))).export("member");
        assertTrue(csv.startsWith("operation_id,festival_id,festival_name,band_name,rating_kind,previous_value,new_value,user,original_recorded_at,corrected_at\n"));
        assertTrue(csv.contains("\"Festival, one\",\"Band \"\"Quoted\"\"\",AFTER,4,3,member,2026-08-01T12:00:00Z,2026-10-17T12:00:00Z"));
        assertFalse(csv.contains(",other,"));
        assertEquals(2,csv.lines().count());
    }
    @Test void unknownOriginalDateStaysEmpty(){
        String csv=new ExportRatingCorrectionsCsvUseCase(repository(List.of(audit("member",Optional.empty())))).export("member");
        assertTrue(csv.contains(",member,,2026-10-17T12:00:00Z"));
    }
    private RatingCorrection audit(String user,Optional<Instant> original){
        return new RatingCorrection(UUID.randomUUID().toString(),user,"festival","Festival, one",new Band("Band \"Quoted\""),
            ArchivedRatingKind.AFTER,4,3,0,original,Instant.parse("2026-10-17T12:00:00Z"));
    }
    private RatingCorrectionRepository repository(List<RatingCorrection> rows){
        return new RatingCorrectionRepository(){
            public List<RatingCorrection> findByUser(String user){return rows;}
            public List<RatingCorrection> findByUserFestivalAndBand(String user,String festival,Band band){return rows;}
        };
    }
}
