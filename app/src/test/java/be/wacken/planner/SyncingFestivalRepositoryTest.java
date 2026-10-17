package be.wacken.planner;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalRepository;

public final class SyncingFestivalRepositoryTest {
    @Test
    public void savesFestivalToSourceBeforeLocalCache() {
        FakeFestivalCache cache = new FakeFestivalCache();
        FakeFestivalRepository source = new FakeFestivalRepository();
        SyncingFestivalRepository repository = new SyncingFestivalRepository(cache, source);

        repository.save(Festival.active("summer-breeze-2027", "Summer Breeze 2027"));

        assertEquals(List.of(Festival.active("summer-breeze-2027", "Summer Breeze 2027")), source.findAll());
        assertEquals(source.findAll(), cache.findAll());
    }

    @Test
    public void keepsLocalFestivalCacheWhenSourceSaveFails() {
        FakeFestivalCache cache = new FakeFestivalCache();
        cache.save(Festival.active("wacken-2026", "Wacken Open Air 2026"));
        FakeFestivalRepository source = new FakeFestivalRepository();
        source.failWrites = true;
        SyncingFestivalRepository repository = new SyncingFestivalRepository(cache, source);

        try {
            repository.save(Festival.active("summer-breeze-2027", "Summer Breeze 2027"));
        } catch (IllegalStateException ignored) {
            // Expected: source-first write rejected the group-wide change.
        }

        assertEquals(List.of(Festival.active("wacken-2026", "Wacken Open Air 2026")), cache.findAll());
    }

    @Test
    public void replacesLocalFestivalCacheFromSourcePull() {
        FakeFestivalCache cache = new FakeFestivalCache();
        cache.save(Festival.active("local-only", "Local Only"));
        FakeFestivalRepository source = new FakeFestivalRepository();
        source.save(Festival.archived("wacken-2026", "Wacken Open Air 2026"));
        source.save(Festival.active("summer-breeze-2027", "Summer Breeze 2027"));
        SyncingFestivalRepository repository = new SyncingFestivalRepository(cache, source);

        repository.syncSourceToCache();

        assertEquals(source.findAll(), cache.findAll());
    }

    private static final class FakeFestivalCache extends FakeFestivalRepository implements SyncingFestivalRepository.Cache {
        @Override
        public void replaceAll(List<Festival> festivals) {
            this.festivals.clear();
            this.festivals.addAll(festivals);
        }
    }

    private static class FakeFestivalRepository implements FestivalRepository {
        protected final List<Festival> festivals = new ArrayList<>();
        private boolean failWrites;

        @Override
        public List<Festival> findAll() {
            return List.copyOf(festivals);
        }

        @Override
        public void save(Festival festival) {
            if (failWrites) {
                throw new IllegalStateException("source unavailable");
            }
            festivals.removeIf(existing -> existing.id().equals(festival.id()));
            festivals.add(festival);
        }
    }
}
