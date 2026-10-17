package be.wacken.planner.application;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;

public final class SearchBandMetadataUseCase {
    private static final String OWN_CATALOG = "Own band database";

    private final BandRepository bands;
    private final List<BandMetadataLookupProvider> providers;

    public SearchBandMetadataUseCase(BandRepository bands, List<BandMetadataLookupProvider> providers) {
        this.bands = Objects.requireNonNull(bands, "bands must not be null");
        this.providers = List.copyOf(Objects.requireNonNull(providers, "providers must not be null"));
    }

    public List<BandMetadataSearchResult> searchMissingMetadata() {
        return searchMissingMetadataRun().results();
    }

    public BandMetadataSearchRun searchMissingMetadataRun() {
        List<Band> allBands = bands.findAll();
        List<BandMetadataSearchResult> results = new ArrayList<>();
        List<String> bandsWithoutProposals = new ArrayList<>();
        Set<String> providerMessages = new LinkedHashSet<>();
        int completeBands = 0;
        int bandsMissingMetadata = 0;
        int bandsNeedingReview = 0;
        int proposalCount = 0;

        for (Band band : allBands) {
            if (missingFields(band).isEmpty()) {
                completeBands++;
                continue;
            }
            bandsMissingMetadata++;
            BandMetadataSearchResult result = search(band.name());
            providerMessages.addAll(result.unavailableProviders());
            if (!result.proposals().isEmpty()) {
                bandsNeedingReview++;
                proposalCount += result.proposals().size();
            } else {
                bandsWithoutProposals.add(result.bandName());
            }
            if (!result.proposals().isEmpty() || !result.unavailableProviders().isEmpty()) {
                results.add(result);
            }
        }

        return new BandMetadataSearchRun(
                results,
                allBands.size(),
                completeBands,
                bandsMissingMetadata,
                bandsNeedingReview,
                proposalCount,
                bandsWithoutProposals,
                List.copyOf(providerMessages)
        );
    }

    public BandMetadataSearchResult search(String bandName) {
        Band band = bands.findByName(bandName).orElse(null);
        if (band == null) {
            return new BandMetadataSearchResult(bandName, List.of(), List.of());
        }

        Set<BandMetadataField> missing = missingFields(band);
        List<BandMetadataProposal> proposals = new ArrayList<>();
        List<String> unavailableProviders = new ArrayList<>();

        addOwnCatalogProposals(band, missing, proposals);
        Set<BandMetadataField> remaining = withoutProposedFields(missing, proposals);
        addProviderProposals(band, remaining, proposals, unavailableProviders);

        return new BandMetadataSearchResult(band.name(), proposals, unavailableProviders);
    }

    private void addOwnCatalogProposals(Band band, Set<BandMetadataField> missing, List<BandMetadataProposal> proposals) {
        for (Band candidate : bands.findAll()) {
            if (candidate.name().equals(band.name()) || !BandNameMatcher.likelySameBand(candidate.name(), band.name())) {
                continue;
            }
            addCandidateProposals(band.name(), OWN_CATALOG, candidate.name(), candidate, OptionalSource.empty(), missing, proposals, 100);
        }
    }

    private void addProviderProposals(
            Band band,
            Set<BandMetadataField> remaining,
            List<BandMetadataProposal> proposals,
            List<String> unavailableProviders
    ) {
        for (BandMetadataLookupProvider provider : providers) {
            if (remaining.isEmpty()) {
                return;
            }
            if (!provider.configured()) {
                unavailableProviders.add(provider.name() + " is not configured.");
                continue;
            }
            try {
                for (BandMetadataProviderCandidate candidate : provider.search(band.name())) {
                    addCandidateProposals(
                            band.name(),
                            provider.name(),
                            candidate.candidateName(),
                            candidate,
                            new OptionalSource(candidate.sourceUrl()),
                            remaining,
                            proposals,
                            candidate.confidence()
                    );
                }
            } catch (BandMetadataLookupException error) {
                unavailableProviders.add(provider.name() + " lookup failed.");
            }
        }
    }

    private void addCandidateProposals(
            String bandName,
            String sourceName,
            String candidateName,
            Band candidate,
            OptionalSource sourceUrl,
            Set<BandMetadataField> fields,
            List<BandMetadataProposal> proposals,
            int confidence
    ) {
        addProposal(bandName, BandMetadataField.BIOGRAPHY, candidate.biography().orElse(null), sourceName, sourceUrl, candidateName, fields, proposals, confidence);
        addProposal(bandName, BandMetadataField.IMAGE_URL, candidate.imageUrl().orElse(null), sourceName, sourceUrl, candidateName, fields, proposals, confidence);
        addProposal(bandName, BandMetadataField.YOUTUBE_URL, candidate.youtubeUrl().orElse(null), sourceName, sourceUrl, candidateName, fields, proposals, confidence);
        addProposal(bandName, BandMetadataField.SPOTIFY_URL, candidate.spotifyUrl().orElse(null), sourceName, sourceUrl, candidateName, fields, proposals, confidence);
    }

    private void addCandidateProposals(
            String bandName,
            String sourceName,
            String candidateName,
            BandMetadataProviderCandidate candidate,
            OptionalSource sourceUrl,
            Set<BandMetadataField> fields,
            List<BandMetadataProposal> proposals,
            int confidence
    ) {
        addProposal(bandName, BandMetadataField.BIOGRAPHY, candidate.biography().orElse(null), sourceName, sourceUrl, candidateName, fields, proposals, confidence);
        addProposal(bandName, BandMetadataField.IMAGE_URL, candidate.imageUrl().orElse(null), sourceName, sourceUrl, candidateName, fields, proposals, confidence);
        addProposal(bandName, BandMetadataField.YOUTUBE_URL, candidate.youtubeUrl().orElse(null), sourceName, sourceUrl, candidateName, fields, proposals, confidence);
        addProposal(bandName, BandMetadataField.SPOTIFY_URL, candidate.spotifyUrl().orElse(null), sourceName, sourceUrl, candidateName, fields, proposals, confidence);
    }

    private void addProposal(
            String bandName,
            BandMetadataField field,
            String value,
            String sourceName,
            OptionalSource sourceUrl,
            String candidateName,
            Set<BandMetadataField> fields,
            List<BandMetadataProposal> proposals,
            int confidence
    ) {
        if (!fields.contains(field) || value == null || value.isBlank()) {
            return;
        }
        proposals.add(new BandMetadataProposal(bandName, field, value, sourceName, sourceUrl.value(), candidateName, confidence));
    }

    private Set<BandMetadataField> withoutProposedFields(Set<BandMetadataField> fields, List<BandMetadataProposal> proposals) {
        if (fields.isEmpty()) {
            return EnumSet.noneOf(BandMetadataField.class);
        }
        Set<BandMetadataField> remaining = EnumSet.copyOf(fields);
        for (BandMetadataProposal proposal : proposals) {
            if (OWN_CATALOG.equals(proposal.sourceName())) {
                remaining.remove(proposal.field());
            }
        }
        return remaining;
    }

    private Set<BandMetadataField> missingFields(Band band) {
        Set<BandMetadataField> fields = EnumSet.noneOf(BandMetadataField.class);
        if (band.biography().isEmpty()) {
            fields.add(BandMetadataField.BIOGRAPHY);
        }
        if (band.imageUrl().isEmpty()) {
            fields.add(BandMetadataField.IMAGE_URL);
        }
        if (band.youtubeUrl().isEmpty()) {
            fields.add(BandMetadataField.YOUTUBE_URL);
        }
        if (band.spotifyUrl().isEmpty()) {
            fields.add(BandMetadataField.SPOTIFY_URL);
        }
        return fields;
    }

    private record OptionalSource(java.util.Optional<String> value) {
        static OptionalSource empty() {
            return new OptionalSource(java.util.Optional.empty());
        }
    }
}
