package be.wacken.planner.application;

import java.util.Comparator;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.FestivalLineupEntry;
import be.wacken.planner.domain.FestivalLineupRepository;
import be.wacken.planner.domain.FestivalLifecycle;
import be.wacken.planner.domain.FestivalPlanningRatingRepository;
import be.wacken.planner.domain.FestivalRepository;
import be.wacken.planner.domain.PersonalBandRatingEvent;
import be.wacken.planner.domain.PersonalBandRatingHistoryRepository;
import be.wacken.planner.domain.RealRatingRepository;
import be.wacken.planner.domain.SavedRating;
import be.wacken.planner.domain.SavedFestivalPlanningRating;

public final class ViewArchivedFestivalHistoryUseCase {
    private static final String LEGACY_WACKEN_FESTIVAL_ID = "wacken-2026";

    private final FestivalRepository festivals;
    private final FestivalLineupRepository lineups;
    private final FestivalPlanningRatingRepository planningRatings;
    private final PersonalBandRatingHistoryRepository personalRatings;
    private final RealRatingRepository legacyRealRatings;

    public ViewArchivedFestivalHistoryUseCase(
            FestivalRepository festivals,
            FestivalLineupRepository lineups,
            FestivalPlanningRatingRepository planningRatings,
            PersonalBandRatingHistoryRepository personalRatings
    ) {
        this(festivals, lineups, planningRatings, personalRatings, new EmptyRealRatingRepository());
    }

    public ViewArchivedFestivalHistoryUseCase(
            FestivalRepository festivals,
            FestivalLineupRepository lineups,
            FestivalPlanningRatingRepository planningRatings,
            PersonalBandRatingHistoryRepository personalRatings,
            RealRatingRepository legacyRealRatings
    ) {
        this.festivals = festivals;
        this.lineups = lineups;
        this.planningRatings = planningRatings;
        this.personalRatings = personalRatings;
        this.legacyRealRatings = legacyRealRatings;
    }

    public ArchivedFestivalHistory show(String userName, String festivalId) {
        Map<String, String> festivalNames = festivals.findAll()
                .stream()
                .collect(Collectors.toMap(festival -> festival.id(), festival -> festival.name()));
        List<FestivalLineupEntry> entries = lineups.findByFestival(festivalId);
        List<SavedFestivalPlanningRating> planning = planningRatings.findByFestival(festivalId);
        List<PersonalBandRatingEvent> festivalPersonalEvents = personalRatings.findByUserAndFestival(userName, festivalId);
        List<SavedRating> legacyRealRatingFallbacks = legacyRealRatingFallbacks(userName, festivalId);
        TreeSet<String> bandNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        entries.stream().map(entry -> entry.band().name()).forEach(bandNames::add);
        planning.stream().map(rating -> rating.band().name()).forEach(bandNames::add);
        festivalPersonalEvents.stream().map(event -> event.band().name()).forEach(bandNames::add);
        legacyRealRatingFallbacks.stream().map(rating -> rating.band().name()).forEach(bandNames::add);

        List<String> bands = List.copyOf(bandNames);
        List<ArchivedPlanningRatingItem> planningItems = planning.stream()
                .sorted(Comparator.comparing((SavedFestivalPlanningRating rating) -> rating.band().name(), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(SavedFestivalPlanningRating::userName, String.CASE_INSENSITIVE_ORDER))
                .map(rating -> new ArchivedPlanningRatingItem(rating.band().name(), rating.userName(), rating.rating().value()))
                .collect(Collectors.toList());
        List<PersonalRatingHistoryItem> history = personalHistory(userName, festivalId, bands, festivalNames, festivalPersonalEvents, legacyRealRatingFallbacks);
        boolean readOnly = FestivalLifecycle.archivedFestivals(festivals.findAll())
                .stream()
                .anyMatch(festival -> festival.id().equals(festivalId));
        return new ArchivedFestivalHistory(
                festivalId,
                Optional.ofNullable(festivalNames.get(festivalId)).orElse(festivalId),
                bands,
                planningItems,
                history,
                readOnly
        );
    }

    private List<SavedRating> legacyRealRatingFallbacks(String userName, String festivalId) {
        if (!LEGACY_WACKEN_FESTIVAL_ID.equals(festivalId)) {
            return List.of();
        }
        return legacyRealRatings.findAll()
                .stream()
                .filter(rating -> rating.userName().equals(userName))
                .filter(rating -> rating.rating().value() > 0)
                .collect(Collectors.toList());
    }

    private List<PersonalRatingHistoryItem> personalHistory(
            String userName,
            String festivalId,
            List<String> bandNames,
            Map<String, String> festivalNames,
            List<PersonalBandRatingEvent> festivalPersonalEvents,
            List<SavedRating> legacyRealRatingFallbacks
    ) {
        Map<String, PersonalBandRatingEvent> eventsById = new LinkedHashMap<>();
        festivalPersonalEvents.forEach(event -> eventsById.put(event.id(), event));
        bandNames.stream()
                .map(Band::new)
                .flatMap(band -> personalRatings.findByUserAndBand(userName, band).stream())
                .forEach(event -> eventsById.put(event.id(), event));
        List<PersonalRatingHistoryItem> history = eventsById.values()
                .stream()
                .map(event -> new PersonalRatingHistoryItem(
                        event.band().name(),
                        event.festivalId().map(festivalNames::get),
                        event.rating().value(),
                        event.createdAt()
                ))
                .sorted(Comparator.comparing(PersonalRatingHistoryItem::createdAt).reversed())
                .collect(Collectors.toList());
        TreeSet<String> bandsWithHistory = history.stream()
                .map(PersonalRatingHistoryItem::bandName)
                .collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));
        List<PersonalRatingHistoryItem> legacyFallbacks = legacyRealRatingFallbacks.stream()
                .filter(rating -> bandNames.stream().anyMatch(name -> name.equalsIgnoreCase(rating.band().name())))
                .filter(rating -> !bandsWithHistory.contains(rating.band().name()))
                .map(rating -> new PersonalRatingHistoryItem(
                        rating.band().name(),
                        Optional.ofNullable(festivalNames.get(festivalId)),
                        rating.rating().value(),
                        Instant.EPOCH
                ))
                .collect(Collectors.toList());
        if (legacyFallbacks.isEmpty()) {
            return history;
        }
        return java.util.stream.Stream.concat(history.stream(), legacyFallbacks.stream())
                .sorted(Comparator.comparing(PersonalRatingHistoryItem::createdAt).reversed())
                .collect(Collectors.toList());
    }

    public record ArchivedPlanningRatingItem(String bandName, String userName, int rating) {
        public String displayText() {
            return bandName + " - " + userName + ": " + rating + " stars";
        }
    }

    public record ArchivedFestivalHistory(
            String festivalId,
            String festivalName,
            List<String> bandNames,
            List<ArchivedPlanningRatingItem> planningRatings,
            List<PersonalRatingHistoryItem> personalRatings,
            boolean readOnly
    ) {
    }

    private static final class EmptyRealRatingRepository implements RealRatingRepository {
        @Override
        public void save(String userName, Band band, be.wacken.planner.domain.Rating rating) {
        }

        @Override
        public Optional<be.wacken.planner.domain.Rating> findByUserAndBand(String userName, Band band) {
            return Optional.empty();
        }
    }
}
