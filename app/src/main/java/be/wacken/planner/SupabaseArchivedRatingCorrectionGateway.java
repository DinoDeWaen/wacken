package be.wacken.planner;

import be.wacken.planner.application.ArchivedRatingCorrectionGateway;
import be.wacken.planner.domain.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import org.json.*;

/** Authenticated own-rating review/correction and strictly validated audit snapshots. */
final class SupabaseArchivedRatingCorrectionGateway implements ArchivedRatingCorrectionGateway {
    record Pending(String afterOperation,Integer planningValue) {}
    record Snapshot(ArchivedRatingReview current,RatingCorrection receipt,Pending pending,
                    List<FestivalAfterRating> after,List<PersonalBandRating> established) {}
    interface Cache {
        Pending capture(ArchivedRatingReview expected);
        void apply(Snapshot snapshot);
        void appendAudits(List<RatingCorrection> audits);
    }
    private final SupabaseMasterDataClient.RequestExecutor requests;
    private final String user;
    private final RatingStateSync.Remote ratings;
    private final Cache cache;
    SupabaseArchivedRatingCorrectionGateway(SupabaseMasterDataClient.RequestExecutor requests,String user,
                                           RatingStateSync.Remote ratings,Cache cache) {
        this.requests=requests;this.user=user;this.ratings=ratings;this.cache=cache;
    }
    @Override public ArchivedRatingReview review(String actor,String festival,Band band,ArchivedRatingKind kind) {
        try {
            own(actor);
            return reviewTarget(festival,band,kind,bandId(band));
        } catch(IOException|JSONException|IllegalArgumentException|java.time.DateTimeException error) {
            throw failure(error);
        }
    }
    @Override public RatingCorrection correct(String operation,ArchivedRatingReview expected,int value) {
        try {
            own(expected.userName());
            String canonical=bandId(expected.band());
            Pending pending=cache.capture(expected);
            JSONObject command=target(expected.festivalId(),canonical,expected.kind())
                    .put("p_operation_id",operation).put("p_expected_rating",expected.value())
                    .put("p_expected_revision",expected.revision()).put("p_new_rating",value);
            JSONObject result=new JSONObject(requests.execute("POST","/rest/v1/rpc/correct_archived_rating",command.toString(),false));
            if(!result.optBoolean("confirmed",false))throw new IOException("Correction was not confirmed.");
            RatingCorrection receipt=parseAudit(result.getJSONObject("correction"));
            if(!operation.equals(receipt.operationId())||!expected.festivalId().equals(receipt.festivalId())
                    ||!expected.band().name().equals(receipt.band().name())||expected.kind()!=receipt.kind()
                    ||expected.value()!=receipt.previousValue()||expected.revision()!=receipt.expectedRevision()
                    ||value!=receipt.newValue())throw new IOException("Correction acknowledgement belongs to different data.");
            ArchivedRatingReview current=reviewTarget(expected.festivalId(),expected.band(),expected.kind(),canonical);
            Snapshot snapshot=new Snapshot(current,receipt,pending,ratings.afterRatings(),ratings.currentRatings());
            try {cache.apply(snapshot);}
            catch(RuntimeException error) {
                throw new SupabaseSyncException("Correction saved remotely; cached/pending data was retained. Retry the same correction to confirm local recovery.",error);
            }
            return receipt;
        } catch(IOException|JSONException|IllegalArgumentException|java.time.DateTimeException error) {
            throw failure(error);
        }
    }
    @Override public List<RatingCorrection> history(String actor,String festival,Band band) {
        own(actor);
        return refreshAudit().stream().filter(a->a.festivalId().equals(festival)&&a.band().name().equals(band.name()))
                .collect(Collectors.toList());
    }
    List<RatingCorrection> refreshAudit() {
        try {
            List<RatingCorrection> result=new ArrayList<>();
            for(int offset=0;;offset+=500) {
                JSONArray page=new JSONArray(requests.execute("GET","/rest/v1/rating_corrections?select=*"
                        +"&user_id=eq."+encode(user)+"&order=corrected_at.asc,operation_id.asc&limit=500&offset="+offset,null,false));
                for(int i=0;i<page.length();i++)result.add(parseAudit(page.getJSONObject(i)));
                if(page.length()<500)break;
            }
            List<RatingCorrection> audits=List.copyOf(result);cache.appendAudits(audits);return audits;
        } catch(IOException|JSONException|IllegalArgumentException|java.time.DateTimeException error) {throw failure(error);}
    }
    private ArchivedRatingReview reviewTarget(String festival,Band band,ArchivedRatingKind kind,String canonical)
            throws IOException,JSONException {
        JSONObject row=new JSONObject(requests.execute("POST","/rest/v1/rpc/review_archived_rating",
                target(festival,canonical,kind).toString(),false));
        own(text(row,"user_id"));
        if(!festival.equals(text(row,"festival_id"))||!canonical.equals(text(row,"band_id"))
                ||!band.name().equals(text(row,"band_name"))||!kind.name().equals(text(row,"kind")))
            throw new IOException("Review belongs to another rating target.");
        return new ArchivedRatingReview(user,festival,band,kind,row.getInt("rating"),row.getLong("revision"),time(row,"recorded_at"));
    }
    private RatingCorrection parseAudit(JSONObject row)throws IOException,JSONException {
        own(text(row,"user_id"));
        return new RatingCorrection(text(row,"operation_id"),user,text(row,"festival_id"),text(row,"festival_name_snapshot"),
                new Band(text(row,"band_name_snapshot")),ArchivedRatingKind.valueOf(text(row,"kind")),
                row.getInt("previous_value"),row.getInt("new_value"),row.getLong("expected_revision"),
                time(row,"original_recorded_at"),SupabasePersonalBandRatingClient.parseCreatedAt(text(row,"corrected_at")));
    }
    private String bandId(Band band)throws IOException,JSONException {
        JSONArray matches=new JSONArray(requests.execute("GET","/rest/v1/bands?select=id&name=eq."+encode(band.name())+"&limit=2",null,false));
        if(matches.length()!=1)throw new IOException("Canonical band is not uniquely available. Reload the lineup.");
        return text(matches.getJSONObject(0),"id");
    }
    private JSONObject target(String festival,String canonical,ArchivedRatingKind kind)throws JSONException {
        return new JSONObject().put("p_festival_id",festival).put("p_band_id",canonical).put("p_kind",kind.name());
    }
    private void own(String actor) {
        if(!user.equals(actor))throw new IllegalArgumentException("You can review/correct only your own rating.");
    }
    private static String text(JSONObject row,String key)throws IOException {
        Object value=row.opt(key);
        if(!(value instanceof String text)||text.isBlank())throw new IOException("Missing correction identity field: "+key);
        return text;
    }
    private Optional<Instant> time(JSONObject row,String key)throws IOException {
        return row.isNull(key)?Optional.empty():Optional.of(SupabasePersonalBandRatingClient.parseCreatedAt(text(row,key)));
    }
    private String encode(String text)throws IOException{return URLEncoder.encode(text,StandardCharsets.UTF_8.name());}
    private SupabaseSyncException failure(Exception error) {
        return new SupabaseSyncException("Correction was not confirmed. Cached and pending data were retained. Reload a stale review, or retry the same correction after reconnecting. "+error.getMessage(),error);
    }
}
