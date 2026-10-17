package be.wacken.planner;

import be.wacken.planner.application.FestivalArchiveGateway;
import be.wacken.planner.domain.*;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import org.json.*;

/** Authenticated archive transaction followed by owner-scoped cache publication. */
final class SupabaseFestivalArchiveGateway implements FestivalArchiveGateway {
    private final SupabaseMasterDataClient.RequestExecutor requests;
    private final RatingStateSync.Remote ratings;
    private final Consumer<Snapshot> cache;
    SupabaseFestivalArchiveGateway(SupabaseMasterDataClient.RequestExecutor requests,
                                   RatingStateSync.Remote ratings,Consumer<Snapshot> cache) {
        this.requests=requests;this.ratings=ratings;this.cache=cache;
    }
    @Override public void archive(Festival selected) {
        try {
            JSONObject result=new JSONObject(requests.execute("POST","/rest/v1/rpc/archive_festival",
                    new JSONObject().put("p_festival_id",selected.id()).toString(),false));
            if(!result.optBoolean("confirmed",false)||!selected.id().equals(result.optString("festival_id","")))
                throw new IOException("Archive acknowledgement does not match this festival.");
            UUID.fromString(result.getString("operation_id"));
            Festival archived=SupabaseMasterDataClient.toFestival(result.getJSONObject("festival"));
            if(!archived.isArchived()||!selected.id().equals(archived.id()))
                throw new IOException("Archived festival snapshot is invalid.");
            // Fetch both complete private snapshots before changing any local state.
            Snapshot snapshot=new Snapshot(archived,ratings.afterRatings(),ratings.currentRatings());
            try {cache.accept(snapshot);}
            catch(RuntimeException error) {
                throw new SupabaseSyncException("Festival saved as archived group-wide; local data was retained. Retry Archive after resolving pending changes.",error);
            }
        } catch(IOException|JSONException|IllegalArgumentException error) {
            throw new SupabaseSyncException("Archive could not be confirmed. Retry Archive; cached and pending data were retained. "+error.getMessage(),error);
        }
    }
    record Snapshot(Festival festival,List<FestivalAfterRating> after,List<PersonalBandRating> current) {
        Snapshot {after=List.copyOf(after);current=List.copyOf(current);}
    }
}
