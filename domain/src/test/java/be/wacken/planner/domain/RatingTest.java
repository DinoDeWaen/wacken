package be.wacken.planner.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RatingTest {
    @Test
    void acceptsUnratedZeroAndFiveExplicitRatingValues() {
        assertEquals(0, Rating.of(0).value());
        assertEquals(1, Rating.of(1).value());
        assertEquals(5, Rating.of(5).value());
    }

    @Test
    void rejectsRatingsBelowZero() {
        DomainValidationException error = assertThrows(
                DomainValidationException.class,
                () -> Rating.of(-1)
        );

        assertEquals("Rating must be between 0 and 5.", error.getMessage());
    }

    @Test
    void rejectsRatingsAboveFive() {
        DomainValidationException error = assertThrows(
                DomainValidationException.class,
                () -> Rating.of(6)
        );

        assertEquals("Rating must be between 0 and 5.", error.getMessage());
    }
}
