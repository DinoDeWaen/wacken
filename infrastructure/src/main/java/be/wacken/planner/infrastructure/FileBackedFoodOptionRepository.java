package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.FoodOption;
import be.wacken.planner.domain.FoodOptionRepository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class FileBackedFoodOptionRepository implements FoodOptionRepository {
    private final FileBackedStorage storage;

    public FileBackedFoodOptionRepository(Path directory) {
        this.storage = new FileBackedStorage(directory, "food-options.tsv");
    }

    @Override
    public void save(FoodOption foodOption) {
        List<FoodOption> foodOptions = new ArrayList<>(findAll());
        foodOptions.add(foodOption);
        storage.writeRows(foodOptions.stream()
                .map(savedFoodOption -> List.of(savedFoodOption.name()))
                .toList());
    }

    @Override
    public List<FoodOption> findAll() {
        return storage.readRows().stream()
                .map(row -> new FoodOption(row.get(0)))
                .toList();
    }
}
