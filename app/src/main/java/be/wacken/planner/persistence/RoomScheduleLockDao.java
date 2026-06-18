package be.wacken.planner.persistence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RoomScheduleLockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(RoomScheduleLock lock);

    @Query("DELETE FROM schedule_locks WHERE groupId = :groupId AND conflictKey = :conflictKey")
    void delete(String groupId, String conflictKey);

    @Query("SELECT * FROM schedule_locks WHERE groupId = :groupId")
    List<RoomScheduleLock> findByGroup(String groupId);

    @Query("SELECT * FROM schedule_locks WHERE groupId = :groupId AND syncStatus = 'PENDING' AND operation = 'UPSERT'")
    List<RoomScheduleLock> findPendingSelections(String groupId);

    @Query("SELECT * FROM schedule_locks WHERE groupId = :groupId AND syncStatus = 'PENDING' AND operation = 'DELETE'")
    List<RoomScheduleLock> findPendingClears(String groupId);

    @Query("DELETE FROM schedule_locks WHERE groupId = :groupId AND syncStatus = 'SYNCED'")
    void deleteSyncedByGroup(String groupId);
}
