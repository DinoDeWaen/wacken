package be.wacken.planner.application;

public record ApplyBandMetadataProposalsResult(boolean success, int updatedFields, int skippedFields, String message) {
    public static ApplyBandMetadataProposalsResult success(int updatedFields, int skippedFields) {
        return new ApplyBandMetadataProposalsResult(
                true,
                updatedFields,
                skippedFields,
                updatedFields + " metadata fields updated. " + skippedFields + " proposals skipped."
        );
    }

    public static ApplyBandMetadataProposalsResult failure(String message) {
        return new ApplyBandMetadataProposalsResult(false, 0, 0, message);
    }
}
