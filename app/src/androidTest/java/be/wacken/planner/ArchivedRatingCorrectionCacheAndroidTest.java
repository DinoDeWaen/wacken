package be.wacken.planner;

import static org.junit.Assert.*;
import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;
import be.wacken.planner.domain.*;
import be.wacken.planner.persistence.*;
import java.time.Instant;
import java.util.*;
import org.junit.*;

public final class ArchivedRatingCorrectionCacheAndroidTest {
    private WackenDatabase database;
    private RoomArchivedRatingCorrectionCache cache;
    private final Band band=new Band("Band");
    private final Instant time=Instant.parse("2026-10-17T12:00:00Z");
    @Before public void setup(){
        database=Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(),WackenDatabase.class).allowMainThreadQueries().build();
        cache=new RoomArchivedRatingCorrectionCache(database,"group","member");
        new RoomFestivalRepository(database).save(Festival.archived("festival","Festival"));
    }
    @After public void cleanup(){database.close();}
    @Test public void acknowledgedCorrectionResolvesOnlyReviewedPendingOperationAndRetainsItsOriginalHistory(){
        var local=new RoomFestivalAfterRatingRepository(database);
        var pending=after(2);local.save(pending);
        var captured=cache.capture(review());
        cache.apply(snapshot(captured));
        assertTrue(local.findPending("member").isEmpty());
        assertEquals(3,local.findByUserFestivalAndBand("member","festival",band).orElseThrow().rating().value());
        assertEquals(2,database.personalBandRatingEvents().findByUserAndFestival("member","festival").size());
        assertEquals(1,new RoomRatingCorrectionRepository(database).findByUser("member").size());
    }
    @Test public void newerPendingEditPreventsAnyCachePublication(){
        var local=new RoomFestivalAfterRatingRepository(database);local.save(after(2));
        var captured=cache.capture(review());var newer=after(5);local.save(newer);
        try{cache.apply(snapshot(captured));fail();}catch(IllegalStateException expected){}
        assertEquals(newer,local.findPending("member").get(0));
        assertTrue(new RoomRatingCorrectionRepository(database).findByUser("member").isEmpty());
        assertTrue(new RoomPersonalBandRatingRepository(database).findByUser("member").isEmpty());
    }
    @Test public void auditFailureRollsBackRatingsAndPendingResolution(){
        var local=new RoomFestivalAfterRatingRepository(database);local.save(after(2));
        var captured=cache.capture(review());
        database.getOpenHelper().getWritableDatabase().execSQL("CREATE TRIGGER reject_audit BEFORE INSERT ON rating_corrections BEGIN SELECT RAISE(ABORT,'Injected audit failure'); END");
        try{cache.apply(snapshot(captured));fail();}catch(RuntimeException expected){}
        assertEquals(2,local.findPending("member").get(0).rating().value());
        assertEquals(1,database.personalBandRatingEvents().findPendingByUser("member").size());
        assertTrue(new RoomPersonalBandRatingRepository(database).findByUser("member").isEmpty());
    }
    private ArchivedRatingReview review(){return new ArchivedRatingReview("member","festival",band,ArchivedRatingKind.AFTER,4,7,Optional.of(time));}
    private FestivalAfterRating after(int value){return new FestivalAfterRating(UUID.randomUUID().toString(),"member","festival",band,Rating.of(value),time);}
    private SupabaseArchivedRatingCorrectionGateway.Snapshot snapshot(SupabaseArchivedRatingCorrectionGateway.Pending pending){
        var server=new FestivalAfterRating("bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb","member","festival",band,Rating.of(3),time);
        var audit=new RatingCorrection("aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa","member","festival","Festival",band,ArchivedRatingKind.AFTER,4,3,7,Optional.of(time),time.plusSeconds(60));
        return new SupabaseArchivedRatingCorrectionGateway.Snapshot(
            new ArchivedRatingReview("member","festival",band,ArchivedRatingKind.AFTER,3,8,Optional.of(time)),
            audit,pending,List.of(server),List.of(new PersonalBandRating("member",band,Rating.of(3),"festival","Festival",server.operationId(),time)));
    }
}
