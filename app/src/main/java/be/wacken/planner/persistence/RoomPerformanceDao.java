package be.wacken.planner.persistence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RoomPerformanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(RoomPerformance performance);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<RoomPerformance> performances);

    @Query("DELETE FROM performances")
    void deleteAll();

    @Query("SELECT * FROM performances")
    List<RoomPerformance> findAll();

    @Query("SELECT COUNT(*) FROM performances")
    int count();
}
