package be.wacken.planner.persistence;

import be.wacken.planner.domain.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public final class RoomRatingCorrectionRepository implements RatingCorrectionRepository {
    private final RoomRatingCorrectionDao corrections;
    public RoomRatingCorrectionRepository(WackenDatabase database){corrections=database.ratingCorrections();}
    public void append(RatingCorrection audit){
        corrections.append(new RoomRatingCorrection(audit.operationId(),audit.userName(),audit.festivalId(),audit.festivalName(),
            audit.band().name(),audit.kind().name(),audit.previousValue(),audit.newValue(),audit.expectedRevision(),
            audit.originalRecordedAt().map(Instant::toString).orElse(null),audit.correctedAt().toString()));
    }
    @Override public List<RatingCorrection> findByUser(String user){
        return corrections.byUser(user).stream().map(this::toDomain).collect(Collectors.toList());
    }
    @Override public List<RatingCorrection> findByUserFestivalAndBand(String user,String festival,Band band){
        return corrections.byTarget(user,festival,band.name()).stream().map(this::toDomain).collect(Collectors.toList());
    }
    private RatingCorrection toDomain(RoomRatingCorrection row){
        return new RatingCorrection(row.operationId,row.userName,row.festivalId,row.festivalName,new Band(row.bandName),
            ArchivedRatingKind.valueOf(row.kind),row.previousValue,row.newValue,row.expectedRevision,
            Optional.ofNullable(row.originalRecordedAt).map(Instant::parse),Instant.parse(row.correctedAt));
    }
}
