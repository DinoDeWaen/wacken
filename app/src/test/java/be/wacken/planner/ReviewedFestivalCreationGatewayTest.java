package be.wacken.planner;

import be.wacken.planner.domain.*;
import be.wacken.planner.application.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.*;
import org.junit.Test;
import static org.junit.Assert.*;

public final class ReviewedFestivalCreationGatewayTest {
    @Test public void retryUsesStableReviewedPayloadAndNeverLegacyRpc() throws Exception {
        List<String> bodies=new ArrayList<>();
        String acknowledged=response("f",true);
        var gateway=new SupabaseFestivalCreationGateway(new SupabaseMasterDataClient((method,path,body,upsert)->{
            assertEquals("/rest/v1/rpc/create_festival_from_reviewed_bands",path);bodies.add(body);
            return acknowledged;
        }),snapshot->{});
        var selected=new ReviewedFestivalBand("AF",Optional.of(new CanonicalBand("canonical",new Band("Abbie Falls"))),true);
        var fresh=new ReviewedFestivalBand("New",Optional.empty(),false);
        assertTrue(gateway.createReviewed("g","u",Festival.active("f","Next"),List.of(selected,fresh)).success());
        assertTrue(gateway.createReviewed("g","u",Festival.active("f","Next"),List.of(fresh,selected)).success());
        assertEquals(bodies.get(0),bodies.get(1));
        JSONObject row=new JSONObject(bodies.get(0)).getJSONArray("p_rows").getJSONObject(0);
        assertEquals("canonical",row.getString("band_id"));assertTrue(row.getBoolean("remember_alias"));
    }
    @Test public void incompleteOrDifferentFestivalAcknowledgementCannotPublishCache() throws Exception {
        AtomicInteger writes=new AtomicInteger();
        for(String result:List.of(response("other",true),response("f",false))) {
            var gateway=new SupabaseFestivalCreationGateway(new SupabaseMasterDataClient((m,p,b,u)->result),s->writes.incrementAndGet());
            try {gateway.createReviewed("g","u",Festival.active("f","Next"),List.of(new ReviewedFestivalBand("New",Optional.empty(),false)));fail("Expected bad acknowledgement");}
            catch(SupabaseSyncException expected) {}
        }
        assertEquals(0,writes.get());
    }
    private String response(String id,boolean aliases) throws Exception {
        JSONObject festival=new JSONObject().put("id",id).put("name","Next").put("status","ACTIVE");
        JSONArray lineup=new JSONArray().put(new JSONObject().put("band_name","Abbie Falls").put("uploaded_display_name","AF"))
            .put(new JSONObject().put("band_name","New").put("uploaded_display_name","New"));
        JSONObject result=new JSONObject().put("group_id","g").put("user_id","u").put("festival",festival)
            .put("festivals",new JSONArray().put(festival)).put("bands",new JSONArray())
            .put("lineup",lineup).put("planning_ratings",new JSONArray()).put("reused_bands",0)
            .put("created_bands",1).put("prefilled_ratings",0);
        if(aliases)result.put("aliases",new JSONArray());
        return result.toString();
    }
}
