package be.wacken.planner.application;

import static org.junit.jupiter.api.Assertions.*;
import be.wacken.planner.domain.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;

final class FestivalAfterRatingIsolationTest {
    private final Band band=new Band("Shared band");
    private final List<Festival> festivals=new ArrayList<>(List.of(Festival.active("new","New festival"),Festival.archived("old","Old festival")));
    private final Map<String,FestivalAfterRating> data=new HashMap<>();
    private final FestivalRepository festivalRepository=new FestivalRepository(){
        public List<Festival> findAll(){return List.copyOf(festivals);}
        public void save(Festival f){throw new AssertionError("Rating cannot change festival status");}
    };
    private final FestivalLineupRepository lineups=new FestivalLineupRepository(){
        public List<FestivalLineupEntry> findByFestival(String id){return List.of(new FestivalLineupEntry(id,band,band.name()));}
        public void saveAllForFestival(String id,List<FestivalLineupEntry> entries){throw new AssertionError("Rating cannot change lineup");}
    };
    private final FestivalAfterRatingRepository after=new FestivalAfterRatingRepository(){
        public void save(FestivalAfterRating rating){data.put(key(rating.userName(),rating.festivalId()),rating);}
        public Optional<FestivalAfterRating> findByUserFestivalAndBand(String user,String festival,Band b){return Optional.ofNullable(data.get(key(user,festival)));}
        public List<FestivalAfterRating> findByUser(String user){return data.values().stream().filter(r->r.userName().equals(user)).toList();}
        public List<FestivalAfterRating> findByFestival(String festival){return data.values().stream().filter(r->r.festivalId().equals(festival)).toList();}
    };

    @Test void givenAnotherFestivalAndMemberWhenAfterRatingChangesThenOnlyTheSelectedKeyChanges(){
        data.put(key("me","old"),rating("me","old",4));data.put(key("friend","new"),rating("friend","new",5));
        assertTrue(useCase().rateBand("me","new",band,3).success());
        assertEquals(3,data.get(key("me","new")).rating().value());
        assertEquals(4,data.get(key("me","old")).rating().value());
        assertEquals(5,data.get(key("friend","new")).rating().value());
        assertEquals(3,data.size());
    }
    @Test void resetIsAnExplicitFestivalValueWithANewOperationAndNoCrossFestivalFallback(){
        assertTrue(useCase().rateBand("me","new",band,3).success());
        String first=data.get(key("me","new")).operationId();
        assertTrue(useCase().rateBand("me","new",band,0).success());
        assertEquals(0,data.get(key("me","new")).rating().value());
        assertNotEquals(first,data.get(key("me","new")).operationId());
        assertEquals(Instant.parse("2026-10-17T14:00:00Z"),data.get(key("me","new")).recordedAt());
    }
    @Test void archivedOrMissingFestivalAndInvalidRatingCannotWrite(){
        assertFalse(useCase().rateBand("me","old",band,3).success());
        assertFalse(useCase().rateBand("me","missing",band,3).success());
        assertFalse(useCase().rateBand("me","new",band,6).success());
        assertTrue(data.isEmpty());
    }
    @Test void aBandOutsideTheSelectedLineupCannotAcquireARating(){
        assertFalse(useCase().rateBand("me","new",new Band("Outside"),3).success());
        assertTrue(data.isEmpty());
    }
    @Test void scopedLegacyReadContractShowsOnlyTheActiveFestivalIncludingReset(){
        data.put(key("me","old"),rating("me","old",5));
        var scoped=new ActiveFestivalAfterRatingRepository(festivalRepository,after);
        assertTrue(scoped.findByUserAndBand("me",band).isEmpty());
        useCase().rateBand("me","new",band,0);
        assertEquals(0,scoped.findByUserAndBand("me",band).orElseThrow().value());
        assertEquals(1,scoped.findAll().size());
    }
    private RecordFestivalAfterRatingUseCase useCase(){
        return new RecordFestivalAfterRatingUseCase(festivalRepository,lineups,after,
                Clock.fixed(Instant.parse("2026-10-17T14:00:00Z"),ZoneOffset.UTC));
    }
    private FestivalAfterRating rating(String user,String festival,int value){
        return new FestivalAfterRating(UUID.randomUUID().toString(),user,festival,band,Rating.of(value),Instant.parse("2026-10-16T12:00:00Z"));
    }
    private String key(String user,String festival){return user+"|"+festival;}
}
