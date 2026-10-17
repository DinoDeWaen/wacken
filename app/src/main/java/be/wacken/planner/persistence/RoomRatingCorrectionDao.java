package be.wacken.planner.persistence;

import androidx.room.*;
import java.util.List;

@Dao
public interface RoomRatingCorrectionDao {
    @Insert(onConflict=OnConflictStrategy.IGNORE) void append(RoomRatingCorrection correction);
    @Query("SELECT * FROM rating_corrections WHERE userName=:user ORDER BY correctedAt,operationId")
    List<RoomRatingCorrection> byUser(String user);
    @Query("SELECT * FROM rating_corrections WHERE userName=:user AND festivalId=:festival AND bandName=:band ORDER BY correctedAt,operationId")
    List<RoomRatingCorrection> byTarget(String user,String festival,String band);
}
