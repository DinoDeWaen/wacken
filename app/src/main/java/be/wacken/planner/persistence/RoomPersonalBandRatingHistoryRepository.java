package be.wacken.planner.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.PersonalBandRatingEvent;
import be.wacken.planner.domain.PersonalBandRatingHistoryRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RealRatingRepository;
import be.wacken.planner.domain.SavedRating;
import be.wacken.planner.PersonalBandRatingSyncLocalStore;

public final class RoomPersonalBandRatingHistoryRepository implements PersonalBandRatingSyncLocalStore {
    private static final String LEGACY_WACKEN_FESTIVAL_ID = "wacken-2026";
    private static final Instant UNKNOWN_LEGACY_CREATED_AT = Instant.EPOCH;

    private final RoomPersonalBandRatingEventDao events;

    public RoomPersonalBandRatingHistoryRepository(WackenDatabase database) {
        this.events = database.personalBandRatingEvents();
    }

    @Override
    public void save(PersonalBandRatingEvent event) {
        events.save(new RoomPersonalBandRatingEvent(
                event.id(),
                event.userName(),
                event.band().name(),
                event.festivalId().orElse(null),
                event.rating().value(),
                event.createdAt().toString(),
                "PENDING"
        ));
    }

    public void saveSynced(PersonalBandRatingEvent event) {
        events.save(new RoomPersonalBandRatingEvent(
                event.id(),
                event.userName(),
                event.band().name(),
                event.festivalId().orElse(null),
                event.rating().value(),
                event.createdAt().toString(),
                "SYNCED"
        ));
    }

    public List<PersonalBandRatingEvent> findPending(String userName) {
        return events.findPendingByUser(userName).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<PersonalBandRatingEvent> findByUserAndBand(String userName, Band band) {
        return events.findByUserAndBand(userName, band.name()).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<PersonalBandRatingEvent> findByUserAndFestival(String userName, String festivalId) {
        return events.findByUserAndFestival(userName, festivalId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    public void backfillLegacyWackenRealRatings(RealRatingRepository legacyRealRatings) {
        for (SavedRating rating : legacyRealRatings.findAll()) {
            if (rating.rating().value() <= 0 || hasReliableWackenHistory(rating.userName(), rating.band())) {
                continue;
            }
            save(new PersonalBandRatingEvent(
                    legacyEventId(rating.userName(), rating.band()),
                    rating.userName(),
                    rating.band(),
                    Optional.of(LEGACY_WACKEN_FESTIVAL_ID),
                    rating.rating(),
                    UNKNOWN_LEGACY_CREATED_AT
            ));
        }
    }

    private boolean hasReliableWackenHistory(String userName, Band band) {
        List<PersonalBandRatingEvent> wackenEvents = findByUserAndBand(userName, band).stream()
                .filter(event -> event.festivalId().filter(LEGACY_WACKEN_FESTIVAL_ID::equals).isPresent())
                .toList();
        boolean hasUserCreatedEvent = wackenEvents.stream()
                .anyMatch(event -> !event.id().equals(legacyEventId(userName, band)));
        boolean hasUnknownDateLegacyEvent = wackenEvents.stream()
                .anyMatch(event -> event.id().equals(legacyEventId(userName, band))
                        && UNKNOWN_LEGACY_CREATED_AT.equals(event.createdAt()));
        return hasUserCreatedEvent || hasUnknownDateLegacyEvent;
    }

    private String legacyEventId(String userName, Band band) {
        return userName + ":" + band.name() + ":legacy-real";
    }

    private PersonalBandRatingEvent toDomain(RoomPersonalBandRatingEvent row) {
        return new PersonalBandRatingEvent(
                row.id,
                row.userName,
                new Band(row.bandName),
                Optional.ofNullable(row.festivalId),
                Rating.of(row.value),
                Instant.parse(row.createdAt)
        );
    }
}
