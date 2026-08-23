package be.wacken.planner.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.DomainValidationException;
import be.wacken.planner.domain.Festival;
import be.wacken.planner.domain.FestivalLifecycle;
import be.wacken.planner.domain.FestivalLineupEntry;
import be.wacken.planner.domain.FestivalLineupRepository;
import be.wacken.planner.domain.FestivalPlanningRatingRepository;
import be.wacken.planner.domain.FestivalRepository;
import be.wacken.planner.domain.PersonalBandRatingHistoryRepository;

public final class AddFestivalUseCase {
    private final FestivalRepository festivals;
    private final BandRepository bands;
    private final FestivalLineupRepository lineups;
    private final FestivalPlanningRatingRepository planningRatings;
    private final PersonalBandRatingHistoryRepository personalRatings;

    public AddFestivalUseCase(
            FestivalRepository festivals,
            BandRepository bands,
            FestivalLineupRepository lineups,
            FestivalPlanningRatingRepository planningRatings,
            PersonalBandRatingHistoryRepository personalRatings
    ) {
        this.festivals = Objects.requireNonNull(festivals, "festivals must not be null");
        this.bands = Objects.requireNonNull(bands, "bands must not be null");
        this.lineups = Objects.requireNonNull(lineups, "lineups must not be null");
        this.planningRatings = Objects.requireNonNull(planningRatings, "planningRatings must not be null");
        this.personalRatings = Objects.requireNonNull(personalRatings, "personalRatings must not be null");
    }

    public AddFestivalResult addFestival(String groupId, String userName, String festivalId, String festivalName, List<Band> uploadedBands) {
        try {
            if (FestivalLifecycle.activeFestival(festivals.findAll()).isPresent()) {
                return AddFestivalResult.failure("Archive the active festival before adding the next one.");
            }
            if (festivalName == null || festivalName.isBlank()) {
                return AddFestivalResult.failure("Festival name must not be blank.");
            }
            Festival festival = Festival.active(festivalId, festivalName);
            if (uploadedBands.isEmpty()) {
                return AddFestivalResult.failure("Select at least one band before adding the festival.");
            }
            festivals.save(festival);
            List<FestivalLineupEntry> entries = new ArrayList<>();
            int reused = 0;
            int created = 0;
            int prefilled = 0;
            for (Band uploadedBand : uploadedBands) {
                Band linkedBand = bands.findByName(uploadedBand.name()).orElse(null);
                if (linkedBand == null) {
                    linkedBand = uploadedBand;
                    bands.save(linkedBand);
                    created++;
                } else {
                    reused++;
                }
                entries.add(new FestivalLineupEntry(festival.id(), linkedBand, uploadedBand.name()));
                if (prefill(groupId, userName, festival, linkedBand)) {
                    prefilled++;
                }
            }
            lineups.saveAllForFestival(festival.id(), entries);
            return AddFestivalResult.added(reused, created, prefilled);
        } catch (DomainValidationException error) {
            return AddFestivalResult.failure(error.getMessage());
        }
    }

    private boolean prefill(String groupId, String userName, Festival festival, Band band) {
        return personalRatings.latestByUserAndBand(userName, band)
                .map(event -> {
                    planningRatings.save(groupId, userName, festival.id(), band, event.rating());
                    return true;
                })
                .orElse(false);
    }
}
