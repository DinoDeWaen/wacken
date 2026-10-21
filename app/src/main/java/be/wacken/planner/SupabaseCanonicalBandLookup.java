package be.wacken.planner;

import be.wacken.planner.domain.Band;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Exact catalog addressing shared by rating adapters; never creates or merges bands. */
final class SupabaseCanonicalBandLookup {
    private final GetRequest requests;

    SupabaseCanonicalBandLookup(GetRequest requests) {
        this.requests = requests;
    }

    String idFor(Band band) throws IOException {
        String response = requests.get("/rest/v1/bands?select=id&name=eq."
                + URLEncoder.encode(band.name(), StandardCharsets.UTF_8) + "&limit=1");
        try {
            JSONArray rows = new JSONArray(response);
            if (rows.length() == 0) {
                throw new IOException("Band is not available in Supabase: " + band.name());
            }
            return rows.getJSONObject(0).getString("id");
        } catch (JSONException error) {
            throw new IOException("Supabase band lookup response could not be read.", error);
        }
    }

    Map<String, String> namesById() throws IOException {
        String response = requests.get("/rest/v1/bands?select=id,name");
        try {
            JSONArray rows = new JSONArray(response);
            Map<String, String> names = new HashMap<>();
            for (int index = 0; index < rows.length(); index++) {
                JSONObject row = rows.getJSONObject(index);
                names.put(row.getString("id"), row.getString("name"));
            }
            return names;
        } catch (JSONException error) {
            throw new IOException("Supabase band lookup response could not be read.", error);
        }
    }

    interface GetRequest {
        String get(String endpoint) throws IOException;
    }
}
