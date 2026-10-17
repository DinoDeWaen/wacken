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
    private final Supplier<ArchiveActiveFestivalUseCase> archive;

    ArchiveFestivalAction(Executor worker, Executor ui, Supplier<ArchiveActiveFestivalUseCase> archive) {
        this.worker = Objects.requireNonNull(worker);
        this.ui = Objects.requireNonNull(ui);
        this.archive = Objects.requireNonNull(archive);
    }

    void execute(Consumer<FestivalStartState> onSuccess, Consumer<String> onFailure) {
        worker.execute(() -> {
            try {
                FestivalStartState state = archive.get().archiveActiveFestival();
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
