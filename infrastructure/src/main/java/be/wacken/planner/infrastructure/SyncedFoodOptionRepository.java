package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.FoodOption;
import be.wacken.planner.domain.FoodOptionRepository;

import java.util.List;
import java.util.Objects;

public final class SyncedFoodOptionRepository implements FoodOptionRepository {
    private final FoodOptionRepository cache;
    private final FoodOptionRepository source;

    public SyncedFoodOptionRepository(FoodOptionRepository cache, FoodOptionRepository source) {
        this.cache = Objects.requireNonNull(cache, "cache must not be null");
        this.source = Objects.requireNonNull(source, "source must not be null");
    }

    public void syncSourceToCache() {
        cache.replaceAll(source.findAll());
    }

    @Override
    public void save(FoodOption foodOption) {
        source.save(foodOption);
        cache.save(foodOption);
    }

    @Override
    public void replaceAll(List<FoodOption> foodOptions) {
        source.replaceAll(foodOptions);
        cache.replaceAll(foodOptions);
    }

    @Override
    public List<FoodOption> findAll() {
        return cache.findAll();
    }
}
