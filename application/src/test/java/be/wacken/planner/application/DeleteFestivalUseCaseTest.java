package be.wacken.planner.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalRepository;

public final class DeleteFestivalUseCaseTest {
    @Test
    public void deletesOnlyTheSelectedFestival() {
        Festivals festivals = new Festivals();
        Gateway gateway = new Gateway();

        Festival deleted = new DeleteFestivalUseCase(festivals, gateway).delete("wacken-2026");

        assertEquals("wacken-2026", deleted.id());
        assertEquals("wacken-2026", gateway.deletedId);
    }

    @Test
    public void rejectsMissingFestivalWithoutCallingGateway() {
        Gateway gateway = new Gateway();
        assertThrows(IllegalStateException.class,
                () -> new DeleteFestivalUseCase(new Festivals(), gateway).delete("unknown"));
        assertEquals(null, gateway.deletedId);
    }

    private static final class Festivals implements FestivalRepository {
        @Override public List<Festival> findAll() {
            return List.of(Festival.active("wacken-2026", "Wacken Open Air 2026"),
                    Festival.archived("other", "Other"));
        }
        @Override public void save(Festival festival) { throw new UnsupportedOperationException(); }
    }

    private static final class Gateway implements DeleteFestivalUseCase.Gateway {
        String deletedId;
        @Override public void delete(Festival festival) { deletedId = festival.id(); }
    }
}
