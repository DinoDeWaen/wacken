package be.wacken.planner;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;

final class SupabaseBandRepository implements BandRepository {
    private final SupabaseMasterDataClient client;

    SupabaseBandRepository(SupabaseMasterDataClient client) {
        this.client = client;
    }

    @Override
    public void save(Band band) {
        try {
            client.saveBand(band);
        } catch (IOException error) {
            throw new SupabaseSyncException(error.getMessage(), error);
        }
    }

    @Override
    public void replaceAll(List<Band> bands) {
        for (Band band : bands) {
            save(band);
        }
    }

    @Override
    public Optional<Band> findByName(String name) {
        return findAll().stream().filter(band -> band.name().equals(name)).findFirst();
    }

    @Override
    public List<Band> findAll() {
        try {
            return client.bands();
        } catch (IOException error) {
            throw new SupabaseSyncException(error.getMessage(), error);
        }
    }
}
