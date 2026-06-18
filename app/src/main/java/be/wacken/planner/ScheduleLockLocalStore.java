package be.wacken.planner;

import java.util.Map;

public interface ScheduleLockLocalStore {
    void savePendingSelection(String groupId, String conflictKey, String selectedCandidateKey);

    void savePendingClear(String groupId, String conflictKey);

    void saveSyncedSelection(String groupId, String conflictKey, String selectedCandidateKey);

    void removeSyncedSelection(String groupId, String conflictKey);

    Map<String, String> findActiveLocks(String groupId);

    Map<String, String> findPendingSelections(String groupId);

    java.util.List<String> findPendingClears(String groupId);

    void replaceSyncedGroupLocks(String groupId, Map<String, String> remoteLocks);
}
