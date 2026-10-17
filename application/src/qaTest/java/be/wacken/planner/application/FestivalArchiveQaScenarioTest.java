package be.wacken.planner.application;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalRepository;

final class FestivalArchiveQaScenarioTest {
    @Test void archiveActiveFestivalShowsArchivedStartState() {
        FestivalRepository repository=new FestivalRepository() {
            private final List<Festival> festivals=new ArrayList<>(List.of(Festival.active("wacken-2026","Wacken Open Air 2026")));
            public List<Festival> findAll(){return List.copyOf(festivals);}
            public void save(Festival festival){festivals.removeIf(f->f.id().equals(festival.id()));festivals.add(festival);}
        };
        FestivalStartState state=new ArchiveActiveFestivalUseCase(repository,f->repository.save(f.archive())).archiveActiveFestival();
        assertTrue(state.activeFestival().isEmpty());
        assertEquals(List.of(Festival.archived("wacken-2026","Wacken Open Air 2026")),state.archivedFestivals());
        assertTrue(state.archivedFestivalsReadOnly());
        assertTrue(state.canAddFestival());
    }
    // Complete creation/reuse/prefill scenarios run against PostgreSQL/PostgREST,
    // including the real Summer Breeze CSV, in verify-festival-creation.sh.
}
