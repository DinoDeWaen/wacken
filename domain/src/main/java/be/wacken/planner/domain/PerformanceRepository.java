package be.wacken.planner.domain;

import java.util.List;

public interface PerformanceRepository {
    void save(Performance performance);

    List<Performance> findAll();
}
