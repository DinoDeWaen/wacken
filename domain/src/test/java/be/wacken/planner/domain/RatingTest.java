package be.wacken.planner.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RatingTest {
    @Test
    void acceptsRatingValuesBetweenZeroAndFour() {
        assertEquals(0, Rating.of(0).value());
        assertEquals(4, Rating.of(4).value());
    }

    @Test
    void rejectsRatingsBelowZero() {
        DomainValidationException error = assertThrows(
                DomainValidationException.class,
                () -> Rating.of(-1)
        );

        assertEquals("Rating must be between 0 and 4.", error.getMessage());
    }

    @Test
    void rejectsRatingsAboveFour() {
        DomainValidationException error = assertThrows(
                DomainValidationException.class,
                () -> Rating.of(5)
        );

        assertEquals("Rating must be between 0 and 4.", error.getMessage());
    }
}
