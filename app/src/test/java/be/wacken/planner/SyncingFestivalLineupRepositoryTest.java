package be.wacken.planner;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.FestivalLineupEntry;

public final class SyncingFestivalLineupRepositoryTest {
    @Test
    public void savesLineupToSourceBeforeLocalCache() {
        FakeLineupCache cache = new FakeLineupCache();
        FakeLineupSource source = new FakeLineupSource();
        SyncingFestivalLineupRepository repository = new SyncingFestivalLineupRepository(cache, source);
        List<FestivalLineupEntry> entries = List.of(new FestivalLineupEntry("summer-breeze-2027", new Band("Any Given Day"), "Any Given Day"));

        repository.saveAllForFestival("summer-breeze-2027", entries);

        assertEquals(entries, source.findByFestival("summer-breeze-2027"));
        assertEquals(entries, cache.findByFestival("summer-breeze-2027"));
    }

    @Test
    public void keepsLocalLineupCacheWhenSourceWriteFails() {
        FakeLineupCache cache = new FakeLineupCache();
        cache.saveAllForFestival("wacken-2026", List.of(new FestivalLineupEntry("wacken-2026", new Band("Airbourne"), "Airbourne")));
        FakeLineupSource source = new FakeLineupSource();
        source.failWrites = true;
        SyncingFestivalLineupRepository repository = new SyncingFestivalLineupRepository(cache, source);

        try {
            repository.saveAllForFestival("summer-breeze-2027", List.of(new FestivalLineupEntry("summer-breeze-2027", new Band("Any Given Day"), "Any Given Day")));
        } catch (IllegalStateException ignored) {
            // Expected: source-first write rejected the group-wide change.
        }

        assertEquals(List.of(new FestivalLineupEntry("wacken-2026", new Band("Airbourne"), "Airbourne")), cache.findAll());
    }

    @Test
    public void replacesLocalLineupCacheFromSourcePull() {
        FakeLineupCache cache = new FakeLineupCache();
        cache.saveAllForFestival("local-only", List.of(new FestivalLineupEntry("local-only", new Band("Local Band"), "Local Band")));
        FakeLineupSource source = new FakeLineupSource();
        source.saveAllForFestival("summer-breeze-2027", List.of(new FestivalLineupEntry("summer-breeze-2027", new Band("Any Given Day"), "Any Given Day")));
        SyncingFestivalLineupRepository repository = new SyncingFestivalLineupRepository(cache, source);

        repository.syncSourceToCache();

        assertEquals(source.findAll(), cache.findAll());
    }

    private static final class FakeLineupCache extends FakeLineupSource implements SyncingFestivalLineupRepository.Cache {
        @Override
        public void replaceAll(List<FestivalLineupEntry> entries) {
            this.entries.clear();
            this.entries.addAll(entries);
        }
    }

    private static class FakeLineupSource implements SyncingFestivalLineupRepository.Source {
        protected final List<FestivalLineupEntry> entries = new ArrayList<>();
        private boolean failWrites;

        @Override
        public void saveAllForFestival(String festivalId, List<FestivalLineupEntry> entries) {
            if (failWrites) {
                throw new IllegalStateException("source unavailable");
            }
            this.entries.removeIf(existing -> existing.festivalId().equals(festivalId));
            this.entries.addAll(entries);
        }

        @Override
        public List<FestivalLineupEntry> findByFestival(String festivalId) {
            return entries.stream()
                    .filter(entry -> entry.festivalId().equals(festivalId))
                    .toList();
        }

        @Override
        public List<FestivalLineupEntry> findAll() {
            return List.copyOf(entries);
        }
    }
}
