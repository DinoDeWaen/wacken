package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryBandRepositoryTest {
    @Test
    void storesAndRetrievesBandsByName() {
        BandRepository repository = new InMemoryBandRepository();
        Band band = new Band("5th Avenue");

        repository.save(band);

        assertEquals(Optional.of(band), repository.findByName("5th Avenue"));
        assertEquals(List.of(band), repository.findAll());
    }
}
