package be.wacken.planner.persistence;

import be.wacken.planner.domain.FoodOption;
import be.wacken.planner.domain.FoodOptionRepository;

import java.util.List;
import java.util.stream.Collectors;

public final class RoomFoodOptionRepository implements FoodOptionRepository {
    private final WackenDatabase database;
    private final RoomFoodOptionDao foodOptions;

    public RoomFoodOptionRepository(WackenDatabase database) {
        this.database = database;
        this.foodOptions = database.foodOptions();
    }

    @Override
    public void save(FoodOption foodOption) {
        foodOptions.save(new RoomFoodOption(foodOption.name()));
    }

    @Override
    public void replaceAll(List<FoodOption> replacements) {
        database.runInTransaction(() -> {
            foodOptions.deleteAll();
            foodOptions.saveAll(replacements.stream().map(foodOption -> new RoomFoodOption(foodOption.name())).collect(Collectors.toList()));
        });
    }

    @Override
    public List<FoodOption> findAll() {
        return foodOptions.findAll().stream().map(row -> new FoodOption(row.name)).collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return foodOptions.count() == 0;
    }
}
