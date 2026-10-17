package be.wacken.planner.application;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.DomainValidationException;
import be.wacken.planner.domain.Festival;

public final class AddFestivalUseCase {
    private final FestivalCreationGateway creation;

    public AddFestivalUseCase(FestivalCreationGateway creation) {
        this.creation = Objects.requireNonNull(creation, "creation must not be null");
    }

    public AddFestivalResult addFestival(String groupId, String userName, String festivalId,
                                        String festivalName, List<Band> uploadedBands) {
        try {
            Festival festival = Festival.active(festivalId, festivalName);
            if (groupId == null || groupId.isBlank() || userName == null || userName.isBlank()) {
                return AddFestivalResult.failure("Sign in before adding a group-wide festival.");
            }
            LinkedHashMap<String, Band> distinctBands = new LinkedHashMap<>();
            for (Band band : Objects.requireNonNull(uploadedBands, "uploadedBands")) {
                distinctBands.putIfAbsent(band.name(), band);
            }
            if (distinctBands.isEmpty()) {
                return AddFestivalResult.failure("Select at least one band before adding the festival.");
            }
            // Check authoritative state inside the creation transaction, never a stale cache.
            return creation.create(groupId, userName, festival, List.copyOf(distinctBands.values()));
        } catch (DomainValidationException error) {
            return AddFestivalResult.failure(error.getMessage());
        }
    }
}
