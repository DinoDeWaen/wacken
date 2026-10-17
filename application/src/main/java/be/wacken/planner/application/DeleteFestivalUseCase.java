package be.wacken.planner.application;

import java.util.Objects;

import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalRepository;

public final class DeleteFestivalUseCase {
    private final FestivalRepository festivals;
    private final Gateway gateway;

    public DeleteFestivalUseCase(FestivalRepository festivals, Gateway gateway) {
        this.festivals = Objects.requireNonNull(festivals);
        this.gateway = Objects.requireNonNull(gateway);
    }

    public Festival delete(String festivalId) {
        Festival selected = festivals.findAll().stream()
                .filter(festival -> festival.id().equals(festivalId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Festival is no longer available. Refresh and try again."));
        gateway.delete(selected);
        return selected;
    }

    public interface Gateway {
        void delete(Festival festival);
    }
}
