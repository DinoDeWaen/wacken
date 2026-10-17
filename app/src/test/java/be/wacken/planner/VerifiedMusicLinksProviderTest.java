package be.wacken.planner;
import static org.junit.Assert.*;
import be.wacken.planner.application.*;
import java.io.IOException;
import java.util.*;
import org.junit.Test;

public final class VerifiedMusicLinksProviderTest {
    private static final String ID="11111111-1111-4111-8111-111111111111";
    private static final String YT="https://www.youtube.com/channel/UC1234567890123456789012";
    private static final String SP="https://open.spotify.com/artist/1234567890123456789012";
    private static final String SITE="https://band.example/music";
    private final List<String> requests=new ArrayList<>();
    private boolean websiteFails;
    private String wikiId=ID;
    private final MusicBrainzMetadataProvider.MusicBrainzHttpClient mb=path->{
        requests.add(path);
        return path.startsWith("/artist?")?"{\"artists\":[{\"id\":\""+ID+"\",\"name\":\"Abbie Falls\",\"score\":100}]}":artist();
    };

    @Test public void retrievesActualArtistAndCorroborationFactsBeforeOfficialPageLinks() throws Exception {
        var result=provider().lookup("Abbie Falls");
        assertEquals(1,result.candidates().size());
        var e=result.candidates().get(0);
        assertEquals(ID,e.identityId());assertEquals(ID,e.corroboratedIdentityId());
        assertEquals("Group",e.artistType());assertEquals(List.of(SITE),e.officialWebsites());
        assertEquals(List.of(SITE),e.corroboratingWebsites());
        assertEquals(List.of(YT,SP),e.pages().get(0).links());
        assertFalse(result.retryable());assertTrue(requests.get(0).contains("Abbie+Falls"));
    }
    @Test public void aRefinedArtistIdentifierDoesNotRunNameSearch() throws Exception {
        provider().lookup(ID);
        assertTrue(requests.get(0).startsWith("/artist/"+ID+"?"));
        assertFalse(requests.stream().anyMatch(p->p.startsWith("/artist?")));
    }
    @Test public void aDifferentCorroboratedIdentityIsNotSilentlyReplaced() throws Exception {
        wikiId="22222222-2222-4222-8222-222222222222";
        var e=provider().lookup("Abbie Falls").candidates().get(0);
        assertEquals(wikiId,e.corroboratedIdentityId());
        // The application will reject this fact mismatch; the adapter cannot label it verified.
    }
    @Test public void websiteFailureIsRetryableAndCannotBeReportedAsCompletedVerification() throws Exception {
        websiteFails=true;var result=provider().lookup("Abbie Falls");
        assertTrue(result.retryable());assertTrue(result.candidates().get(0).pages().isEmpty());
        assertFalse(result.messages().isEmpty());
    }
    @Test public void jsonNullArtistValuesNeverCreateNullProfiles() throws Exception {
        var provider=new VerifiedMusicLinksProvider(path->"{\"artists\":[{\"id\":null,\"name\":null}]}",
                path->{throw new AssertionError("Invalid identity must not trigger corroboration");},
                url->{throw new AssertionError("Invalid identity must not trigger website retrieval");},List.of());
        assertTrue(provider.lookup("Unknown").candidates().isEmpty());
    }
    private VerifiedMusicLinksProvider provider(){
        return new VerifiedMusicLinksProvider(mb,path->wiki(),url->{
            if(websiteFails)throw new IOException("Unavailable");
            return new MusicLinkEvidence.Page(url,List.of(YT,SP));
        },List.of());
    }
    private String artist(){
        return "{\"id\":\""+ID+"\",\"name\":\"Abbie Falls\",\"type\":\"Group\",\"country\":\"CZ\",\"disambiguation\":\"metalcore band\",\"aliases\":[],\"relations\":["
                +relation("official homepage",SITE)+","+relation("wikidata","https://www.wikidata.org/wiki/Q123")+","
                +relation("youtube",YT)+","+relation("streaming",SP)+"]}";
    }
    private String relation(String type,String resource){return "{\"type\":\""+type+"\",\"url\":{\"resource\":\""+resource+"\"}}";}
    private String wiki(){
        return "{\"entities\":{\"Q123\":{\"labels\":{\"en\":{\"value\":\"Abbie Falls\"}},\"claims\":{"
                +claim("P434",wikiId)+","+claim("P856",SITE)+","+claim("P1902","1234567890123456789012")+","
                +claim("P2397","UC1234567890123456789012")+"}}}}";
    }
    private String claim(String property,String value){return "\""+property+"\":[{\"mainsnak\":{\"datavalue\":{\"value\":\""+value+"\"}}}]";}
}

