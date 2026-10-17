package be.wacken.planner.persistence;

import androidx.room.Room;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.platform.app.InstrumentationRegistry;
import be.wacken.planner.domain.*;
import java.time.Instant;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public final class BandAliasDatabaseTest {
    @Test public void aliasSnapshotAndExistingRatingPublishOrRollbackTogether() {
        var db=Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(),WackenDatabase.class).allowMainThreadQueries().build();
        try {
            var repository=new RoomBandAliasRepository(db);
            var alias=new ConfirmedBandAlias("AF",new CanonicalBand("known",new Band("Abbie Falls")),"member",Instant.EPOCH);
            repository.replaceAll(List.of(alias));assertEquals(List.of(alias),repository.findAll());
            db.getOpenHelper().getWritableDatabase().execSQL("CREATE TRIGGER reject_alias BEFORE INSERT ON band_aliases BEGIN SELECT RAISE(ABORT,'Injected alias failure'); END");
            try {repository.replaceAll(List.of(alias));fail("Expected rollback");} catch(RuntimeException expected) {}
            assertEquals(List.of(alias),repository.findAll());
        } finally {db.close();}
    }
    @Test public void migrationElevenToTwelvePreservesPendingAndAuditTables() {
        var context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        String name="alias-migration-test.db";context.deleteDatabase(name);
        var helper=new FrameworkSQLiteOpenHelperFactory().create(SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(name).callback(new SupportSQLiteOpenHelper.Callback(11) {
                public void onCreate(SupportSQLiteDatabase sql) {
                    sql.execSQL("CREATE TABLE pending_fixture (id TEXT PRIMARY KEY,value INTEGER)");
                    sql.execSQL("INSERT INTO pending_fixture VALUES('unsent',4)");
                    WackenDatabase.MIGRATION_10_11.migrate(sql);
                }
                public void onUpgrade(SupportSQLiteDatabase db,int oldVersion,int newVersion) {}
            }).build());
        try {
            var sql=helper.getWritableDatabase();WackenDatabase.MIGRATION_11_12.migrate(sql);
            try(var rows=sql.query("SELECT value FROM pending_fixture WHERE id='unsent'")) {assertTrue(rows.moveToFirst());assertEquals(4,rows.getInt(0));}
            try(var rows=sql.query("SELECT count(*) FROM band_aliases")) {assertTrue(rows.moveToFirst());assertEquals(0,rows.getInt(0));}
            try(var rows=sql.query("SELECT count(*) FROM rating_corrections")) {assertTrue(rows.moveToFirst());}
        } finally {helper.close();context.deleteDatabase(name);}
    }
}
