package be.wacken.planner.domain;

import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;

final class RatingCorrectionTest {
    private final Band band=new Band("Band");
    private final Instant original=Instant.parse("2026-08-01T12:00:00Z");
    private final Instant corrected=Instant.parse("2026-10-17T12:00:00Z");
    @Test void keepsOriginalContextSeparateFromCorrectionTime(){
        var audit=audit(ArchivedRatingKind.AFTER,3);
        assertEquals(Optional.of(original),audit.originalRecordedAt());assertEquals(corrected,audit.correctedAt());
        assertEquals(4,audit.previousValue());assertEquals(3,audit.newValue());
    }
    @Test void planningMayBecomeUnratedWithoutDeletingAfterHistory(){
        assertEquals(0,audit(ArchivedRatingKind.PLANNING,0).newValue());
        assertThrows(DomainValidationException.class,()->audit(ArchivedRatingKind.AFTER,0));
    }
    @Test void auditRequiresOperationActorAndReviewedRevision(){
        assertThrows(DomainValidationException.class,()->new RatingCorrection("bad","member","festival","Festival",band,ArchivedRatingKind.AFTER,4,3,0,Optional.of(original),corrected));
        assertThrows(DomainValidationException.class,()->new RatingCorrection(UUID.randomUUID().toString(),"","festival","Festival",band,ArchivedRatingKind.AFTER,4,3,0,Optional.of(original),corrected));
        assertThrows(DomainValidationException.class,()->new RatingCorrection(UUID.randomUUID().toString(),"member","festival","Festival",band,ArchivedRatingKind.AFTER,4,3,-2,Optional.of(original),corrected));
    }
    @Test void missingOriginalDateRemainsUnknown(){
        var audit=new RatingCorrection(UUID.randomUUID().toString(),"member","festival",null,band,ArchivedRatingKind.AFTER,0,3,-1,null,corrected);
        assertTrue(audit.originalRecordedAt().isEmpty());assertEquals("",audit.festivalName());
    }
    @Test void reviewValidatesItsIdentityValueAndRevision(){
        var review=new ArchivedRatingReview("member","festival",band,ArchivedRatingKind.AFTER,0,-1,null);
        assertTrue(review.recordedAt().isEmpty());assertEquals(-1,review.revision());
        assertThrows(DomainValidationException.class,()->new ArchivedRatingReview("","festival",band,ArchivedRatingKind.AFTER,3,0,Optional.empty()));
        assertThrows(DomainValidationException.class,()->new ArchivedRatingReview("member","festival",band,ArchivedRatingKind.AFTER,3,-2,Optional.empty()));
        assertThrows(DomainValidationException.class,()->new ArchivedRatingReview("member","festival",band,ArchivedRatingKind.AFTER,6,0,Optional.empty()));
    }
    private RatingCorrection audit(ArchivedRatingKind kind,int value){
        return new RatingCorrection(UUID.randomUUID().toString(),"member","festival","Festival",band,kind,4,value,0,Optional.of(original),corrected);
    }
}
