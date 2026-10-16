package be.wacken.planner;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

import be.wacken.planner.application.DeleteFestivalUseCase;
import be.wacken.planner.domain.Festival;

final class SupabaseFestivalDeletionGateway implements DeleteFestivalUseCase.Gateway {
    private final SupabaseMasterDataClient remote;
    private final Consumer<Festival> local;

    SupabaseFestivalDeletionGateway(SupabaseMasterDataClient remote, Consumer<Festival> local) {
        this.remote = Objects.requireNonNull(remote);
        this.local = Objects.requireNonNull(local);
    }

    @Override
    public void delete(Festival festival) {
        try {
            remote.deleteFestival(festival.id());
        } catch (IOException error) {
            throw new SupabaseSyncException(error.getMessage(), error);
        }
        try {
            local.accept(festival);
        } catch (RuntimeException error) {
            throw new IllegalStateException("Festival deleted in Supabase, but local cleanup failed. Sync again to refresh this device.", error);
        }
    }
}
