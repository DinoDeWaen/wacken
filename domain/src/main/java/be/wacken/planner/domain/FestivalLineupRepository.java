package be.wacken.planner.domain;

import java.util.List;

public interface FestivalLineupRepository {
    void saveAllForFestival(String festivalId, List<FestivalLineupEntry> entries);

    List<FestivalLineupEntry> findByFestival(String festivalId);
}
