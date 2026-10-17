package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.Stage;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class FileBackedPerformanceRepository implements PerformanceRepository {
    private final FileBackedStorage storage;

    public FileBackedPerformanceRepository(Path directory) {
        this.storage = new FileBackedStorage(directory, "performances.tsv");
    }

    @Override
    public void save(Performance performance) {
        List<Performance> performances = new ArrayList<>(findAll());
        performances.add(performance);
        writePerformances(performances);
    }

    @Override
    public void replaceAll(List<Performance> performances) {
        writePerformances(performances);
    }

    private void writePerformances(List<Performance> performances) {
        storage.writeRows(performances.stream()
                .map(savedPerformance -> java.util.Arrays.asList(
                        savedPerformance.band().name(),
                        savedPerformance.stage().name(),
                        savedPerformance.start().toString(),
                        savedPerformance.end().toString()
                ))
                .collect(Collectors.toList()));
    }

    @Override
    public List<Performance> findAll() {
        return storage.readRows().stream()
                .map(row -> new Performance(
                        new Band(row.get(0)),
                        new Stage(row.get(1)),
                        LocalDateTime.parse(row.get(2)),
                        LocalDateTime.parse(row.get(3))
                ))
                .collect(Collectors.toList());
    }
}
