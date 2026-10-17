package be.wacken.planner.persistence;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "stage_distances", primaryKeys = {"fromStageName", "toStageName"})
public final class RoomStageDistance {
    @NonNull
    public String fromStageName;
    @NonNull
    public String toStageName;
    public int walkingMinutes;

    public RoomStageDistance(@NonNull String fromStageName, @NonNull String toStageName, int walkingMinutes) {
        this.fromStageName = fromStageName;
        this.toStageName = toStageName;
        this.walkingMinutes = walkingMinutes;
    }
}
