package be.wacken.planner.persistence;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "festivals")
public final class RoomFestival {
    @PrimaryKey
    @NonNull
    public String id;
    @NonNull
    public String name;
    @NonNull
    public String status;

    public RoomFestival(@NonNull String id, @NonNull String name, @NonNull String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }
}
