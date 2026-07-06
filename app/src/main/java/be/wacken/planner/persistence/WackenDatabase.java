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
                RoomScheduleLock.class
        },
        version = 5,
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

    public abstract RoomBandDao bands();

    public abstract RoomStageDao stages();

    public abstract RoomPerformanceDao performances();

    public abstract RoomStageDistanceDao stageDistances();

    public abstract RoomFoodOptionDao foodOptions();

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
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return instance;
    }
}
