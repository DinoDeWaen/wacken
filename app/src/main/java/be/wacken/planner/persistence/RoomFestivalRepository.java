package be.wacken.planner.persistence;

import java.util.List;
import java.util.stream.Collectors;

import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalRepository;
import be.wacken.planner.domain.FestivalStatus;

public final class RoomFestivalRepository implements FestivalRepository {
    public static final String DEFAULT_FESTIVAL_ID = "wacken-2026";
    public static final String DEFAULT_FESTIVAL_NAME = "Wacken Open Air 2026";

    private final RoomFestivalDao festivals;

    public RoomFestivalRepository(WackenDatabase database) {
        this.festivals = database.festivals();
    }

    public void seedDefaultActiveFestivalIfEmpty() {
        if (festivals.count() == 0) {
            save(Festival.active(DEFAULT_FESTIVAL_ID, DEFAULT_FESTIVAL_NAME));
        }
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
    }

    private Festival toDomain(RoomFestival row) {
        return new Festival(row.id, row.name, FestivalStatus.valueOf(row.status));
    }
}
