package be.wacken.planner.domain;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class ReviewedFestivalBandTest {
    private final CanonicalBand band=new CanonicalBand("stable-id",new Band("Abbie Falls"));
    @Test void aliasRetainsIdentitySpellingActorAndTime() {
        var alias=new ConfirmedBandAlias("  AF Band  ",band,"member",Instant.EPOCH);
        assertEquals("AF Band",alias.name()); assertEquals(band,alias.canonical());
        assertEquals("member",alias.confirmedBy()); assertEquals(Instant.EPOCH,alias.confirmedAt());
        assertEquals("af band",ConfirmedBandAlias.key(" AF\t Band\n"));
        assertEquals("æon!",ConfirmedBandAlias.key("æon!"));
    }
    @Test void invalidIdentityOrUnattributedAliasIsRejected() {
        assertThrows(DomainValidationException.class,()->new CanonicalBand(" ",new Band("Band")));
        assertThrows(NullPointerException.class,()->new CanonicalBand("id",null));
        assertThrows(DomainValidationException.class,()->new ConfirmedBandAlias("alias",band," ",Instant.EPOCH));
        assertThrows(NullPointerException.class,()->new ConfirmedBandAlias("alias",band,"user",null));
        assertThrows(NullPointerException.class,()->new ConfirmedBandAlias("alias",null,"user",Instant.EPOCH));
    }
    @Test void explicitNoMatchCannotRememberAnUnselectedIdentity() {
        var row=new ReviewedFestivalBand("New",Optional.empty(),false);
        assertTrue(row.selected().isEmpty());assertFalse(row.rememberAlias());
        assertThrows(DomainValidationException.class,()->new ReviewedFestivalBand("New",Optional.empty(),true));
        assertThrows(DomainValidationException.class,()->new ReviewedFestivalBand(" ",Optional.of(band),false));
        assertThrows(NullPointerException.class,()->new ReviewedFestivalBand("New",null,false));
    }
}
