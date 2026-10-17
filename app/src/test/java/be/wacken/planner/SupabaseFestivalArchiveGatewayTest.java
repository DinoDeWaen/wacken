package be.wacken.planner;

import static org.junit.Assert.*;
import be.wacken.planner.domain.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONObject;
import org.junit.Test;

public final class SupabaseFestivalArchiveGatewayTest {
    @Test public void publishesOnlyAfterConfirmedArchiveAndBothPrivateSnapshots() throws Exception {
        List<SupabaseFestivalArchiveGateway.Snapshot> cached=new ArrayList<>();
        List<String> calls=new ArrayList<>();
        SupabaseFestivalArchiveGateway gateway=new SupabaseFestivalArchiveGateway((m,p,b,u)->{
            calls.add(p);assertEquals("POST",m);
            try {assertEquals("festival",new JSONObject(b).getString("p_festival_id"));}
            catch(org.json.JSONException error){throw new IOException(error);}
            return response("festival",true);
        },remote(calls,false),cached::add);
        gateway.archive(Festival.active("festival","Festival"));
        assertEquals(List.of("/rest/v1/rpc/archive_festival","after","current"),calls);
        assertTrue(cached.get(0).festival().isArchived());
    }
    @Test public void offlineOrUnconfirmedArchiveNeverPublishesCache() {
        for(String response:List.of("offline",response("festival",false),response("other",true))) {
            AtomicInteger writes=new AtomicInteger();
            SupabaseFestivalArchiveGateway gateway=new SupabaseFestivalArchiveGateway((m,p,b,u)->{
                if(response.equals("offline"))throw new IOException("Network unavailable");return response;
            },remote(new ArrayList<>(),false),s->writes.incrementAndGet());
            try {gateway.archive(Festival.active("festival","Festival"));fail("Expected unconfirmed archive");}
            catch(SupabaseSyncException expected){assertTrue(expected.getMessage().contains("retained"));}
            assertEquals(0,writes.get());
        }
    }
    @Test public void failedSecondSnapshotKeepsAllLocalStateForRetry() {
        AtomicInteger writes=new AtomicInteger();
        SupabaseFestivalArchiveGateway gateway=new SupabaseFestivalArchiveGateway((m,p,b,u)->response("festival",true),
                remote(new ArrayList<>(),true),s->writes.incrementAndGet());
        try {gateway.archive(Festival.active("festival","Festival"));fail();}
        catch(SupabaseSyncException expected){assertTrue(expected.getMessage().contains("Retry Archive"));}
        assertEquals(0,writes.get());
    }
    @Test public void cacheConflictReportsCommittedRemoteStateAndPreservesRecovery() {
        SupabaseFestivalArchiveGateway gateway=new SupabaseFestivalArchiveGateway((m,p,b,u)->response("festival",true),
                remote(new ArrayList<>(),false),s->{throw new IllegalStateException("Pending edit");});
        try {gateway.archive(Festival.active("festival","Festival"));fail();}
        catch(SupabaseSyncException expected){assertTrue(expected.getMessage().contains("saved as archived group-wide"));}
    }
    private RatingStateSync.Remote remote(List<String> calls,boolean failCurrent) {
        return new RatingStateSync.Remote(){
            public void push(FestivalAfterRating ignored){fail("Archive must sync before entering this gateway");}
            public List<FestivalAfterRating> afterRatings(){calls.add("after");return List.of();}
            public List<PersonalBandRating> currentRatings()throws IOException{
                calls.add("current");if(failCurrent)throw new IOException("Pull failed");return List.of();
            }
        };
    }
    private static String response(String festival,boolean confirmed) {
        return "{\"confirmed\":"+confirmed+",\"festival_id\":\""+festival+"\",\"operation_id\":\"aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa\","
                +"\"festival\":{\"id\":\""+festival+"\",\"name\":\"Festival\",\"status\":\"ARCHIVED\"}}";
    }
}
