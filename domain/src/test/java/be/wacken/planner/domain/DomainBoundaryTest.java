package be.wacken.planner.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DomainBoundaryTest {
    @Test
    void namesTheDomainBoundary() {
        assertEquals("domain", DomainBoundary.name());
    }
}
