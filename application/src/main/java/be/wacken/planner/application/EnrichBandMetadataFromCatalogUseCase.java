package be.wacken.planner.application;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;

public final class EnrichBandMetadataFromCatalogUseCase {
    private final BandRepository bands;

    public EnrichBandMetadataFromCatalogUseCase(BandRepository bands) {
        this.bands = Objects.requireNonNull(bands, "bands must not be null");
    }

    public BandMetadataEnrichmentResult enrichMissingMetadata() {
        List<Band> catalog = bands.findAll();
        int updated = 0;
        for (Band band : catalog) {
            Band enriched = enrichFromCatalog(band, catalog);
            if (!enriched.equals(band)) {
                bands.save(enriched);
                updated++;
            }
        }
        int remaining = (int) bands.findAll().stream().filter(this::hasMissingMetadata).count();
        return new BandMetadataEnrichmentResult(updated, remaining, false);
    }

    private Band enrichFromCatalog(Band band, List<Band> catalog) {
        Optional<String> biography = band.biography();
        Optional<String> imageUrl = band.imageUrl();
        Optional<String> youtubeUrl = band.youtubeUrl();
        Optional<String> spotifyUrl = band.spotifyUrl();
        for (Band candidate : catalog) {
            if (candidate.name().equals(band.name()) || !BandNameMatcher.likelySameBand(candidate.name(), band.name())) {
                continue;
            }
            biography = firstPresent(biography, candidate.biography());
            imageUrl = firstPresent(imageUrl, candidate.imageUrl());
            youtubeUrl = firstPresent(youtubeUrl, candidate.youtubeUrl());
            spotifyUrl = firstPresent(spotifyUrl, candidate.spotifyUrl());
        }
        return new Band(band.name(), biography, imageUrl, youtubeUrl, spotifyUrl);
    }

    private Optional<String> firstPresent(Optional<String> current, Optional<String> candidate) {
        return current.isPresent() ? current : candidate;
    }

    private boolean hasMissingMetadata(Band band) {
        return band.biography().isEmpty()
                || band.imageUrl().isEmpty()
                || band.youtubeUrl().isEmpty()
                || band.spotifyUrl().isEmpty();
    }
}
