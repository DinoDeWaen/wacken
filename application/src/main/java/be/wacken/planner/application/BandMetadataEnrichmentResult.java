package be.wacken.planner.application;

public record BandMetadataEnrichmentResult(int updatedBands, int remainingMissingBands, boolean externalLookupConfigured) {
    public String message() {
        if (externalLookupConfigured) {
            return updatedBands + " bands updated from metadata sources.";
        }
        return updatedBands + " bands updated from the band database. Online metadata lookup is not configured yet.";
    }
}
