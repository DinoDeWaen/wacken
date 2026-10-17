package be.wacken.planner.persistence;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "festival_after_ratings", primaryKeys = {"userName", "festivalId", "bandName"})
public final class RoomFestivalAfterRating {
    @NonNull public String userName;
    @NonNull public String festivalId;
    @NonNull public String bandName;
    @NonNull public String operationId;
    public int value;
    @NonNull public String recordedAt;
    @NonNull public String syncStatus;
    public boolean requiresReview;

    public RoomFestivalAfterRating(@NonNull String userName, @NonNull String festivalId, @NonNull String bandName,
                                   @NonNull String operationId, int value, @NonNull String recordedAt,
                                   @NonNull String syncStatus, boolean requiresReview) {
        this.userName=userName;this.festivalId=festivalId;this.bandName=bandName;this.operationId=operationId;
        this.value=value;this.recordedAt=recordedAt;this.syncStatus=syncStatus;this.requiresReview=requiresReview;
    }
}

