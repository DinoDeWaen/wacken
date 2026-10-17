package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.FoodOption;
import be.wacken.planner.domain.FoodOptionRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryFoodOptionRepositoryTest {
    @Test
    void storesFoodOptions() {
        FoodOptionRepository repository = new InMemoryFoodOptionRepository();
        FoodOption food = new FoodOption("Pizza");

        repository.save(food);

        assertEquals(List.of(food), repository.findAll());
    }
}
