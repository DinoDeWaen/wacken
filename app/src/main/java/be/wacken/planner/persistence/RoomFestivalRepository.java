package be.wacken.planner.persistence;

import java.util.List;
import java.util.stream.Collectors;

import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalRepository;
import be.wacken.planner.domain.FestivalStatus;
import be.wacken.planner.SyncingFestivalRepository;

public final class RoomFestivalRepository implements FestivalRepository, SyncingFestivalRepository.Cache {
    public static final String DEFAULT_FESTIVAL_ID = "wacken-2026";
    public static final String DEFAULT_FESTIVAL_NAME = "Wacken Open Air 2026";

    private final WackenDatabase database;
    private final RoomFestivalDao festivals;
    private final RoomFestivalCacheMarkerDao marker;

    public RoomFestivalRepository(WackenDatabase database) {
        this.database = database;
        this.festivals = database.festivals();
        this.marker = database.festivalCacheMarker();
    }

    public void seedDefaultActiveFestivalIfEmpty() {
        database.runInTransaction(() -> {
            if (marker.count() == 0) {
                if (festivals.count() == 0) {
                    save(Festival.active(DEFAULT_FESTIVAL_ID, DEFAULT_FESTIVAL_NAME));
                }
                marker.markInitialized(new RoomFestivalCacheMarker(1));
            }
        });
    }

    @Override
    public List<Festival> findAll() {
        return festivals.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Festival festival) {
        festivals.save(new RoomFestival(festival.id(), festival.name(), festival.status().name()));
        marker.markInitialized(new RoomFestivalCacheMarker(1));
    }

    public void replaceAll(List<Festival> replacements) {
        database.runInTransaction(() -> {
            for (Festival existing : findAll()) {
                if (replacements.stream().noneMatch(festival -> festival.id().equals(existing.id()))) {
                    new RoomFestivalDeletion(database).delete(existing);
                }
            }
            festivals.deleteAll();
            festivals.saveAll(replacements.stream()
                    .map(festival -> new RoomFestival(festival.id(), festival.name(), festival.status().name()))
                    .collect(Collectors.toList()));
            marker.markInitialized(new RoomFestivalCacheMarker(1));
        });
    }

    private Festival toDomain(RoomFestival row) {
        return new Festival(row.id, row.name, FestivalStatus.valueOf(row.status));
    }
}
