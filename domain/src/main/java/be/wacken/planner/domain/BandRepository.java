package be.wacken.planner.domain;

import java.util.List;
import java.util.Optional;

public interface BandRepository {
    void save(Band band);

    Optional<Band> findByName(String name);

    List<Band> findAll();
}
