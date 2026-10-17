package be.wacken.planner.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;

final class BandMetadataSearchFrameworkUseCaseTest {
    @Test
    void proposesOnlyMissingFields() {
        FakeBandRepository bands = new FakeBandRepository(List.of(new Band(
                "Any Given Day",
                Optional.of("Keep this bio"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        )));
        FakeProvider provider = FakeProvider.configured("Fake source", List.of(new BandMetadataProviderCandidate(
                "Any Given Day",
                Optional.of("External bio"),
                Optional.of("https://images.test/agd.jpg"),
                Optional.of("https://youtube.test/agd"),
                Optional.of("https://spotify.test/agd"),
                Optional.of("https://source.test/agd"),
                82
        )));

        BandMetadataSearchResult result = new SearchBandMetadataUseCase(bands, List.of(provider)).search("Any Given Day");

        assertEquals(List.of(BandMetadataField.IMAGE_URL, BandMetadataField.YOUTUBE_URL, BandMetadataField.SPOTIFY_URL),
                result.proposals().stream().map(BandMetadataProposal::field).toList());
    }

    @Test
    void prefersOwnCatalogMetadataBeforeExternalProviders() {
        FakeBandRepository bands = new FakeBandRepository(List.of(
                new Band("Any Given Day"),
                new Band(
                        "Any given Day",
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.of("https://spotify.test/catalog")
                )
        ));
        FakeProvider provider = FakeProvider.configured("External source", List.of(new BandMetadataProviderCandidate(
                "Any Given Day",
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of("https://spotify.test/external"),
                Optional.of("https://source.test/agd"),
                75
        )));

        BandMetadataSearchResult result = new SearchBandMetadataUseCase(bands, List.of(provider)).search("Any Given Day");

        assertEquals(1, result.proposals().size());
        assertEquals("Own band database", result.proposals().get(0).sourceName());
        assertEquals("https://spotify.test/catalog", result.proposals().get(0).proposedValue());
    }

    @Test
    void acceptedProposalsAreRequiredBeforeBandIsChanged() {
        FakeBandRepository bands = new FakeBandRepository(List.of(new Band("Any Given Day")));
        BandMetadataProposal proposal = new BandMetadataProposal(
                "Any Given Day",
                BandMetadataField.IMAGE_URL,
                "https://images.test/agd.jpg",
                "Fake source",
                Optional.of("https://source.test/agd"),
                "Any Given Day",
                82
        );

        new SearchBandMetadataUseCase(bands, List.of(FakeProvider.configured("Fake source", List.of(new BandMetadataProviderCandidate(
                "Any Given Day",
                Optional.empty(),
                Optional.of("https://images.test/agd.jpg"),
                Optional.empty(),
                Optional.empty(),
                Optional.of("https://source.test/agd"),
                82
        ))))).search("Any Given Day");

        assertTrue(bands.findByName("Any Given Day").orElseThrow().imageUrl().isEmpty());

        ApplyBandMetadataProposalsResult result = new ApplyBandMetadataProposalsUseCase(bands)
                .apply("Any Given Day", List.of(proposal));

        assertEquals(1, result.updatedFields());
        assertEquals(Optional.of("https://images.test/agd.jpg"), bands.findByName("Any Given Day").orElseThrow().imageUrl());
    }

    @Test
    void rejectedOrMissingProposalsLeaveBandUnchanged() {
        FakeBandRepository bands = new FakeBandRepository(List.of(new Band("Unknown Act")));

        ApplyBandMetadataProposalsResult result = new ApplyBandMetadataProposalsUseCase(bands)
                .apply("Unknown Act", List.of());

        assertEquals(0, result.updatedFields());
        assertEquals(new Band("Unknown Act"), bands.findByName("Unknown Act").orElseThrow());
    }

    @Test
    void unavailableProvidersAreReportedWithoutChangingBands() {
        FakeBandRepository bands = new FakeBandRepository(List.of(new Band("Unknown Act")));

        BandMetadataSearchResult result = new SearchBandMetadataUseCase(bands, List.of(FakeProvider.unconfigured("Spotify")))
                .search("Unknown Act");

        assertEquals(List.of("Spotify is not configured."), result.unavailableProviders());
        assertTrue(result.proposals().isEmpty());
        assertEquals(new Band("Unknown Act"), bands.findByName("Unknown Act").orElseThrow());
    }

    @Test
    void searchRunSummarizesDoneNeededReviewAndProviderStatus() {
        FakeBandRepository bands = new FakeBandRepository(List.of(
                new Band(
                        "Complete Band",
                        Optional.of("Bio"),
                        Optional.of("https://images.test/complete.jpg"),
                        Optional.of("https://youtube.test/complete"),
                        Optional.of("https://spotify.test/complete")
                ),
                new Band("Needs Review"),
                new Band("Still Missing")
        ));
        MatchingProvider provider = new MatchingProvider("Needs Review", new BandMetadataProviderCandidate(
                "Needs Review",
                Optional.of("External bio"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of("https://source.test/needs-review"),
                80
        ));
        FakeProvider unconfigured = FakeProvider.unconfigured("Spotify");

        BandMetadataSearchRun run = new SearchBandMetadataUseCase(bands, List.of(provider, unconfigured))
                .searchMissingMetadataRun();

        assertEquals(3, run.totalBands());
        assertEquals(1, run.completeBands());
        assertEquals(2, run.bandsMissingMetadata());
        assertEquals(1, run.bandsNeedingReview());
        assertEquals(1, run.proposalCount());
        assertEquals(List.of("Still Missing"), run.bandsWithoutProposals());
        assertEquals(List.of("Spotify is not configured."), run.providerMessages());
    }

    @Test
    void applyingProposalDoesNotOverwriteExistingMetadata() {
        FakeBandRepository bands = new FakeBandRepository(List.of(new Band(
                "Any Given Day",
                Optional.of("Keep this bio"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        )));
        BandMetadataProposal overwriteAttempt = new BandMetadataProposal(
                "Any Given Day",
                BandMetadataField.BIOGRAPHY,
                "Replacement bio",
                "Fake source",
                Optional.empty(),
                "Any Given Day",
                70
        );

        ApplyBandMetadataProposalsResult result = new ApplyBandMetadataProposalsUseCase(bands)
                .apply("Any Given Day", List.of(overwriteAttempt));

        assertEquals(0, result.updatedFields());
        assertEquals(1, result.skippedFields());
        assertEquals(Optional.of("Keep this bio"), bands.findByName("Any Given Day").orElseThrow().biography());
    }

    private static final class FakeProvider implements BandMetadataLookupProvider {
        private final String name;
        private final boolean configured;
        private final List<BandMetadataProviderCandidate> candidates;

        private FakeProvider(String name, boolean configured, List<BandMetadataProviderCandidate> candidates) {
            this.name = name;
            this.configured = configured;
            this.candidates = candidates;
        }

        static FakeProvider configured(String name, List<BandMetadataProviderCandidate> candidates) {
            return new FakeProvider(name, true, candidates);
        }

        static FakeProvider unconfigured(String name) {
            return new FakeProvider(name, false, List.of());
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public boolean configured() {
            return configured;
        }

        @Override
        public List<BandMetadataProviderCandidate> search(String bandName) {
            return candidates;
        }
    }

    private static final class MatchingProvider implements BandMetadataLookupProvider {
        private final String matchingBandName;
        private final BandMetadataProviderCandidate candidate;

        MatchingProvider(String matchingBandName, BandMetadataProviderCandidate candidate) {
            this.matchingBandName = matchingBandName;
            this.candidate = candidate;
        }

        @Override
        public String name() {
            return "Fake source";
        }

        @Override
        public boolean configured() {
            return true;
        }

        @Override
        public List<BandMetadataProviderCandidate> search(String bandName) {
            return matchingBandName.equals(bandName) ? List.of(candidate) : List.of();
        }
    }

    private static final class FakeBandRepository implements BandRepository {
        private final List<Band> bands = new ArrayList<>();

        FakeBandRepository(List<Band> bands) {
            this.bands.addAll(bands);
        }

        @Override
        public void save(Band band) {
            bands.removeIf(existing -> existing.name().equals(band.name()));
            bands.add(band);
        }

        @Override
        public void replaceAll(List<Band> bands) {
            this.bands.clear();
            this.bands.addAll(bands);
        }

        @Override
        public Optional<Band> findByName(String name) {
            return bands.stream().filter(band -> band.name().equals(name)).findFirst();
        }

        @Override
        public List<Band> findAll() {
            return List.copyOf(bands);
        }
    }
}
