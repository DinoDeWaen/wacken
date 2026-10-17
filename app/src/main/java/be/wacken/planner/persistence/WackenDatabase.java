package be.wacken.planner.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.migration.Migration;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
        entities = {
                RoomBand.class,
                RoomStage.class,
                RoomPerformance.class,
                RoomStageDistance.class,
                RoomFoodOption.class,
                RoomRating.class,
                RoomRealRating.class,
                RoomScheduleLock.class,
                RoomFestival.class,
                RoomFestivalCacheMarker.class,
                RoomFestivalLineupEntry.class,
                RoomFestivalPlanningRating.class,
                RoomPersonalBandRatingEvent.class,
                RoomFestivalAfterRating.class,
                RoomPersonalBandRating.class,
                RoomRatingCorrection.class,
                RoomBandAlias.class
        },
        version = 12,
        exportSchema = false
)
public abstract class WackenDatabase extends RoomDatabase {
    private static volatile WackenDatabase instance;
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE ratings ADD COLUMN groupId TEXT NOT NULL DEFAULT 'local'");
            database.execSQL("ALTER TABLE ratings ADD COLUMN syncStatus TEXT NOT NULL DEFAULT 'SYNCED'");
        }
    };
    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("UPDATE ratings SET value = value + 1 WHERE value BETWEEN 0 AND 4");
        }
    };
    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("""
                    CREATE TABLE IF NOT EXISTS schedule_locks (
                        groupId TEXT NOT NULL,
                        conflictKey TEXT NOT NULL,
                        selectedCandidateKey TEXT NOT NULL,
                        syncStatus TEXT NOT NULL,
                        operation TEXT NOT NULL,
                        PRIMARY KEY(groupId, conflictKey)
                    )
                    """);
        }
    };
    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("""
                    CREATE TABLE IF NOT EXISTS real_ratings (
                        userName TEXT NOT NULL,
                        bandName TEXT NOT NULL,
                        value INTEGER NOT NULL,
                        PRIMARY KEY(userName, bandName)
                    )
                    """);
        }
    };
    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("""
                    CREATE TABLE IF NOT EXISTS festivals (
                        id TEXT NOT NULL,
                        name TEXT NOT NULL,
                        status TEXT NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """);
            database.execSQL("""
                    INSERT OR IGNORE INTO festivals (id, name, status)
                    VALUES ('wacken-2026', 'Wacken Open Air 2026', 'ACTIVE')
                    """);
        }
    };
    private static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("""
                    CREATE TABLE IF NOT EXISTS festival_lineup_entries (
                        festivalId TEXT NOT NULL,
                        bandName TEXT NOT NULL,
                        uploadedDisplayName TEXT NOT NULL,
                        PRIMARY KEY(festivalId, bandName)
                    )
                    """);
            database.execSQL("""
                    CREATE TABLE IF NOT EXISTS festival_planning_ratings (
                        groupId TEXT NOT NULL,
                        userName TEXT NOT NULL,
                        festivalId TEXT NOT NULL,
                        bandName TEXT NOT NULL,
                        value INTEGER NOT NULL,
                        syncStatus TEXT NOT NULL,
                        PRIMARY KEY(groupId, userName, festivalId, bandName)
                    )
                    """);
            database.execSQL("""
                    CREATE TABLE IF NOT EXISTS personal_band_rating_events (
                        id TEXT NOT NULL,
                        userName TEXT NOT NULL,
                        bandName TEXT NOT NULL,
                        festivalId TEXT,
                        value INTEGER NOT NULL,
                        createdAt TEXT NOT NULL,
                        syncStatus TEXT NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """);
            database.execSQL("""
                    INSERT OR IGNORE INTO festival_lineup_entries (festivalId, bandName, uploadedDisplayName)
                    SELECT 'wacken-2026', name, name FROM bands
                    """);
            database.execSQL("""
                    INSERT OR IGNORE INTO festival_planning_ratings (groupId, userName, festivalId, bandName, value, syncStatus)
                    SELECT groupId, userName, 'wacken-2026', bandName, value, syncStatus FROM ratings
                    """);
            database.execSQL("""
                    INSERT OR IGNORE INTO personal_band_rating_events (id, userName, bandName, festivalId, value, createdAt, syncStatus)
                    SELECT userName || ':' || bandName || ':legacy-real', userName, bandName, 'wacken-2026', value, '1970-01-01T00:00:00Z', 'PENDING'
                    FROM real_ratings
                    WHERE value > 0
            """);
        }
    };
    private static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("DROP INDEX IF EXISTS idx_festivals_one_active");
        }
    };
    static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("""
                    CREATE TABLE performances_new (
                        festivalId TEXT NOT NULL,
                        bandName TEXT NOT NULL,
                        stageName TEXT NOT NULL,
                        start TEXT NOT NULL,
                        end TEXT NOT NULL,
                        PRIMARY KEY(festivalId, bandName, start)
                    )
                    """);
            database.execSQL("INSERT INTO performances_new SELECT 'wacken-2026', bandName, stageName, start, end FROM performances");
            database.execSQL("DROP TABLE performances");
            database.execSQL("ALTER TABLE performances_new RENAME TO performances");
            database.execSQL("""
                    CREATE TABLE schedule_locks_new (
                        groupId TEXT NOT NULL,
                        festivalId TEXT NOT NULL,
                        conflictKey TEXT NOT NULL,
                        selectedCandidateKey TEXT NOT NULL,
                        syncStatus TEXT NOT NULL,
                        operation TEXT NOT NULL,
                        PRIMARY KEY(groupId, festivalId, conflictKey)
                    )
                    """);
            database.execSQL("INSERT INTO schedule_locks_new SELECT groupId, 'wacken-2026', conflictKey, selectedCandidateKey, syncStatus, operation FROM schedule_locks");
            database.execSQL("DROP TABLE schedule_locks");
            database.execSQL("ALTER TABLE schedule_locks_new RENAME TO schedule_locks");
            database.execSQL("ALTER TABLE personal_band_rating_events ADD COLUMN festivalNameSnapshot TEXT");
            database.execSQL("UPDATE personal_band_rating_events SET festivalNameSnapshot = (SELECT name FROM festivals WHERE id = personal_band_rating_events.festivalId)");
            database.execSQL("CREATE TABLE festival_cache_marker (id INTEGER NOT NULL, PRIMARY KEY(id))");
            database.execSQL("INSERT INTO festival_cache_marker (id) SELECT 1 WHERE EXISTS (SELECT 1 FROM festivals)");
        }
    };

    public static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("""
                    CREATE TABLE IF NOT EXISTS festival_after_ratings (
                        userName TEXT NOT NULL, festivalId TEXT NOT NULL, bandName TEXT NOT NULL,
                        operationId TEXT NOT NULL, value INTEGER NOT NULL, recordedAt TEXT NOT NULL,
                        syncStatus TEXT NOT NULL, requiresReview INTEGER NOT NULL,
                        PRIMARY KEY(userName, festivalId, bandName))
                    """);
            database.execSQL("""
                    CREATE TABLE IF NOT EXISTS personal_band_ratings (
                        userName TEXT NOT NULL, bandName TEXT NOT NULL, value INTEGER NOT NULL,
                        sourceFestivalId TEXT NOT NULL, sourceFestivalName TEXT NOT NULL,
                        sourceEventId TEXT NOT NULL, establishedAt TEXT NOT NULL,
                        PRIMARY KEY(userName, bandName))
                    """);
            // Keep every original event and legacy global value. Recovered after-values need review:
            // older apps did not persist resets, so their last positive event cannot prove a final value.
            database.execSQL("""
                    INSERT OR IGNORE INTO festival_after_ratings
                    SELECT e.userName,e.festivalId,e.bandName,e.id,e.value,e.createdAt,
                        CASE WHEN e.syncStatus='PENDING' THEN 'REVIEW' ELSE 'SYNCED' END,1
                    FROM personal_band_rating_events e JOIN festivals f ON f.id=e.festivalId
                    WHERE e.value BETWEEN 1 AND 5 AND length(e.id)=36
                      AND e.createdAt>'1970-01-01T00:00:00Z'
                      AND e.id GLOB '????????-????-????-????-????????????'
                      AND length(replace(e.id,'-',''))=32 AND lower(replace(e.id,'-','')) NOT GLOB '*[^0-9a-f]*'
                      AND NOT EXISTS (SELECT 1 FROM personal_band_rating_events newer
                        WHERE newer.userName=e.userName AND newer.festivalId=e.festivalId AND newer.bandName=e.bandName
                          AND (newer.createdAt>e.createdAt OR (newer.createdAt=e.createdAt AND newer.id>e.id)))
                    """);
            // Only acknowledged, dated archived history establishes a migration preference.
            // Active, pending and unknown-date events stay available without being promoted.
            database.execSQL("""
                    INSERT OR IGNORE INTO personal_band_ratings
                    SELECT e.userName,e.bandName,e.value,e.festivalId,coalesce(e.festivalNameSnapshot,f.name),e.id,e.createdAt
                    FROM personal_band_rating_events e JOIN festivals f ON f.id=e.festivalId
                    WHERE f.status='ARCHIVED' AND e.syncStatus='SYNCED' AND e.value BETWEEN 1 AND 5
                      AND length(e.id)=36 AND e.createdAt>'1970-01-01T00:00:00Z'
                      AND e.id GLOB '????????-????-????-????-????????????'
                      AND length(replace(e.id,'-',''))=32 AND lower(replace(e.id,'-','')) NOT GLOB '*[^0-9a-f]*'
                      AND NOT EXISTS (SELECT 1 FROM personal_band_rating_events newer JOIN festivals nf ON nf.id=newer.festivalId
                        WHERE nf.status='ARCHIVED' AND newer.syncStatus='SYNCED' AND newer.userName=e.userName
                          AND newer.bandName=e.bandName AND (newer.createdAt>e.createdAt
                            OR (newer.createdAt=e.createdAt AND newer.id>e.id)))
                    """);
        }
    };

    public static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS rating_corrections (operationId TEXT NOT NULL PRIMARY KEY, userName TEXT NOT NULL, festivalId TEXT NOT NULL, festivalName TEXT NOT NULL, bandName TEXT NOT NULL, kind TEXT NOT NULL, previousValue INTEGER NOT NULL, newValue INTEGER NOT NULL, expectedRevision INTEGER NOT NULL, originalRecordedAt TEXT, correctedAt TEXT NOT NULL)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_rating_corrections_userName_festivalId_bandName ON rating_corrections(userName,festivalId,bandName)");
        }
    };

    public static final Migration MIGRATION_11_12 = new Migration(11, 12) {
        @Override public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS band_aliases (aliasKey TEXT NOT NULL PRIMARY KEY, aliasName TEXT NOT NULL, bandId TEXT NOT NULL, bandName TEXT NOT NULL, confirmedBy TEXT NOT NULL, confirmedAt TEXT NOT NULL)");
        }
    };

    public abstract RoomBandAliasDao bandAliases();

    public abstract RoomRatingCorrectionDao ratingCorrections();

    public abstract RoomRatingStateDao ratingStates();

    public abstract RoomBandDao bands();

    public abstract RoomStageDao stages();

    public abstract RoomPerformanceDao performances();

    public abstract RoomStageDistanceDao stageDistances();

    public abstract RoomFoodOptionDao foodOptions();

    public abstract RoomFestivalDao festivals();

    public abstract RoomFestivalCacheMarkerDao festivalCacheMarker();

    public abstract RoomFestivalLineupEntryDao festivalLineups();

    public abstract RoomFestivalPlanningRatingDao festivalPlanningRatings();

    public abstract RoomPersonalBandRatingEventDao personalBandRatingEvents();

    public abstract RoomRatingDao ratings();

    public abstract RoomRealRatingDao realRatings();

    public abstract RoomScheduleLockDao scheduleLocks();

    public static WackenDatabase get(Context context) {
        if (instance == null) {
            synchronized (WackenDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    WackenDatabase.class,
                                    "wacken-cache.db"
                            )
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return instance;
    }
}
