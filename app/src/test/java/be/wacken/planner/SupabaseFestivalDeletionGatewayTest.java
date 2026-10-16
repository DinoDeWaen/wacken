package be.wacken.planner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import be.wacken.planner.domain.Festival;

public final class SupabaseFestivalDeletionGatewayTest {
    private static final Festival WACKEN = Festival.active("wacken-2026", "Wacken Open Air 2026");

    @Test
    public void remoteDenialLeavesTheLocalFestivalIntact() {
        List<Festival> localDeletes = new ArrayList<>();
        SupabaseMasterDataClient remote = new SupabaseMasterDataClient((method, endpoint, body, upsert) ->
                "DELETE".equals(method) ? "" : "[{\"id\":\"wacken-2026\"}]");

        assertThrows(SupabaseSyncException.class,
                () -> new SupabaseFestivalDeletionGateway(remote, localDeletes::add).delete(WACKEN));

        assertEquals(List.of(), localDeletes);
    }

    @Test
    public void confirmedRemoteDeletionCleansOnlyTheSelectedLocalFestival() {
        List<Festival> localDeletes = new ArrayList<>();
        List<String> requests = new ArrayList<>();
        SupabaseMasterDataClient remote = new SupabaseMasterDataClient((method, endpoint, body, upsert) -> {
            requests.add(method + " " + endpoint);
            return "[]";
        });

        new SupabaseFestivalDeletionGateway(remote, localDeletes::add).delete(WACKEN);

        assertEquals(List.of(WACKEN), localDeletes);
        assertEquals(2, requests.size());
        assertEquals(true, requests.get(0).startsWith("DELETE /rest/v1/festivals?id=eq.wacken-2026"));
    }
}
