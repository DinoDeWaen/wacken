package be.wacken.planner.persistence;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "personal_band_rating_events")
public final class RoomPersonalBandRatingEvent {
    @PrimaryKey
    @NonNull
    public String id;
    @NonNull
    public String userName;
    @NonNull
    public String bandName;
    public String festivalId;
    public int value;
    @NonNull
    public String createdAt;
    @NonNull
    public String syncStatus;

    public RoomPersonalBandRatingEvent(@NonNull String id, @NonNull String userName, @NonNull String bandName, String festivalId, int value, @NonNull String createdAt, @NonNull String syncStatus) {
        this.id = id;
        this.userName = userName;
        this.bandName = bandName;
        this.festivalId = festivalId;
        this.value = value;
        this.createdAt = createdAt;
        this.syncStatus = syncStatus;
    }
}
