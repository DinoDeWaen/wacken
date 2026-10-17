package be.wacken.planner.persistence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RoomBandDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(RoomBand band);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<RoomBand> bands);

    @Query("DELETE FROM bands")
    void deleteAll();

    @Query("SELECT * FROM bands WHERE name = :name")
    RoomBand findByName(String name);

    @Query("SELECT * FROM bands")
    List<RoomBand> findAll();

    @Query("SELECT COUNT(*) FROM bands")
    int count();
}
