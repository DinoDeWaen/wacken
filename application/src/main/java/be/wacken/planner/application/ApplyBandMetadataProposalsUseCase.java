package be.wacken.planner.application;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;

public final class ApplyBandMetadataProposalsUseCase {
    private final BandRepository bands;

    public ApplyBandMetadataProposalsUseCase(BandRepository bands) {
        this.bands = Objects.requireNonNull(bands, "bands must not be null");
    }

    public ApplyBandMetadataProposalsResult apply(String bandName, List<BandMetadataProposal> acceptedProposals) {
        Band band = bands.findByName(bandName).orElse(null);
        if (band == null) {
            return ApplyBandMetadataProposalsResult.failure("Band no longer exists.");
        }

        Optional<String> biography = band.biography();
        Optional<String> imageUrl = band.imageUrl();
        Optional<String> youtubeUrl = band.youtubeUrl();
        Optional<String> spotifyUrl = band.spotifyUrl();
        int updated = 0;
        int skipped = 0;

        for (BandMetadataProposal proposal : acceptedProposals) {
            if (!proposal.bandName().equals(band.name())) {
                skipped++;
                continue;
            }
            switch (proposal.field()) {
                case BIOGRAPHY -> {
                    if (biography.isPresent()) {
                        skipped++;
                    } else {
                        biography = Optional.of(proposal.proposedValue());
                        updated++;
                    }
                }
                case IMAGE_URL -> {
                    if (imageUrl.isPresent()) {
                        skipped++;
                    } else {
                        imageUrl = Optional.of(proposal.proposedValue());
                        updated++;
                    }
                }
                case YOUTUBE_URL -> {
                    if (youtubeUrl.isPresent()) {
                        skipped++;
                    } else {
                        youtubeUrl = Optional.of(proposal.proposedValue());
                        updated++;
                    }
                }
                case SPOTIFY_URL -> {
                    if (spotifyUrl.isPresent()) {
                        skipped++;
                    } else {
                        spotifyUrl = Optional.of(proposal.proposedValue());
                        updated++;
                    }
                }
            }
        }

        if (updated > 0) {
            bands.save(new Band(band.name(), biography, imageUrl, youtubeUrl, spotifyUrl));
        }
        return ApplyBandMetadataProposalsResult.success(updated, skipped);
    }
}
