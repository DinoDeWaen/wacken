package be.wacken.planner.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalRepository;

final class RenameActiveFestivalUseCaseTest {
    @Test
    void renamesActiveFestivalAndPreservesIdentity() {
        FakeFestivalRepository festivals = new FakeFestivalRepository(List.of(
                Festival.active("summer-breeze-2027", "Summer Breeze"),
                Festival.archived("wacken-2026", "Wacken Open Air 2026")
        ));

        RenameActiveFestivalResult result = new RenameActiveFestivalUseCase(festivals)
                .rename("  Summer Breeze 2027  ");

        assertTrue(result.success());
        assertEquals("Renamed active festival.", result.message());
        assertEquals(Festival.active("summer-breeze-2027", "Summer Breeze 2027"), festivals.findAll().get(0));
        assertEquals(Festival.archived("wacken-2026", "Wacken Open Air 2026"), festivals.findAll().get(1));
    }

    @Test
    void rejectsBlankActiveFestivalName() {
        FakeFestivalRepository festivals = new FakeFestivalRepository(List.of(Festival.active("summer-breeze-2027", "Summer Breeze")));

        RenameActiveFestivalResult result = new RenameActiveFestivalUseCase(festivals).rename("   ");

        assertFalse(result.success());
        assertEquals("Festival name must not be blank.", result.message());
        assertEquals(Festival.active("summer-breeze-2027", "Summer Breeze"), festivals.findAll().get(0));
    }

    @Test
    void failsWhenNoFestivalIsActive() {
        FakeFestivalRepository festivals = new FakeFestivalRepository(List.of(Festival.archived("wacken-2026", "Wacken Open Air 2026")));

        RenameActiveFestivalResult result = new RenameActiveFestivalUseCase(festivals).rename("Summer Breeze 2027");

        assertFalse(result.success());
        assertEquals("No active festival is available to rename.", result.message());
        assertEquals(List.of(Festival.archived("wacken-2026", "Wacken Open Air 2026")), festivals.findAll());
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
            festivals.removeIf(existing -> existing.id().equals(festival.id()));
            festivals.add(0, festival);
        }
    }
}
