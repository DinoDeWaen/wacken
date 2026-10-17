package be.wacken.planner.persistence;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "personal_band_ratings", primaryKeys = {"userName", "bandName"})
public final class RoomPersonalBandRating {
    @NonNull public String userName;
    @NonNull public String bandName;
    public int value;
    @NonNull public String sourceFestivalId;
    @NonNull public String sourceFestivalName;
    @NonNull public String sourceEventId;
    @NonNull public String establishedAt;
    public RoomPersonalBandRating(@NonNull String userName, @NonNull String bandName, int value,
                                  @NonNull String sourceFestivalId, @NonNull String sourceFestivalName,
                                  @NonNull String sourceEventId, @NonNull String establishedAt) {
        this.userName=userName;this.bandName=bandName;this.value=value;this.sourceFestivalId=sourceFestivalId;
        this.sourceFestivalName=sourceFestivalName;this.sourceEventId=sourceEventId;this.establishedAt=establishedAt;
    }
}

