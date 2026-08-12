package be.wacken.planner.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalRepository;

final class FestivalArchiveQaScenarioTest {
    @Test
    void archiveActiveFestivalShowsArchivedStartState() {
        // Given a festival is active
        FakeFestivalRepository repository = new FakeFestivalRepository(List.of(Festival.active("wacken-2026", "Wacken Open Air 2026")));

        // When a user archives the festival
        FestivalStartState state = new ArchiveActiveFestivalUseCase(repository).archiveActiveFestival();

        // Then the festival becomes read-only history and no festival is active
        assertTrue(state.activeFestival().isEmpty());
        assertEquals(List.of(Festival.archived("wacken-2026", "Wacken Open Air 2026")), state.archivedFestivals());
        assertTrue(state.archivedFestivalsReadOnly());
        assertTrue(state.canAddFestival());
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
