package be.wacken.planner.application;

import java.time.Instant;
import java.util.Optional;

public record PersonalRatingHistoryItem(String bandName, Optional<String> festivalName, int rating, Instant createdAt) {
    public String displayText() {
        String festival = festivalName.orElse("Unknown festival");
        if (Instant.EPOCH.equals(createdAt)) {
            return festival + " - " + rating + " stars - date unknown";
        }
        return festival + " - " + rating + " stars - " + createdAt.toString();
    }
}
