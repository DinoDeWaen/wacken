package be.wacken.planner.application;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import be.wacken.planner.domain.Festival;

public record FestivalStartState(Optional<Festival> activeFestival, List<Festival> archivedFestivals) {
    public FestivalStartState {
        activeFestival = Objects.requireNonNull(activeFestival, "activeFestival must not be null");
        archivedFestivals = List.copyOf(Objects.requireNonNull(archivedFestivals, "archivedFestivals must not be null"));
    }

    public static FestivalStartState active(Festival festival) {
        return new FestivalStartState(Optional.of(festival), List.of());
    }

    public static FestivalStartState archive(List<Festival> archivedFestivals) {
        return new FestivalStartState(Optional.empty(), archivedFestivals);
    }

    public boolean hasActiveFestival() {
        return activeFestival.isPresent();
    }

    public boolean showArchivedFestivals() {
        return activeFestival.isEmpty();
    }

    public boolean canAddFestival() {
        return activeFestival.isEmpty();
    }

    public boolean archivedFestivalsReadOnly() {
        return archivedFestivals.stream().allMatch(Festival::isReadOnly);
    }
}
