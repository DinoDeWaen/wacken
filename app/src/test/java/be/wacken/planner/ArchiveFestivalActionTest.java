package be.wacken.planner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import be.wacken.planner.application.FestivalStartState;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalRepository;

public final class ArchiveFestivalActionTest {
    @Test
    public void archivesOnWorkerAndReportsSuccessOnUi() {
        QueuedExecutor worker = new QueuedExecutor();
        QueuedExecutor ui = new QueuedExecutor();
        FakeFestivals festivals = new FakeFestivals();
        List<FestivalStartState> successes = new ArrayList<>();
        List<String> failures = new ArrayList<>();
        ArchiveFestivalAction action = new ArchiveFestivalAction(worker, ui, () -> new be.wacken.planner.application.ArchiveActiveFestivalUseCase(festivals,f->festivals.save(f.archive())));

        action.execute(successes::add, failures::add);

        assertTrue(festivals.findAll().get(0).isActive());
        assertTrue(successes.isEmpty());
        worker.runNext();
        assertTrue(festivals.findAll().get(0).isArchived());
        assertTrue(successes.isEmpty());
        ui.runNext();
        assertFalse(successes.get(0).hasActiveFestival());
        assertTrue(failures.isEmpty());
    }

    @Test
    public void failedRemoteWriteLeavesFestivalActiveAndReportsErrorOnUi() {
        QueuedExecutor worker = new QueuedExecutor();
        QueuedExecutor ui = new QueuedExecutor();
        FakeFestivals festivals = new FakeFestivals();
        festivals.failSave = true;
        List<FestivalStartState> successes = new ArrayList<>();
        List<String> failures = new ArrayList<>();
        ArchiveFestivalAction action = new ArchiveFestivalAction(worker, ui, () -> new be.wacken.planner.application.ArchiveActiveFestivalUseCase(festivals,f->festivals.save(f.archive())));

        action.execute(successes::add, failures::add);
        worker.runNext();
        ui.runNext();

        assertTrue(festivals.findAll().get(0).isActive());
        assertTrue(successes.isEmpty());
        assertEquals(List.of("Supabase rejected archive"), failures);
    }

    private static final class QueuedExecutor implements Executor {
        private final List<Runnable> tasks = new ArrayList<>();

        @Override
        public void execute(Runnable task) {
            tasks.add(task);
        }

        void runNext() {
            tasks.remove(0).run();
        }
    }

    private static final class FakeFestivals implements FestivalRepository {
        private Festival festival = Festival.active("wacken-2026", "Wacken Open Air 2026");
        private boolean failSave;

        @Override
        public List<Festival> findAll() {
            return List.of(festival);
        }

        @Override
        public void save(Festival replacement) {
            if (failSave) {
                throw new SupabaseSyncException("Supabase rejected archive", null);
            }
            festival = replacement;
        }
    }
}
