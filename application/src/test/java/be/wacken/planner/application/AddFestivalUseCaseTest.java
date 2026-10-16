package be.wacken.planner.application;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Festival;

final class AddFestivalUseCaseTest {
    @Test void validatesNameBeforeCallingPersistence() {
        AtomicInteger writes=new AtomicInteger();
        AddFestivalUseCase useCase=new AddFestivalUseCase((g,u,f,b)->{writes.incrementAndGet();return AddFestivalResult.added(0,1,0);});
        assertFalse(useCase.addFestival("group","user","id","  ",List.of(new Band("Act"))).success());
        assertEquals(0,writes.get());
    }
    @Test void validatesIdentitySessionAndEmptyLineupBeforePersistence() {
        AtomicInteger writes=new AtomicInteger();
        AddFestivalUseCase useCase=new AddFestivalUseCase((g,u,f,b)->{writes.incrementAndGet();return AddFestivalResult.added(0,1,0);});
        assertFalse(useCase.addFestival("group","user","","Festival",List.of(new Band("Act"))).success());
        assertFalse(useCase.addFestival("","user","id","Festival",List.of(new Band("Act"))).success());
        assertFalse(useCase.addFestival("group","user","id","Festival",List.of()).success());
        assertEquals(0,writes.get());
    }
    @Test void sendsOneCompleteCreationWithDistinctExactNames() {
        AtomicInteger writes=new AtomicInteger();
        AddFestivalUseCase useCase=new AddFestivalUseCase((g,u,f,b)->{
            writes.incrementAndGet();
            assertEquals(Festival.active("id","Summer Breeze"),f);
            assertEquals(List.of(new Band("Any Given Day"),new Band("Any given Day")),b);
            return AddFestivalResult.added(1,1,1);
        });
        AddFestivalResult result=useCase.addFestival("group","user","id"," Summer Breeze ",
                List.of(new Band("Any Given Day"),new Band("Any given Day"),new Band("Any Given Day")));
        assertTrue(result.success());
        assertEquals(1,writes.get());
        assertEquals(1,result.prefilledRatings());
    }
    @Test void returnsAuthoritativeConflictWithoutUsingCachedState() {
        AddFestivalUseCase useCase=new AddFestivalUseCase((g,u,f,b)->AddFestivalResult.failure("Archive the active festival before adding the next one."));
        assertFalse(useCase.addFestival("group","user","id","Festival",List.of(new Band("Act"))).success());
    }
}
