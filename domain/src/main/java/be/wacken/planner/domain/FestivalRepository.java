package be.wacken.planner.domain;

import java.util.List;

public interface FestivalRepository {
    List<Festival> findAll();

    void save(Festival festival);
}
