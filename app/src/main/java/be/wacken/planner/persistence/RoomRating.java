package be.wacken.planner.persistence;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "ratings", primaryKeys = {"userName", "bandName"})
public final class RoomRating {
    @NonNull
    public String userName;
    @NonNull
    public String bandName;
    public int value;
    @NonNull
    public String groupId;
    @NonNull
    public String syncStatus;

    @Ignore
    public RoomRating(@NonNull String userName, @NonNull String bandName, int value) {
        this(userName, bandName, value, "local", "SYNCED");
    }

    public RoomRating(@NonNull String userName, @NonNull String bandName, int value, @NonNull String groupId, @NonNull String syncStatus) {
        this.userName = userName;
        this.bandName = bandName;
        this.value = value;
        this.groupId = groupId;
        this.syncStatus = syncStatus;
    }
}
