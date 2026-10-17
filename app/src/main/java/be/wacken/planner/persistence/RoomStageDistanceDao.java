package be.wacken.planner.persistence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RoomStageDistanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(RoomStageDistance distance);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<RoomStageDistance> distances);

    @Query("DELETE FROM stage_distances")
    void deleteAll();

    @Query("SELECT * FROM stage_distances WHERE fromStageName = :fromStageName AND toStageName = :toStageName")
    RoomStageDistance findBetween(String fromStageName, String toStageName);

    @Query("SELECT * FROM stage_distances")
    List<RoomStageDistance> findAll();

    @Query("SELECT COUNT(*) FROM stage_distances")
    int count();
}
