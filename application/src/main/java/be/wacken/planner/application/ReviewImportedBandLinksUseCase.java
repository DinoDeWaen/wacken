package be.wacken.planner.application;

import java.util.Comparator;
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

public final class ReviewImportedBandLinksUseCase {
    private final FestivalRepository festivals;
    private final FestivalLineupRepository lineups;
    private final BandRepository bands;

    public ReviewImportedBandLinksUseCase(FestivalRepository festivals, FestivalLineupRepository lineups, BandRepository bands) {
        this.festivals = Objects.requireNonNull(festivals, "festivals must not be null");
        this.lineups = Objects.requireNonNull(lineups, "lineups must not be null");
        this.bands = Objects.requireNonNull(bands, "bands must not be null");
    }

    public List<BandLinkCandidate> review() {
        Optional<Festival> active = FestivalLifecycle.activeFestival(festivals.findAll());
        if (active.isEmpty()) {
            return List.of();
        }
        return lineups.findByFestival(active.get().id()).stream()
                .map(entry -> candidateFor(entry, entry.uploadedDisplayName()))
                .filter(candidate -> !candidate.candidateBandNames().isEmpty())
                .collect(Collectors.toList());
    }

    public BandLinkCandidate search(String uploadedDisplayName, String currentBandName, String searchTerm) {
        return new BandLinkCandidate(uploadedDisplayName, currentBandName, searchTerm, candidateNames(currentBandName, searchTerm));
    }

    private BandLinkCandidate candidateFor(FestivalLineupEntry entry, String searchTerm) {
        return new BandLinkCandidate(
                entry.uploadedDisplayName(),
                entry.band().name(),
                searchTerm,
                candidateNames(entry.band().name(), searchTerm)
        );
    }

    private List<String> candidateNames(String currentBandName, String searchTerm) {
        return bands.findAll().stream()
                .map(Band::name)
                .filter(name -> !name.equals(currentBandName))
                .filter(name -> BandNameMatcher.matchesSearch(name, searchTerm))
                .sorted(Comparator.comparing(String::toString, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }
}
