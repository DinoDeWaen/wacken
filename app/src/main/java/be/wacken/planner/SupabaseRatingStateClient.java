package be.wacken.planner;

import be.wacken.planner.domain.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import org.json.*;

/** Reuses authenticated transport. Source facts are keyed by canonical server band identity. */
final class SupabaseRatingStateClient implements RatingStateSync.Remote {
    private final SupabaseMasterDataClient.RequestExecutor requests;
    private final String user;
    SupabaseRatingStateClient(SupabaseSessionManager sessions,String user){
        this(new SupabaseMasterDataClient(sessions)::request,user);
    }
    SupabaseRatingStateClient(SupabaseMasterDataClient.RequestExecutor requests,String user){
        this.requests=requests;this.user=user;
    }

    @Override public void push(FestivalAfterRating rating)throws IOException{
        if(!user.equals(rating.userName()))throw new IOException("Cannot save another member's after-rating.");
        try{
            JSONArray matches=new JSONArray(requests.execute("GET","/rest/v1/bands?select=id&name=eq."+encode(rating.band().name())+"&limit=2",null,false));
            if(matches.length()!=1)throw new IOException("The canonical band could not be identified uniquely. Refresh the lineup.");
            JSONObject body=new JSONObject().put("p_operation_id",rating.operationId())
                    .put("p_festival_id",rating.festivalId()).put("p_band_id",text(matches.getJSONObject(0),"id"))
                    .put("p_rating",rating.rating().value()).put("p_recorded_at",rating.recordedAt().toString());
            JSONObject result=new JSONObject(requests.execute("POST","/rest/v1/rpc/save_festival_after_rating",body.toString(),false));
            if(!result.optBoolean("confirmed",false)||!rating.operationId().equals(result.optString("operation_id","")))
                throw new IOException("After-rating save could not be confirmed. Retry pending work.");
        }catch(JSONException|IllegalArgumentException error){throw new IOException("After-rating response could not be read. Refresh or retry pending work.",error);}
    }
    @Override public List<FestivalAfterRating> afterRatings()throws IOException{
        List<FestivalAfterRating> result=new ArrayList<>();
        try{
            for(JSONObject row:rows("festival_after_ratings","user_id,festival_id,operation_id,rating,recorded_at,requires_review,band:bands(name)")){
                own(row);
                result.add(new FestivalAfterRating(text(row,"operation_id"),user,text(row,"festival_id"),
                        new Band(text(row.getJSONObject("band"),"name")),Rating.of(row.getInt("rating")),
                        time(text(row,"recorded_at")),row.getBoolean("requires_review")));
            }
            return List.copyOf(result);
        }catch(JSONException|IllegalArgumentException|java.time.DateTimeException error){throw new IOException("Festival after-rating snapshot is invalid; cached data was kept.",error);}
    }
    @Override public List<PersonalBandRating> currentRatings()throws IOException{
        List<PersonalBandRating> result=new ArrayList<>();
        try{
            for(JSONObject row:rows("personal_band_ratings","user_id,rating,source_festival_id,source_festival_name,source_event_id,established_at,band:bands(name)")){
                own(row);
                result.add(new PersonalBandRating(user,new Band(text(row.getJSONObject("band"),"name")),
                        Rating.of(row.getInt("rating")),text(row,"source_festival_id"),text(row,"source_festival_name"),
                        text(row,"source_event_id"),time(text(row,"established_at"))));
            }
            return List.copyOf(result);
        }catch(JSONException|IllegalArgumentException|java.time.DateTimeException error){throw new IOException("Established band-rating snapshot is invalid; cached data was kept.",error);}
    }
    private List<JSONObject> rows(String table,String select)throws IOException,JSONException{
        List<JSONObject> result=new ArrayList<>();
        for(int offset=0;;offset+=500){
            JSONArray page=new JSONArray(requests.execute("GET","/rest/v1/"+table+"?select="+select
                    +"&user_id=eq."+encode(user)+"&order=band_id.asc"
                    +(table.equals("festival_after_ratings")?",festival_id.asc":"")+"&limit=500&offset="+offset,null,false));
            for(int index=0;index<page.length();index++)result.add(page.getJSONObject(index));
            if(page.length()<500)return result;
        }
    }
    private void own(JSONObject row)throws IOException,JSONException{
        if(!user.equals(text(row,"user_id")))throw new IOException("Unexpected owner in rating snapshot; cached data was kept.");
    }
    private static String text(JSONObject object,String key)throws IOException,JSONException{
        Object value=object.opt(key);
        if(!(value instanceof String text)||text.isBlank())throw new IOException("Missing rating identity/source field: "+key);
        return text;
    }
    private static Instant time(String value){return SupabasePersonalBandRatingClient.parseCreatedAt(value);}
    private String encode(String value)throws IOException{return URLEncoder.encode(value,StandardCharsets.UTF_8.name());}
}
