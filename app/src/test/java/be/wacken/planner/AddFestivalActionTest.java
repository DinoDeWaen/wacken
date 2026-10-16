package be.wacken.planner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import be.wacken.planner.application.AddFestivalResult;

public final class AddFestivalActionTest {
    @Test
    public void savesOnWorkerAndReportsResultOnUi() {
        QueuedExecutor worker = new QueuedExecutor();
        QueuedExecutor ui = new QueuedExecutor();
        List<String> events = new ArrayList<>();
        AddFestivalAction action = new AddFestivalAction(worker, ui);

        action.execute(() -> {
            events.add("save");
            return AddFestivalResult.added(1, 2, 0);
        }, result -> events.add("success"), message -> events.add("failure"));

        assertTrue(events.isEmpty());
        worker.runNext();
        assertEquals(List.of("save"), events);
        ui.runNext();
        assertEquals(List.of("save", "success"), events);
    }

    @Test
    public void failureReturnsUsefulMessageOnUi() {
        QueuedExecutor worker = new QueuedExecutor();
        QueuedExecutor ui = new QueuedExecutor();
        List<String> failures = new ArrayList<>();
        AddFestivalAction action = new AddFestivalAction(worker, ui);

        action.execute(() -> {
            throw new RuntimeException();
        }, result -> { throw new AssertionError("unexpected success"); }, failures::add);

        worker.runNext();
        assertTrue(failures.isEmpty());
        ui.runNext();
        assertEquals(List.of("Festival could not be added. Please try again."), failures);
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
}
