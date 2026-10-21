package be.wacken.planner.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class MemberRatingSummaryCharacterizationTest {
    @Test
    void allProjectionSummariesKeepOrderTwoSpaceSeparatorAndPositiveStarCounts() {
        assertSummaries(List.of(), "");
        assertSummaries(List.of(new PersonRatingStars("zoe", 5), new PersonRatingStars("Alice", 1)),
                "Z ★★★★★  A ★");
        assertSummaries(List.of(new PersonRatingStars("Élodie", 3)), "É ★★★");
    }

    @Test
    void allProjectionSummariesKeepKnownAndUnknownMemberPrivacyLabels() {
        assertSummaries(List.of(
                new PersonRatingStars("21dad490-3b20-4377-b880-44fb4a93221c", 4),
                new PersonRatingStars("f4dbc343-5c61-476d-8352-024528ff0000", 2),
                new PersonRatingStars("11111111-1111-4111-8111-111111111111", 3)),
                "D ★★★★  S ★★  U ★★★");
    }

    private static void assertSummaries(List<PersonRatingStars> people, String expected) {
        assertEquals(expected, new BandListItem("Band", "Stage", "TBA", "TBA", 0, true, people).personRatingSummary());
        assertEquals(expected, new BandDetailItem("Band", Optional.empty(), Optional.empty(), 0, true,
                Optional.empty(), Optional.empty(), people).personRatingSummary());
        assertEquals(expected, new ScheduleDecisionCandidate("Band", 4, "Stage",
                LocalDateTime.parse("2026-07-30T18:00"), LocalDateTime.parse("2026-07-30T19:00"),
                "SELECTED", true, people).personRatingSummary());
    }
}
