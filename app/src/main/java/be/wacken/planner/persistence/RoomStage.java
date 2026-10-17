package be.wacken.planner.persistence;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "stages")
public final class RoomStage {
    @PrimaryKey
    @NonNull
    public String name;

    public RoomStage(@NonNull String name) {
        this.name = name;
    }
}
