package be.wacken.planner.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FoodOptionTest {
    @Test
    void requiresFoodName() {
        DomainValidationException error = assertThrows(
                DomainValidationException.class,
                () -> new FoodOption(" ")
        );

        assertEquals("Food option name must not be blank.", error.getMessage());
    }
}
