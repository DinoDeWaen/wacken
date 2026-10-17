package be.wacken.planner;

import static org.junit.Assert.*;
import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;
import be.wacken.planner.application.*;
import be.wacken.planner.domain.*;
import be.wacken.planner.persistence.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.*;

public final class FestivalArchiveAndroidTest {
    private WackenDatabase database;
    private final String user="fixture-user";
    private final Band band=new Band("Fixture Band");
    private final Festival festival=Festival.active("fixture-festival","Fixture Festival");
    @Before public void setup() {
        database=Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(),WackenDatabase.class).allowMainThreadQueries().build();
        new RoomFestivalRepository(database).save(festival);
        new RoomFestivalPlanningRatingRepository(database).saveSynced(new SavedFestivalPlanningRating("group",user,festival.id(),band,Rating.of(5)));
        database.ratingStates().saveCurrent(new RoomPersonalBandRating(user,band.name(),4,"old","Old","old-event","2026-01-01T00:00:00Z"));
        database.ratingStates().saveAfter(new RoomFestivalAfterRating(user,festival.id(),band.name(),"aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa",3,"2026-10-17T12:00:00Z","SYNCED",false));
    }
    @After public void cleanup(){database.close();}
    @Test public void acknowledgedArchivePublishesStatusAndProvenanceWithoutChangingFestivalRatings() {
        new RoomFestivalArchiveCache(database,user,()->false).accept(snapshot());
        assertTrue(new RoomFestivalRepository(database).findAll().get(0).isArchived());
        assertEquals(3,database.ratingStates().current(user,band.name()).value);
        assertEquals(festival.id(),database.ratingStates().current(user,band.name()).sourceFestivalId);
        assertEquals(5,new RoomFestivalPlanningRatingRepository(database).findByUserFestivalAndBand(user,festival.id(),band).orElseThrow().value());
        assertEquals(3,database.ratingStates().after(user,festival.id(),band.name()).value);
    }
    @Test public void pendingEditArrivingDuringRemoteArchiveIsRetainedForRecovery() {
        RoomFestivalAfterRatingRepository after=new RoomFestivalAfterRatingRepository(database);
        after.save(new FestivalAfterRating(UUID.randomUUID().toString(),user,festival.id(),band,Rating.of(2),Instant.now()));
        try {new RoomFestivalArchiveCache(database,user,()->!after.findPending(user).isEmpty()).accept(snapshot());fail();}
        catch(IllegalStateException expected){assertTrue(expected.getMessage().contains("retained"));}
        assertTrue(new RoomFestivalRepository(database).findAll().get(0).isActive());
        assertEquals(4,database.ratingStates().current(user,band.name()).value);
        assertEquals(2,after.findPending(user).get(0).rating().value());
    }
    @Test public void localFailureRollsBackRatingSnapshotsAndFestivalStatus() {
        database.getOpenHelper().getWritableDatabase().execSQL("CREATE TRIGGER reject_archive_cache BEFORE INSERT ON festivals WHEN NEW.status='ARCHIVED' BEGIN SELECT RAISE(ABORT,'Injected cache failure'); END");
        try {new RoomFestivalArchiveCache(database,user,()->false).accept(snapshot());fail();}
        catch(RuntimeException expected){assertTrue(new RoomFestivalRepository(database).findAll().get(0).isActive());}
        assertEquals(4,database.ratingStates().current(user,band.name()).value);
        assertEquals(3,database.ratingStates().after(user,festival.id(),band.name()).value);
    }
    @Test public void failedWorkerArchiveReportsFailureOnMainThreadWithoutPublishingSuccess()throws Exception {
        CountDownLatch finished=new CountDownLatch(1);
        AtomicReference<String> failure=new AtomicReference<>();
        AtomicReference<FestivalStartState> success=new AtomicReference<>();
        RoomFestivalRepository festivals=new RoomFestivalRepository(database);
        new ArchiveFestivalAction(command->new Thread(command).start(),
                command->InstrumentationRegistry.getInstrumentation().runOnMainSync(command),
                ()->new ArchiveActiveFestivalUseCase(festivals,f->{throw new SupabaseSyncException("Offline: pending work retained",null);}))
                .execute(state->{success.set(state);finished.countDown();},error->{
                    assertEquals(android.os.Looper.getMainLooper(),android.os.Looper.myLooper());
                    failure.set(error);finished.countDown();
                });
        assertTrue(finished.await(10,TimeUnit.SECONDS));
        assertNull(success.get());assertTrue(failure.get().contains("retained"));
        assertTrue(festivals.findAll().get(0).isActive());
        assertEquals(4,database.ratingStates().current(user,band.name()).value);
    }
    private SupabaseFestivalArchiveGateway.Snapshot snapshot(){
        return new SupabaseFestivalArchiveGateway.Snapshot(festival.archive(),
                List.of(new FestivalAfterRating("aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa",user,festival.id(),band,Rating.of(3),Instant.parse("2026-10-17T12:00:00Z"))),
                List.of(new PersonalBandRating(user,band,Rating.of(3),festival.id(),festival.name(),"aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa",Instant.parse("2026-10-17T15:00:00Z"))));
    }
}
