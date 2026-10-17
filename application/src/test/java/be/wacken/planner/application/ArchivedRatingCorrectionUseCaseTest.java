package be.wacken.planner.application;

import static org.junit.jupiter.api.Assertions.*;
import be.wacken.planner.domain.*;
import java.util.*;
import org.junit.jupiter.api.Test;

final class ArchivedRatingCorrectionUseCaseTest {
    private final Band band=new Band("Band");
    private final Festival festival=Festival.archived("archived","Archived");
    private final List<String> calls=new ArrayList<>();
    private final ArchivedRatingCorrectionGateway gateway=new ArchivedRatingCorrectionGateway(){
        public ArchivedRatingReview review(String user,String festival,Band target,ArchivedRatingKind kind){
            calls.add("review");return new ArchivedRatingReview(user,festival,target,kind,3,7,Optional.empty());
        }
        public RatingCorrection correct(String operation,ArchivedRatingReview review,int value){
            calls.add(operation+":"+value);return null;
        }
        public List<RatingCorrection> history(String user,String festival,Band target){return List.of();}
    };
    @Test void reviewsOnlyOwnArchivedFestivalTarget(){
        var review=useCase(festival).review("member","archived",band,ArchivedRatingKind.AFTER);
        assertEquals("member",review.userName());assertEquals(7,review.revision());assertEquals(List.of("review"),calls);
    }
    @Test void activeFestivalCannotEnterCorrectionMode(){
        assertThrows(DomainValidationException.class,()->useCase(Festival.active("archived","Active")).review("member","archived",band,ArchivedRatingKind.AFTER));
        assertTrue(calls.isEmpty());
    }
    @Test void anotherMembersReviewedValueCannotBeSubmitted(){
        var expected=new ArchivedRatingReview("other","archived",band,ArchivedRatingKind.AFTER,3,7,Optional.empty());
        assertThrows(DomainValidationException.class,()->useCase(festival).correct("member",UUID.randomUUID().toString(),expected,2));
        assertTrue(calls.isEmpty());
    }
    @Test void afterResetIsOutsideCorrectionScopeWhilePlanningCanBeUnrated(){
        var useCase=useCase(festival);
        var after=new ArchivedRatingReview("member","archived",band,ArchivedRatingKind.AFTER,3,7,Optional.empty());
        assertThrows(DomainValidationException.class,()->useCase.correct("member",UUID.randomUUID().toString(),after,0));
        var planning=new ArchivedRatingReview("member","archived",band,ArchivedRatingKind.PLANNING,3,7,Optional.empty());
        String operation=UUID.randomUUID().toString();
        useCase.correct("member",operation,planning,0);
        assertEquals(List.of(operation+":0"),calls);
    }
    @Test void missingIdentityOrOutsideLineupNeverReachesGateway(){
        var useCase=useCase(festival);
        assertThrows(DomainValidationException.class,()->useCase.review("","archived",band,ArchivedRatingKind.AFTER));
        assertThrows(DomainValidationException.class,()->useCase.review("member","archived",new Band("Outside"),ArchivedRatingKind.AFTER));
        assertTrue(calls.isEmpty());
    }
    @Test void malformedOperationAndInvalidValueNeverReachGateway(){
        var expected=new ArchivedRatingReview("member","archived",band,ArchivedRatingKind.AFTER,3,7,Optional.empty());
        assertThrows(DomainValidationException.class,()->useCase(festival).correct("member","bad",expected,2));
        assertThrows(DomainValidationException.class,()->useCase(festival).correct("member",UUID.randomUUID().toString(),expected,6));
        assertTrue(calls.isEmpty());
    }
    @Test void aDifferentOwnersReviewCannotBeShownAsMyCorrectionTarget(){
        ArchivedRatingCorrectionGateway wrong=new ArchivedRatingCorrectionGateway(){
            public ArchivedRatingReview review(String user,String festival,Band target,ArchivedRatingKind kind){return new ArchivedRatingReview("other",festival,target,kind,3,7,Optional.empty());}
            public RatingCorrection correct(String operation,ArchivedRatingReview expected,int value){fail();return null;}
            public List<RatingCorrection> history(String user,String festival,Band target){return List.of();}
        };
        assertThrows(DomainValidationException.class,()->useCase(festival,wrong).review("member","archived",band,ArchivedRatingKind.AFTER));
    }
    private ArchivedRatingCorrectionUseCase useCase(Festival current){return useCase(current,gateway);}
    private ArchivedRatingCorrectionUseCase useCase(Festival current,ArchivedRatingCorrectionGateway selectedGateway){
        FestivalRepository festivals=new FestivalRepository(){
            public List<Festival> findAll(){return List.of(current);}
            public void save(Festival ignored){fail("Correction cannot change lifecycle");}
        };
        FestivalLineupRepository lineup=new FestivalLineupRepository(){
            public List<FestivalLineupEntry> findByFestival(String id){return List.of(new FestivalLineupEntry(id,band,band.name()));}
            public void saveAllForFestival(String id,List<FestivalLineupEntry> entries){fail();}
        };
        return new ArchivedRatingCorrectionUseCase(festivals,lineup,selectedGateway);
    }
}
