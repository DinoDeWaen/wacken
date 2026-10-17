package be.wacken.planner.application;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalLifecycle;
import be.wacken.planner.domain.FestivalLineupEntry;
import be.wacken.planner.domain.FestivalLineupRepository;
import be.wacken.planner.domain.FestivalRepository;

public final class LinkImportedBandUseCase {
    private final FestivalRepository festivals;
    private final FestivalLineupRepository lineups;
    private final BandRepository bands;

    public LinkImportedBandUseCase(FestivalRepository festivals, FestivalLineupRepository lineups, BandRepository bands) {
        this.festivals = Objects.requireNonNull(festivals, "festivals must not be null");
        this.lineups = Objects.requireNonNull(lineups, "lineups must not be null");
        this.bands = Objects.requireNonNull(bands, "bands must not be null");
    }

    public BandLinkResult link(String currentBandName, String uploadedDisplayName, String selectedBandName) {
        if (selectedBandName == null || selectedBandName.isBlank()) {
            return BandLinkResult.noMatch(uploadedDisplayName);
        }
        Optional<Festival> active = FestivalLifecycle.activeFestival(festivals.findAll());
        if (active.isEmpty()) {
            return BandLinkResult.failure("No active festival is available for band linking.");
        }
        Band targetBand = bands.findByName(selectedBandName)
                .orElse(null);
        if (targetBand == null) {
            return BandLinkResult.failure("Selected band no longer exists.");
        }
        List<FestivalLineupEntry> existing = lineups.findByFestival(active.get().id());
        boolean linked = existing.stream().anyMatch(entry -> entry.band().name().equals(currentBandName));
        if (!linked) {
            return BandLinkResult.failure("Imported band is no longer in the active festival lineup.");
        }
        List<FestivalLineupEntry> replacements = existing.stream()
                .map(entry -> entry.band().name().equals(currentBandName)
                        ? new FestivalLineupEntry(entry.festivalId(), targetBand, entry.uploadedDisplayName())
                        : entry)
                .collect(Collectors.toList());
        lineups.saveAllForFestival(active.get().id(), replacements);
        return BandLinkResult.linked(uploadedDisplayName, targetBand.name());
    }
}
