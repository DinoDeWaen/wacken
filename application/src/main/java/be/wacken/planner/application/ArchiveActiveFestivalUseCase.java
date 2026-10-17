package be.wacken.planner.application;

import java.util.List;
import java.util.Objects;

import be.wacken.planner.domain.DomainValidationException;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalLifecycle;
import be.wacken.planner.domain.FestivalRepository;

public final class ArchiveActiveFestivalUseCase {
    private final FestivalRepository festivals;
    private final FestivalArchiveGateway archive;

    public ArchiveActiveFestivalUseCase(FestivalRepository festivals, FestivalArchiveGateway archive) {
        this.festivals = Objects.requireNonNull(festivals, "festivals must not be null");
        this.archive = Objects.requireNonNull(archive, "archive must not be null");
    }

    public FestivalStartState archiveActiveFestival() {
        List<Festival> allFestivals = festivals.findAll();
        Festival activeFestival = FestivalLifecycle.activeFestival(allFestivals)
                .orElseThrow(() -> new DomainValidationException("No active festival is available to archive."));
        archive.archive(activeFestival);
        return new ShowFestivalStartUseCase(festivals).show();
    }
}
