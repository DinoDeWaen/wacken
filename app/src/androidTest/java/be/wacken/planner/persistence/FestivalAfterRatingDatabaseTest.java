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

public final class FestivalAfterRatingDatabaseTest {
    private WackenDatabase database;
    private RoomFestivalAfterRatingRepository repository;
    private final Band band=new Band("One catalog band");

    @Before public void setup(){
        database=Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(),WackenDatabase.class)
                .allowMainThreadQueries().build();
        repository=new RoomFestivalAfterRatingRepository(database);
        new RoomBandRepository(database).save(band);
        new RoomFestivalRepository(database).save(Festival.active("active","Active"));
        new RoomFestivalRepository(database).save(Festival.archived("archived","Archived"));
    }
    @After public void close(){database.close();}

    @Test public void afterAndHistorySaveTogetherWithoutTouchingEstablishedOrPlanningRatings(){
        database.ratingStates().saveCurrent(new RoomPersonalBandRating("me",band.name(),4,"archived","Archived",
                UUID.randomUUID().toString(),"2026-08-01T12:00:00Z"));
        database.festivalPlanningRatings().save(new RoomFestivalPlanningRating("group","me","active",band.name(),5,"PENDING"));
        repository.save(rating("me","active",3));
        assertEquals(3,repository.findByUserFestivalAndBand("me","active",band).orElseThrow().rating().value());
        assertEquals(4,new RoomPersonalBandRatingRepository(database).findByUserAndBand("me",band).orElseThrow().rating().value());
        assertEquals(5,database.festivalPlanningRatings().findExact("group","me","active",band.name()).value);
        assertEquals(1,database.personalBandRatingEvents().findByUserAndFestival("me","active").size());
    }
    @Test public void resetIsPendingAndCannotResurrectAPreviousPositiveHistoryEvent(){
        var positive=rating("me","active",4);repository.save(positive);
        repository.save(rating("me","active",0));
        assertEquals(0,repository.findByUserFestivalAndBand("me","active",band).orElseThrow().rating().value());
        assertEquals(1,repository.findPending("me").size());
        assertEquals(1,database.personalBandRatingEvents().findByUserAndFestival("me","active").size());
    }
    @Test public void lateAcknowledgementAndPullCannotOverwriteNewerPendingEdits(){
        var old=rating("me","active",3);repository.save(old);
        var newer=rating("me","active",5);repository.save(newer);
        repository.acknowledge(old);
        repository.mergeRemote(List.of(old),"me");
        assertEquals(newer,repository.findByUserFestivalAndBand("me","active",band).orElseThrow());
        assertEquals(1,repository.findPending("me").size());
        repository.acknowledge(newer);assertTrue(repository.findPending("me").isEmpty());
        repository.mergeRemote(List.of(),"me");
        assertTrue(repository.findByUserFestivalAndBand("me","active",band).isEmpty());
        assertEquals(2,database.personalBandRatingEvents().findByUserAndFestival("me","active").size());
    }
    @Test public void differentMembersAndFestivalsHaveSeparateBusinessKeys(){
        repository.save(rating("me","active",3));repository.save(rating("friend","active",5));
        repository.save(rating("me","archived",4));
        assertEquals(2,repository.findByUser("me").size());
        assertEquals(1,repository.findByUser("friend").size());
        assertEquals(2,repository.findByFestival("active").size());
    }
    @Test public void sqliteFailureRollsBackAfterAndHistoryTogether(){
        database.getOpenHelper().getWritableDatabase().execSQL(
                "CREATE TRIGGER fail_after_history BEFORE INSERT ON personal_band_rating_events BEGIN SELECT RAISE(ABORT,'injected history failure'); END");
        try{repository.save(rating("me","active",3));fail("Expected transactional failure");}
        catch(android.database.sqlite.SQLiteException expected){}
        assertTrue(repository.findByUser("me").isEmpty());
    }
    @Test public void unknownLegacyOwnershipIsFlaggedWithoutAssigningItToTheSignedInMember(){
        database.realRatings().save(new RoomRealRating("unassigned-legacy-user",band.name(),4));
        assertTrue(database.ratingStates().hasUnresolvedLegacy(band.name()));
        assertTrue(new RoomPersonalBandRatingRepository(database).findByUserAndBand("me",band).isEmpty());
        assertTrue(repository.findByUser("me").isEmpty());
        assertEquals(4,database.realRatings().findAll().get(0).value);
    }
    @Test public void versionNineMigrationKeepsHistoryAndPendingWorkWithoutPromotingActiveOrUnknownDates(){
        SupportSQLiteOpenHelper helper=new FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(InstrumentationRegistry.getInstrumentation().getTargetContext()).name(null)
                .callback(new SupportSQLiteOpenHelper.Callback(9){
                    @Override public void onCreate(SupportSQLiteDatabase sql){
                        sql.execSQL("CREATE TABLE festivals (id TEXT,name TEXT,status TEXT)");
                        sql.execSQL("INSERT INTO festivals VALUES ('old','Old','ARCHIVED'),('now','Now','ACTIVE')");
                        sql.execSQL("CREATE TABLE real_ratings (userName TEXT,bandName TEXT,value INTEGER)");
                        sql.execSQL("INSERT INTO real_ratings VALUES ('me','Band',3),('me','Unknown',5)");
                        sql.execSQL("CREATE TABLE personal_band_rating_events (id TEXT,userName TEXT,bandName TEXT,festivalId TEXT,festivalNameSnapshot TEXT,value INTEGER,createdAt TEXT,syncStatus TEXT)");
                        sql.execSQL("INSERT INTO personal_band_rating_events VALUES "+
                            "('11111111-1111-4111-8111-111111111111','me','Band','old','Old',4,'2026-08-01T12:00:00Z','SYNCED'),"+
                            "('22222222-2222-4222-8222-222222222222','me','Band','now','Now',3,'2026-10-01T12:00:00Z','PENDING'),"+
                            "('legacy','me','Unknown','old','Old',5,'1970-01-01T00:00:00Z','PENDING')");
                    }
                    @Override public void onUpgrade(SupportSQLiteDatabase sql,int oldVersion,int newVersion){}
                }).build());
        try{
            SupportSQLiteDatabase sql=helper.getWritableDatabase();WackenDatabase.MIGRATION_9_10.migrate(sql);
            try(var rows=sql.query("SELECT value,sourceFestivalId FROM personal_band_ratings WHERE bandName='Band'")){
                assertTrue(rows.moveToFirst());assertEquals(4,rows.getInt(0));assertEquals("old",rows.getString(1));
            }
            try(var rows=sql.query("SELECT value,syncStatus,requiresReview FROM festival_after_ratings WHERE festivalId='now'")){
                assertTrue(rows.moveToFirst());assertEquals(3,rows.getInt(0));assertEquals("REVIEW",rows.getString(1));assertEquals(1,rows.getInt(2));
            }
            try(var rows=sql.query("SELECT COUNT(*) FROM personal_band_ratings WHERE bandName='Unknown'")){assertTrue(rows.moveToFirst());assertEquals(0,rows.getInt(0));}
            try(var rows=sql.query("SELECT COUNT(*) FROM personal_band_rating_events")){assertTrue(rows.moveToFirst());assertEquals(3,rows.getInt(0));}
            try(var rows=sql.query("SELECT COUNT(*) FROM real_ratings")){assertTrue(rows.moveToFirst());assertEquals(2,rows.getInt(0));}
        }finally{helper.close();}
    }
    private FestivalAfterRating rating(String user,String festival,int value){
        return new FestivalAfterRating(UUID.randomUUID().toString(),user,festival,band,Rating.of(value),Instant.now());
    }
}
