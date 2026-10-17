package be.wacken.planner.domain;

import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

final class PersonalAndFestivalRatingStateTest {
    private final Band band=new Band("One canonical band");
    private final Instant date=Instant.parse("2026-10-17T14:00:00Z");
    @Test void establishedPreferenceRequiresPositiveValueAndRetainsItsSourceAfterFestivalDeletion(){
        var preference=new PersonalBandRating("me",band,Rating.of(4),"deleted-festival","Festival snapshot","source-event",date);
        assertEquals("me",preference.userName());assertEquals(band,preference.band());assertEquals(4,preference.rating().value());
        assertEquals("deleted-festival",preference.sourceFestivalId());assertEquals("Festival snapshot",preference.sourceFestivalName());
        assertEquals("source-event",preference.sourceEventId());assertEquals(date,preference.establishedAt());
        assertThrows(DomainValidationException.class,()->new PersonalBandRating("me",band,Rating.of(0),"festival","Festival","event",date));
    }
    @Test void anEstablishedOpinionCannotLoseItsUserOrSourceProvenance(){
        assertThrows(DomainValidationException.class,()->new PersonalBandRating(null,band,Rating.of(4),"festival","Festival","event",date));
        assertThrows(DomainValidationException.class,()->new PersonalBandRating(" ",band,Rating.of(4),"festival","Festival","event",date));
        assertThrows(DomainValidationException.class,()->new PersonalBandRating("me",band,Rating.of(4),null,"Festival","event",date));
        assertThrows(DomainValidationException.class,()->new PersonalBandRating("me",band,Rating.of(4)," ","Festival","event",date));
        assertThrows(DomainValidationException.class,()->new PersonalBandRating("me",band,Rating.of(4),"festival","Festival",null,date));
        assertThrows(DomainValidationException.class,()->new PersonalBandRating("me",band,Rating.of(4),"festival","Festival"," ",date));
        assertEquals("",new PersonalBandRating("me",band,Rating.of(4),"festival",null,"event",date).sourceFestivalName());
    }
    @Test void afterRatingAllowsDurableResetButRequiresAnExplicitUserFestivalAndOperation(){
        String operation=UUID.randomUUID().toString();
        var reset=new FestivalAfterRating(operation," me "," festival ",band,Rating.of(0),date);
        assertEquals(operation,reset.operationId());assertEquals("me",reset.userName());assertEquals("festival",reset.festivalId());
        assertEquals(band,reset.band());assertEquals(0,reset.rating().value());assertEquals(date,reset.recordedAt());assertFalse(reset.requiresReview());
        assertThrows(IllegalArgumentException.class,()->new FestivalAfterRating("bad","me","festival",band,Rating.of(4),date));
        assertThrows(DomainValidationException.class,()->new FestivalAfterRating(operation," ","festival",band,Rating.of(4),date));
        assertThrows(DomainValidationException.class,()->new FestivalAfterRating(operation,"me",null,band,Rating.of(4),date));
    }
    @Test void recoveredHistoryRemainsMarkedForExplicitReview(){
        var recovered=new FestivalAfterRating(UUID.randomUUID().toString(),"me","festival",band,Rating.of(3),date,true);
        assertTrue(recovered.requiresReview());
        assertNotEquals(recovered,new FestivalAfterRating(recovered.operationId(),"other","festival",band,Rating.of(3),date,true));
    }
}

