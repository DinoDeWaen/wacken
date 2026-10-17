package be.wacken.planner.domain;

import java.util.List;

/** Append-only correction facts available for offline detail/export. */
public interface RatingCorrectionRepository {
    List<RatingCorrection> findByUser(String user);
    List<RatingCorrection> findByUserFestivalAndBand(String user,String festival,Band band);
}
