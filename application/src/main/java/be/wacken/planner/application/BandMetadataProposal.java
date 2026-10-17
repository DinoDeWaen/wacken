package be.wacken.planner.application;

import java.util.Optional;

public record BandMetadataProposal(
        String bandName,
        BandMetadataField field,
        String proposedValue,
        String sourceName,
        Optional<String> sourceUrl,
        String candidateName,
        int confidence
) {
    public BandMetadataProposal {
        bandName = requireText(bandName, "bandName");
        proposedValue = requireText(proposedValue, "proposedValue");
        sourceName = requireText(sourceName, "sourceName");
        candidateName = requireText(candidateName, "candidateName");
        sourceUrl = sourceUrl == null ? Optional.empty() : sourceUrl.map(String::trim).filter(text -> !text.isBlank());
        confidence = Math.max(0, Math.min(100, confidence));
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }
}
