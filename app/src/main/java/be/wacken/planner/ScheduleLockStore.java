package be.wacken.planner;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

interface ScheduleLockStore {
    Map<String, String> pullGroupLocks() throws IOException;

    void saveGroupLock(String conflictKey, String selectedCandidateKey) throws IOException;

    void clearGroupLock(String conflictKey) throws IOException;

    final class NoOp implements ScheduleLockStore {
        @Override
        public Map<String, String> pullGroupLocks() {
            return Collections.emptyMap();
        }

        @Override
        public void saveGroupLock(String conflictKey, String selectedCandidateKey) {
        }

        @Override
        public void clearGroupLock(String conflictKey) {
        }
    }
}
