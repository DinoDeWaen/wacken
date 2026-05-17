package be.wacken.planner.persistence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RoomRatingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(RoomRating rating);

    @Query("SELECT * FROM ratings WHERE userName = :userName AND bandName = :bandName")
    RoomRating findByUserAndBand(String userName, String bandName);

    @Query("SELECT * FROM ratings")
    List<RoomRating> findAll();

    @Query("SELECT * FROM ratings WHERE groupId = :groupId AND userName = :userName AND syncStatus = 'PENDING'")
    List<RoomRating> findPending(String groupId, String userName);

    @Query("SELECT COUNT(*) FROM ratings")
    int count();
}
