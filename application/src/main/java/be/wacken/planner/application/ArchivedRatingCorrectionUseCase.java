package be.wacken.planner.application;

import be.wacken.planner.domain.*;
import java.util.*;

/** Own archived edits only; adapters enforce this same atomic revision/ownership boundary remotely. */
public final class ArchivedRatingCorrectionUseCase {
    private final FestivalRepository festivals;
    private final FestivalLineupRepository lineup;
    private final ArchivedRatingCorrectionGateway gateway;
    public ArchivedRatingCorrectionUseCase(FestivalRepository festivals,FestivalLineupRepository lineup,
                                           ArchivedRatingCorrectionGateway gateway) {
        this.festivals=Objects.requireNonNull(festivals);this.lineup=Objects.requireNonNull(lineup);
        this.gateway=Objects.requireNonNull(gateway);
    }
    public ArchivedRatingReview review(String user,String festival,Band band,ArchivedRatingKind kind) {
        validateTarget(user,festival,band);Objects.requireNonNull(kind);
        ArchivedRatingReview result=gateway.review(user,festival,band,kind);
        if(!user.equals(result.userName())||!festival.equals(result.festivalId())
                ||!band.name().equals(result.band().name())||kind!=result.kind())
            throw new DomainValidationException("The reviewed rating belongs to another target. Reload.");
        return result;
    }
    public RatingCorrection correct(String user,String operationId,ArchivedRatingReview expected,int value) {
        Objects.requireNonNull(expected);
        if(!Objects.equals(user,expected.userName()))
            throw new DomainValidationException("You can correct only your own archived rating.");
        validateTarget(user,expected.festivalId(),expected.band());
        Rating.of(value);
        if(expected.kind()==ArchivedRatingKind.AFTER&&value==0)
            throw new DomainValidationException("Choose1–5 for an archived after-rating; history cannot be deleted.");
        try {UUID.fromString(operationId);}
        catch(RuntimeException error){throw new DomainValidationException("A valid correction operation is required.");}
        return gateway.correct(operationId,expected,value);
    }
    private void validateTarget(String user,String festival,Band band) {
        if(user==null||user.isBlank())throw new DomainValidationException("Sign in before correcting an archived rating.");
        Objects.requireNonNull(band);
        boolean archived=festivals.findAll().stream().anyMatch(f->f.id().equals(festival)&&f.isArchived());
        if(!archived)throw new DomainValidationException("Choose an archived festival before correcting a rating.");
        if(lineup.findByFestival(festival).stream().noneMatch(entry->entry.band().name().equals(band.name())))
            throw new DomainValidationException("Choose a band in this archived festival.");
    }
}
