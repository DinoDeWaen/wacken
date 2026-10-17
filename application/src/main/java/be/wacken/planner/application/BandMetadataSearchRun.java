package be.wacken.planner.application;

import java.util.List;

public record BandMetadataSearchRun(
        List<BandMetadataSearchResult> results,
        int totalBands,
        int completeBands,
        int bandsMissingMetadata,
        int bandsNeedingReview,
        int proposalCount,
        List<String> bandsWithoutProposals,
        List<String> providerMessages
) {
    public BandMetadataSearchRun {
        results = List.copyOf(results);
        bandsWithoutProposals = List.copyOf(bandsWithoutProposals);
        providerMessages = List.copyOf(providerMessages);
    }
}
