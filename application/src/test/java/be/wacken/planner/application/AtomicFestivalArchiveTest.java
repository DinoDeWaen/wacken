package be.wacken.planner.application;

import static org.junit.jupiter.api.Assertions.*;
import be.wacken.planner.domain.*;
import java.util.*;
import org.junit.jupiter.api.Test;

final class AtomicFestivalArchiveTest {
    @Test void delegatesTheCompleteArchiveBeforeReadingAcknowledgedState() {
        Festival[] stored={Festival.active("festival","Festival")};
        FestivalRepository repository=repository(stored);
        List<Festival> calls=new ArrayList<>();
        FestivalStartState result=new ArchiveActiveFestivalUseCase(repository,festival->{
            assertTrue(stored[0].isActive());calls.add(festival);stored[0]=festival.archive();
        }).archiveActiveFestival();
        assertEquals(List.of(Festival.active("festival","Festival")),calls);
        assertFalse(result.hasActiveFestival());
    }
    @Test void failedTransactionCannotPublishStatusOnlySuccess() {
        Festival[] stored={Festival.active("festival","Festival")};
        assertThrows(IllegalStateException.class,()->new ArchiveActiveFestivalUseCase(repository(stored),
                festival->{throw new IllegalStateException("Promotion failed");}).archiveActiveFestival());
        assertTrue(stored[0].isActive());
    }
    @Test void missingActiveFestivalNeverReachesTransaction() {
        Festival[] stored={Festival.active("festival","Festival").archive()};
        assertThrows(DomainValidationException.class,()->new ArchiveActiveFestivalUseCase(repository(stored),
                festival->{fail("No archive expected");}).archiveActiveFestival());
    }
    private FestivalRepository repository(Festival[] stored) {
        return new FestivalRepository(){
            public List<Festival> findAll(){return List.of(stored[0]);}
            public void save(Festival ignored){fail("Application must not perform a status-only save");}
        };
    }
}
