package be.wacken.planner.persistence;

import java.time.Instant;
import java.util.List;
import be.wacken.planner.domain.*;

public final class RoomBandAliasRepository {
    private final WackenDatabase database;
    public RoomBandAliasRepository(WackenDatabase database) { this.database=database; }
    public List<ConfirmedBandAlias> findAll() {
        return database.bandAliases().findAll().stream().map(row -> new ConfirmedBandAlias(row.aliasName,
            new CanonicalBand(row.bandId,new Band(row.bandName)),row.confirmedBy,Instant.parse(row.confirmedAt))).toList();
    }
    public void replaceAll(List<ConfirmedBandAlias> aliases) {
        database.runInTransaction(() -> {
            database.bandAliases().clear();
            aliases.forEach(a -> database.bandAliases().save(new RoomBandAlias(ConfirmedBandAlias.key(a.name()),
                a.name(),a.canonical().id(),a.canonical().band().name(),a.confirmedBy(),a.confirmedAt().toString())));
        });
    }
}
