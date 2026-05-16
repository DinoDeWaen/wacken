package be.wacken.planner.domain;

import java.util.List;

public interface FoodOptionRepository {
    void save(FoodOption foodOption);

    void replaceAll(List<FoodOption> foodOptions);

    List<FoodOption> findAll();
}
