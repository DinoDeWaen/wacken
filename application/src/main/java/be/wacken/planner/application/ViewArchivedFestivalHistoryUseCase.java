package be.wacken.planner.application;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.FestivalLineupEntry;
import be.wacken.planner.domain.FestivalLineupRepository;
import be.wacken.planner.domain.FestivalLifecycle;
import be.wacken.planner.domain.FestivalPlanningRatingRepository;
import be.wacken.planner.domain.FestivalRepository;
import be.wacken.planner.domain.PersonalBandRatingHistoryRepository;

public final class ViewArchivedFestivalHistoryUseCase {
    private final FestivalRepository festivals;
    private final FestivalLineupRepository lineups;
    private final FestivalPlanningRatingRepository planningRatings;
    private final PersonalBandRatingHistoryRepository personalRatings;

    public ViewArchivedFestivalHistoryUseCase(
            FestivalRepository festivals,
            FestivalLineupRepository lineups,
            FestivalPlanningRatingRepository planningRatings,
            PersonalBandRatingHistoryRepository personalRatings
    ) {
        this.festivals = festivals;
        this.lineups = lineups;
        this.planningRatings = planningRatings;
        this.personalRatings = personalRatings;
    }

    public ArchivedFestivalHistory show(String userName, String festivalId) {
        Map<String, String> festivalNames = festivals.findAll()
                .stream()
                .collect(Collectors.toMap(festival -> festival.id(), festival -> festival.name()));
        List<FestivalLineupEntry> entries = lineups.findByFestival(festivalId);
        List<String> bands = entries.stream()
                .map(entry -> entry.band().name())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
        List<PersonalRatingHistoryItem> history = entries.stream()
                .map(FestivalLineupEntry::band)
                .flatMap(band -> personalRatings.findByUserAndBand(userName, band).stream())
                .map(event -> new PersonalRatingHistoryItem(
                        event.band().name(),
                        event.festivalId().map(festivalNames::get),
                        event.rating().value(),
                        event.createdAt()
                ))
                .sorted(Comparator.comparing(PersonalRatingHistoryItem::createdAt).reversed())
                .toList();
        int planningRatingCount = planningRatings.findByFestival(festivalId).size();
        boolean readOnly = FestivalLifecycle.archivedFestivals(festivals.findAll())
                .stream()
                .anyMatch(festival -> festival.id().equals(festivalId));
        return new ArchivedFestivalHistory(
                festivalId,
                Optional.ofNullable(festivalNames.get(festivalId)).orElse(festivalId),
                bands,
                history,
                planningRatingCount,
                readOnly
        );
    }

    public record ArchivedFestivalHistory(
            String festivalId,
            String festivalName,
            List<String> bandNames,
            List<PersonalRatingHistoryItem> personalRatings,
            int planningRatingCount,
            boolean readOnly
    ) {
    }
}
