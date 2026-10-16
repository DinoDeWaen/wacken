package be.wacken.planner;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import be.wacken.planner.application.AddFestivalResult;
import be.wacken.planner.application.FestivalCreationGateway;
import be.wacken.planner.domain.*;

final class SupabaseFestivalCreationGateway implements FestivalCreationGateway {
    private final SupabaseMasterDataClient client;
    private final Consumer<Snapshot> cache;

    SupabaseFestivalCreationGateway(SupabaseMasterDataClient client, Consumer<Snapshot> cache) {
        this.client = client;
        this.cache = cache;
    }

    @Override
    public AddFestivalResult create(String groupId, String userName, Festival festival, List<Band> bands) {
        try {
            List<String> names = bands.stream().map(Band::name).distinct().sorted().collect(Collectors.toList());
            JSONArray identity = new JSONArray().put(groupId).put(userName).put(festival.id())
                    .put(festival.name()).put(new JSONArray(names));
            String requestId = UUID.nameUUIDFromBytes(identity.toString().getBytes(StandardCharsets.UTF_8)).toString();
            JSONObject command = new JSONObject().put("p_request_id", requestId).put("p_group_id", groupId)
                    .put("p_festival_id", festival.id()).put("p_festival_name", festival.name())
                    .put("p_band_names", new JSONArray(names));
            Snapshot snapshot = parse(client.createFestival(command.toString()), groupId, userName);
            try {
                cache.accept(snapshot);
            } catch (RuntimeException error) {
                throw new SupabaseSyncException("Festival saved group-wide, but the local cache could not be updated. Retry the same name and CSV.", error);
            }
            return snapshot.result();
        } catch (IOException error) {
            throw new SupabaseSyncException("Could not confirm festival creation. Retry the same name and CSV. "
                    + error.getMessage(), error);
        } catch (org.json.JSONException error) {
            throw new SupabaseSyncException("Could not read the festival result. Retry the same name and CSV.", error);
        }
    }

    static Snapshot parse(String response, String groupId, String userName) throws org.json.JSONException {
        JSONObject data = new JSONObject(response);
        if (!groupId.equals(data.getString("group_id")) || !userName.equals(data.getString("user_id"))) {
            throw new org.json.JSONException("Festival result belongs to another session.");
        }
        List<Festival> festivals = new ArrayList<>();
        JSONArray festivalRows = data.getJSONArray("festivals");
        for (int i=0;i<festivalRows.length();i++) festivals.add(SupabaseMasterDataClient.toFestival(festivalRows.getJSONObject(i)));
        Festival festival = SupabaseMasterDataClient.toFestival(data.getJSONObject("festival"));
        List<Band> bands = new ArrayList<>();
        JSONArray bandRows = data.getJSONArray("bands");
        for (int i=0;i<bandRows.length();i++) bands.add(SupabaseMasterDataClient.toBand(bandRows.getJSONObject(i)));
        List<FestivalLineupEntry> lineup = new ArrayList<>();
        JSONArray lineupRows = data.getJSONArray("lineup");
        for (int i=0;i<lineupRows.length();i++) {
            JSONObject row=lineupRows.getJSONObject(i);
            lineup.add(new FestivalLineupEntry(festival.id(),new Band(row.getString("band_name")),row.getString("uploaded_display_name")));
        }
        List<SavedFestivalPlanningRating> ratings = new ArrayList<>();
        JSONArray ratingRows = data.getJSONArray("planning_ratings");
        for (int i=0;i<ratingRows.length();i++) {
            JSONObject row=ratingRows.getJSONObject(i);
            ratings.add(new SavedFestivalPlanningRating(groupId,userName,festival.id(),
                    new Band(row.getString("band_name")),Rating.of(row.getInt("rating"))));
        }
        return new Snapshot(groupId,userName,festival,List.copyOf(festivals),List.copyOf(bands),
                List.copyOf(lineup),List.copyOf(ratings),AddFestivalResult.added(
                data.getInt("reused_bands"),data.getInt("created_bands"),data.getInt("prefilled_ratings")));
    }

    record Snapshot(String groupId, String userName, Festival festival, List<Festival> festivals,
                    List<Band> bands, List<FestivalLineupEntry> lineup,
                    List<SavedFestivalPlanningRating> ratings, AddFestivalResult result) { }
}
