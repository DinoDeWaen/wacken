package be.wacken.planner;

import static org.junit.Assert.*;
import be.wacken.planner.domain.*;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import org.junit.Test;

public final class RatingStateSyncTest {
    private final String user="user";
    private final Band band=new Band("Band");
    private final Map<String,FestivalAfterRating> pending=new HashMap<>();
    private final List<FestivalAfterRating> acknowledged=new ArrayList<>();
    private final List<String> calls=new ArrayList<>();
    private final RatingStateSyncStore cache=new RatingStateSyncStore(){
        public void save(FestivalAfterRating r){pending.put(r.operationId(),r);}
        public Optional<FestivalAfterRating> findByUserFestivalAndBand(String u,String f,Band b){return Optional.empty();}
        public List<FestivalAfterRating> findByUser(String u){return List.copyOf(pending.values());}
        public List<FestivalAfterRating> findByFestival(String f){return List.copyOf(pending.values());}
        public List<FestivalAfterRating> findPending(String u){return List.copyOf(pending.values());}
        public void acknowledge(FestivalAfterRating r){acknowledged.add(r);pending.remove(r.operationId());}
        public void mergeRemote(List<FestivalAfterRating> r,String u){calls.add("after-pull");}
        public void replaceCurrent(List<PersonalBandRating> r,String u){calls.add("band-pull");}
    };
    @Test public void recoveredLegacyPendingDataCannotBeSentAsExplicitlyConfirmed() throws Exception{
        var recovered=new FestivalAfterRating(UUID.randomUUID().toString(),user,"festival",band,Rating.of(3),Instant.now(),true);
        pending.put(recovered.operationId(),recovered);
        sync(false).sync();
        assertTrue(acknowledged.isEmpty());assertEquals(1,pending.size());
        assertEquals(List.of("after-pull","band-pull"),calls);
    }
    @Test public void resetIsPushedAndAcknowledgedBeforePull() throws Exception{
        var reset=new FestivalAfterRating(UUID.randomUUID().toString(),user,"festival",band,Rating.of(0),Instant.now());
        pending.put(reset.operationId(),reset);sync(false).sync();
        assertEquals(List.of(reset),acknowledged);assertEquals(List.of("push0","after-pull","band-pull"),calls);
    }
    @Test public void remoteFailureKeepsPendingAndCannotPullOverTheUnconfirmedWrite() throws Exception{
        var rating=new FestivalAfterRating(UUID.randomUUID().toString(),user,"festival",band,Rating.of(3),Instant.now());
        pending.put(rating.operationId(),rating);
        try{sync(true).sync();fail("Expected offline failure");}catch(IOException expected){}
        assertTrue(acknowledged.isEmpty());assertEquals(1,pending.size());assertTrue(calls.isEmpty());
    }
    private RatingStateSync sync(boolean offline){
        return new RatingStateSync(cache,new RatingStateSync.Remote(){
            public void push(FestivalAfterRating r)throws IOException{if(offline)throw new IOException("offline");calls.add("push"+r.rating().value());}
            public List<FestivalAfterRating> afterRatings(){return List.of();}
            public List<PersonalBandRating> currentRatings(){return List.of();}
        },user);
    }
}

