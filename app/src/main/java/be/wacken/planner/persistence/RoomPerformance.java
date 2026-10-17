package be.wacken.planner.persistence;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "performances", primaryKeys = {"festivalId", "bandName", "start"})
public final class RoomPerformance {
    @NonNull
    public String festivalId;
    @NonNull
    public String bandName;
    @NonNull
    public String stageName;
    @NonNull
    public String start;
    @NonNull
    public String end;

    public RoomPerformance(@NonNull String festivalId, @NonNull String bandName, @NonNull String stageName,
                           @NonNull String start, @NonNull String end) {
        this.festivalId = festivalId;
        this.bandName = bandName;
        this.stageName = stageName;
        this.start = start;
        this.end = end;
    }
}
