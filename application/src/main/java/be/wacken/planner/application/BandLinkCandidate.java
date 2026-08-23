package be.wacken.planner.application;

import java.util.List;

public record BandLinkCandidate(String uploadedDisplayName, String currentBandName, String searchTerm, List<String> candidateBandNames) {
}
