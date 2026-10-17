package be.wacken.planner.application;

import java.util.List;
import java.util.Objects;

import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalLifecycle;
import be.wacken.planner.domain.FestivalRepository;

public final class ShowFestivalStartUseCase {
    private final FestivalRepository festivals;

    public ShowFestivalStartUseCase(FestivalRepository festivals) {
        this.festivals = Objects.requireNonNull(festivals, "festivals must not be null");
    }

    public FestivalStartState show() {
        List<Festival> allFestivals = festivals.findAll();
        return FestivalLifecycle.activeFestival(allFestivals)
                .map(FestivalStartState::active)
                .orElseGet(() -> FestivalStartState.archive(FestivalLifecycle.archivedFestivals(allFestivals)));
    }
}
