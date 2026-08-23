package be.wacken.planner.application;

import java.util.List;

public record BandMetadataSearchResult(
        String bandName,
        List<BandMetadataProposal> proposals,
        List<String> unavailableProviders
) {
    public BandMetadataSearchResult {
        proposals = List.copyOf(proposals);
        unavailableProviders = List.copyOf(unavailableProviders);
    }
}
