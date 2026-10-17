package be.wacken.planner.application;

import java.util.List;

/** Retrieved facts, not a caller-supplied verification decision. */
public record MusicLinkEvidence(
        String identityId, String name, String artistType, String description, List<String> aliases,
        String sourceUrl, String corroborationUrl, String corroboratedIdentityId, String corroborationName,
        List<String> officialWebsites, List<String> corroboratingWebsites,
        List<String> youtubeUrls, List<String> spotifyUrls, List<Page> pages) {
    public MusicLinkEvidence {
        identityId=text(identityId); name=text(name); artistType=text(artistType); description=text(description);
        sourceUrl=text(sourceUrl);corroborationUrl=text(corroborationUrl);
        corroboratedIdentityId=text(corroboratedIdentityId);corroborationName=text(corroborationName);
        aliases=copy(aliases);officialWebsites=copy(officialWebsites);corroboratingWebsites=copy(corroboratingWebsites);
        youtubeUrls=copy(youtubeUrls);spotifyUrls=copy(spotifyUrls);pages=pages==null?List.of():List.copyOf(pages);
    }
    public record Page(String sourceUrl,List<String> links){
        public Page {sourceUrl=text(sourceUrl);links=copy(links);}
    }
    private static String text(String value){return value==null?"":value.trim();}
    private static List<String> copy(List<String> values){
        return values==null?List.of():values.stream().filter(java.util.Objects::nonNull).map(String::trim).filter(s->!s.isBlank()).toList();
    }
}

