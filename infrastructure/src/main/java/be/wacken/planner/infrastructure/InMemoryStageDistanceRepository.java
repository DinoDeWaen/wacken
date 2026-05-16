package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.StageDistance;
import be.wacken.planner.domain.StageDistanceRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InMemoryStageDistanceRepository implements StageDistanceRepository {
    private final Map<StagePair, StageDistance> distancesByStagePair = new HashMap<>();

    @Override
    public void save(StageDistance distance) {
        distancesByStagePair.put(new StagePair(distance.from(), distance.to()), distance);
    }

    @Override
    public void replaceAll(List<StageDistance> distances) {
        distancesByStagePair.clear();
        distances.forEach(this::save);
    }

    @Override
    public Optional<StageDistance> findBetween(Stage from, Stage to) {
        if (from.equals(to)) {
            return Optional.of(StageDistance.between(from, to, 0));
        }
        return Optional.ofNullable(distancesByStagePair.get(new StagePair(from, to)));
    }

    private record StagePair(Stage from, Stage to) {
    }
}
