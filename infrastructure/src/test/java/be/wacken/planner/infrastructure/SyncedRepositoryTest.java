package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.SavedRating;
import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.StageDistance;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SyncedRepositoryTest {
    @Test
    void syncsBandsFromBackendLikeSourceIntoCacheAndReadsFromCache() {
        InMemoryBandRepository cache = new InMemoryBandRepository();
        InMemoryBandRepository source = new InMemoryBandRepository();
        source.replaceAll(List.of(new Band("Backend Band")));
        SyncedBandRepository repository = new SyncedBandRepository(cache, source);

        repository.syncSourceToCache();
        source.replaceAll(List.of(new Band("Source Changed Later")));

        assertEquals(List.of(new Band("Backend Band")), repository.findAll());
        assertEquals(List.of(new Band("Backend Band")), cache.findAll());
    }

    @Test
    void writesBandChangesToSourceAndCache() {
        InMemoryBandRepository cache = new InMemoryBandRepository();
        InMemoryBandRepository source = new InMemoryBandRepository();
        SyncedBandRepository repository = new SyncedBandRepository(cache, source);

        repository.save(new Band("Rated Soon"));

        assertEquals(List.of(new Band("Rated Soon")), cache.findAll());
        assertEquals(List.of(new Band("Rated Soon")), source.findAll());
    }

    @Test
    void keepsExistingBandCacheWhenSourceSyncFails() {
        InMemoryBandRepository cache = new InMemoryBandRepository();
        cache.replaceAll(List.of(new Band("Cached Band")));
        SyncedBandRepository repository = new SyncedBandRepository(cache, new FailingBandRepository());

        try {
            repository.syncSourceToCache();
        } catch (IllegalStateException ignored) {
            // Expected: the source failed before cache replacement could start.
        }

        assertEquals(List.of(new Band("Cached Band")), repository.findAll());
    }

    @Test
    void syncsStageDistancesFromSourceIntoCache() {
        Stage faster = new Stage("Faster");
        Stage harder = new Stage("Harder");
        StageDistance distance = StageDistance.between(faster, harder, 7);
        InMemoryStageDistanceRepository cache = new InMemoryStageDistanceRepository();
        InMemoryStageDistanceRepository source = new InMemoryStageDistanceRepository();
        source.replaceAll(List.of(distance));
        SyncedStageDistanceRepository repository = new SyncedStageDistanceRepository(cache, source);

        repository.syncSourceToCache();

        assertEquals(List.of(distance), cache.findAll());
        assertEquals(distance, repository.findBetween(faster, harder).orElseThrow());
    }

    @Test
    void writesRatingsToSourceAndCacheAndReadsFromCache() {
        Band band = new Band("5th Avenue");
        InMemoryRatingRepository cache = new InMemoryRatingRepository();
        InMemoryRatingRepository source = new InMemoryRatingRepository();
        SyncedRatingRepository repository = new SyncedRatingRepository(cache, source);

        repository.save("dino", band, Rating.of(4));
        source.save("dino", band, Rating.of(1));

        assertEquals(Rating.of(4), repository.findByUserAndBand("dino", band).orElseThrow());
        assertEquals(List.of(new SavedRating("dino", band, Rating.of(4))), cache.findAll());
        assertEquals(Rating.of(1), source.findByUserAndBand("dino", band).orElseThrow());
    }

    @Test
    void syncsRatingsFromSourceIntoCache() {
        Band band = new Band("5th Avenue");
        InMemoryRatingRepository cache = new InMemoryRatingRepository();
        InMemoryRatingRepository source = new InMemoryRatingRepository();
        source.save("dino", band, Rating.of(3));
        SyncedRatingRepository repository = new SyncedRatingRepository(cache, source);

        repository.syncSourceToCache();

        assertEquals(Rating.of(3), repository.findByUserAndBand("dino", band).orElseThrow());
        assertEquals(List.of(new SavedRating("dino", band, Rating.of(3))), cache.findAll());
        assertTrue(repository.findAll().contains(new SavedRating("dino", band, Rating.of(3))));
    }

    private static final class FailingBandRepository implements BandRepository {
        @Override
        public void save(Band band) {
            throw new IllegalStateException("source unavailable");
        }

        @Override
        public void replaceAll(List<Band> bands) {
            throw new IllegalStateException("source unavailable");
        }

        @Override
        public Optional<Band> findByName(String name) {
            throw new IllegalStateException("source unavailable");
        }

        @Override
        public List<Band> findAll() {
            throw new IllegalStateException("source unavailable");
        }
    }
}
