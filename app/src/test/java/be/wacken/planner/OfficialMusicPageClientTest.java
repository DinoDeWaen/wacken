package be.wacken.planner;
import static org.junit.Assert.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public final class OfficialMusicPageClientTest {
    @Test public void rejectsPrivateAddressesBeforeOpeningAnyConnection() throws Exception {
        AtomicInteger opened=new AtomicInteger();
        var client=new OfficialMusicPageClient(host->new InetAddress[]{InetAddress.getByName("127.0.0.1")},
                url->{opened.incrementAndGet();return new Fake(url,200,"");},(html,base)->List.of(),64,2);
        expectFailure(()->client.read("https://band.example"));
        assertEquals(0,opened.get());
    }
    @Test public void redirectsAreValidatedAndPrivateDestinationsAreNeverOpened() throws Exception {
        List<String> opened=new ArrayList<>();
        var client=new OfficialMusicPageClient(host->new InetAddress[]{InetAddress.getByName(host.equals("private.example")?"10.0.0.1":"8.8.8.8")},
                url->{opened.add(url.getHost());Fake f=new Fake(url,302,"");f.location="https://private.example/secret";return f;},
                (html,base)->List.of(),64,2);
        expectFailure(()->client.read("https://band.example"));
        assertEquals(List.of("band.example"),opened);
    }
    @Test public void boundsResponseBytesAndClosesConnectionOnFailure() throws Exception {
        Fake f=new Fake(new URL("https://band.example"),200,"x".repeat(65));
        var client=new OfficialMusicPageClient(host->publicAddress(),url->f,(html,base)->List.of(),64,2);
        expectFailure(()->client.read("https://band.example"));assertTrue(f.closed);
    }
    @Test public void boundedHttpsResponseMapsOnlyParsedAnchorFacts() throws Exception {
        Fake f=new Fake(new URL("https://band.example"),200,"<a href='/music'>Music</a>");
        var client=new OfficialMusicPageClient(host->publicAddress(),url->f,(html,base)->List.of(base.resolve("/music").toString()),64,2);
        var page=client.read("https://band.example");
        assertEquals(List.of("https://band.example/music"),page.links());assertTrue(f.closed);
        assertFalse(f.getInstanceFollowRedirects());assertEquals(5000,f.getConnectTimeout());assertEquals(5000,f.getReadTimeout());
    }
    @Test public void redirectLoopsAndInsecureOrCredentialBearingUrlsFailClosed() throws Exception {
        var client=new OfficialMusicPageClient(host->publicAddress(),url->{Fake f=new Fake(url,302,"");f.location="/again";return f;},
                (html,base)->List.of(),64,1);
        expectFailure(()->client.read("https://band.example"));
        expectFailure(()->client.read("file:///etc/passwd"));
        expectFailure(()->client.read("https://user:password@band.example/"));
    }
    private static InetAddress[] publicAddress() throws UnknownHostException {return new InetAddress[]{InetAddress.getByName("8.8.8.8")};}
    private interface Attempt{void run()throws Exception;}
    private void expectFailure(Attempt action)throws Exception{try{action.run();fail("Expected bounded retrieval failure");}catch(IOException expected){}}
    private static final class Fake extends HttpURLConnection {
        private final int status;private final byte[] body;private String location;private boolean closed;
        Fake(URL url,int status,String body){super(url);this.status=status;this.body=body.getBytes(java.nio.charset.StandardCharsets.UTF_8);}
        public int getResponseCode(){return status;}
        public String getContentType(){return "text/html; charset=utf-8";}
        public String getHeaderField(String name){return name.equals("Location")?location:null;}
        public InputStream getInputStream(){return new ByteArrayInputStream(body);}
        public void disconnect(){closed=true;}
        public boolean usingProxy(){return false;}
        public void connect(){}
    }
}

