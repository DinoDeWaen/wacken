package be.wacken.planner.infrastructure;

import static org.junit.jupiter.api.Assertions.*;
import be.wacken.planner.application.*;
import be.wacken.planner.domain.*;
import java.util.*;
import org.junit.jupiter.api.Test;

/** Acceptance scenarios exercise the real remote-first repository composition. */
final class VerifiedMusicLinksSyncTest {
    private static final String NAME="Review Band", ID="11111111-1111-4111-8111-111111111111";
    private static final String YT="https://www.youtube.com/channel/UC1234567890123456789012";
    private final InMemoryBandRepository source=new InMemoryBandRepository();
    private final InMemoryBandRepository adminCache=new InMemoryBandRepository();

    @Test void givenApprovedEvidenceWhenSavedThenOtherMembersReceiveTheProfileOnSync(){
        source.save(new Band(NAME));adminCache.save(new Band(NAME));
        var app=application(new SyncedBandRepository(adminCache,source));
        var review=app.searchBand(NAME,NAME,()->false);
        assertTrue(source.findByName(NAME).orElseThrow().youtubeUrl().isEmpty(),"Lookup alone never writes");
        assertTrue(app.approve(review,0).success());
        var memberCache=new InMemoryBandRepository();
        new SyncedBandRepository(memberCache,source).syncSourceToCache();
        assertEquals(Optional.of(YT),memberCache.findByName(NAME).orElseThrow().youtubeUrl());
        assertEquals(source.findByName(NAME),adminCache.findByName(NAME));
    }

    @Test void givenRemoteSaveFailureWhenApprovedThenCacheDoesNotClaimSuccessAndRetryCanFinish(){
        source.save(new Band(NAME));adminCache.save(new Band(NAME));
        BandRepository unavailable=new BandRepository(){
            public void save(Band b){throw new IllegalStateException("offline");}
            public void replaceAll(List<Band> b){throw new AssertionError();}
            public Optional<Band> findByName(String n){return source.findByName(n);}
            public List<Band> findAll(){return source.findAll();}
        };
        var app=application(new SyncedBandRepository(adminCache,unavailable));
        var review=app.searchBand(NAME,NAME,()->false);
        assertFalse(app.approve(review,0).success());
        assertTrue(adminCache.findByName(NAME).orElseThrow().youtubeUrl().isEmpty());
        assertTrue(source.findByName(NAME).orElseThrow().youtubeUrl().isEmpty());
        assertTrue(application(new SyncedBandRepository(adminCache,source)).approve(review,0).success());
    }
    private FindBandMusicLinksUseCase application(BandRepository repository){
        FestivalLineupRepository lineups=new FestivalLineupRepository(){
            public List<FestivalLineupEntry> findByFestival(String id){return List.of();}
            public void saveAllForFestival(String id,List<FestivalLineupEntry> e){throw new AssertionError();}
        };
        String site="https://band.example/";
        var proof=new MusicLinkEvidence(ID,NAME,"Group","Distinct artist",List.of(),
                "https://musicbrainz.org/artist/"+ID,"https://www.wikidata.org/wiki/Q123",ID,NAME,
                List.of(site),List.of(site),List.of(YT),List.of(),List.of(new MusicLinkEvidence.Page(site,List.of(YT))));
        return new FindBandMusicLinksUseCase(repository,lineups,term->new MusicLinkLookupProvider.Lookup(List.of(proof),List.of()));
    }
}

