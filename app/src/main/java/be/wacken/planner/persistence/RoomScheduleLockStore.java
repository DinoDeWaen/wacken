package be.wacken.planner.persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import be.wacken.planner.ScheduleLockLocalStore;

public final class RoomScheduleLockStore implements ScheduleLockLocalStore {
    private static final String SYNCED = "SYNCED";
    private static final String PENDING = "PENDING";
    private static final String UPSERT = "UPSERT";
    private static final String DELETE = "DELETE";

    private final RoomScheduleLockDao locks;

    public RoomScheduleLockStore(WackenDatabase database) {
        this.locks = database.scheduleLocks();
    }

    @Override
    public void savePendingSelection(String groupId, String conflictKey, String selectedCandidateKey) {
        locks.save(new RoomScheduleLock(groupId, conflictKey, selectedCandidateKey, PENDING, UPSERT));
    }

    @Override
    public void savePendingClear(String groupId, String conflictKey) {
        locks.save(new RoomScheduleLock(groupId, conflictKey, "", PENDING, DELETE));
    }

    @Override
    public void saveSyncedSelection(String groupId, String conflictKey, String selectedCandidateKey) {
        locks.save(new RoomScheduleLock(groupId, conflictKey, selectedCandidateKey, SYNCED, UPSERT));
    }

    @Override
    public void removeSyncedSelection(String groupId, String conflictKey) {
        locks.delete(groupId, conflictKey);
    }

    @Override
    public Map<String, String> findActiveLocks(String groupId) {
        Map<String, String> active = new LinkedHashMap<>();
        for (RoomScheduleLock lock : locks.findByGroup(groupId)) {
            if (!DELETE.equals(lock.operation)) {
                active.put(lock.conflictKey, lock.selectedCandidateKey);
            }
        }
        return active;
    }

    @Override
    public Map<String, String> findPendingSelections(String groupId) {
        Map<String, String> pending = new LinkedHashMap<>();
        for (RoomScheduleLock lock : locks.findPendingSelections(groupId)) {
            pending.put(lock.conflictKey, lock.selectedCandidateKey);
        }
        return pending;
    }

    @Override
    public List<String> findPendingClears(String groupId) {
        List<String> pending = new ArrayList<>();
        for (RoomScheduleLock lock : locks.findPendingClears(groupId)) {
            pending.add(lock.conflictKey);
        }
        return pending;
    }

    @Override
    public void replaceSyncedGroupLocks(String groupId, Map<String, String> remoteLocks) {
        Map<String, String> pendingSelections = findPendingSelections(groupId);
        List<String> pendingClears = findPendingClears(groupId);
        locks.deleteSyncedByGroup(groupId);
        for (Map.Entry<String, String> lock : remoteLocks.entrySet()) {
            if (!pendingSelections.containsKey(lock.getKey()) && !pendingClears.contains(lock.getKey())) {
                saveSyncedSelection(groupId, lock.getKey(), lock.getValue());
            }
        }
    }
}
