package be.wacken.planner.persistence;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "festival_cache_marker")
public final class RoomFestivalCacheMarker {
    @PrimaryKey
    public int id;

    public RoomFestivalCacheMarker(int id) {
        this.id = id;
    }
}
