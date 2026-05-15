package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.StageDistance;
import be.wacken.planner.domain.StageDistanceRepository;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class FileBackedStageDistanceRepository implements StageDistanceRepository {
    private final FileBackedStorage storage;

    public FileBackedStageDistanceRepository(Path directory) {
        this.storage = new FileBackedStorage(directory, "stage-distances.tsv");
    }

    @Override
    public void save(StageDistance distance) {
        Map<StagePair, StageDistance> distancesByStagePair = loadDistancesByStagePair();
        distancesByStagePair.put(new StagePair(distance.from(), distance.to()), distance);
        storage.writeRows(distancesByStagePair.values().stream()
                .map(savedDistance -> List.of(
                        savedDistance.from().name(),
                        savedDistance.to().name(),
                        Integer.toString(savedDistance.walkingMinutes())
                ))
                .toList());
    }

    @Override
    public Optional<StageDistance> findBetween(Stage from, Stage to) {
        if (from.equals(to)) {
            return Optional.of(StageDistance.between(from, to, 0));
        }
        return Optional.ofNullable(loadDistancesByStagePair().get(new StagePair(from, to)));
    }

    private Map<StagePair, StageDistance> loadDistancesByStagePair() {
        Map<StagePair, StageDistance> distancesByStagePair = new LinkedHashMap<>();
        for (List<String> row : storage.readRows()) {
            StageDistance distance = StageDistance.between(
                    new Stage(row.get(0)),
                    new Stage(row.get(1)),
                    Integer.parseInt(row.get(2))
            );
            distancesByStagePair.put(new StagePair(distance.from(), distance.to()), distance);
        }
        return distancesByStagePair;
    }

    private record StagePair(Stage from, Stage to) {
    }
}
