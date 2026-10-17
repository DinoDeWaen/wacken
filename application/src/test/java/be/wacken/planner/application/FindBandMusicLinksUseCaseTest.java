package be.wacken.planner.application;

import static org.junit.jupiter.api.Assertions.*;
import be.wacken.planner.domain.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class FindBandMusicLinksUseCaseTest {
    private static final String ID="11111111-1111-4111-8111-111111111111";
    private static final String YT="https://www.youtube.com/channel/UC1234567890123456789012";
    private static final String SP="https://open.spotify.com/artist/1234567890123456789012";
    private static final String SITE="https://band.example/music";
    private final Map<String,Band> data=new LinkedHashMap<>();
    private final List<String> queries=new ArrayList<>();
    private List<MusicLinkEvidence> evidence=List.of(proof(ID));
    private boolean fail;

    private FindBandMusicLinksUseCase useCase(){
        BandRepository bands=new BandRepository(){
            public Optional<Band> findByName(String name){return Optional.ofNullable(data.get(name));}
            public List<Band> findAll(){return List.copyOf(data.values());}
            public void save(Band band){data.put(band.name(),band);}
            public void replaceAll(List<Band> bands){throw new AssertionError("Discovery must not replace the catalog");}
        };
        FestivalLineupRepository lineup=new FestivalLineupRepository(){
            public List<FestivalLineupEntry> findByFestival(String id){return List.of(new FestivalLineupEntry(id,new Band("Abbie Falls"),"Abbie Falls"));}
            public void saveAllForFestival(String id,List<FestivalLineupEntry> entries){throw new AssertionError("Discovery must not change lineup");}
        };
        return new FindBandMusicLinksUseCase(bands,lineup,term->{
            queries.add(term);
            if(fail)throw new BandMetadataLookupException("Rate limited",new RuntimeException());
            return new MusicLinkLookupProvider.Lookup(evidence,List.of());
        });
    }
    private void seed(){data.put("Abbie Falls",new Band("Abbie Falls"));data.put("Other band",new Band("Other band"));}

    @Test void lookupUsesEditedTermWithoutRenamingOrSavingUntilApproval(){
        seed();var app=useCase();var review=app.searchBand("Abbie Falls",ID,()->false);
        assertEquals(List.of(ID),queries);assertTrue(data.get("Abbie Falls").youtubeUrl().isEmpty());
        assertTrue(review.candidates().get(0).verified());
        assertTrue(app.approve(review,0).success());
        assertEquals(Optional.of(YT),data.get("Abbie Falls").youtubeUrl());
        assertEquals(Optional.of(SP),data.get("Abbie Falls").spotifyUrl());
        assertEquals("Abbie Falls",data.get("Abbie Falls").name());
        assertTrue(data.get("Other band").youtubeUrl().isEmpty());
    }
    @Test void festivalScopeExcludesOtherCatalogBandsAndSkipsCompletedWorkOnRetry(){
        seed();var app=useCase();var first=app.searchFestival("festival-a",Map.of(),Map.of(),()->false,r->{});
        assertEquals(List.of("Abbie Falls"),queries);assertEquals(1,first.size());
        app.searchFestival("festival-a",Map.of(),first,()->false,r->{});
        assertEquals(1,queries.size());
    }
    @Test void nameAndRankingWithoutEvidenceCanNeverBeApproved(){
        seed();evidence=List.of(new MusicLinkEvidence(ID,"Abbie Falls","Group","Name match only",List.of(),
                "https://musicbrainz.org/artist/"+ID,"","","",List.of(),List.of(),List.of(YT),List.of(SP),List.of()));
        var app=useCase();var review=app.searchBand("Abbie Falls","Abbie Falls",()->false);
        assertFalse(review.candidates().get(0).verified());assertFalse(app.approve(review,0).success());
        assertTrue(data.get("Abbie Falls").youtubeUrl().isEmpty());
    }
    @Test void corroboratedIdentityMismatchRejectsAnOtherwiseMatchingOfficialProfile(){
        seed();var p=proof(ID);
        evidence=List.of(new MusicLinkEvidence(p.identityId(),p.name(),p.artistType(),p.description(),p.aliases(),
                p.sourceUrl(),p.corroborationUrl(),"22222222-2222-4222-8222-222222222222",p.corroborationName(),
                p.officialWebsites(),p.corroboratingWebsites(),p.youtubeUrls(),p.spotifyUrls(),p.pages()));
        var app=useCase();var r=app.searchBand("Abbie Falls","Abbie Falls",()->false);
        assertFalse(r.candidates().get(0).verified());assertFalse(app.approve(r,0).success());
    }
    @Test void sameNameArtistsStayAmbiguousUntilSearchIsRefined(){
        seed();evidence=List.of(proof(ID),proof("22222222-2222-4222-8222-222222222222"));
        var app=useCase();var r=app.searchBand("Abbie Falls","Abbie Falls",()->false);
        assertTrue(r.candidates().stream().noneMatch(FindBandMusicLinksUseCase.Candidate::verified));
        assertFalse(app.approve(r,0).success());
        evidence=List.of(proof(ID));
        assertTrue(app.searchBand("Abbie Falls",ID,()->false).candidates().get(0).verified());
    }
    @Test void knownFieldsAndBiographyStayUnchangedEvenIfTheyArriveAfterLookup(){
        seed();var app=useCase();var review=app.searchBand("Abbie Falls","Abbie Falls",()->false);
        data.put("Abbie Falls",new Band("Abbie Falls",Optional.of("Original"),Optional.empty(),Optional.of(YT),Optional.empty()));
        assertTrue(app.approve(review,0).success());
        assertEquals(Optional.of(YT),data.get("Abbie Falls").youtubeUrl());
        assertEquals(Optional.of("Original"),data.get("Abbie Falls").biography());
    }
    @Test void conflictingKnownIdentityCannotAcquireAnotherArtistsMissingProfile(){
        seed();var app=useCase();var review=app.searchBand("Abbie Falls","Abbie Falls",()->false);
        data.put("Abbie Falls",new Band("Abbie Falls",Optional.empty(),Optional.empty(),Optional.of("https://www.youtube.com/@anotherBand"),Optional.empty()));
        assertFalse(app.approve(review,0).success());assertTrue(data.get("Abbie Falls").spotifyUrl().isEmpty());
    }
    @Test void completeKnownProfilesAvoidLookupAndUnsupportedFieldsNeverGetFalseSave(){
        seed();data.put("Abbie Falls",new Band("Abbie Falls",Optional.empty(),Optional.empty(),Optional.of(YT),Optional.of(SP)));
        assertEquals(FindBandMusicLinksUseCase.State.ALREADY_COMPLETE,useCase().searchBand("Abbie Falls","Abbie Falls",()->false).state());
        assertTrue(queries.isEmpty());
    }
    @Test void failedEvidenceRetrievalRemainsRetryableEvenWhenAnIdentityWasFound(){
        seed();var app=new FindBandMusicLinksUseCase(new BandRepository(){
            public Optional<Band> findByName(String name){return Optional.ofNullable(data.get(name));}
            public List<Band> findAll(){return List.copyOf(data.values());}
            public void save(Band band){throw new AssertionError("Incomplete verification must never write");}
            public void replaceAll(List<Band> bands){throw new AssertionError();}
        },new FestivalLineupRepository(){
            public List<FestivalLineupEntry> findByFestival(String id){return List.of();}
            public void saveAllForFestival(String id,List<FestivalLineupEntry> rows){throw new AssertionError();}
        },term->new MusicLinkLookupProvider.Lookup(evidence,List.of("Evidence fetch failed"),true));
        var review=app.searchBand("Abbie Falls","Abbie Falls",()->false);
        assertEquals(FindBandMusicLinksUseCase.State.ERROR,review.state());assertTrue(review.retryable());
        assertFalse(app.approve(review,0).success());
    }
    @Test void noMatchRejectionAndCancellationNeverWrite(){
        seed();var app=useCase();evidence=List.of();
        var r=app.searchBand("Abbie Falls","Abbie Falls",()->false);assertEquals(FindBandMusicLinksUseCase.State.NO_MATCH,r.state());
        assertFalse(app.approve(r,0).success());
        assertEquals(FindBandMusicLinksUseCase.State.CANCELLED,app.searchBand("Abbie Falls","Abbie Falls",()->true).state());
        assertTrue(data.get("Abbie Falls").youtubeUrl().isEmpty());
    }
    @Test void failureIsRetryableWithoutFalseSuccessOrChangedMetadata(){
        seed();var app=useCase();fail=true;var first=app.searchFestival("festival-a",Map.of(),Map.of(),()->false,r->{});
        assertEquals(FindBandMusicLinksUseCase.State.ERROR,first.get("Abbie Falls").state());
        fail=false;var retry=app.searchFestival("festival-a",Map.of(),first,()->false,r->{});
        assertEquals(2,queries.size());assertTrue(retry.get("Abbie Falls").candidates().get(0).verified());
        assertTrue(data.get("Abbie Falls").youtubeUrl().isEmpty());
    }
    @Test void playlistVideosAndUserProfilesDoNotBecomeMusicLinks(){
        seed();var p=proof(ID);evidence=List.of(new MusicLinkEvidence(p.identityId(),p.name(),p.artistType(),p.description(),p.aliases(),
                p.sourceUrl(),p.corroborationUrl(),ID,p.corroborationName(),p.officialWebsites(),p.corroboratingWebsites(),
                List.of("https://youtube.com/watch?v=video"),List.of("https://open.spotify.com/user/account"),
                List.of(new MusicLinkEvidence.Page(SITE,List.of("https://youtube.com/watch?v=video","https://open.spotify.com/user/account")))));
        assertFalse(useCase().searchBand("Abbie Falls","Abbie Falls",()->false).candidates().get(0).verified());
    }
    @Test void cancelledBatchStopsAfterCurrentBandAndPreservesCompletedResults(){
        seed();var cancelled=new AtomicBoolean();
        var r=useCase().searchFestival("festival-a",Map.of(),Map.of(),cancelled::get,v->{
            if(v.state()!=FindBandMusicLinksUseCase.State.SEARCHING)cancelled.set(true);
        });
        assertEquals(1,r.size());assertTrue(r.get("Abbie Falls").candidates().get(0).verified());
    }
    private static MusicLinkEvidence proof(String id){
        return new MusicLinkEvidence(id,"Abbie Falls","Group","Czech metalcore band",List.of(),
                "https://musicbrainz.org/artist/"+id,"https://www.wikidata.org/wiki/Q123",id,"Abbie Falls",
                List.of(SITE),List.of(SITE),List.of(YT),List.of(SP),List.of(new MusicLinkEvidence.Page(SITE,List.of(YT,SP))));
    }
}
