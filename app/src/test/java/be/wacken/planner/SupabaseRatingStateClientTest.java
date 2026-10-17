package be.wacken.planner;

import static org.junit.Assert.*;
import be.wacken.planner.domain.*;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import org.json.*;
import org.junit.Test;

public final class SupabaseRatingStateClientTest {
    private final String user="11111111-1111-1111-1111-111111111111";
    private final List<String> paths=new ArrayList<>();
    @Test public void resetHasExplicitFestivalCanonicalBandAndStableOperationPayload() throws Exception{
        var state=new FestivalAfterRating(UUID.randomUUID().toString(),user,"festival",new Band("Band"),Rating.of(0),Instant.now());
        var client=new SupabaseRatingStateClient((method,path,body,upsert)->{
            paths.add(path);
            if(method.equals("GET"))return "[{\"id\":\"canonical-band\"}]";
            try {
            JSONObject request=new JSONObject(body);
            assertEquals(state.operationId(),request.getString("p_operation_id"));
            assertEquals("festival",request.getString("p_festival_id"));assertEquals("canonical-band",request.getString("p_band_id"));
            assertEquals(0,request.getInt("p_rating"));assertFalse(request.has("user_id"));
            return "{\"confirmed\":true,\"operation_id\":\""+state.operationId()+"\"}";
            } catch(JSONException error){throw new IOException(error);}
        },user);
        client.push(state);assertEquals("/rest/v1/rpc/save_festival_after_rating",paths.get(1));
    }
    @Test public void anotherMembersWriteIsRejectedBeforeNetwork() throws Exception{
        var client=new SupabaseRatingStateClient((m,p,b,u)->{throw new AssertionError();},user);
        try{client.push(new FestivalAfterRating(UUID.randomUUID().toString(),"other","festival",new Band("Band"),Rating.of(3),Instant.now()));fail();}
        catch(IOException expected){}
    }
    @Test public void parsesScopedRecoveredValueAndEstablishedProvenanceWithoutJsonNullStrings() throws Exception{
        var client=new SupabaseRatingStateClient((m,p,b,u)->p.contains("festival_after_ratings")
                ?"[{\"operation_id\":\"44444444-4444-4444-8444-444444444444\",\"user_id\":\""+user+"\",\"festival_id\":\"festival\",\"band\":{\"name\":\"Band\"},\"rating\":0,\"recorded_at\":\"2026-10-17T14:00:00+00:00\",\"requires_review\":true}]"
                :"[{\"user_id\":\""+user+"\",\"band\":{\"name\":\"Band\"},\"rating\":4,\"source_festival_id\":\"old\",\"source_festival_name\":\"Old\",\"source_event_id\":\"event\",\"established_at\":\"2026-08-01T12:00:00Z\"}]",user);
        assertTrue(client.afterRatings().get(0).requiresReview());assertEquals(0,client.afterRatings().get(0).rating().value());
        assertEquals("old",client.currentRatings().get(0).sourceFestivalId());
    }
    @Test public void malformedOrWrongOwnerSnapshotCannotClearExistingCache() throws Exception{
        for(String row:List.of("{\"user_id\":null}","{\"user_id\":\"other\"}")){
            var client=new SupabaseRatingStateClient((m,p,b,u)->"["+row+"]",user);
            try{client.afterRatings();fail("Invalid snapshot was accepted");}catch(IOException expected){}
        }
    }
    @Test public void malformedTimestampIsReportedAsASnapshotFailure() throws Exception{
        JSONObject row=new JSONObject().put("user_id",user).put("festival_id","festival")
                .put("operation_id","44444444-4444-4444-8444-444444444444").put("rating",3)
                .put("band",new JSONObject().put("name","Band")).put("recorded_at","not-a-date").put("requires_review",false);
        var client=new SupabaseRatingStateClient((m,p,b,u)->"["+row+"]",user);
        try{client.afterRatings();fail("A malformed source date was accepted");}catch(IOException expected){}
    }
    @Test public void anAcknowledgementForADifferentOperationCannotClaimSuccess() throws Exception{
        var client=new SupabaseRatingStateClient((m,p,b,u)->m.equals("GET")?"[{\"id\":\"canonical-band\"}]"
                :"{\"confirmed\":true,\"operation_id\":\"different\"}",user);
        try{client.push(new FestivalAfterRating(UUID.randomUUID().toString(),user,"festival",new Band("Band"),Rating.of(3),Instant.now()));fail();}
        catch(IOException expected){}
    }
}
