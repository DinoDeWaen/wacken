package be.wacken.planner.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SavedRatingTest {
    @Test
    void trimsUserNameAndKeepsBandAndRating() {
        Band band = new Band("5th Avenue");
        Rating rating = Rating.of(4);

        SavedRating savedRating = new SavedRating(" dino ", band, rating);

        assertEquals("dino", savedRating.userName());
        assertEquals(band, savedRating.band());
        assertEquals(rating, savedRating.rating());
    }

    @Test
    void rejectsBlankUserName() {
        DomainValidationException error = assertThrows(
                DomainValidationException.class,
                () -> new SavedRating(" ", new Band("5th Avenue"), Rating.of(4))
        );

        assertEquals("Rating user name must not be blank.", error.getMessage());
    }

    @Test
    void rejectsMissingBand() {
        NullPointerException error = assertThrows(
                NullPointerException.class,
                () -> new SavedRating("dino", null, Rating.of(4))
        );

        assertEquals("band must not be null", error.getMessage());
    }

    @Test
    void rejectsMissingRating() {
        NullPointerException error = assertThrows(
                NullPointerException.class,
                () -> new SavedRating("dino", new Band("5th Avenue"), null)
        );

        assertEquals("rating must not be null", error.getMessage());
    }
}
