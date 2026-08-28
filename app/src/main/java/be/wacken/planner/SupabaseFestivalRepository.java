package be.wacken.planner;

import java.io.IOException;
import java.util.List;

import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalRepository;

final class SupabaseFestivalRepository implements FestivalRepository {
    private final SupabaseMasterDataClient client;

    SupabaseFestivalRepository(SupabaseMasterDataClient client) {
        this.client = client;
    }

    @Override
    public List<Festival> findAll() {
        try {
            return client.festivals();
        } catch (IOException error) {
            throw new SupabaseSyncException(error.getMessage(), error);
        }
    }

    @Override
    public void save(Festival festival) {
        try {
            client.saveFestival(festival);
        } catch (IOException error) {
            throw new SupabaseSyncException(error.getMessage(), error);
        }
    }
}
