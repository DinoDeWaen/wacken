package be.wacken.planner;

import java.io.IOException;
import java.util.Map;

final class SyncingScheduleLockStore implements ScheduleLockStore {
    private final ScheduleLockLocalStore local;
    private final ScheduleLockStore remote;
    private final AuthSession session;

    SyncingScheduleLockStore(ScheduleLockLocalStore local, ScheduleLockStore remote, AuthSession session) {
        this.local = local;
        this.remote = remote;
        this.session = session;
    }

    @Override
    public Map<String, String> pullGroupLocks() throws IOException {
        try {
            syncPendingLocks();
            Map<String, String> remoteLocks = remote.pullGroupLocks();
            local.replaceSyncedGroupLocks(session.groupId(), remoteLocks);
        } catch (IOException error) {
            SupabaseDiagnostics.warn("schedule_lock_sync", "remote_unavailable_using_local", "group_id=" + session.groupId(), error);
        }
        return local.findActiveLocks(session.groupId());
    }

    @Override
    public void saveGroupLock(String conflictKey, String selectedCandidateKey) {
        local.savePendingSelection(session.groupId(), conflictKey, selectedCandidateKey);
    }

    @Override
    public void clearGroupLock(String conflictKey) {
        local.savePendingClear(session.groupId(), conflictKey);
    }

    void syncPendingLocks() throws IOException {
        for (Map.Entry<String, String> selection : local.findPendingSelections(session.groupId()).entrySet()) {
            remote.saveGroupLock(selection.getKey(), selection.getValue());
            local.saveSyncedSelection(session.groupId(), selection.getKey(), selection.getValue());
        }
        for (String conflictKey : local.findPendingClears(session.groupId())) {
            remote.clearGroupLock(conflictKey);
            local.removeSyncedSelection(session.groupId(), conflictKey);
        }
    }
}
