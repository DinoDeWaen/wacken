package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;

import java.util.ArrayList;
import java.util.List;

public final class InMemoryPerformanceRepository implements PerformanceRepository {
    private final List<Performance> performances = new ArrayList<>();

    @Override
    public void save(Performance performance) {
        performances.add(performance);
    }

    @Override
    public void replaceAll(List<Performance> replacements) {
        performances.clear();
        performances.addAll(replacements);
    }

    @Override
    public List<Performance> findAll() {
        return new ArrayList<>(performances);
    }
}
