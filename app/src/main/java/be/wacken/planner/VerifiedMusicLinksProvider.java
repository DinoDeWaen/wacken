package be.wacken.planner;

import be.wacken.planner.application.*;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.json.*;

/** Collects source facts. The application use case, not this adapter, decides verification. */
final class VerifiedMusicLinksProvider implements MusicLinkLookupProvider {
    private static final String UUID="(?i)[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}";
    private final MusicBrainzMetadataProvider.MusicBrainzHttpClient musicBrainz;
    private final WikidataMetadataProvider.WikidataHttpClient wikidata;
    private final OfficialMusicPageClient.PageLoader pages;
    private final List<BandMetadataLookupProvider> optionalSearch;

    VerifiedMusicLinksProvider(){
        this(new MusicBrainzMetadataProvider.RateLimitedMusicBrainzHttpClient(BuildConfig.MUSICBRAINZ_USER_AGENT),
                new WikidataMetadataProvider.UrlConnectionWikidataHttpClient(),new OfficialMusicPageClient()::read,
                List.of(new SpotifyMetadataProvider(BuildConfig.SPOTIFY_CLIENT_ID,BuildConfig.SPOTIFY_CLIENT_SECRET),
                        new YouTubeMetadataProvider(BuildConfig.YOUTUBE_API_KEY)));
    }
    VerifiedMusicLinksProvider(MusicBrainzMetadataProvider.MusicBrainzHttpClient musicBrainz,
                              WikidataMetadataProvider.WikidataHttpClient wikidata,
                              OfficialMusicPageClient.PageLoader pages,List<BandMetadataLookupProvider> optionalSearch){
        this.musicBrainz=musicBrainz;this.wikidata=wikidata;this.pages=pages;this.optionalSearch=List.copyOf(optionalSearch);
    }

    @Override public Lookup lookup(String searchTerm)throws BandMetadataLookupException {
        List<MusicLinkEvidence> candidates=new ArrayList<>();
        List<String> messages=new ArrayList<>();
        boolean retryable=false;
        for(BandMetadataLookupProvider source:optionalSearch)
            if(!source.configured())messages.add(source.name()+" is not configured; public identity sources are still checked.");
        try {
            for(JSONObject artist:artists(searchTerm)){
                interrupted();
                String id=text(artist,"id");
                if(!id.matches(UUID))continue;
                JSONObject details=new JSONObject(musicBrainz.get("/artist/"+id+"?inc=url-rels+aliases&fmt=json"));
                if(!id.equalsIgnoreCase(text(details,"id")))continue;
                JSONObject item=wikidataItem(details,id);
                List<String> official=relationships(details,"official homepage");
                List<MusicLinkEvidence.Page> retrieved=new ArrayList<>();
                // Without corroborating identity/website facts, a webpage alone cannot suffice.
                if(id.equalsIgnoreCase(first(claims(item,"P434")))){
                    for(String website:official.stream().limit(2).toList()){
                        interrupted();
                        try {retrieved.add(pages.read(website));}
                        catch(IOException error){
                            retryable=true;messages.add("Official website evidence unavailable for "+text(details,"name")+". Retry unfinished verification.");
                        }
                    }
                }
                List<String> youtube=new ArrayList<>(musicRelationships(details,true));
                List<String> spotify=new ArrayList<>(musicRelationships(details,false));
                for(String channel:claims(item,"P2397"))youtube.add("https://www.youtube.com/channel/"+channel);
                for(String profile:claims(item,"P1902"))spotify.add("https://open.spotify.com/artist/"+profile);
                candidates.add(new MusicLinkEvidence(id,text(details,"name"),text(details,"type"),
                        description(details),aliases(details),"https://musicbrainz.org/artist/"+id,
                        text(item,"id").matches("Q[0-9]+")?"https://www.wikidata.org/wiki/"+text(item,"id"):"",
                        first(claims(item,"P434")),englishLabel(item),official,claims(item,"P856"),youtube,spotify,retrieved));
            }
            if(candidates.isEmpty()){
                for(BandMetadataLookupProvider source:optionalSearch){
                    interrupted();if(!source.configured())continue;
                    try {
                        for(BandMetadataProviderCandidate candidate:source.search(searchTerm)){
                            candidates.add(new MusicLinkEvidence("",candidate.candidateName(),"",
                                    "Unverified "+source.name()+" search result",List.of(),candidate.sourceUrl().orElse(""),
                                    "","","",List.of(),List.of(),candidate.youtubeUrl().stream().toList(),
                                    candidate.spotifyUrl().stream().toList(),List.of()));
                        }
                    }catch(BandMetadataLookupException error){retryable=true;messages.add(source.name()+" lookup failed. Retry later.");}
                }
            }
            return new Lookup(candidates,messages,retryable);
        }catch(IOException|JSONException error){
            String message=error.getMessage()!=null&&error.getMessage().contains("429")
                    ?"Sources are rate limited (HTTP 429). Retry unfinished work later."
                    :"Sources are unavailable. Check connectivity and retry unfinished work.";
            throw new BandMetadataLookupException(message,error);
        }
    }

    private List<JSONObject> artists(String term)throws IOException,JSONException {
        String value=term==null?"":term.trim();
        if(value.matches(UUID)){
            // Identity refinement uses the authoritative identifier, never a renamed local band.
            return List.of(new JSONObject().put("id",value));
        }
        String escaped=value.replace("\\","\\\\").replace("\"","\\\"");
        JSONObject root=new JSONObject(musicBrainz.get("/artist?query="+encode("artist:\""+escaped+"\"")+"&limit=3&fmt=json"));
        return objects(root.optJSONArray("artists"));
    }
    private JSONObject wikidataItem(JSONObject artist,String mbid)throws IOException,JSONException {
        for(String url:relationships(artist,"wikidata")){
            String qid=entityId(url);
            if(!qid.isEmpty())return entity(qid);
        }
        JSONObject search=new JSONObject(wikidata.get("?action=wbsearchentities&search="+encode(text(artist,"name"))
                +"&language=en&type=item&limit=3&format=json"));
        for(JSONObject result:objects(search.optJSONArray("search"))){
            interrupted();String id=text(result,"id");
            if(!id.matches("Q[0-9]+"))continue;
            JSONObject candidate=entity(id);
            if(claims(candidate,"P434").contains(mbid))return candidate;
        }
        return new JSONObject();
    }
    private JSONObject entity(String id)throws IOException,JSONException {
        interrupted();
        JSONObject root=new JSONObject(wikidata.get("?action=wbgetentities&ids="+id
                +"&props=labels%7Cdescriptions%7Cclaims&languages=en&format=json"));
        JSONObject entities=root.optJSONObject("entities");
        JSONObject item=entities==null?null:entities.optJSONObject(id);
        if(item==null)return new JSONObject();
        // The requested key is authoritative even when the API omits an explicit id in fixtures.
        item.put("id",id);return item;
    }
    private String entityId(String value){
        try {
            URI uri=URI.create(value);
            if(uri.getHost()!=null&&(uri.getHost().equalsIgnoreCase("wikidata.org")||uri.getHost().equalsIgnoreCase("www.wikidata.org"))
                    &&uri.getUserInfo()==null&&uri.getPath().matches("/wiki/Q[0-9]+"))return uri.getPath().substring(6);
        }catch(IllegalArgumentException ignored){}
        return "";
    }
    private List<String> relationships(JSONObject artist,String type){
        List<String> urls=new ArrayList<>();
        for(JSONObject relation:objects(artist.optJSONArray("relations"))){
            JSONObject url=relation.optJSONObject("url");
            if(type.equals(text(relation,"type"))&&url!=null&&!text(url,"resource").isBlank())urls.add(text(url,"resource"));
        }
        return urls.stream().distinct().toList();
    }
    private List<String> musicRelationships(JSONObject artist,boolean youtube){
        List<String> urls=new ArrayList<>();
        for(JSONObject relation:objects(artist.optJSONArray("relations"))){
            JSONObject url=relation.optJSONObject("url");
            if(url!=null)FindBandMusicLinksUseCase.canonicalProfile(text(url,"resource"),youtube).ifPresent(urls::add);
        }
        return urls.stream().distinct().toList();
    }
    private List<String> aliases(JSONObject artist){
        return objects(artist.optJSONArray("aliases")).stream().map(a->text(a,"name")).filter(s->!s.isBlank()).toList();
    }
    private List<String> claims(JSONObject item,String property){
        JSONObject claims=item.optJSONObject("claims");if(claims==null)return List.of();
        List<String> values=new ArrayList<>();
        for(JSONObject claim:objects(claims.optJSONArray(property))){
            if("deprecated".equals(text(claim,"rank")))continue;
            JSONObject snak=claim.optJSONObject("mainsnak"),value=snak==null?null:snak.optJSONObject("datavalue");
            if(value!=null&&!text(value,"value").isBlank())values.add(text(value,"value"));
        }
        return values.stream().distinct().toList();
    }
    private String englishLabel(JSONObject item){
        JSONObject labels=item.optJSONObject("labels"),english=labels==null?null:labels.optJSONObject("en");
        return english==null?"":text(english,"value");
    }
    private String description(JSONObject artist){
        return String.join(" · ",List.of(text(artist,"country"),text(artist,"disambiguation")).stream().filter(s->!s.isBlank()).toList());
    }
    private static String text(JSONObject object,String key){
        Object value=object.opt(key);return value instanceof String s?s.trim():"";
    }
    private List<JSONObject> objects(JSONArray array){
        if(array==null)return List.of();List<JSONObject> result=new ArrayList<>();
        for(int i=0;i<array.length();i++){JSONObject object=array.optJSONObject(i);if(object!=null)result.add(object);}return result;
    }
    private String encode(String value){
        try{return URLEncoder.encode(value,StandardCharsets.UTF_8.name());}
        catch(java.io.UnsupportedEncodingException impossible){throw new AssertionError(impossible);}
    }
    private String first(List<String> values){return values.size()==1?values.get(0):"";}
    private void interrupted()throws IOException {if(Thread.currentThread().isInterrupted())throw new java.io.InterruptedIOException("Lookup cancelled.");}
}
