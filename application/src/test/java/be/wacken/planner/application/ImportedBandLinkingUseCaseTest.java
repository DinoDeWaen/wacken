package be.wacken.planner.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalLineupEntry;
import be.wacken.planner.domain.FestivalLineupRepository;
import be.wacken.planner.domain.FestivalRepository;

final class ImportedBandLinkingUseCaseTest {
    @Test
    void findsCaseInsensitiveCandidateForAnyGivenDay() {
        FakeFestivalRepository festivals = new FakeFestivalRepository(List.of(Festival.active("summer-breeze-2027", "Summer Breeze 2027")));
        FakeLineupRepository lineups = new FakeLineupRepository(List.of(
                new FestivalLineupEntry("summer-breeze-2027", new Band("Any Given Day"), "Any Given Day")
        ));
        FakeBandRepository bands = new FakeBandRepository(List.of(
                new Band("Any Given Day"),
                new Band("Any given Day", Optional.of("Wacken bio"), Optional.of("https://image.test/agd.jpg"), Optional.of("https://youtube.test/agd"), Optional.of("https://open.spotify.com/artist/agd"))
        ));

        List<BandLinkCandidate> candidates = new ReviewImportedBandLinksUseCase(festivals, lineups, bands).review();

        assertEquals(1, candidates.size());
        assertEquals("Any Given Day", candidates.get(0).uploadedDisplayName());
        assertEquals("Any Given Day", candidates.get(0).currentBandName());
        assertEquals(List.of("Any given Day"), candidates.get(0).candidateBandNames());
    }

    @Test
    void manualSearchTermCanFindCatalogCandidates() {
        FakeFestivalRepository festivals = new FakeFestivalRepository(List.of(Festival.active("summer-breeze-2027", "Summer Breeze 2027")));
        FakeLineupRepository lineups = new FakeLineupRepository(List.of());
        FakeBandRepository bands = new FakeBandRepository(List.of(new Band("Any given Day"), new Band("Angelus Apatrida")));

        BandLinkCandidate candidate = new ReviewImportedBandLinksUseCase(festivals, lineups, bands)
                .search("Any Given Day", "Any Given Day", "given day");

        assertEquals(List.of("Any given Day"), candidate.candidateBandNames());
    }

    @Test
    void confirmedLinkReusesExistingBandIdentityAndMetadata() {
        FakeFestivalRepository festivals = new FakeFestivalRepository(List.of(Festival.active("summer-breeze-2027", "Summer Breeze 2027")));
        FakeLineupRepository lineups = new FakeLineupRepository(List.of(
                new FestivalLineupEntry("summer-breeze-2027", new Band("Any Given Day"), "Any Given Day")
        ));
        Band goldenSource = new Band(
                "Any given Day",
                Optional.of("Wacken bio"),
                Optional.of("https://image.test/agd.jpg"),
                Optional.of("https://youtube.test/agd"),
                Optional.of("https://open.spotify.com/artist/agd")
        );
        FakeBandRepository bands = new FakeBandRepository(List.of(new Band("Any Given Day"), goldenSource));

        BandLinkResult result = new LinkImportedBandUseCase(festivals, lineups, bands)
                .link("Any Given Day", "Any Given Day", "Any given Day");

        assertTrue(result.success());
        FestivalLineupEntry linked = lineups.findByFestival("summer-breeze-2027").get(0);
        assertEquals("Any given Day", linked.band().name());
        assertEquals("Any Given Day", linked.uploadedDisplayName());
        assertEquals(goldenSource, linked.band());
    }

    @Test
    void noMatchLeavesUploadedBandLinkedAsItWas() {
        FakeFestivalRepository festivals = new FakeFestivalRepository(List.of(Festival.active("summer-breeze-2027", "Summer Breeze 2027")));
        FakeLineupRepository lineups = new FakeLineupRepository(List.of(
                new FestivalLineupEntry("summer-breeze-2027", new Band("Unknown Act"), "Unknown Act")
        ));

        BandLinkResult result = new LinkImportedBandUseCase(festivals, lineups, new FakeBandRepository(List.of(new Band("Known Act"))))
                .link("Unknown Act", "Unknown Act", "");

        assertTrue(result.success());
        assertEquals("No match selected for Unknown Act.", result.message());
        assertEquals("Unknown Act", lineups.findByFestival("summer-breeze-2027").get(0).band().name());
    }

    @Test
    void metadataEnrichmentCopiesMissingFieldsWithoutOverwritingExistingValues() {
        Band partial = new Band(
                "Any Given Day",
                Optional.of("Keep this bio"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
        Band goldenSource = new Band(
                "Any given Day",
                Optional.of("Do not overwrite with this bio"),
                Optional.of("https://image.test/agd.jpg"),
                Optional.of("https://youtube.test/agd"),
                Optional.of("https://open.spotify.com/artist/agd")
        );
        FakeBandRepository bands = new FakeBandRepository(List.of(partial, goldenSource));

        BandMetadataEnrichmentResult result = new EnrichBandMetadataFromCatalogUseCase(bands).enrichMissingMetadata();

        assertEquals(1, result.updatedBands());
        Band enriched = bands.findByName("Any Given Day").orElseThrow();
        assertEquals(Optional.of("Keep this bio"), enriched.biography());
        assertEquals(Optional.of("https://image.test/agd.jpg"), enriched.imageUrl());
        assertEquals(Optional.of("https://youtube.test/agd"), enriched.youtubeUrl());
        assertEquals(Optional.of("https://open.spotify.com/artist/agd"), enriched.spotifyUrl());
        assertFalse(result.externalLookupConfigured());
    }

    private static final class FakeFestivalRepository implements FestivalRepository {
        private final List<Festival> festivals = new ArrayList<>();

        FakeFestivalRepository(List<Festival> festivals) {
            this.festivals.addAll(festivals);
        }

        @Override
        public List<Festival> findAll() {
            return List.copyOf(festivals);
        }

        @Override
        public void save(Festival festival) {
            festivals.removeIf(existing -> existing.id().equals(festival.id()));
            festivals.add(festival);
        }
    }

    private static final class FakeLineupRepository implements FestivalLineupRepository {
        private final List<FestivalLineupEntry> entries = new ArrayList<>();

        FakeLineupRepository(List<FestivalLineupEntry> entries) {
            this.entries.addAll(entries);
        }

        @Override
        public void saveAllForFestival(String festivalId, List<FestivalLineupEntry> entries) {
            this.entries.removeIf(entry -> entry.festivalId().equals(festivalId));
            this.entries.addAll(entries);
        }

        @Override
        public List<FestivalLineupEntry> findByFestival(String festivalId) {
            return entries.stream().filter(entry -> entry.festivalId().equals(festivalId)).toList();
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
