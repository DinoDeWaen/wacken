package be.wacken.planner.application;

import be.wacken.planner.domain.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class ReviewFestivalBandsUseCaseTest {
    private final CanonicalBand known = new CanonicalBand("band-1", new Band("Abbie Falls"));
    private final ConfirmedBandAlias alias = new ConfirmedBandAlias("AF band", known, "member", Instant.EPOCH);
    private ReviewFestivalBandsUseCase review() { return new ReviewFestivalBandsUseCase(() -> new FestivalBandCatalog.Snapshot(List.of(known),List.of(alias))); }

    @Test void exactNamesAreCertainButFuzzyAndRememberedAliasesRequireExplicitChoice() {
        var rows=review().review(List.of(new Band("Abbie Falls"),new Band("Abbie Fall"),new Band("AF BAND"),new Band("Unrelated")));
        assertEquals(Optional.of(known),rows.get(0).exactMatch());
        assertEquals(List.of(known),rows.get(1).suggestions());
        assertEquals(Optional.empty(),rows.get(1).exactMatch());
        assertEquals(List.of(known),rows.get(2).suggestions());
        assertEquals(Optional.empty(),rows.get(2).exactMatch());
        assertTrue(rows.get(3).suggestions().isEmpty());
    }
    @Test void reviewIsDistinctAndCancellationHasNoCreationSideEffects() {
        assertEquals(1,review().review(List.of(new Band("Abbie Fall"),new Band("Abbie Fall"))).size());
    }
    @Test void remembersOnlyAnExplicitSelectedCanonicalIdentity() {
        assertThrows(DomainValidationException.class,()->new ReviewedFestivalBand("Unknown",Optional.empty(),true));
        var choice=new ReviewedFestivalBand("AF band",Optional.of(known),true);
        assertEquals("band-1",choice.selected().orElseThrow().id());
        assertEquals("af band",ConfirmedBandAlias.key("  AF\t BAND  "));
    }
    @Test void reviewedCreationValidatesSessionAndDuplicateImportedRowsBeforeWriting() {
        AtomicInteger writes=new AtomicInteger();
        FestivalCreationGateway gateway=new FestivalCreationGateway() {
            public AddFestivalResult create(String g,String u,Festival f,List<Band> b) { fail("Legacy path must not receive reviewed choices");return null; }
            public AddFestivalResult createReviewed(String g,String u,Festival f,List<ReviewedFestivalBand> b) { writes.incrementAndGet();assertEquals("band-1",b.get(0).selected().orElseThrow().id());return AddFestivalResult.added(1,0,1); }
        };
        var useCase=new AddFestivalUseCase(gateway);
        var selected=new ReviewedFestivalBand("AF band",Optional.of(known),true);
        assertFalse(useCase.addReviewedFestival("","user","new","Next",List.of(selected)).success());
        assertFalse(useCase.addReviewedFestival("g","u","new","Next",List.of()).success());
        assertFalse(useCase.addReviewedFestival("g","u","new","Next",List.of(selected,selected)).success());
        assertTrue(useCase.addReviewedFestival("g","u","new","Next",List.of(selected)).success());
        assertEquals(1,writes.get());
    }
}
