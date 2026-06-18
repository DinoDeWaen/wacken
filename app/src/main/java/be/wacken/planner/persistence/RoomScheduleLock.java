package be.wacken.planner.persistence;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "schedule_locks", primaryKeys = {"groupId", "conflictKey"})
public final class RoomScheduleLock {
    @NonNull
    public String groupId;
    @NonNull
    public String conflictKey;
    @NonNull
    public String selectedCandidateKey;
    @NonNull
    public String syncStatus;
    @NonNull
    public String operation;

    public RoomScheduleLock(
            @NonNull String groupId,
            @NonNull String conflictKey,
            @NonNull String selectedCandidateKey,
            @NonNull String syncStatus,
            @NonNull String operation
    ) {
        this.groupId = groupId;
        this.conflictKey = conflictKey;
        this.selectedCandidateKey = selectedCandidateKey;
        this.syncStatus = syncStatus;
        this.operation = operation;
    }
}
