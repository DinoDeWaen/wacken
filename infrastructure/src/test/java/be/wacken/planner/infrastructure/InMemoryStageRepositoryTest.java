package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.StageRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryStageRepositoryTest {
    @Test
    void storesAndRetrievesStagesByName() {
        StageRepository repository = new InMemoryStageRepository();
        Stage stage = new Stage("Faster");

        repository.save(stage);

        assertEquals(Optional.of(stage), repository.findByName("Faster"));
        assertEquals(List.of(stage), repository.findAll());
    }
}
