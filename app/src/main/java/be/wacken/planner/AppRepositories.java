package be.wacken.planner;

import android.content.Context;

import java.nio.file.Path;

import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.FoodOptionRepository;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.StageDistanceRepository;
import be.wacken.planner.domain.StageRepository;
import be.wacken.planner.infrastructure.FileBackedBandRepository;
import be.wacken.planner.infrastructure.FileBackedFoodOptionRepository;
import be.wacken.planner.infrastructure.FileBackedPerformanceRepository;
import be.wacken.planner.infrastructure.FileBackedStageDistanceRepository;
import be.wacken.planner.infrastructure.FileBackedStageRepository;
import be.wacken.planner.infrastructure.SyncedBandRepository;
import be.wacken.planner.infrastructure.SyncedFoodOptionRepository;
import be.wacken.planner.infrastructure.SyncedPerformanceRepository;
import be.wacken.planner.infrastructure.SyncedStageDistanceRepository;
import be.wacken.planner.infrastructure.SyncedStageRepository;
import be.wacken.planner.persistence.RoomBandRepository;
import be.wacken.planner.persistence.RoomFoodOptionRepository;
import be.wacken.planner.persistence.RoomPerformanceRepository;
import be.wacken.planner.persistence.RoomRatingRepository;
import be.wacken.planner.persistence.RoomStageDistanceRepository;
import be.wacken.planner.persistence.RoomStageRepository;
import be.wacken.planner.persistence.WackenDatabase;

final class AppRepositories {
    private enum SourceMode {
        SUPABASE,
        TSV_FALLBACK
    }

    private final SyncedBandRepository bands;
    private final SyncedStageRepository stages;
    private final SyncedPerformanceRepository performances;
    private final SyncedStageDistanceRepository distances;
    private final SyncedFoodOptionRepository foodOptions;
    private final RatingRepository ratings;
    private final SyncingRatingRepository syncingRatings;

    AppRepositories(Context context) {
        this(context, SourceMode.SUPABASE);
    }

    static AppRepositories tsvFallback(Context context) {
        return new AppRepositories(context, SourceMode.TSV_FALLBACK);
    }

    private AppRepositories(Context context, SourceMode sourceMode) {
        Path storageDirectory = context.getFilesDir().toPath();
        WackenDatabase database = WackenDatabase.get(context);

        RoomBandRepository bandCache = new RoomBandRepository(database);
        RoomStageRepository stageCache = new RoomStageRepository(database);
        RoomPerformanceRepository performanceCache = new RoomPerformanceRepository(database);
        RoomStageDistanceRepository distanceCache = new RoomStageDistanceRepository(database);
        RoomFoodOptionRepository foodCache = new RoomFoodOptionRepository(database);
        RoomRatingRepository ratingCache = new RoomRatingRepository(database);

        BandRepository bandSource;
        StageRepository stageSource;
        PerformanceRepository performanceSource;
        StageDistanceRepository distanceSource;
        FoodOptionRepository foodSource;
        AuthSessionStore authSessionStore = new AuthSessionStore(context);
        SupabaseSessionManager sessionManager = new SupabaseSessionManager(authSessionStore, new SupabaseAuthClient());
        if (sourceMode == SourceMode.SUPABASE) {
            SupabaseMasterDataClient client = new SupabaseMasterDataClient(sessionManager);
            bandSource = new SupabaseBandRepository(client);
            stageSource = new SupabaseStageRepository(client);
            performanceSource = new SupabasePerformanceRepository(client);
            distanceSource = new SupabaseStageDistanceRepository(client);
            foodSource = new SupabaseFoodOptionRepository(client);
        } else {
            bandSource = new FileBackedBandRepository(storageDirectory);
            stageSource = new FileBackedStageRepository(storageDirectory);
            performanceSource = new FileBackedPerformanceRepository(storageDirectory);
            distanceSource = new FileBackedStageDistanceRepository(storageDirectory);
            foodSource = new FileBackedFoodOptionRepository(storageDirectory);
        }

        this.bands = new SyncedBandRepository(bandCache, bandSource);
        this.stages = new SyncedStageRepository(stageCache, stageSource);
        this.performances = new SyncedPerformanceRepository(performanceCache, performanceSource);
        this.distances = new SyncedStageDistanceRepository(distanceCache, distanceSource);
        this.foodOptions = new SyncedFoodOptionRepository(foodCache, foodSource);
        if (sourceMode == SourceMode.SUPABASE) {
            this.syncingRatings = new SyncingRatingRepository(
                    ratingCache,
                    new SupabaseRatingClient(sessionManager),
                    authSessionStore.load()
            );
            this.ratings = syncingRatings;
        } else {
            this.syncingRatings = null;
            this.ratings = ratingCache;
        }

        if (sourceMode == SourceMode.TSV_FALLBACK) {
            seedCacheFromSourceIfNeeded(bandCache, stageCache, performanceCache, distanceCache, foodCache);
        }
    }

    BandRepository bands() {
        return bands;
    }

    StageRepository stages() {
        return stages;
    }

    PerformanceRepository performances() {
        return performances;
    }

    StageDistanceRepository distances() {
        return distances;
    }

    FoodOptionRepository foodOptions() {
        return foodOptions;
    }

    RatingRepository ratings() {
        return ratings;
    }

    void syncRatings() {
        if (syncingRatings == null) {
            return;
        }
        try {
            syncingRatings.syncPendingRatings();
            syncingRatings.pullGroupRatings();
        } catch (Exception error) {
            throw new SupabaseSyncException(error.getMessage(), error);
        }
    }

    void syncMasterDataFromSource() {
        bands.syncSourceToCache();
        stages.syncSourceToCache();
        performances.syncSourceToCache();
        distances.syncSourceToCache();
        foodOptions.syncSourceToCache();
    }

    private void seedCacheFromSourceIfNeeded(
            RoomBandRepository bandCache,
            RoomStageRepository stageCache,
            RoomPerformanceRepository performanceCache,
            RoomStageDistanceRepository distanceCache,
            RoomFoodOptionRepository foodCache
    ) {
        if (bandCache.isEmpty()) {
            bands.syncSourceToCache();
        }
        if (stageCache.isEmpty()) {
            stages.syncSourceToCache();
        }
        if (performanceCache.isEmpty()) {
            performances.syncSourceToCache();
        }
        if (distanceCache.isEmpty()) {
            distances.syncSourceToCache();
        }
        if (foodCache.isEmpty()) {
            foodOptions.syncSourceToCache();
        }
    }
}
