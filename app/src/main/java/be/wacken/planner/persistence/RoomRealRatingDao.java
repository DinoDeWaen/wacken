package be.wacken.planner.persistence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RoomRealRatingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(RoomRealRating rating);

    @Query("SELECT * FROM real_ratings WHERE userName = :userName AND bandName = :bandName")
    RoomRealRating findByUserAndBand(String userName, String bandName);

    @Query("SELECT * FROM real_ratings")
    List<RoomRealRating> findAll();
}
