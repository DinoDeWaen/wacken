package be.wacken.planner.application;

import java.util.List;
import java.util.Objects;

import be.wacken.planner.domain.DomainValidationException;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalLifecycle;
import be.wacken.planner.domain.FestivalRepository;

public final class RenameActiveFestivalUseCase {
    private final FestivalRepository festivals;

    public RenameActiveFestivalUseCase(FestivalRepository festivals) {
        this.festivals = Objects.requireNonNull(festivals, "festivals must not be null");
    }

    public RenameActiveFestivalResult rename(String name) {
        try {
            List<Festival> allFestivals = festivals.findAll();
            Festival activeFestival = FestivalLifecycle.activeFestival(allFestivals).orElse(null);
            if (activeFestival == null) {
                return RenameActiveFestivalResult.failure("No active festival is available to rename.");
            }
            festivals.save(activeFestival.rename(name));
            return RenameActiveFestivalResult.renamed();
        } catch (DomainValidationException error) {
            return RenameActiveFestivalResult.failure(error.getMessage());
        }
    }
}
