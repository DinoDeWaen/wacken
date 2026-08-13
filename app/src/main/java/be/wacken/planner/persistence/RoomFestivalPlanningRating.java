package be.wacken.planner.persistence;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "festival_planning_ratings", primaryKeys = {"groupId", "userName", "festivalId", "bandName"})
public final class RoomFestivalPlanningRating {
    @NonNull
    public String groupId;
    @NonNull
    public String userName;
    @NonNull
    public String festivalId;
    @NonNull
    public String bandName;
    public int value;
    @NonNull
    public String syncStatus;

    public RoomFestivalPlanningRating(@NonNull String groupId, @NonNull String userName, @NonNull String festivalId, @NonNull String bandName, int value, @NonNull String syncStatus) {
        this.groupId = groupId;
        this.userName = userName;
        this.festivalId = festivalId;
        this.bandName = bandName;
        this.value = value;
        this.syncStatus = syncStatus;
    }
}
