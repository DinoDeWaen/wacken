package be.wacken.planner.persistence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RoomFoodOptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(RoomFoodOption foodOption);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<RoomFoodOption> foodOptions);

    @Query("DELETE FROM food_options")
    void deleteAll();

    @Query("SELECT * FROM food_options")
    List<RoomFoodOption> findAll();

    @Query("SELECT COUNT(*) FROM food_options")
    int count();
}
