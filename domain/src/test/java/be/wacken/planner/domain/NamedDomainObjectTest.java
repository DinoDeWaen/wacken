package be.wacken.planner.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NamedDomainObjectTest {
    @Test
    void bandNameMustBePresent() {
        DomainValidationException error = assertThrows(
                DomainValidationException.class,
                () -> new Band(" ")
        );

        assertEquals("Band name must not be blank.", error.getMessage());
    }

    @Test
    void stageNameMustBePresent() {
        DomainValidationException error = assertThrows(
                DomainValidationException.class,
                () -> new Stage(" ")
        );

        assertEquals("Stage name must not be blank.", error.getMessage());
    }
}
