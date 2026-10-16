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
                RoomPersonalBandRatingEvent.class
        },
        version = 9,
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
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return instance;
    }
}
