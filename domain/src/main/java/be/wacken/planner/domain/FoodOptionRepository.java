package be.wacken.planner.domain;

import java.util.List;

public interface FoodOptionRepository {
    void save(FoodOption foodOption);

    List<FoodOption> findAll();
}
