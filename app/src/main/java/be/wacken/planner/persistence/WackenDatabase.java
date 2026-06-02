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
                RoomRating.class
        },
        version = 3,
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

    public abstract RoomBandDao bands();

    public abstract RoomStageDao stages();

    public abstract RoomPerformanceDao performances();

    public abstract RoomStageDistanceDao stageDistances();

    public abstract RoomFoodOptionDao foodOptions();

    public abstract RoomRatingDao ratings();

    public static WackenDatabase get(Context context) {
        if (instance == null) {
            synchronized (WackenDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    WackenDatabase.class,
                                    "wacken-cache.db"
                            )
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return instance;
    }
}
