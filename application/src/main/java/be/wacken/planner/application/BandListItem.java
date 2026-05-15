package be.wacken.planner.application;

import java.util.Optional;

public record BandListItem(String bandName, String stageName, String startTime, String endTime, Optional<Integer> rating) {
}
