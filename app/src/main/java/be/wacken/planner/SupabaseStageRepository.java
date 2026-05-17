package be.wacken.planner;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.StageRepository;

final class SupabaseStageRepository implements StageRepository {
    private final SupabaseMasterDataClient client;

    SupabaseStageRepository(SupabaseMasterDataClient client) {
        this.client = client;
    }

    @Override
    public void save(Stage stage) {
    }

    @Override
    public void replaceAll(List<Stage> stages) {
    }

    @Override
    public Optional<Stage> findByName(String name) {
        return findAll().stream().filter(stage -> stage.name().equals(name)).findFirst();
    }

    @Override
    public List<Stage> findAll() {
        try {
            return client.stages();
        } catch (IOException error) {
            throw new SupabaseSyncException(error.getMessage(), error);
        }
    }
}
