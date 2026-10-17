package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.FestivalLineupRepository;
import java.net.URI;
import java.text.Normalizer;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** Owns music-only scope, identity evidence, ambiguity, retry and approval. No UI/network framework. */
public final class FindBandMusicLinksUseCase {
    public enum State { SEARCHING, REVIEW, NO_MATCH, ALREADY_COMPLETE, NOT_FOUND, ERROR, CANCELLED }
    public record Candidate(MusicLinkEvidence evidence,Optional<String> youtubeUrl,Optional<String> spotifyUrl,String rationale){
        public boolean verified(){return youtubeUrl.isPresent()||spotifyUrl.isPresent();}
    }
    public record Review(String bandName,String searchTerm,State state,List<Candidate> candidates,List<String> messages){
        public Review {candidates=List.copyOf(candidates);messages=List.copyOf(messages);}
        public boolean retryable(){return state==State.ERROR||state==State.CANCELLED;}
    }
    private final BandRepository bands;
    private final FestivalLineupRepository lineups;
    private final MusicLinkLookupProvider provider;

    public FindBandMusicLinksUseCase(BandRepository bands,FestivalLineupRepository lineups,MusicLinkLookupProvider provider){
        this.bands=Objects.requireNonNull(bands);this.lineups=Objects.requireNonNull(lineups);this.provider=Objects.requireNonNull(provider);
    }

    public List<Band> festivalBands(String festivalId){
        if(festivalId==null||festivalId.isBlank())return List.of();
        return lineups.findByFestival(festivalId).stream().map(e->e.band().name()).distinct()
                .map(bands::findByName).flatMap(Optional::stream).filter(this::missingMusic).toList();
    }
    public Optional<Band> band(String name){return bands.findByName(name);}

    public Review searchBand(String bandName,String searchTerm,BooleanSupplier cancelled){
        String term=searchTerm==null||searchTerm.isBlank()?bandName:searchTerm.trim();
        if(cancelled.getAsBoolean())return empty(bandName,term,State.CANCELLED,"Search cancelled. No links changed.");
        Band band=bands.findByName(bandName).orElse(null);
        if(band==null)return empty(bandName,term,State.NOT_FOUND,"Band no longer exists.");
        if(!missingMusic(band))return empty(bandName,term,State.ALREADY_COMPLETE,"Existing music links kept. Nothing missing.");
        try {
            MusicLinkLookupProvider.Lookup lookup=provider.lookup(term);
            if(cancelled.getAsBoolean())return empty(bandName,term,State.CANCELLED,"Search cancelled. No links changed.");
            if(lookup.candidates().isEmpty())return new Review(bandName,term,lookup.retryable()?State.ERROR:State.NO_MATCH,List.of(),
                    append(lookup.messages(),"No verified match. Missing links stay empty."));
            List<Candidate> checked=lookup.candidates().stream().map(e->forCurrentBand(band,e)).toList();
            long identities=checked.stream().filter(Candidate::verified).map(c->c.evidence().identityId()).distinct().count();
            if(identities>1)checked=checked.stream().map(c->new Candidate(c.evidence(),Optional.empty(),Optional.empty(),
                    "Ambiguous namesakes. Refine to the intended artist identifier after checking its sources.")).toList();
            return new Review(bandName,term,lookup.retryable()?State.ERROR:State.REVIEW,checked,lookup.messages());
        } catch(BandMetadataLookupException error){
            if(cancelled.getAsBoolean())return empty(bandName,term,State.CANCELLED,"Search cancelled. No links changed.");
            return empty(bandName,term,State.ERROR,error.getMessage()==null
                    ?"Sources could not be checked. No links changed. Retry unfinished work.":error.getMessage());
        } catch(RuntimeException error){
            if(cancelled.getAsBoolean())return empty(bandName,term,State.CANCELLED,"Search cancelled. No links changed.");
            return empty(bandName,term,State.ERROR,"Sources could not be checked. No links changed. Retry unfinished work.");
        }
    }

    public Map<String,Review> searchFestival(String festivalId,Map<String,String> searchTerms,Map<String,Review> previous,
                                             BooleanSupplier cancelled,Consumer<Review> progress){
        Map<String,Review> results=new LinkedHashMap<>();
        for(Band band:festivalBands(festivalId)){
            if(cancelled.getAsBoolean())break;
            String term=searchTerms.getOrDefault(band.name(),band.name()).trim();
            if(term.isBlank())term=band.name();
            Review old=previous.get(band.name());
            boolean reusable=old!=null&&old.bandName().equals(band.name())&&old.state()!=State.SEARCHING
                    &&!old.retryable()&&old.searchTerm().equals(term);
            if(!reusable)progress.accept(empty(band.name(),term,State.SEARCHING,"Checking identity sources…"));
            Review review=reusable?old:searchBand(band.name(),term,cancelled);
            results.put(band.name(),review);progress.accept(review);
        }
        return Collections.unmodifiableMap(results);
    }

    public ApplyBandMetadataProposalsResult approve(Review review,int selected){
        if(review==null||review.state()!=State.REVIEW||selected<0||selected>=review.candidates().size())
            return ApplyBandMetadataProposalsResult.failure("Choose an identity-verified result first.");
        // Re-evaluate raw evidence; a checked UI control or a passed boolean is never verification.
        if(!review.candidates().get(selected).verified())
            return ApplyBandMetadataProposalsResult.failure("Unverified or ambiguous links cannot be saved.");
        if(review.candidates().stream().filter(Candidate::verified).map(c->c.evidence().identityId()).distinct().count()>1)
            return ApplyBandMetadataProposalsResult.failure("Resolve the band identity before saving.");
        Band current=bands.findByName(review.bandName()).orElse(null);
        if(current==null)return ApplyBandMetadataProposalsResult.failure("Band no longer exists.");
        Candidate candidate=forCurrentBand(current,review.candidates().get(selected).evidence());
        if(!candidate.verified())return ApplyBandMetadataProposalsResult.failure(candidate.rationale());
        List<BandMetadataProposal> proposals=new ArrayList<>();
        candidate.youtubeUrl().ifPresent(url->proposals.add(proposal(review.bandName(),candidate,BandMetadataField.YOUTUBE_URL,url)));
        candidate.spotifyUrl().ifPresent(url->proposals.add(proposal(review.bandName(),candidate,BandMetadataField.SPOTIFY_URL,url)));
        try {
            return new ApplyBandMetadataProposalsUseCase(bands).apply(review.bandName(),proposals);
        } catch(RuntimeException error){
            return ApplyBandMetadataProposalsResult.failure("Save could not be confirmed. Retry; existing links are not overwritten.");
        }
    }

    private BandMetadataProposal proposal(String band,Candidate candidate,BandMetadataField field,String url){
        return new BandMetadataProposal(band,field,url,"Verified official artist links",Optional.of(candidate.evidence().sourceUrl()),
                candidate.evidence().name(),100);
    }
    private boolean missingMusic(Band band){return band.youtubeUrl().isEmpty()||band.spotifyUrl().isEmpty();}
    private Review empty(String band,String term,State state,String message){return new Review(band,term,state,List.of(),List.of(message));}
    private List<String> append(List<String> original,String value){List<String> copy=new ArrayList<>(original);copy.add(value);return copy;}

    private Candidate forCurrentBand(Band band,MusicLinkEvidence evidence){
        Candidate verified=verify(band.name(),evidence);
        if(conflicts(band.youtubeUrl(),evidence.youtubeUrls(),true)
                ||conflicts(band.spotifyUrl(),evidence.spotifyUrls(),false))
            return new Candidate(evidence,Optional.empty(),Optional.empty(),
                    "The result conflicts with a known band profile. Check the identity before adding links.");
        Optional<String> youtube=band.youtubeUrl().isEmpty()?verified.youtubeUrl():Optional.empty();
        Optional<String> spotify=band.spotifyUrl().isEmpty()?verified.spotifyUrl():Optional.empty();
        return new Candidate(evidence,youtube,spotify,verified.rationale());
    }

    private boolean conflicts(Optional<String> known,List<String> candidates,boolean youtube){
        if(known.isEmpty())return false;
        Optional<String> canonical=canonicalProfile(known.get(),youtube);
        return canonical.isEmpty()||candidates.stream().map(url->canonicalProfile(url,youtube))
                .flatMap(Optional::stream).noneMatch(canonical.get()::equals);
    }

    private Candidate verify(String band,MusicLinkEvidence e){
        boolean identity=e.identityId().matches("(?i)[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}")
                &&e.identityId().equalsIgnoreCase(e.corroboratedIdentityId())
                &&source(e.sourceUrl(),"musicbrainz.org","/artist/"+e.identityId())
                &&source(e.corroborationUrl(),"wikidata.org",null)
                &&e.artistType().equalsIgnoreCase("Group")&&matchesName(band,e)
                &&matchesName(e.corroborationName(),e);
        if(!identity)return new Candidate(e,Optional.empty(),Optional.empty(),
                "Unverified identity. A name or ranking is insufficient; matching authoritative artist identifiers and band identity are required.");
        Set<String> links=new LinkedHashSet<>();
        for(MusicLinkEvidence.Page page:e.pages()){
            String site=websiteKey(page.sourceUrl());
            if(!site.isEmpty()&&e.officialWebsites().stream().map(FindBandMusicLinksUseCase::websiteKey).anyMatch(site::equals)
                    &&e.corroboratingWebsites().stream().map(FindBandMusicLinksUseCase::websiteKey).anyMatch(site::equals))links.addAll(page.links());
        }
        Optional<String> youtube=uniqueOfficialProfile(e.youtubeUrls(),links,true);
        Optional<String> spotify=uniqueOfficialProfile(e.spotifyUrls(),links,false);
        return new Candidate(e,youtube,spotify,youtube.isPresent()||spotify.isPresent()
                ?"Artist identity corroborated by MusicBrainz and Wikidata; each offered profile is linked by the official artist website. Only verified missing fields can be saved."
                :"No unique official cross-link could be verified. Missing profiles remain empty; inspect sources or refine the identity.");
    }

    private boolean matchesName(String name,MusicLinkEvidence evidence){
        String normalized=normalizeName(name);
        return !normalized.isEmpty()&&(normalized.equals(normalizeName(evidence.name()))
                ||evidence.aliases().stream().map(FindBandMusicLinksUseCase::normalizeName).anyMatch(normalized::equals));
    }
    private static String normalizeName(String value){
        return value==null?"":Normalizer.normalize(value,Normalizer.Form.NFKC).trim().replaceAll("\\s+"," ").toLowerCase(Locale.ROOT);
    }
    private boolean source(String value,String host,String path){
        try {
            URI uri=URI.create(value);String domain=uri.getHost();
            return "https".equalsIgnoreCase(uri.getScheme())&&uri.getUserInfo()==null&&(uri.getPort()==-1||uri.getPort()==443)
                    &&domain!=null&&(domain.equalsIgnoreCase(host)||domain.equalsIgnoreCase("www."+host))
                    &&(path==null?uri.getPath().matches("/wiki/Q[0-9]+"):uri.getPath().equalsIgnoreCase(path));
        }catch(IllegalArgumentException error){return false;}
    }
    private Optional<String> uniqueOfficialProfile(List<String> candidates,Set<String> linked,boolean youtube){
        Set<String> official=new LinkedHashSet<>();
        linked.stream().map(url->canonicalProfile(url,youtube)).flatMap(Optional::stream).forEach(official::add);
        List<String> matches=candidates.stream().map(url->canonicalProfile(url,youtube)).flatMap(Optional::stream)
                .filter(official::contains).distinct().toList();
        return matches.size()==1?Optional.of(matches.get(0)):Optional.empty();
    }
    public static Optional<String> canonicalProfile(String value,boolean youtube){
        try {
            URI uri=URI.create(value.trim());String host=uri.getHost(),path=uri.getPath();
            if(!"https".equalsIgnoreCase(uri.getScheme())||host==null||uri.getUserInfo()!=null||(uri.getPort()!=-1&&uri.getPort()!=443))
                return Optional.empty();
            if(youtube&&(host.equalsIgnoreCase("youtube.com")||host.equalsIgnoreCase("www.youtube.com"))
                    &&(path.matches("/channel/UC[A-Za-z0-9_-]{22}/?")||path.matches("/@[A-Za-z0-9._-]{3,30}/?")))
                return Optional.of("https://www.youtube.com"+path.replaceAll("/$",""));
            if(!youtube&&host.equalsIgnoreCase("open.spotify.com")&&path.matches("/artist/[A-Za-z0-9]{22}/?"))
                return Optional.of("https://open.spotify.com"+path.replaceAll("/$",""));
        }catch(IllegalArgumentException|NullPointerException ignored){}
        return Optional.empty();
    }
    private static String websiteKey(String value){
        try {
            URI uri=URI.create(value).normalize();String host=uri.getHost();
            if(host==null||uri.getUserInfo()!=null||!(uri.getScheme().equalsIgnoreCase("https")||uri.getScheme().equalsIgnoreCase("http"))
                    ||(uri.getPort()!=-1&&uri.getPort()!=443))return "";
            return host.toLowerCase(Locale.ROOT).replaceFirst("^www\\.","")+uri.getPath().replaceAll("/$","")
                    +(uri.getQuery()==null?"":"?"+uri.getQuery());
        }catch(IllegalArgumentException|NullPointerException error){return "";}
    }
}
