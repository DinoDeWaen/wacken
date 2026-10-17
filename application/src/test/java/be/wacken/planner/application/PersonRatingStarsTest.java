package be.wacken.planner.application;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PersonRatingStarsTest {
    @Test
    void rendersKnownSofieAndDinoIdsAsCompactLabels() {
        assertEquals(
                "D ★★★",
                new PersonRatingStars("21dad490-3b20-4377-b880-44fb4a93221c", 3).displayText()
        );
        assertEquals(
                "S ★★★★★",
                new PersonRatingStars("f4dbc343-5c61-476d-8352-024528ff0000", 5).displayText()
        );
    }

    @Test
    void hidesUnknownUuidValuesBehindCompactFallbackLabel() {
        String displayText = new PersonRatingStars("aaaaaaaa-bbbb-4ccc-8ddd-eeeeeeeeeeee", 4).displayText();

        assertEquals("U ★★★★", displayText);
        assertFalse(displayText.contains("aaaaaaaa"));
    }

    @Test
    void rendersNormalNamesAsCompactInitials() {
        assertEquals("D ★★★★", new PersonRatingStars("dino", 4).displayText());
        assertEquals("S ★★★★★", new PersonRatingStars("Sofie", 5).displayText());
    }
}
