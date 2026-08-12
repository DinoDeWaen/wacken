package be.wacken.planner.persistence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RoomFestivalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(RoomFestival festival);

    @Query("SELECT * FROM festivals ORDER BY CASE status WHEN 'ACTIVE' THEN 0 ELSE 1 END, name")
    List<RoomFestival> findAll();

    @Query("SELECT COUNT(*) FROM festivals")
    int count();
}
