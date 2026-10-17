package be.wacken.planner;

import be.wacken.planner.persistence.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** Publishes archive acknowledgement and personal ratings in the same Room transaction. */
final class RoomFestivalArchiveCache implements Consumer<SupabaseFestivalArchiveGateway.Snapshot> {
    private final WackenDatabase database;
    private final String user;
    private final BooleanSupplier hasPending;
    RoomFestivalArchiveCache(WackenDatabase database,String user,BooleanSupplier hasPending) {
        this.database=database;this.user=user;this.hasPending=hasPending;
    }
    @Override public void accept(SupabaseFestivalArchiveGateway.Snapshot snapshot) {
        database.runInTransaction(()->{
            if(hasPending.getAsBoolean())
                throw new IllegalStateException("New pending edits were retained. Review archived corrections before retrying.");
            RoomFestivalAfterRatingRepository ratings=new RoomFestivalAfterRatingRepository(database);
            ratings.mergeRemote(snapshot.after(),user);
            ratings.replaceCurrent(snapshot.current(),user);
            new RoomFestivalRepository(database).save(snapshot.festival());
        });
    }
}
