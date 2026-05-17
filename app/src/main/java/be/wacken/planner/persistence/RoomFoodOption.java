package be.wacken.planner.persistence;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_options")
public final class RoomFoodOption {
    @PrimaryKey
    @NonNull
    public String name;

    public RoomFoodOption(@NonNull String name) {
        this.name = name;
    }
}
