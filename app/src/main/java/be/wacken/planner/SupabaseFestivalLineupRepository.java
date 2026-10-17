package be.wacken.planner;

import java.io.IOException;
import java.util.List;

import be.wacken.planner.domain.FestivalLineupEntry;
import be.wacken.planner.domain.FestivalLineupRepository;

final class SupabaseFestivalLineupRepository implements FestivalLineupRepository, SyncingFestivalLineupRepository.Source {
    private final SupabaseMasterDataClient client;

    SupabaseFestivalLineupRepository(SupabaseMasterDataClient client) {
        this.client = client;
    }

    @Override
    public void saveAllForFestival(String festivalId, List<FestivalLineupEntry> entries) {
        try {
            client.saveFestivalLineup(festivalId, entries);
        } catch (IOException error) {
            throw new SupabaseSyncException(error.getMessage(), error);
        }
    }

    @Override
    public List<FestivalLineupEntry> findByFestival(String festivalId) {
        try {
            return client.festivalLineupEntries(festivalId);
        } catch (IOException error) {
            throw new SupabaseSyncException(error.getMessage(), error);
        }
    }

    @Override
    public List<FestivalLineupEntry> findAll() {
        try {
            return client.festivalLineupEntries();
        } catch (IOException error) {
            throw new SupabaseSyncException(error.getMessage(), error);
        }
    }
}
