package be.wacken.planner.application;

import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApplicationBoundaryTest {
    @Test
    void namesTheApplicationBoundaryAndItsDomainDependency() {
        assertEquals("application -> domain", ApplicationBoundary.name());
    }

    @Test
    void canUseMockitoForApplicationTestsWhenAFakeIsNotEnough() {
        @SuppressWarnings("unchecked")
        Supplier<String> dependency = mock(Supplier.class);
        when(dependency.get()).thenReturn("mocked");

        assertEquals("mocked", dependency.get());
    }
}
