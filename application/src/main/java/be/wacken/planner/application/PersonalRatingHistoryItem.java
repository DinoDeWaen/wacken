package be.wacken.planner.application;

import java.time.Instant;
import java.util.Optional;

public record PersonalRatingHistoryItem(String bandName, Optional<String> festivalName, int rating, Instant createdAt) {
    public String displayText() {
        String festival = festivalName.orElse("Unknown festival");
        return festival + " - " + rating + " stars - " + createdAt.toString();
    }
}
