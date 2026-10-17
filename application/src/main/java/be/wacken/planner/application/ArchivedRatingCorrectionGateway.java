package be.wacken.planner.application;

import be.wacken.planner.domain.*;
import java.util.List;

/** Authorized review and remote-first atomic correction/audit with acknowledged cache publication. */
public interface ArchivedRatingCorrectionGateway {
    ArchivedRatingReview review(String user,String festival,Band band,ArchivedRatingKind kind);
    RatingCorrection correct(String operationId,ArchivedRatingReview expected,int newValue);
    List<RatingCorrection> history(String user,String festival,Band band);
}
