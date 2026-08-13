package be.wacken.planner.persistence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RoomPersonalBandRatingEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(RoomPersonalBandRatingEvent event);

    @Query("SELECT * FROM personal_band_rating_events WHERE userName = :userName AND bandName = :bandName ORDER BY createdAt DESC")
    List<RoomPersonalBandRatingEvent> findByUserAndBand(String userName, String bandName);

    @Query("SELECT * FROM personal_band_rating_events WHERE userName = :userName AND syncStatus = 'PENDING'")
    List<RoomPersonalBandRatingEvent> findPendingByUser(String userName);
}
