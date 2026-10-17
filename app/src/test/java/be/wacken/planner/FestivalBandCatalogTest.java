package be.wacken.planner;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import be.wacken.planner.domain.*;
import org.json.*;
import org.junit.Test;
import static org.junit.Assert.*;

public final class FestivalBandCatalogTest {
    @Test public void reviewPagesCatalogAndKeepsStableIdsAndConfirmedAliasProvenance() throws Exception {
        JSONArray full=new JSONArray();
        for(int i=0;i<500;i++)full.put(new JSONObject().put("id","id-"+i).put("name","Band "+i));
        String page=full.toString();AtomicInteger published=new AtomicInteger();
        var catalog=new SupabaseFestivalBandCatalog(new SupabaseMasterDataClient((method,path,body,upsert)->{
            if(path.startsWith("/rest/v1/bands?"))return path.endsWith("offset=0")?page:"[{\"id\":\"last\",\"name\":\"Abbie Falls\"}]";
            return "[{\"alias_name\":\"AF\",\"band_id\":\"last\",\"band\":{\"name\":\"Abbie Falls\"},\"confirmed_by\":\"member\",\"confirmed_at\":\"2026-10-17T12:00:00Z\"}]";
        }),aliases->{assertEquals("last",aliases.get(0).canonical().id());published.incrementAndGet();});
        var result=catalog.load();assertEquals(501,result.bands().size());assertEquals("last",result.bands().get(500).id());
        assertEquals("member",result.aliases().get(0).confirmedBy());assertEquals(1,published.get());
    }
    @Test public void failedAliasFetchRetainsCacheInsteadOfPublishingEmptySnapshot() {
        AtomicInteger writes=new AtomicInteger();
        var catalog=new SupabaseFestivalBandCatalog(new SupabaseMasterDataClient((m,p,b,u)->{throw new IOException("Offline");}),a->writes.incrementAndGet());
        try {catalog.refreshAliases();fail("Expected unavailable");}catch(SupabaseSyncException expected) {}
        assertEquals(0,writes.get());
    }
}
