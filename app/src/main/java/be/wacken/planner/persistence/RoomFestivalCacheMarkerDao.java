package be.wacken.planner.persistence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface RoomFestivalCacheMarkerDao {
    @Query("SELECT COUNT(*) FROM festival_cache_marker")
    int count();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void markInitialized(RoomFestivalCacheMarker marker);
}
