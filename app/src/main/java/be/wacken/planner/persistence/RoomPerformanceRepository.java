package be.wacken.planner.persistence;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public final class RoomPerformanceRepository implements PerformanceRepository {
    private final WackenDatabase database;
    private final RoomPerformanceDao performances;

    public RoomPerformanceRepository(WackenDatabase database) {
        this.database = database;
        this.performances = database.performances();
    }

    @Override
    public void save(Performance performance) {
        performances.save(toRow(performance));
    }

    @Override
    public void replaceAll(List<Performance> replacements) {
        database.runInTransaction(() -> {
            performances.deleteAll();
            performances.saveAll(replacements.stream().map(this::toRow).collect(Collectors.toList()));
        });
    }

    @Override
    public List<Performance> findAll() {
        return performances.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return performances.count() == 0;
    }

    private RoomPerformance toRow(Performance performance) {
        return new RoomPerformance(
                performance.band().name(),
                performance.stage().name(),
                performance.start().toString(),
                performance.end().toString()
        );
    }

    private Performance toDomain(RoomPerformance row) {
        return new Performance(
                new Band(row.bandName),
                new Stage(row.stageName),
                LocalDateTime.parse(row.start),
                LocalDateTime.parse(row.end)
        );
    }
}
