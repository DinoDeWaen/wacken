package be.wacken.planner.persistence;

import java.util.List;
import java.util.stream.Collectors;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.FestivalLineupEntry;
import be.wacken.planner.domain.FestivalLineupRepository;

public final class RoomFestivalLineupRepository implements FestivalLineupRepository {
    private final RoomFestivalLineupEntryDao lineups;

    public RoomFestivalLineupRepository(WackenDatabase database) {
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
}
