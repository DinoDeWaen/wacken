package be.wacken.planner.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/** Reviewed owner-scoped value and optimistic revision; -1 means no stored target. */
public record ArchivedRatingReview(String userName,String festivalId,Band band,
                                   ArchivedRatingKind kind,int value,long revision,
                                   Optional<Instant> recordedAt) {
    public ArchivedRatingReview {
        if(userName==null||userName.isBlank()||festivalId==null||festivalId.isBlank())
            throw new DomainValidationException("A correction requires a signed-in member and archived festival.");
        Objects.requireNonNull(band,"band");Objects.requireNonNull(kind,"kind");
        Rating.of(value);
        if(revision< -1)throw new DomainValidationException("Reload this rating before correcting it.");
        recordedAt=recordedAt==null?Optional.empty():recordedAt;
    }
}
