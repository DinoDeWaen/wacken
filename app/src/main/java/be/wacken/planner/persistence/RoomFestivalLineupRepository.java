package be.wacken.planner.persistence;

import java.util.List;
import java.util.stream.Collectors;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.FestivalLineupEntry;
import be.wacken.planner.domain.FestivalLineupRepository;
import be.wacken.planner.SyncingFestivalLineupRepository;

public final class RoomFestivalLineupRepository implements FestivalLineupRepository, SyncingFestivalLineupRepository.Cache {
    private final WackenDatabase database;
    private final RoomFestivalLineupEntryDao lineups;

    public RoomFestivalLineupRepository(WackenDatabase database) {
        this.database = database;
        this.lineups = database.festivalLineups();
    }

    @Override
    public void saveAllForFestival(String festivalId, List<FestivalLineupEntry> entries) {
        lineups.deleteByFestival(festivalId);
        lineups.saveAll(entries.stream()
                .map(entry -> new RoomFestivalLineupEntry(entry.festivalId(), entry.band().name(), entry.uploadedDisplayName()))
                .collect(Collectors.toList()));
    }

    @Override
    public List<FestivalLineupEntry> findByFestival(String festivalId) {
        return lineups.findByFestival(festivalId)
                .stream()
                .map(row -> new FestivalLineupEntry(row.festivalId, new Band(row.bandName), row.uploadedDisplayName))
                .collect(Collectors.toList());
    }

    public List<FestivalLineupEntry> findAll() {
        return lineups.findAll()
                .stream()
                .map(row -> new FestivalLineupEntry(row.festivalId, new Band(row.bandName), row.uploadedDisplayName))
                .collect(Collectors.toList());
    }

    public void replaceAll(List<FestivalLineupEntry> entries) {
        database.runInTransaction(() -> {
            lineups.deleteAll();
            lineups.saveAll(entries.stream()
                    .map(entry -> new RoomFestivalLineupEntry(entry.festivalId(), entry.band().name(), entry.uploadedDisplayName()))
                    .collect(Collectors.toList()));
        });
    }
}
