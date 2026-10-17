package be.wacken.planner;

import java.util.List;
import java.util.Objects;

import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;

final class FestivalScopedPerformanceRepository implements PerformanceRepository {
    private final PerformanceRepository delegate;
    private final String festivalId;

    FestivalScopedPerformanceRepository(PerformanceRepository delegate, String festivalId) {
        this.delegate = Objects.requireNonNull(delegate);
        this.festivalId = festivalId;
    }

    @Override
    public void save(Performance performance) {
        if (!festivalId.equals(performance.festivalId())) {
            throw new IllegalArgumentException("Performance belongs to another festival.");
        }
        delegate.save(performance);
    }

    @Override
    public void replaceAll(List<Performance> performances) {
        throw new UnsupportedOperationException("Use the explicit master-data import target to replace performances.");
    }

    @Override
    public List<Performance> findAll() {
        return delegate.findAll().stream().filter(performance -> festivalId.equals(performance.festivalId())).toList();
    }
}
