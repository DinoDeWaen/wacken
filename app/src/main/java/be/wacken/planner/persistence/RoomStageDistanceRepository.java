package be.wacken.planner.persistence;

import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.StageDistance;
import be.wacken.planner.domain.StageDistanceRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class RoomStageDistanceRepository implements StageDistanceRepository {
    private final WackenDatabase database;
    private final RoomStageDistanceDao distances;

    public RoomStageDistanceRepository(WackenDatabase database) {
        this.database = database;
        this.distances = database.stageDistances();
    }

    @Override
    public void save(StageDistance distance) {
        distances.save(toRow(distance));
    }

    @Override
    public void replaceAll(List<StageDistance> replacements) {
        database.runInTransaction(() -> {
            distances.deleteAll();
            distances.saveAll(replacements.stream().map(this::toRow).collect(Collectors.toList()));
        });
    }

    @Override
    public Optional<StageDistance> findBetween(Stage from, Stage to) {
        if (from.equals(to)) {
            return Optional.of(StageDistance.between(from, to, 0));
        }
        return Optional.ofNullable(distances.findBetween(from.name(), to.name())).map(this::toDomain);
    }

    @Override
    public List<StageDistance> findAll() {
        return distances.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return distances.count() == 0;
    }

    private RoomStageDistance toRow(StageDistance distance) {
        return new RoomStageDistance(distance.from().name(), distance.to().name(), distance.walkingMinutes());
    }

    private StageDistance toDomain(RoomStageDistance row) {
        return StageDistance.between(new Stage(row.fromStageName), new Stage(row.toStageName), row.walkingMinutes);
    }
}
