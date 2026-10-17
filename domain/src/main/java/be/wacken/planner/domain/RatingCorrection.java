package be.wacken.planner.domain;

import java.time.Instant;
import java.util.*;

/** Immutable before/after audit; original recorded time is distinct from correction time. */
public record RatingCorrection(String operationId,String userName,String festivalId,
                               String festivalName,Band band,ArchivedRatingKind kind,
                               int previousValue,int newValue,long expectedRevision,
                               Optional<Instant> originalRecordedAt,Instant correctedAt) {
    public RatingCorrection {
        try {operationId=UUID.fromString(operationId).toString();}
        catch(RuntimeException error){throw new DomainValidationException("A correction requires a valid operation identity.");}
        if(userName==null||userName.isBlank()||festivalId==null||festivalId.isBlank())
            throw new DomainValidationException("A correction requires its member and festival identity.");
        Objects.requireNonNull(band,"band");Objects.requireNonNull(kind,"kind");
        Objects.requireNonNull(correctedAt,"correctedAt");
        Rating.of(previousValue);Rating.of(newValue);
        if(kind==ArchivedRatingKind.AFTER&&newValue==0)
            throw new DomainValidationException("Archived after-ratings can be corrected to1–5; deleting history is not supported.");
        if(expectedRevision< -1)throw new DomainValidationException("A correction requires the reviewed revision.");
        festivalName=festivalName==null?"":festivalName;
        originalRecordedAt=originalRecordedAt==null?Optional.empty():originalRecordedAt;
    }
}
