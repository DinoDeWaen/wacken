package be.wacken.planner.persistence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RoomStageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(RoomStage stage);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<RoomStage> stages);

    @Query("DELETE FROM stages")
    void deleteAll();

    @Query("SELECT * FROM stages WHERE name = :name")
    RoomStage findByName(String name);

    @Query("SELECT * FROM stages")
    List<RoomStage> findAll();

    @Query("SELECT COUNT(*) FROM stages")
    int count();
}
