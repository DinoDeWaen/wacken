package be.wacken.planner;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public final class SyncingScheduleLockStoreTest {
    private static final AuthSession SESSION = new AuthSession(
            "access",
            "refresh",
            "user-1",
            "dino@example.com",
            9_999_999_999L,
            "group-1",
            "member"
    );

    @Test
    public void savesSelectionsLocallyAsPendingWhenOffline() {
        FakeLocalLocks local = new FakeLocalLocks();
        FakeRemoteLocks remote = new FakeRemoteLocks();
        SyncingScheduleLockStore store = new SyncingScheduleLockStore(local, remote, SESSION);

        store.saveGroupLock("conflict-a", "candidate-b");

        assertEquals(Map.of("conflict-a", "candidate-b"), local.findActiveLocks("group-1"));
        assertEquals(Map.of("conflict-a", "candidate-b"), local.findPendingSelections("group-1"));
        assertEquals(Map.of(), remote.locks);
    }

    @Test
    public void returnsLocalLocksWhenRemotePullFails() throws Exception {
        FakeLocalLocks local = new FakeLocalLocks();
        local.savePendingSelection("group-1", "conflict-a", "candidate-b");
        FakeRemoteLocks remote = new FakeRemoteLocks();
        remote.failPull = true;
        SyncingScheduleLockStore store = new SyncingScheduleLockStore(local, remote, SESSION);

        Map<String, String> locks = store.pullGroupLocks();

        assertEquals(Map.of("conflict-a", "candidate-b"), locks);
        assertEquals(Map.of(), local.findPendingSelections("group-1"));
    }

    @Test
    public void pushesPendingSelectionsAndMarksThemSyncedWhenRemoteWorks() throws Exception {
        FakeLocalLocks local = new FakeLocalLocks();
        local.savePendingSelection("group-1", "conflict-a", "candidate-b");
        FakeRemoteLocks remote = new FakeRemoteLocks();
        SyncingScheduleLockStore store = new SyncingScheduleLockStore(local, remote, SESSION);

        Map<String, String> locks = store.pullGroupLocks();

        assertEquals(Map.of("conflict-a", "candidate-b"), remote.locks);
        assertEquals(Map.of("conflict-a", "candidate-b"), locks);
        assertEquals(Map.of(), local.findPendingSelections("group-1"));
    }

    @Test
    public void keepsPendingLocalSelectionAheadOfOlderRemoteSelection() throws Exception {
        FakeLocalLocks local = new FakeLocalLocks();
        local.savePendingSelection("group-1", "conflict-a", "local-candidate");
        FakeRemoteLocks remote = new FakeRemoteLocks();
        remote.locks.put("conflict-a", "remote-candidate");
        remote.failSaves = true;
        SyncingScheduleLockStore store = new SyncingScheduleLockStore(local, remote, SESSION);

        Map<String, String> locks = store.pullGroupLocks();

        assertEquals(Map.of("conflict-a", "local-candidate"), locks);
        assertEquals(Map.of("conflict-a", "local-candidate"), local.findPendingSelections("group-1"));
    }

    @Test
    public void queuesClearsAndRemovesSyncedLockAfterSuccessfulRemoteClear() throws Exception {
        FakeLocalLocks local = new FakeLocalLocks();
        local.saveSyncedSelection("group-1", "conflict-a", "candidate-b");
        FakeRemoteLocks remote = new FakeRemoteLocks();
        remote.locks.put("conflict-a", "candidate-b");
        SyncingScheduleLockStore store = new SyncingScheduleLockStore(local, remote, SESSION);

        store.clearGroupLock("conflict-a");
        assertEquals(Map.of(), local.findActiveLocks("group-1"));

        Map<String, String> locks = store.pullGroupLocks();

        assertEquals(Map.of(), remote.locks);
        assertEquals(Map.of(), locks);
        assertEquals(List.of(), local.findPendingClears("group-1"));
    }

    private static final class FakeRemoteLocks implements ScheduleLockStore {
        private final Map<String, String> locks = new LinkedHashMap<>();
        private boolean failPull;
        private boolean failSaves;

        @Override
        public Map<String, String> pullGroupLocks() throws IOException {
            if (failPull) {
                throw new IOException("offline");
            }
            return new LinkedHashMap<>(locks);
        }

        @Override
        public void saveGroupLock(String conflictKey, String selectedCandidateKey) throws IOException {
            if (failSaves) {
                throw new IOException("offline");
            }
            locks.put(conflictKey, selectedCandidateKey);
        }

        @Override
        public void clearGroupLock(String conflictKey) {
            locks.remove(conflictKey);
        }
    }

    private static final class FakeLocalLocks implements ScheduleLockLocalStore {
        private final Map<String, Row> rows = new LinkedHashMap<>();

        @Override
        public void savePendingSelection(String groupId, String conflictKey, String selectedCandidateKey) {
            rows.put(key(groupId, conflictKey), new Row(groupId, conflictKey, selectedCandidateKey, "PENDING", "UPSERT"));
        }

        @Override
        public void savePendingClear(String groupId, String conflictKey) {
            rows.put(key(groupId, conflictKey), new Row(groupId, conflictKey, "", "PENDING", "DELETE"));
        }

        @Override
        public void saveSyncedSelection(String groupId, String conflictKey, String selectedCandidateKey) {
            rows.put(key(groupId, conflictKey), new Row(groupId, conflictKey, selectedCandidateKey, "SYNCED", "UPSERT"));
        }

        @Override
        public void removeSyncedSelection(String groupId, String conflictKey) {
            rows.remove(key(groupId, conflictKey));
        }

        @Override
        public Map<String, String> findActiveLocks(String groupId) {
            Map<String, String> locks = new LinkedHashMap<>();
            for (Row row : rows.values()) {
                if (row.groupId.equals(groupId) && !"DELETE".equals(row.operation)) {
                    locks.put(row.conflictKey, row.selectedCandidateKey);
                }
            }
            return locks;
        }

        @Override
        public Map<String, String> findPendingSelections(String groupId) {
            Map<String, String> locks = new LinkedHashMap<>();
            for (Row row : rows.values()) {
                if (row.groupId.equals(groupId) && "PENDING".equals(row.syncStatus) && "UPSERT".equals(row.operation)) {
                    locks.put(row.conflictKey, row.selectedCandidateKey);
                }
            }
            return locks;
        }

        @Override
        public List<String> findPendingClears(String groupId) {
            List<String> clears = new ArrayList<>();
            for (Row row : rows.values()) {
                if (row.groupId.equals(groupId) && "PENDING".equals(row.syncStatus) && "DELETE".equals(row.operation)) {
                    clears.add(row.conflictKey);
                }
            }
            return clears;
        }

        @Override
        public void replaceSyncedGroupLocks(String groupId, Map<String, String> remoteLocks) {
            rows.values().removeIf(row -> row.groupId.equals(groupId) && "SYNCED".equals(row.syncStatus));
            Map<String, String> pendingSelections = findPendingSelections(groupId);
            List<String> pendingClears = findPendingClears(groupId);
            for (Map.Entry<String, String> lock : remoteLocks.entrySet()) {
                if (!pendingSelections.containsKey(lock.getKey()) && !pendingClears.contains(lock.getKey())) {
                    saveSyncedSelection(groupId, lock.getKey(), lock.getValue());
                }
            }
        }

        private String key(String groupId, String conflictKey) {
            return groupId + "|" + conflictKey;
        }

        private record Row(String groupId, String conflictKey, String selectedCandidateKey, String syncStatus, String operation) {
        }
    }
}
