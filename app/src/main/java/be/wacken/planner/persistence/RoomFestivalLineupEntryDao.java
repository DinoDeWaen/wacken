package be.wacken.planner.persistence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RoomFestivalLineupEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<RoomFestivalLineupEntry> entries);

    @Query("DELETE FROM festival_lineup_entries WHERE festivalId = :festivalId")
    void deleteByFestival(String festivalId);

    @Query("DELETE FROM festival_lineup_entries")
    void deleteAll();

    @Query("SELECT * FROM festival_lineup_entries WHERE festivalId = :festivalId ORDER BY uploadedDisplayName")
    List<RoomFestivalLineupEntry> findByFestival(String festivalId);

    @Query("SELECT * FROM festival_lineup_entries ORDER BY festivalId, uploadedDisplayName")
    List<RoomFestivalLineupEntry> findAll();
}
