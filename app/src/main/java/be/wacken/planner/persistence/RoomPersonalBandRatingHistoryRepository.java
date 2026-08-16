package be.wacken.planner.persistence;

import java.time.Instant;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
        backfillLegacyWackenRealRatings(legacyRealRatings, Optional.empty());
    }

    public void backfillLegacyWackenRealRatings(RealRatingRepository legacyRealRatings, Optional<String> currentUserName) {
        for (SavedRating rating : recoverableLegacyRatings(legacyRealRatings.findAll(), currentUserName)) {
            String targetUserName = currentUserName.orElse(rating.userName());
            if (rating.rating().value() <= 0 || hasReliableWackenHistory(targetUserName, rating.band())) {
                continue;
            }
            deleteStaleLegacyEvents(targetUserName, rating.band());
            save(new PersonalBandRatingEvent(
                    legacyEventId(targetUserName, rating.band()),
                    targetUserName,
                    rating.band(),
                    Optional.of(LEGACY_WACKEN_FESTIVAL_ID),
                    rating.rating(),
                    UNKNOWN_LEGACY_CREATED_AT
            ));
        }
    }

    private List<SavedRating> recoverableLegacyRatings(List<SavedRating> ratings, Optional<String> currentUserName) {
        List<SavedRating> positiveRatings = ratings.stream()
                .filter(rating -> rating.rating().value() > 0)
                .collect(Collectors.toList());
        if (currentUserName.isEmpty()) {
            return positiveRatings;
        }
        return positiveRatings.stream()
                .collect(Collectors.toMap(
                        rating -> rating.band().name().toLowerCase(java.util.Locale.ROOT),
                        rating -> rating,
                        (first, second) -> first.userName().equals(currentUserName.orElseThrow()) ? first : second
                ))
                .values()
                .stream()
                .collect(Collectors.toList());
    }

    private boolean hasReliableWackenHistory(String userName, Band band) {
        List<PersonalBandRatingEvent> wackenEvents = findByUserAndBand(userName, band).stream()
                .filter(event -> event.festivalId().filter(LEGACY_WACKEN_FESTIVAL_ID::equals).isPresent())
                .collect(Collectors.toList());
        boolean hasUserCreatedEvent = wackenEvents.stream()
                .anyMatch(event -> !event.id().equals(legacyEventId(userName, band))
                        && !event.id().equals(staleLegacyEventId(userName, band)));
        boolean hasUnknownDateLegacyEvent = wackenEvents.stream()
                .anyMatch(event -> event.id().equals(legacyEventId(userName, band))
                        && UNKNOWN_LEGACY_CREATED_AT.equals(event.createdAt()));
        return hasUserCreatedEvent || hasUnknownDateLegacyEvent;
    }

    private void deleteStaleLegacyEvents(String userName, Band band) {
        events.deleteById(staleLegacyEventId(userName, band));
    }

    private String legacyEventId(String userName, Band band) {
        return UUID.nameUUIDFromBytes(staleLegacyEventId(userName, band).getBytes(StandardCharsets.UTF_8)).toString();
    }

    private String staleLegacyEventId(String userName, Band band) {
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
