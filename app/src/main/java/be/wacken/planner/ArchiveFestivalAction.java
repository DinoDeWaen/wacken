package be.wacken.planner;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

import be.wacken.planner.application.ArchiveActiveFestivalUseCase;
import be.wacken.planner.application.FestivalStartState;
import be.wacken.planner.domain.FestivalRepository;

final class ArchiveFestivalAction {
    private final Executor worker;
    private final Executor ui;
    private final Supplier<FestivalRepository> festivals;

    ArchiveFestivalAction(Executor worker, Executor ui, Supplier<FestivalRepository> festivals) {
        this.worker = Objects.requireNonNull(worker);
        this.ui = Objects.requireNonNull(ui);
        this.festivals = Objects.requireNonNull(festivals);
    }

    void execute(Consumer<FestivalStartState> onSuccess, Consumer<String> onFailure) {
        worker.execute(() -> {
            try {
                FestivalStartState state = new ArchiveActiveFestivalUseCase(festivals.get()).archiveActiveFestival();
                ui.execute(() -> onSuccess.accept(state));
            } catch (RuntimeException error) {
                String message = error.getMessage() == null || error.getMessage().isBlank()
                        ? "Archive could not be completed."
                        : error.getMessage();
                ui.execute(() -> onFailure.accept(message));
            }
        });
    }
}
