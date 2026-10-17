package be.wacken.planner;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.StageDistance;
import be.wacken.planner.domain.StageDistanceRepository;

final class SupabaseStageDistanceRepository implements StageDistanceRepository {
    private final SupabaseMasterDataClient client;

    SupabaseStageDistanceRepository(SupabaseMasterDataClient client) {
        this.client = client;
    }

    @Override
    public void save(StageDistance distance) {
    }

    @Override
    public void replaceAll(List<StageDistance> distances) {
    }

    @Override
    public Optional<StageDistance> findBetween(Stage from, Stage to) {
        return findAll().stream()
                .filter(distance -> distance.from().equals(from) && distance.to().equals(to))
                .findFirst();
    }

    @Override
    public List<StageDistance> findAll() {
        try {
            return client.stageDistances();
        } catch (IOException error) {
            throw new SupabaseSyncException(error.getMessage(), error);
        }
    }
}
