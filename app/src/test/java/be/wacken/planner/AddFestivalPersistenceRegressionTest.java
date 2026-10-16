package be.wacken.planner;

import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import be.wacken.planner.application.*;
import be.wacken.planner.domain.*;

/** Real HTTP, PostgREST, repository migrations, constraints, and member RLS.
 * The mandatory integration runner supplies an isolated loopback API and fixture JWTs. */
public final class AddFestivalPersistenceRegressionTest {
    static final String GROUP="00000000-0000-0000-0000-000000000001";
    static final String USER="11111111-1111-1111-1111-111111111111";
    private String api;
    private String token;
    private String container;
    private final List<SupabaseFestivalCreationGateway.Snapshot> snapshots=new ArrayList<>();

    @Before public void setup() throws Exception {
        api=System.getenv("WACKEN_FESTIVAL_API");
        Assume.assumeTrue("Run scripts/verify-festival-creation.sh for the database integration gate",api!=null);
        assertTrue("Tests must never target production",api.startsWith("http://127.0.0.1:"));
        token=Objects.requireNonNull(System.getenv("WACKEN_FESTIVAL_TOKEN"));
        container=Objects.requireNonNull(System.getenv("WACKEN_FESTIVAL_DB"));
        assertTrue(container.startsWith("wacken-festival-"));
        sql("TRUNCATE public.bands,public.festivals CASCADE;"
            +"DROP TRIGGER IF EXISTS test_reject_band ON public.bands;"
            +"INSERT INTO public.festivals(id,name,status) VALUES('wacken-2026','Wacken history','ARCHIVED');"
            +"INSERT INTO public.bands(id,name,biography_html) VALUES('any-given-day','Any given Day','Golden biography'),('airbourne','Airbourne','Original'),('h-blockx','H-Blockx','Original');"
            +"INSERT INTO public.personal_band_rating_events(user_id,band_id,rating,created_at) VALUES"
            +"('"+USER+"','airbourne',2,'2026-01-01'),('"+USER+"','airbourne',4,'2026-02-01'),"
            +"('22222222-2222-2222-2222-222222222222','airbourne',5,'2026-03-01');");
    }

    @Test public void completeSummerBreezeCsvDoesNotOverwriteOrCollideWithKnownBand() throws Exception {
        Path fixture=List.of(Path.of("data/summer-breeze-2027/bands.csv"),Path.of("../data/summer-breeze-2027/bands.csv"))
                .stream().filter(Files::exists).findFirst().orElseThrow();
        List<Band> names=FestivalCsvReader.bandNames(new String(Files.readAllBytes(fixture),StandardCharsets.UTF_8))
                .stream().map(Band::new).collect(Collectors.toList());
        AddFestivalResult result=create("summer-breeze",names);
        assertTrue(result.message(),result.success());
        assertEquals(63,names.size());
        assertEquals("63",sql("SELECT count(*) FROM public.festival_lineup_entries WHERE festival_id='summer-breeze'"));
        assertEquals("Any given Day|Golden biography",sql("SELECT name,biography_html FROM public.bands WHERE id='any-given-day'"));
        assertEquals("2",sql("SELECT count(*) FROM public.bands WHERE name IN ('Any Given Day','Any given Day')"));
        assertEquals(63,snapshots.get(0).lineup().size());
    }

    @Test public void failedBandWriteDoesNotLeaveAnActiveFestivalOrPartialCatalog() throws Exception {
        sql("CREATE OR REPLACE FUNCTION public.test_reject_band() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN IF NEW.name='Rejected Act' THEN RAISE EXCEPTION 'Injected failure'; END IF; RETURN NEW; END $$;"
                +"CREATE TRIGGER test_reject_band BEFORE INSERT ON public.bands FOR EACH ROW EXECUTE FUNCTION public.test_reject_band();");
        expectFailure(()->create("failed",List.of(new Band("First Act"),new Band("Rejected Act"))));
        assertEquals("0",sql("SELECT count(*) FROM public.festivals WHERE status='ACTIVE'"));
        assertEquals("3",sql("SELECT count(*) FROM public.bands"));
        assertTrue(snapshots.isEmpty());
    }

    @Test public void duplicateCsvBandRowsProduceOneLineupEntry() throws Exception {
        assertTrue(create("duplicates",List.of(new Band("Airbourne"),new Band("Airbourne"))).success());
        assertEquals("1",sql("SELECT count(*) FROM public.festival_lineup_entries WHERE festival_id='duplicates'"));
    }

    @Test public void archivedFestivalIdentityCannotBeReactivatedByCreatingAnotherFestival() throws Exception {
        expectFailure(()->create("wacken-2026",List.of(new Band("New Act"))));
        assertEquals("ARCHIVED|Wacken history",sql("SELECT status,name FROM public.festivals WHERE id='wacken-2026'"));
        assertEquals("3",sql("SELECT count(*) FROM public.bands"));
    }

    @Test public void unicodeAndPunctuationCollisionsStayDistinctAndPrefillIsPersonal() throws Exception {
        AddFestivalResult result=create("collisions",List.of(new Band("A+B"),new Band("A B"),new Band("東京"),new Band("!!!"),new Band("Airbourne")));
        assertEquals(4,result.createdBands());
        assertEquals(1,result.reusedBands());
        assertEquals(1,result.prefilledRatings());
        assertEquals("4",sql("SELECT rating FROM public.festival_planning_ratings WHERE festival_id='collisions'"));
        assertEquals("3",sql("SELECT count(*) FROM public.personal_band_rating_events"));
    }

    @Test public void lostResponseRetryUsesSameRequestAndPreservesLaterEdits() throws Exception {
        SupabaseMasterDataClient.RequestExecutor transport=http(token);
        AddFestivalUseCase lost=new AddFestivalUseCase(new SupabaseFestivalCreationGateway(
                new SupabaseMasterDataClient((method,path,body,upsert)->{
                    transport.execute(method,path,body,upsert);
                    throw new IOException("Simulated lost response after commit");
                }),snapshots::add));
        expectFailure(()->lost.addFestival(GROUP,USER,"retry","retry",List.of(new Band("Airbourne"))));
        assertTrue(snapshots.isEmpty());
        sql("UPDATE public.festival_planning_ratings SET rating=2 WHERE festival_id='retry'");
        assertTrue(create("retry",List.of(new Band("Airbourne"))).success());
        assertEquals("1",sql("SELECT count(*) FROM public.festival_creation_requests"));
        assertEquals("2",sql("SELECT rating FROM public.festival_planning_ratings WHERE festival_id='retry'"));
        assertEquals(2,snapshots.get(0).ratings().get(0).rating().value());
    }

    @Test public void concurrentIdenticalRequestsCommitOnce() throws Exception {
        ExecutorService workers=Executors.newFixedThreadPool(2);
        try {
            Callable<AddFestivalResult> command=()->new AddFestivalUseCase(new SupabaseFestivalCreationGateway(
                    new SupabaseMasterDataClient(http(token)),snapshot->{}))
                    .addFestival(GROUP,USER,"concurrent","concurrent",List.of(new Band("New Act")));
            Future<AddFestivalResult> first=workers.submit(command),second=workers.submit(command);
            assertTrue(first.get(20,TimeUnit.SECONDS).success());
            assertTrue(second.get(20,TimeUnit.SECONDS).success());
            assertEquals("1",sql("SELECT count(*) FROM public.festival_creation_requests"));
            assertEquals("1",sql("SELECT count(*) FROM public.bands WHERE name='New Act'"));
        } finally {workers.shutdownNow();}
    }

    @Test public void authoritativeActiveFestivalRejectsCreationWithNoPartialWrites() throws Exception {
        assertTrue(create("already-active",List.of(new Band("Airbourne"))).success());
        expectFailure(()->create("stale-cache",List.of(new Band("Must Not Persist"))));
        assertEquals("0",sql("SELECT count(*) FROM public.bands WHERE name='Must Not Persist'"));
        assertEquals("1",sql("SELECT count(*) FROM public.festivals WHERE status='ACTIVE'"));
    }

    @Test public void nonMemberCannotCreateThroughActualApi() throws Exception {
        AddFestivalUseCase denied=new AddFestivalUseCase(new SupabaseFestivalCreationGateway(
                new SupabaseMasterDataClient(http(System.getenv("WACKEN_FESTIVAL_OUTSIDER_TOKEN"))),snapshots::add));
        expectFailure(()->denied.addFestival(GROUP,"33333333-3333-3333-3333-333333333333","denied","Denied",List.of(new Band("Denied"))));
        assertTrue(snapshots.isEmpty());
        assertEquals("0",sql("SELECT count(*) FROM public.festivals WHERE status='ACTIVE'"));
    }

    @Test public void concurrentDifferentRequestsLeaveOnlyOneCompleteFestival() throws Exception {
        ExecutorService workers=Executors.newFixedThreadPool(2);
        try {
            List<Future<Boolean>> results=new ArrayList<>();
            for (String id:List.of("first-request","second-request")) {
                results.add(workers.submit(()->{
                    try {
                        return new AddFestivalUseCase(new SupabaseFestivalCreationGateway(
                                new SupabaseMasterDataClient(http(token)),snapshot->{}))
                                .addFestival(GROUP,USER,id,id,List.of(new Band(id))).success();
                    } catch(SupabaseSyncException expected) {
                        assertTrue(expected.getMessage(),expected.getMessage().contains("active festival"));
                        return false;
                    }
                }));
            }
            int successes=0;
            for(Future<Boolean> result:results)if(result.get(20,TimeUnit.SECONDS))successes++;
            assertEquals(1,successes);
            assertEquals("1",sql("SELECT count(*) FROM public.festival_creation_requests"));
            assertEquals("1",sql("SELECT count(*) FROM public.festival_lineup_entries"));
            assertEquals("4",sql("SELECT count(*) FROM public.bands"));
        } finally {workers.shutdownNow();}
    }

    private AddFestivalResult create(String id,List<Band> bands) {
        return new AddFestivalUseCase(new SupabaseFestivalCreationGateway(new SupabaseMasterDataClient(http(token)),snapshots::add))
                .addFestival(GROUP,USER,id,id,bands);
    }
    private void expectFailure(Runnable command) {
        try {command.run();fail("Expected creation failure");}
        catch(SupabaseSyncException expected){assertFalse(expected.getMessage().isBlank());}
    }
    private SupabaseMasterDataClient.RequestExecutor http(String bearer) {
        return (method,path,body,upsert)->{
            HttpURLConnection connection=(HttpURLConnection)new URL(api+path.replace("/rest/v1","")).openConnection();
            connection.setConnectTimeout(10000);connection.setReadTimeout(10000);
            connection.setRequestMethod(method);
            connection.setRequestProperty("Authorization","Bearer "+bearer);
            connection.setRequestProperty("Content-Type","application/json");
            try {
                if(body!=null){connection.setDoOutput(true);try(OutputStream out=connection.getOutputStream()){out.write(body.getBytes(StandardCharsets.UTF_8));}}
                int status=connection.getResponseCode();
                InputStream stream=status>=400?connection.getErrorStream():connection.getInputStream();
                String response=new BufferedReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
                if(status>=400)throw new IOException("HTTP "+status+": "+response);
                return response;
            } finally {connection.disconnect();}
        };
    }
    private String sql(String command) throws Exception {
        Process process=new ProcessBuilder("docker","exec",container,"psql","-XAt","-v","ON_ERROR_STOP=1","-U","postgres","-c",command).start();
        String result=new BufferedReader(new InputStreamReader(process.getInputStream(),StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
        String errors=new BufferedReader(new InputStreamReader(process.getErrorStream(),StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
        assertEquals(errors,0,process.waitFor());
        return result.trim();
    }
}
