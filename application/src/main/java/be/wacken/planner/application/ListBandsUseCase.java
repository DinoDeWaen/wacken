package be.wacken.planner.application;

import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class ListBandsUseCase {
    private final PerformanceRepository performances;

    public ListBandsUseCase(PerformanceRepository performances) {
        this.performances = Objects.requireNonNull(performances, "performances must not be null");
    }

    public List<BandListItem> listBands() {
        return performances.findAll()
                .stream()
                .sorted(Comparator.comparing(Performance::start))
                .map(performance -> new BandListItem(
                        performance.band().name(),
                        performance.stage().name(),
                        performance.start().toString(),
                        performance.end().toString()
                ))
                .toList();
    }
}
