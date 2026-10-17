package be.wacken.planner;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import be.wacken.planner.persistence.*;

/** Publishes the complete creation snapshot in one Room transaction. */
final class RoomFestivalCreationCache implements Consumer<SupabaseFestivalCreationGateway.Snapshot> {
    private final WackenDatabase database;

    RoomFestivalCreationCache(WackenDatabase database) {
        this.database = database;
    }

    @Override
    public void accept(SupabaseFestivalCreationGateway.Snapshot snapshot) {
        database.runInTransaction(() -> {
            new RoomFestivalRepository(database).replaceAll(snapshot.festivals());
            RoomBandRepository bands = new RoomBandRepository(database);
            snapshot.bands().forEach(bands::save);
            new RoomFestivalLineupRepository(database).saveAllForFestival(snapshot.festival().id(),snapshot.lineup());
            RoomFestivalPlanningRatingRepository ratings = new RoomFestivalPlanningRatingRepository(database);
            Set<String> pending = ratings.findPending(snapshot.groupId(),snapshot.userName()).stream()
                    .filter(row -> row.festivalId().equals(snapshot.festival().id()))
                    .map(row -> row.band().name()).collect(Collectors.toSet());
            snapshot.ratings().stream().filter(row -> !pending.contains(row.band().name())).forEach(ratings::saveSynced);
            if(snapshot.aliases()!=null) new RoomBandAliasRepository(database).replaceAll(snapshot.aliases());
        });
    }
}
