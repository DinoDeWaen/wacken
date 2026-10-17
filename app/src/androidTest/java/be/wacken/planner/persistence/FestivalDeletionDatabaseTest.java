package be.wacken.planner.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.SavedFestivalPlanningRating;

public final class FestivalDeletionDatabaseTest {
    private WackenDatabase database;

    @Before
    public void setUp() {
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(), WackenDatabase.class)
                .allowMainThreadQueries().build();
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void deletionPreservesOtherFestivalBandsAndPersonalHistory() {
        RoomFestivalRepository festivals = seedTwoFestivals();

        new RoomFestivalDeletion(database).delete(Festival.active("wacken-2026", "Wacken Open Air 2026"));

        assertOnlyOtherFestivalRemains();
        var history = database.personalBandRatingEvents().findByUserAndBand("user", "Airbourne");
        assertEquals(1, history.size());
        assertEquals(4, history.get(0).value);
        assertNull(history.get(0).festivalId);
        assertEquals("Wacken Open Air 2026", history.get(0).festivalNameSnapshot);
        festivals.seedDefaultActiveFestivalIfEmpty();
        assertEquals("other", festivals.findAll().get(0).id());
    }

    @Test
    public void remoteDeletionCleansTheSameLocalFestivalOnSync() {
        RoomFestivalRepository festivals = seedTwoFestivals();

        festivals.replaceAll(List.of(Festival.archived("other", "Other festival")));

        assertOnlyOtherFestivalRemains();
    }

    @Test
    public void discardDeletesOnlyAnUnchangedPendingRow() {
        RoomFestivalPlanningRatingRepository ratings = new RoomFestivalPlanningRatingRepository(database);
        ratings.save("group", "user", "missing", new Band("Airbourne"), Rating.of(4));
        ratings.save("group", "user", "other", new Band("Airbourne"), Rating.of(2));
        SavedFestivalPlanningRating expected = new SavedFestivalPlanningRating(
                "group", "user", "missing", new Band("Airbourne"), Rating.of(4));

        ratings.discardPendingRating(expected);

        assertEquals(1, ratings.findAll().size());
        assertEquals("other", ratings.findAll().get(0).festivalId());
        try {
            ratings.discardPendingRating(expected);
            fail("A changed or absent row must not be discarded");
        } catch (IllegalStateException expectedFailure) {
            assertEquals(1, ratings.findAll().size());
        }
    }

    @Test
    public void versionEightMigrationPreservesExistingScheduleAndHistory() {
        SupportSQLiteOpenHelper helper = new FrameworkSQLiteOpenHelperFactory().create(
                SupportSQLiteOpenHelper.Configuration.builder(InstrumentationRegistry.getInstrumentation().getTargetContext())
                        .name(null)
                        .callback(new SupportSQLiteOpenHelper.Callback(8) {
                            @Override public void onCreate(SupportSQLiteDatabase sql) {
                                sql.execSQL("CREATE TABLE festivals (id TEXT, name TEXT, status TEXT)");
                                sql.execSQL("INSERT INTO festivals VALUES ('wacken-2026', 'Wacken Open Air 2026', 'ACTIVE')");
                                sql.execSQL("CREATE TABLE performances (bandName TEXT, stageName TEXT, start TEXT, end TEXT)");
                                sql.execSQL("INSERT INTO performances VALUES ('Airbourne', 'Main', '2026-08-01T12:00', '2026-08-01T13:00')");
                                sql.execSQL("CREATE TABLE schedule_locks (groupId TEXT, conflictKey TEXT, selectedCandidateKey TEXT, syncStatus TEXT, operation TEXT)");
                                sql.execSQL("INSERT INTO schedule_locks VALUES ('group', 'conflict', 'winner', 'PENDING', 'UPSERT')");
                                sql.execSQL("CREATE TABLE personal_band_rating_events (festivalId TEXT)");
                                sql.execSQL("INSERT INTO personal_band_rating_events VALUES ('wacken-2026')");
                            }
                            @Override public void onUpgrade(SupportSQLiteDatabase sql, int oldVersion, int newVersion) { }
                        }).build());
        try {
            SupportSQLiteDatabase sql = helper.getWritableDatabase();
            WackenDatabase.MIGRATION_8_9.migrate(sql);
            try (var rows = sql.query("SELECT festivalId, bandName FROM performances")) {
                assertTrue(rows.moveToFirst());
                assertEquals("wacken-2026", rows.getString(0));
                assertEquals("Airbourne", rows.getString(1));
            }
            try (var rows = sql.query("SELECT festivalId, syncStatus FROM schedule_locks")) {
                assertTrue(rows.moveToFirst());
                assertEquals("wacken-2026", rows.getString(0));
                assertEquals("PENDING", rows.getString(1));
            }
            try (var rows = sql.query("SELECT festivalNameSnapshot FROM personal_band_rating_events")) {
                assertTrue(rows.moveToFirst());
                assertEquals("Wacken Open Air 2026", rows.getString(0));
            }
        } finally {
            helper.close();
        }
    }

    private RoomFestivalRepository seedTwoFestivals() {
        RoomFestivalRepository festivals = new RoomFestivalRepository(database);
        festivals.save(Festival.active("wacken-2026", "Wacken Open Air 2026"));
        festivals.save(Festival.archived("other", "Other festival"));
        database.bands().save(new RoomBand("Airbourne", null, null, null, null));
        for (String id : List.of("wacken-2026", "other")) {
            database.festivalLineups().saveAll(List.of(new RoomFestivalLineupEntry(id, "Airbourne", "Airbourne")));
            database.performances().save(new RoomPerformance(id, "Airbourne", "Main", "2026-08-01T12:00", "2026-08-01T13:00"));
            database.scheduleLocks().save(new RoomScheduleLock("group", id, "same-conflict", "winner", "PENDING", "UPSERT"));
            database.festivalPlanningRatings().save(new RoomFestivalPlanningRating("group", "user", id, "Airbourne", 3, "PENDING"));
        }
        database.personalBandRatingEvents().save(new RoomPersonalBandRatingEvent(
                "event", "user", "Airbourne", "wacken-2026", null, 4, "2026-08-01T13:00:00Z", "PENDING"));
        return festivals;
    }

    private void assertOnlyOtherFestivalRemains() {
        assertEquals(1, database.festivals().findAll().size());
        assertEquals("other", database.festivals().findAll().get(0).id);
        assertEquals(1, database.bands().findAll().size());
        assertEquals(0, database.festivalLineups().findByFestival("wacken-2026").size());
        assertEquals(1, database.festivalLineups().findByFestival("other").size());
        assertEquals(1, database.performances().findAll().size());
        assertEquals("other", database.performances().findAll().get(0).festivalId);
        assertEquals(0, database.scheduleLocks().findByGroup("group", "wacken-2026").size());
        assertEquals(1, database.scheduleLocks().findByGroup("group", "other").size());
        assertEquals(1, database.scheduleLocks().countPending("group"));
        assertEquals(1, database.festivalPlanningRatings().findAll().size());
        assertEquals("other", database.festivalPlanningRatings().findAll().get(0).festivalId);
    }
}
