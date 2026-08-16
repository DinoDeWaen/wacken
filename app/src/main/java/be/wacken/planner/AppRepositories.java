package be.wacken.planner;

import android.content.Context;

import java.nio.file.Path;
import java.util.Optional;

import be.wacken.planner.application.ActiveFestivalRatingRepository;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.FestivalLineupRepository;
import be.wacken.planner.domain.FestivalPlanningRatingRepository;
import be.wacken.planner.domain.FestivalRepository;
import be.wacken.planner.domain.FoodOptionRepository;
import be.wacken.planner.domain.PersonalBandRatingHistoryRepository;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.RealRatingRepository;
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
import be.wacken.planner.persistence.RoomFestivalLineupRepository;
import be.wacken.planner.persistence.RoomFestivalPlanningRatingRepository;
import be.wacken.planner.persistence.RoomFestivalRepository;
import be.wacken.planner.persistence.RoomFoodOptionRepository;
import be.wacken.planner.persistence.RoomPersonalBandRatingHistoryRepository;
import be.wacken.planner.persistence.RoomPerformanceRepository;
import be.wacken.planner.persistence.RoomRatingRepository;
import be.wacken.planner.persistence.RoomRealRatingRepository;
import be.wacken.planner.persistence.RoomScheduleLockStore;
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
    private final FestivalRepository festivals;
    private final FestivalLineupRepository festivalLineups;
    private final FestivalPlanningRatingRepository festivalPlanningRatings;
    private final PersonalBandRatingHistoryRepository personalBandRatings;
    private final RatingRepository ratings;
    private final RealRatingRepository realRatings;
    private final SyncingFestivalPlanningRatingRepository syncingPlanningRatings;
    private final SyncingPersonalBandRatingHistoryRepository syncingPersonalRatings;
    private final SyncingScheduleLockStore syncingScheduleLocks;
    private final ScheduleLockStore scheduleLocks;
    private final RatingSyncLocalStore ratingCache;
    private final RoomFestivalPlanningRatingRepository festivalPlanningRatingCache;
    private final RoomPersonalBandRatingHistoryRepository personalRatingCache;
    private final ScheduleLockLocalStore scheduleLockCache;
    private final AuthSession session;

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
        RoomFestivalRepository festivalCache = new RoomFestivalRepository(database);
        RoomFestivalLineupRepository lineupCache = new RoomFestivalLineupRepository(database);
        RoomFestivalPlanningRatingRepository planningRatingCache = new RoomFestivalPlanningRatingRepository(database);
        RoomPersonalBandRatingHistoryRepository personalRatingCache = new RoomPersonalBandRatingHistoryRepository(database);
        RoomRatingRepository ratingCache = new RoomRatingRepository(database);
        RoomRealRatingRepository realRatingCache = new RoomRealRatingRepository(database);
        RoomScheduleLockStore scheduleLockCache = new RoomScheduleLockStore(database);
        AuthSessionStore authSessionStore = new AuthSessionStore(context);
        this.session = authSessionStore.load();
        this.ratingCache = ratingCache;
        festivalCache.seedDefaultActiveFestivalIfEmpty();
        this.festivals = festivalCache;
        this.festivalLineups = lineupCache;
        this.festivalPlanningRatingCache = planningRatingCache;
        this.personalRatingCache = personalRatingCache;
        this.realRatings = realRatingCache;
        personalRatingCache.backfillLegacyWackenRealRatings(
                realRatingCache,
                session.isPresent() ? Optional.of(session.userId()) : Optional.empty()
        );
        this.scheduleLockCache = scheduleLockCache;

        BandRepository bandSource;
        StageRepository stageSource;
        PerformanceRepository performanceSource;
        StageDistanceRepository distanceSource;
        FoodOptionRepository foodSource;
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
        if (sourceMode == SourceMode.SUPABASE && session.isPresent()) {
            SupabaseScheduleLockClient scheduleLockClient = new SupabaseScheduleLockClient(sessionManager);
            this.syncingPlanningRatings = new SyncingFestivalPlanningRatingRepository(
                    planningRatingCache,
                    new SupabaseFestivalPlanningRatingClient(sessionManager),
                    session
            );
            this.festivalPlanningRatings = syncingPlanningRatings;
            this.ratings = new ActiveFestivalRatingRepository(festivals, festivalPlanningRatings, session.groupId());
            this.syncingPersonalRatings = new SyncingPersonalBandRatingHistoryRepository(
                    personalRatingCache,
                    new SupabasePersonalBandRatingClient(sessionManager),
                    session
            );
            this.personalBandRatings = syncingPersonalRatings;
            this.syncingScheduleLocks = new SyncingScheduleLockStore(scheduleLockCache, scheduleLockClient, session);
            this.scheduleLocks = syncingScheduleLocks;
        } else {
            this.syncingPlanningRatings = null;
            this.syncingPersonalRatings = null;
            this.festivalPlanningRatings = planningRatingCache;
            this.ratings = new ActiveFestivalRatingRepository(festivals, festivalPlanningRatings, "local");
            this.personalBandRatings = personalRatingCache;
            this.syncingScheduleLocks = null;
            this.scheduleLocks = new ScheduleLockStore.NoOp();
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

    FestivalRepository festivals() {
        return festivals;
    }

    FestivalLineupRepository festivalLineups() {
        return festivalLineups;
    }

    FestivalPlanningRatingRepository festivalPlanningRatings() {
        return festivalPlanningRatings;
    }

    PersonalBandRatingHistoryRepository personalBandRatings() {
        return personalBandRatings;
    }

    RatingRepository ratings() {
        return ratings;
    }

    RealRatingRepository realRatings() {
        return realRatings;
    }

    ScheduleLockStore scheduleLocks() {
        return scheduleLocks;
    }

    PendingSyncSummary pendingSyncSummary() {
        if (!session.isPresent()) {
            return PendingSyncSummary.of(0, 0);
        }
        int pendingRatings = ratingCache.findPending(session.groupId(), session.userId()).size()
                + festivalPlanningRatingCache.findPending(session.groupId(), session.userId()).size()
                + personalRatingCache.findPending(session.userId()).size();
        int pendingScheduleChoices = scheduleLockCache.findPendingSelections(session.groupId()).size()
                + scheduleLockCache.findPendingClears(session.groupId()).size();
        return PendingSyncSummary.of(pendingRatings, pendingScheduleChoices);
    }

    void syncScheduleLocks() {
        if (syncingScheduleLocks == null) {
            SupabaseDiagnostics.info("schedule_lock_sync", "skipped", "remote_repository=false");
            return;
        }
        try {
            SupabaseDiagnostics.info("schedule_lock_sync", "start", "remote_repository=true");
            syncingScheduleLocks.pullGroupLocks();
            SupabaseDiagnostics.info("schedule_lock_sync", "success", "remote_repository=true");
        } catch (Exception error) {
            SupabaseDiagnostics.warn("schedule_lock_sync", "failed", "remote_repository=true", error);
            throw new SupabaseSyncException(error.getMessage(), error);
        }
    }

    void syncRatings() {
        if (syncingPlanningRatings == null) {
            SupabaseDiagnostics.info("ratings_sync", "skipped", "remote_repository=false");
            return;
        }
        try {
            SupabaseDiagnostics.info("ratings_sync", "start", "remote_repository=true");
            syncingPlanningRatings.syncPendingRatings();
            syncingPlanningRatings.pullGroupRatings();
            if (syncingPersonalRatings != null) {
                syncingPersonalRatings.syncPendingEvents();
                syncingPersonalRatings.pullUserEvents();
            }
            SupabaseDiagnostics.info("ratings_sync", "success", "remote_repository=true");
        } catch (Exception error) {
            SupabaseDiagnostics.warn("ratings_sync", "failed", "remote_repository=true", error);
            throw new SupabaseSyncException(error.getMessage(), error);
        }
    }

    void syncMasterDataFromSource() {
        try {
            SupabaseDiagnostics.info("master_data_sync", "start", "source=remote_or_assets");
            bands.syncSourceToCache();
            stages.syncSourceToCache();
            performances.syncSourceToCache();
            distances.syncSourceToCache();
            foodOptions.syncSourceToCache();
            SupabaseDiagnostics.info("master_data_sync", "success", "source=remote_or_assets");
        } catch (RuntimeException error) {
            SupabaseDiagnostics.warn("master_data_sync", "failed", "source=remote_or_assets", error);
            throw error;
        }
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
