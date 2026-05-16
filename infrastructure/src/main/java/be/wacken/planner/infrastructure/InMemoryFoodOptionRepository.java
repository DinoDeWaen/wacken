package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.FoodOption;
import be.wacken.planner.domain.FoodOptionRepository;

import java.util.ArrayList;
import java.util.List;

public final class InMemoryFoodOptionRepository implements FoodOptionRepository {
    private final List<FoodOption> foodOptions = new ArrayList<>();

    @Override
    public void save(FoodOption foodOption) {
        foodOptions.add(foodOption);
    }

    @Override
    public void replaceAll(List<FoodOption> replacements) {
        foodOptions.clear();
        foodOptions.addAll(replacements);
    }

    @Override
    public List<FoodOption> findAll() {
        return new ArrayList<>(foodOptions);
    }
}
