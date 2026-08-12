package be.wacken.planner.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalRepository;

final class FestivalStartUseCaseTest {
    @Test
    void showsActiveFestivalAsEditableStartState() {
        FestivalRepository repository = new FakeFestivalRepository(List.of(Festival.active("wacken-2026", "Wacken Open Air 2026")));

        FestivalStartState state = new ShowFestivalStartUseCase(repository).show();

        assertTrue(state.hasActiveFestival());
        assertEquals("Wacken Open Air 2026", state.activeFestival().orElseThrow().name());
        assertFalse(state.showArchivedFestivals());
        assertFalse(state.canAddFestival());
    }

    @Test
    void archivesActiveFestivalAndLeavesNoActiveFestival() {
        FakeFestivalRepository repository = new FakeFestivalRepository(List.of(Festival.active("wacken-2026", "Wacken Open Air 2026")));

        FestivalStartState state = new ArchiveActiveFestivalUseCase(repository).archiveActiveFestival();

        assertTrue(state.activeFestival().isEmpty());
        assertTrue(repository.findAll().get(0).isArchived());
        assertTrue(state.showArchivedFestivals());
        assertTrue(state.canAddFestival());
    }

    @Test
    void showsArchivedFestivalsReadOnlyWhenNoFestivalIsActive() {
        Festival archived = Festival.archived("wacken-2026", "Wacken Open Air 2026");
        FestivalRepository repository = new FakeFestivalRepository(List.of(archived));

        FestivalStartState state = new ShowFestivalStartUseCase(repository).show();

        assertFalse(state.hasActiveFestival());
        assertTrue(state.showArchivedFestivals());
        assertTrue(state.canAddFestival());
        assertEquals(List.of(archived), state.archivedFestivals());
        assertTrue(state.archivedFestivalsReadOnly());
    }

    private static final class FakeFestivalRepository implements FestivalRepository {
        private final List<Festival> festivals = new ArrayList<>();

        FakeFestivalRepository(List<Festival> festivals) {
            this.festivals.addAll(festivals);
        }

        @Override
        public List<Festival> findAll() {
            return List.copyOf(festivals);
        }

        @Override
        public void save(Festival festival) {
            for (int index = 0; index < festivals.size(); index++) {
                if (festivals.get(index).id().equals(festival.id())) {
                    festivals.set(index, festival);
                    return;
                }
            }
            festivals.add(festival);
        }
    }
}
