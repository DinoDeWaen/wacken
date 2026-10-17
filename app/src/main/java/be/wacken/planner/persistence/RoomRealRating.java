package be.wacken.planner.persistence;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "real_ratings", primaryKeys = {"userName", "bandName"})
public final class RoomRealRating {
    @NonNull
    public String userName;
    @NonNull
    public String bandName;
    public int value;

    public RoomRealRating(@NonNull String userName, @NonNull String bandName, int value) {
        this.userName = userName;
        this.bandName = bandName;
        this.value = value;
    }
}
