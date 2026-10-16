package be.wacken.planner;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

import be.wacken.planner.application.AddFestivalResult;

final class AddFestivalAction {
    private final Executor worker;
    private final Executor ui;

    AddFestivalAction(Executor worker, Executor ui) {
        this.worker = Objects.requireNonNull(worker);
        this.ui = Objects.requireNonNull(ui);
    }

    void execute(Supplier<AddFestivalResult> addFestival,
                 Consumer<AddFestivalResult> onResult,
                 Consumer<String> onFailure) {
        worker.execute(() -> {
            try {
                AddFestivalResult result = addFestival.get();
                ui.execute(() -> onResult.accept(result));
            } catch (RuntimeException error) {
                String message = error.getMessage() == null || error.getMessage().isBlank()
                        ? "Festival could not be added. Please try again."
                        : error.getMessage();
                ui.execute(() -> onFailure.accept(message));
            }
        });
    }
}
