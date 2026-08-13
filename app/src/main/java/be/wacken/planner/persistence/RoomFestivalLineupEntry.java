package be.wacken.planner.persistence;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "festival_lineup_entries", primaryKeys = {"festivalId", "bandName"})
public final class RoomFestivalLineupEntry {
    @NonNull
    public String festivalId;
    @NonNull
    public String bandName;
    @NonNull
    public String uploadedDisplayName;

    public RoomFestivalLineupEntry(@NonNull String festivalId, @NonNull String bandName, @NonNull String uploadedDisplayName) {
        this.festivalId = festivalId;
        this.bandName = bandName;
        this.uploadedDisplayName = uploadedDisplayName;
    }
}
