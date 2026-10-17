package be.wacken.planner;

import static org.junit.Assert.*;
import be.wacken.planner.domain.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.*;
import org.junit.Test;

public final class ArchivedRatingCorrectionGatewayTest {
    private final String user="member", operation="aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa";
    private final AtomicInteger writes=new AtomicInteger();
    @Test public void sendsReviewedRevisionWithoutClientSuppliedActorAndPublishesOnlyConfirmedAudit(){
        var client=client((method,path,body,upsert)->{
            if(method.equals("GET"))return "[{\"id\":\"band\"}]";
            if(path.endsWith("review_archived_rating"))return "{\"user_id\":\"member\",\"festival_id\":\"festival\",\"band_id\":\"band\",\"band_name\":\"Band\",\"kind\":\"AFTER\",\"rating\":2,\"revision\":8,\"recorded_at\":null}";
            try {JSONObject command=new JSONObject(body);
                assertEquals(7,command.getLong("p_expected_revision"));assertFalse(command.has("user_id"));
                assertEquals(operation,command.getString("p_operation_id"));
            }catch(JSONException error){throw new IOException(error);}
            return receipt(user);
        });
        var saved=client.correct(operation,review(),2);
        assertEquals(2,saved.newValue());assertEquals(1,writes.get());
    }
    @Test public void staleOrOfflineWriteNeverPublishesCache(){
        var client=client((method,path,body,upsert)->{
            if(method.equals("GET"))return "[{\"id\":\"band\"}]";
            throw new IOException("Reload the rating");
        });
        try{client.correct(operation,review(),2);fail();}
        catch(SupabaseSyncException expected){assertTrue(expected.getMessage().contains("Reload"));}
        assertEquals(0,writes.get());
    }
    @Test public void anotherOwnersReceiptCannotBePublished(){
        var client=client((method,path,body,upsert)->method.equals("GET")?"[{\"id\":\"band\"}]":receipt("other"));
        try{client.correct(operation,review(),2);fail();}
        catch(SupabaseSyncException expected){}
        assertEquals(0,writes.get());
    }
    private ArchivedRatingReview review(){return new ArchivedRatingReview(user,"festival",new Band("Band"),ArchivedRatingKind.AFTER,3,7,Optional.empty());}
    private SupabaseArchivedRatingCorrectionGateway client(SupabaseMasterDataClient.RequestExecutor requests){
        RatingStateSync.Remote ratings=new RatingStateSync.Remote(){
            public void push(FestivalAfterRating ignored){fail();}
            public List<FestivalAfterRating> afterRatings(){return List.of();}
            public List<PersonalBandRating> currentRatings(){return List.of();}
        };
        SupabaseArchivedRatingCorrectionGateway.Cache cache=new SupabaseArchivedRatingCorrectionGateway.Cache(){
            public SupabaseArchivedRatingCorrectionGateway.Pending capture(ArchivedRatingReview expected){return new SupabaseArchivedRatingCorrectionGateway.Pending(null,null);}
            public void apply(SupabaseArchivedRatingCorrectionGateway.Snapshot snapshot){writes.incrementAndGet();}
            public void appendAudits(List<RatingCorrection> audit){}
        };
        return new SupabaseArchivedRatingCorrectionGateway(requests,user,ratings,cache);
    }
    private String receipt(String owner){
        return "{\"confirmed\":true,\"correction\":{\"operation_id\":\""+operation+"\",\"user_id\":\""+owner+"\",\"festival_id\":\"festival\",\"festival_name_snapshot\":\"Festival\",\"band_name_snapshot\":\"Band\",\"kind\":\"AFTER\",\"previous_value\":3,\"new_value\":2,\"expected_revision\":7,\"original_recorded_at\":null,\"corrected_at\":\"2026-10-17T12:00:00Z\"}}";
    }
}
