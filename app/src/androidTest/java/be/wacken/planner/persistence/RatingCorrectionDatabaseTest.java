package be.wacken.planner.persistence;

import static org.junit.Assert.*;
import androidx.room.Room;
import androidx.sqlite.db.*;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.platform.app.InstrumentationRegistry;
import be.wacken.planner.domain.*;
import java.time.Instant;
import java.util.*;
import org.junit.*;

public final class RatingCorrectionDatabaseTest {
    private WackenDatabase database;
    @Before public void setup(){
        database=Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(),WackenDatabase.class).allowMainThreadQueries().build();
    }
    @After public void cleanup(){database.close();}
    @Test public void replayCannotRewriteAuditAndOwnersRemainSeparate(){
        var repository=new RoomRatingCorrectionRepository(database);
        String operation=UUID.randomUUID().toString();
        repository.append(audit(operation,"member",3));
        repository.append(audit(operation,"member",5));
        repository.append(audit(UUID.randomUUID().toString(),"other",2));
        var facts=repository.findByUserFestivalAndBand("member","archived",new Band("Band"));
        assertEquals(1,facts.size());assertEquals(3,facts.get(0).newValue());
        assertEquals(Optional.of(Instant.parse("2026-08-01T12:00:00Z")),facts.get(0).originalRecordedAt());
        assertEquals(1,repository.findByUser("other").size());
    }
    @Test public void festivalDeletionKeepsItsCorrectionContext(){
        var repository=new RoomRatingCorrectionRepository(database);
        repository.append(audit(UUID.randomUUID().toString(),"member",3));
        new RoomFestivalRepository(database).save(Festival.archived("archived","Archived"));
        database.festivals().deleteById("archived");
        assertEquals("Archived",repository.findByUser("member").get(0).festivalName());
    }
    @Test public void additiveVersionTenMigrationLeavesExistingPendingDataUntouched(){
        var helper=new FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(InstrumentationRegistry.getInstrumentation().getTargetContext()).name(null)
                .callback(new SupportSQLiteOpenHelper.Callback(10){
                    public void onCreate(SupportSQLiteDatabase sql){
                        sql.execSQL("CREATE TABLE retained_pending(id TEXT,value INTEGER,syncStatus TEXT)");
                        sql.execSQL("INSERT INTO retained_pending VALUES('pending',3,'PENDING')");
                    }
                    public void onUpgrade(SupportSQLiteDatabase sql,int oldVersion,int newVersion){}
                }).build());
        try{
            var sql=helper.getWritableDatabase();WackenDatabase.MIGRATION_10_11.migrate(sql);
            try(var rows=sql.query("SELECT value,syncStatus FROM retained_pending")){
                assertTrue(rows.moveToFirst());assertEquals(3,rows.getInt(0));assertEquals("PENDING",rows.getString(1));
            }
            sql.execSQL("INSERT INTO rating_corrections VALUES('operation','member','archived','Archived','Band','AFTER',4,3,0,NULL,'2026-10-17T12:00:00Z')");
            try(var rows=sql.query("SELECT originalRecordedAt FROM rating_corrections")){
                assertTrue(rows.moveToFirst());assertTrue(rows.isNull(0));
            }
        }finally{helper.close();}
    }
    private RatingCorrection audit(String operation,String user,int value){
        return new RatingCorrection(operation,user,"archived","Archived",new Band("Band"),ArchivedRatingKind.AFTER,4,value,0,
            Optional.of(Instant.parse("2026-08-01T12:00:00Z")),Instant.parse("2026-10-17T12:00:00Z"));
    }
}
