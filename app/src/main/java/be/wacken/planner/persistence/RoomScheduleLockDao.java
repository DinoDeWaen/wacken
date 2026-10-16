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

    @Query("DELETE FROM schedule_locks WHERE groupId = :groupId AND festivalId = :festivalId AND conflictKey = :conflictKey")
    void delete(String groupId, String festivalId, String conflictKey);

    @Query("SELECT * FROM schedule_locks WHERE groupId = :groupId AND festivalId = :festivalId")
    List<RoomScheduleLock> findByGroup(String groupId, String festivalId);

    @Query("SELECT * FROM schedule_locks WHERE groupId = :groupId AND festivalId = :festivalId AND syncStatus = 'PENDING' AND operation = 'UPSERT'")
    List<RoomScheduleLock> findPendingSelections(String groupId, String festivalId);

    @Query("SELECT * FROM schedule_locks WHERE groupId = :groupId AND festivalId = :festivalId AND syncStatus = 'PENDING' AND operation = 'DELETE'")
    List<RoomScheduleLock> findPendingClears(String groupId, String festivalId);

    @Query("DELETE FROM schedule_locks WHERE groupId = :groupId AND festivalId = :festivalId AND syncStatus = 'SYNCED'")
    void deleteSyncedByGroup(String groupId, String festivalId);

    @Query("DELETE FROM schedule_locks WHERE festivalId = :festivalId")
    void deleteByFestival(String festivalId);

    @Query("SELECT COUNT(*) FROM schedule_locks WHERE groupId = :groupId AND syncStatus = 'PENDING'")
    int countPending(String groupId);
}
