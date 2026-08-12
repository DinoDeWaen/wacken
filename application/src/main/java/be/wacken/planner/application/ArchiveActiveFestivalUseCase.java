package be.wacken.planner.application;

import java.util.List;
import java.util.Objects;

import be.wacken.planner.domain.DomainValidationException;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalLifecycle;
import be.wacken.planner.domain.FestivalRepository;

public final class ArchiveActiveFestivalUseCase {
    private final FestivalRepository festivals;

    public ArchiveActiveFestivalUseCase(FestivalRepository festivals) {
        this.festivals = Objects.requireNonNull(festivals, "festivals must not be null");
    }

    public FestivalStartState archiveActiveFestival() {
        List<Festival> allFestivals = festivals.findAll();
        Festival activeFestival = FestivalLifecycle.activeFestival(allFestivals)
                .orElseThrow(() -> new DomainValidationException("No active festival is available to archive."));
        festivals.save(activeFestival.archive());
        return new ShowFestivalStartUseCase(festivals).show();
    }
}
