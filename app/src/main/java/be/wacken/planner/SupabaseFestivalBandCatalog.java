package be.wacken.planner;

import be.wacken.planner.application.FestivalBandCatalog;
import be.wacken.planner.domain.*;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import org.json.*;

/** Fresh authenticated review data; a failed fetch never replaces the alias cache. */
final class SupabaseFestivalBandCatalog implements FestivalBandCatalog {
    private final SupabaseMasterDataClient client;
    private final Consumer<List<ConfirmedBandAlias>> cache;
    SupabaseFestivalBandCatalog(SupabaseMasterDataClient client,Consumer<List<ConfirmedBandAlias>> cache) {
        this.client=client;this.cache=cache;
    }
    @Override public Snapshot load() {
        try {
            List<CanonicalBand> bands=new ArrayList<>();
            for(JSONObject row:rows("/rest/v1/bands?select=id,name,biography_html,image_url,youtube_url,spotify_artist_id&active=eq.true&order=id.asc"))
                bands.add(new CanonicalBand(row.getString("id"),SupabaseMasterDataClient.toBand(row)));
            List<ConfirmedBandAlias> aliases=loadAliases();
            return new Snapshot(bands,aliases);
        } catch(IOException|JSONException|IllegalArgumentException error) {
            throw new SupabaseSyncException("Could not load bands for review. Check connectivity and the database migration, then retry.",error);
        }
    }
    List<ConfirmedBandAlias> loadAliases() throws IOException,JSONException {
        List<ConfirmedBandAlias> aliases=new ArrayList<>();
        for(JSONObject row:rows("/rest/v1/band_aliases?select=alias_name,band_id,confirmed_by,confirmed_at,band:bands(name)&order=alias_key.asc"))
            aliases.add(new ConfirmedBandAlias(row.getString("alias_name"),
                new CanonicalBand(row.getString("band_id"),new Band(row.getJSONObject("band").getString("name"))),
                row.getString("confirmed_by"),Instant.parse(row.getString("confirmed_at"))));
        List<ConfirmedBandAlias> result=List.copyOf(aliases);cache.accept(result);return result;
    }
    void refreshAliases() {
        try {loadAliases();}
        catch(IOException|JSONException|IllegalArgumentException error) {
            throw new SupabaseSyncException("Could not refresh confirmed band aliases; cached data was retained.",error);
        }
    }
    static List<ConfirmedBandAlias> parseAliases(JSONArray rows) throws JSONException {
        List<ConfirmedBandAlias> result=new ArrayList<>();
        for(int i=0;i<rows.length();i++) {
            JSONObject row=rows.getJSONObject(i);
            result.add(new ConfirmedBandAlias(row.getString("alias_name"),
                new CanonicalBand(row.getString("band_id"),new Band(row.getString("band_name"))),
                row.getString("confirmed_by"),Instant.parse(row.getString("confirmed_at"))));
        }
        return List.copyOf(result);
    }
    private List<JSONObject> rows(String endpoint) throws IOException,JSONException {
        List<JSONObject> rows=new ArrayList<>();
        for(int offset=0;;offset+=500) {
            JSONArray page=new JSONArray(client.request("GET",endpoint+"&limit=500&offset="+offset,null,false));
            for(int i=0;i<page.length();i++)rows.add(page.getJSONObject(i));
            if(page.length()<500)return rows;
        }
    }
}
