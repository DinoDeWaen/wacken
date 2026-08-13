package be.wacken.planner.persistence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RoomFestivalPlanningRatingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(RoomFestivalPlanningRating rating);

    @Query("SELECT * FROM festival_planning_ratings WHERE userName = :userName AND festivalId = :festivalId AND bandName = :bandName")
    RoomFestivalPlanningRating findByUserFestivalAndBand(String userName, String festivalId, String bandName);

    @Query("SELECT * FROM festival_planning_ratings WHERE festivalId = :festivalId")
    List<RoomFestivalPlanningRating> findByFestival(String festivalId);

    @Query("SELECT * FROM festival_planning_ratings")
    List<RoomFestivalPlanningRating> findAll();

    @Query("SELECT * FROM festival_planning_ratings WHERE groupId = :groupId AND userName = :userName AND syncStatus = 'PENDING'")
    List<RoomFestivalPlanningRating> findPending(String groupId, String userName);
}
