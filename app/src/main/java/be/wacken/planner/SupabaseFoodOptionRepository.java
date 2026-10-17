package be.wacken.planner;

import java.io.IOException;
import java.util.List;

import be.wacken.planner.domain.FoodOption;
import be.wacken.planner.domain.FoodOptionRepository;

final class SupabaseFoodOptionRepository implements FoodOptionRepository {
    private final SupabaseMasterDataClient client;

    SupabaseFoodOptionRepository(SupabaseMasterDataClient client) {
        this.client = client;
    }

    @Override
    public void save(FoodOption foodOption) {
    }

    @Override
    public void replaceAll(List<FoodOption> foodOptions) {
    }

    @Override
    public List<FoodOption> findAll() {
        try {
            return client.foodOptions();
        } catch (IOException error) {
            throw new SupabaseSyncException(error.getMessage(), error);
        }
    }
}
