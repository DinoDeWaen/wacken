package be.wacken.planner;

import java.io.IOException;
import java.util.List;

import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;

final class SupabasePerformanceRepository implements PerformanceRepository {
    private final SupabaseMasterDataClient client;

    SupabasePerformanceRepository(SupabaseMasterDataClient client) {
        this.client = client;
    }

    @Override
    public void save(Performance performance) {
    }

    @Override
    public void replaceAll(List<Performance> performances) {
    }

    @Override
    public List<Performance> findAll() {
        try {
            return client.performances();
        } catch (IOException error) {
            throw new SupabaseSyncException(error.getMessage(), error);
        }
    }
}
