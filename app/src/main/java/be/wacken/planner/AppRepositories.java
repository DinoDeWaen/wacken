package be.wacken.planner;

import be.wacken.planner.application.FestivalCreationGateway;
import be.wacken.planner.application.AddFestivalResult;

import android.content.Context;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

import be.wacken.planner.application.ActiveFestivalRatingRepository;
import be.wacken.planner.application.ActiveFestivalAfterRatingRepository;
import be.wacken.planner.application.DeleteFestivalUseCase;
import be.wacken.planner.application.RecoverPendingPlanningRatingUseCase;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.FestivalLineupRepository;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalPlanningRatingRepository;
import be.wacken.planner.domain.FestivalAfterRatingRepository;
import be.wacken.planner.domain.PersonalBandRatingRepository;
import be.wacken.planner.domain.FestivalRepository;
import be.wacken.planner.domain.FoodOptionRepository;
import be.wacken.planner.domain.PersonalBandRatingHistoryRepository;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.RealRatingRepository;
import be.wacken.planner.domain.StageDistanceRepository;
import be.wacken.planner.domain.StageRepository;
import be.wacken.planner.domain.SavedFestivalPlanningRating;
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
import be.wacken.planner.infrastructure.TsvImportTransaction;
import be.wacken.planner.persistence.RoomBandRepository;
import be.wacken.planner.persistence.RoomFestivalLineupRepository;
import be.wacken.planner.persistence.RoomFestivalDeletion;
import be.wacken.planner.persistence.RoomFestivalPlanningRatingRepository;
import be.wacken.planner.persistence.RoomFestivalRepository;
import be.wacken.planner.persistence.RoomFoodOptionRepository;
import be.wacken.planner.persistence.RoomPersonalBandRatingHistoryRepository;
import be.wacken.planner.persistence.RoomPerformanceRepository;
import be.wacken.planner.persistence.RoomRatingRepository;
import be.wacken.planner.persistence.RoomFestivalAfterRatingRepository;
import be.wacken.planner.persistence.RoomPersonalBandRatingRepository;
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
    private final FestivalAfterRatingRepository afterRatings;
    private final PersonalBandRatingRepository currentBandRatings;
    private final RatingStateSync ratingStateSync;
    private final RoomFestivalAfterRatingRepository afterRatingCache;
    private final SyncingFestivalRepository syncingFestivals;
    private final SyncingFestivalLineupRepository syncingFestivalLineups;
    private final SyncingFestivalPlanningRatingRepository syncingPlanningRatings;
    private final RecoverPendingPlanningRatingUseCase pendingPlanningRatingRecovery;
    private final DeleteFestivalUseCase festivalDeletion;
    private final SyncingPersonalBandRatingHistoryRepository syncingPersonalRatings;
    private final SyncingScheduleLockStore syncingScheduleLocks;
    private final ScheduleLockStore scheduleLocks;
    private final RatingSyncLocalStore ratingCache;
    private final RoomFestivalPlanningRatingRepository festivalPlanningRatingCache;
    private final RoomPersonalBandRatingHistoryRepository personalRatingCache;
    private final WackenDatabase database;
    private final SupabaseSessionManager sessionManager;
    private final AuthSession session;
    private final Consumer<Runnable> festivalImportTransaction;
    private final FestivalCreationGateway festivalCreation;

    AppRepositories(Context context) {
        this(context, SourceMode.SUPABASE);
    }

    static AppRepositories tsvFallback(Context context) {
        return new AppRepositories(context, SourceMode.TSV_FALLBACK);
    }

    private AppRepositories(Context context, SourceMode sourceMode) {
        Path storageDirectory = context.getFilesDir().toPath();
        WackenDatabase database = WackenDatabase.get(context);
        this.database = database;
        this.festivalImportTransaction = sourceMode == SourceMode.TSV_FALLBACK
                ? new TsvImportTransaction(storageDirectory, database::runInTransaction)
                : null;

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
        this.afterRatingCache = new RoomFestivalAfterRatingRepository(database);
        this.afterRatings = afterRatingCache;
        this.currentBandRatings = new RoomPersonalBandRatingRepository(database);
        AuthSessionStore authSessionStore = new AuthSessionStore(context);
        this.session = authSessionStore.load();
        this.ratingCache = ratingCache;
        festivalCache.seedDefaultActiveFestivalIfEmpty();
        String scheduleFestivalId = activeFestivalId(festivalCache);
        if (scheduleFestivalId.isEmpty()) {
            scheduleFestivalId = RoomFestivalRepository.DEFAULT_FESTIVAL_ID;
        }
        RoomScheduleLockStore scheduleLockCache = new RoomScheduleLockStore(database, scheduleFestivalId);
        this.festivalPlanningRatingCache = planningRatingCache;
        this.personalRatingCache = personalRatingCache;

        BandRepository bandSource;
        StageRepository stageSource;
        PerformanceRepository performanceSource;
        StageDistanceRepository distanceSource;
        FoodOptionRepository foodSource;
        SupabaseMasterDataClient supabaseMasterDataClient = null;
        SupabaseSessionManager sessionManager = new SupabaseSessionManager(authSessionStore, new SupabaseAuthClient());
        this.sessionManager = sessionManager;
        if (sourceMode == SourceMode.SUPABASE) {
            supabaseMasterDataClient = new SupabaseMasterDataClient(sessionManager);
            bandSource = new SupabaseBandRepository(supabaseMasterDataClient);
            stageSource = new SupabaseStageRepository(supabaseMasterDataClient);
            performanceSource = new SupabasePerformanceRepository(supabaseMasterDataClient);
            distanceSource = new SupabaseStageDistanceRepository(supabaseMasterDataClient);
            foodSource = new SupabaseFoodOptionRepository(supabaseMasterDataClient);
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
            this.syncingFestivals = new SyncingFestivalRepository(
                    festivalCache,
                    new SupabaseFestivalRepository(supabaseMasterDataClient)
            );
            this.festivals = syncingFestivals;
            this.syncingFestivalLineups = new SyncingFestivalLineupRepository(
                    lineupCache,
                    new SupabaseFestivalLineupRepository(supabaseMasterDataClient)
            );
            this.festivalLineups = syncingFestivalLineups;
            SupabaseScheduleLockClient scheduleLockClient = new SupabaseScheduleLockClient(sessionManager, scheduleFestivalId);
            SupabaseFestivalPlanningRatingClient planningRatingRemote = new SupabaseFestivalPlanningRatingClient(sessionManager);
            this.syncingPlanningRatings = new SyncingFestivalPlanningRatingRepository(
                    planningRatingCache,
                    planningRatingRemote,
                    session,
                    festivals
            );
            this.pendingPlanningRatingRecovery = new RecoverPendingPlanningRatingUseCase(
                    planningRatingCache,
                    (festivalId, band) -> planningRatingRemote.hasRating(session, festivalId, band),
                    festivals);
            this.festivalDeletion = new DeleteFestivalUseCase(festivals,
                    new SupabaseFestivalDeletionGateway(supabaseMasterDataClient,
                            new RoomFestivalDeletion(database)::delete));
            this.festivalPlanningRatings = syncingPlanningRatings;
            this.ratings = new ActiveFestivalRatingRepository(festivals, festivalPlanningRatings, session.groupId());
            this.syncingPersonalRatings = new SyncingPersonalBandRatingHistoryRepository(
                    personalRatingCache,
                    new SupabasePersonalBandRatingClient(sessionManager),
                    session
            );
            this.personalBandRatings = syncingPersonalRatings;
            this.ratingStateSync = new RatingStateSync(afterRatingCache,
                    new SupabaseRatingStateClient(sessionManager, session.userId()), session.userId());
            this.syncingScheduleLocks = new SyncingScheduleLockStore(scheduleLockCache, scheduleLockClient, session);
            this.scheduleLocks = syncingScheduleLocks;
        } else {
            this.festivalDeletion = null;
            this.syncingPlanningRatings = null;
            this.pendingPlanningRatingRecovery = null;
            this.syncingPersonalRatings = null;
            this.syncingFestivals = null;
            this.syncingFestivalLineups = null;
            this.festivals = festivalCache;
            this.festivalLineups = lineupCache;
            this.festivalPlanningRatings = planningRatingCache;
            this.ratings = new ActiveFestivalRatingRepository(festivals, festivalPlanningRatings, "local");
            this.personalBandRatings = personalRatingCache;
            this.ratingStateSync = null;
            this.syncingScheduleLocks = null;
            this.scheduleLocks = new ScheduleLockStore.NoOp();
        }

        this.realRatings = new ActiveFestivalAfterRatingRepository(festivals, afterRatings);

        this.festivalCreation = sourceMode == SourceMode.SUPABASE && session.isPresent()
                ? new SupabaseFestivalCreationGateway(supabaseMasterDataClient, new RoomFestivalCreationCache(database))
                : (group, user, festival, uploaded) -> AddFestivalResult.failure("Sign in before adding a group-wide festival.");

        if (sourceMode == SourceMode.TSV_FALLBACK) {
            seedCacheFromSourceIfNeeded(bandCache, stageCache, performanceCache, distanceCache, foodCache);
        }
    }

    be.wacken.planner.application.FestivalCreationGateway festivalCreation() {
        return festivalCreation;
    }

    BandRepository bands() {
        return bands;
    }

    StageRepository stages() {
        return stages;
    }

    PerformanceRepository performances() {
        return performancesForFestival(activeFestivalId(festivals));
    }

    PerformanceRepository performancesForFestival(String festivalId) {
        return new FestivalScopedPerformanceRepository(performances, festivalId);
    }

    PerformanceRepository performanceImportTarget() {
        return performances;
    }

    Festival deleteFestival(String festivalId) {
        if (festivalDeletion == null) {
            throw new IllegalStateException("Sign in before deleting a festival.");
        }
        return festivalDeletion.delete(festivalId);
    }

    private static String activeFestivalId(FestivalRepository festivals) {
        return festivals.findAll().stream()
                .filter(Festival::isActive)
                .map(Festival::id)
                .findFirst()
                .orElse("");
    }

    StageDistanceRepository distances() {
        return distances;
    }

    FoodOptionRepository foodOptions() {
        return foodOptions;
    }

    Consumer<Runnable> festivalImportTransaction() {
        if (festivalImportTransaction == null) {
            throw new IllegalStateException("Festival CSV transaction requires TSV fallback repositories.");
        }
        return festivalImportTransaction;
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

    FestivalAfterRatingRepository afterRatings() { return afterRatings; }

    PersonalBandRatingRepository currentBandRatings() { return currentBandRatings; }

    boolean hasUnresolvedLegacyRating(String bandName) { return database.ratingStates().hasUnresolvedLegacy(bandName); }

    ScheduleLockStore scheduleLocks() {
        return scheduleLocks;
    }

    PendingSyncSummary pendingSyncSummary() {
        if (!session.isPresent()) {
            return PendingSyncSummary.of(0, 0);
        }
        int pendingRatings = ratingCache.findPending(session.groupId(), session.userId()).size()
                + festivalPlanningRatingCache.findPending(session.groupId(), session.userId()).size()
                + personalRatingCache.findPending(session.userId()).size()
                + afterRatingCache.findPending(session.userId()).stream().filter(r -> !r.requiresReview()).collect(java.util.stream.Collectors.toList()).size();
        int pendingScheduleChoices = database.scheduleLocks().countPending(session.groupId());
        return PendingSyncSummary.of(pendingRatings, pendingScheduleChoices);
    }

    void syncScheduleLocks() {
        if (syncingScheduleLocks == null) {
            SupabaseDiagnostics.info("schedule_lock_sync", "skipped", "remote_repository=false");
            return;
        }
        try {
            SupabaseDiagnostics.info("schedule_lock_sync", "start", "remote_repository=true");
            for (Festival festival : festivals.findAll()) {
                new SyncingScheduleLockStore(new RoomScheduleLockStore(database, festival.id()),
                        new SupabaseScheduleLockClient(sessionManager, festival.id()), session).pullGroupLocks();
            }
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
            if (ratingStateSync != null) ratingStateSync.sync();
            SupabaseDiagnostics.info("ratings_sync", "success", "remote_repository=true");
        } catch (Exception error) {
            SupabaseDiagnostics.warn("ratings_sync", "failed", "remote_repository=true", error);
            throw new SupabaseSyncException(error.getMessage(), error);
        }
    }

    void recoverPendingWackenRating(SavedFestivalPlanningRating expected) throws IOException {
        if (pendingPlanningRatingRecovery == null) {
            throw new IllegalStateException("Sign in before recovering a pending rating.");
        }
        pendingPlanningRatingRecovery.moveToWacken2026(expected, session.groupId(), session.userId());
    }

    void discardPendingRating(SavedFestivalPlanningRating expected) {
        if (pendingPlanningRatingRecovery == null) {
            throw new IllegalStateException("Sign in before discarding a pending rating.");
        }
        pendingPlanningRatingRecovery.discardPending(expected, session.groupId(), session.userId());
    }

    void syncMasterDataFromSource() {
        try {
            SupabaseDiagnostics.info("master_data_sync", "start", "source=remote_or_assets");
            bands.syncSourceToCache();
            if (syncingFestivals != null) {
                syncingFestivals.syncSourceToCache();
            }
            if (syncingFestivalLineups != null) {
                syncingFestivalLineups.syncSourceToCache();
            }
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
