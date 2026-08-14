package be.wacken.planner.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.PersonalBandRatingEvent;
import be.wacken.planner.domain.PersonalBandRatingHistoryRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.PersonalBandRatingSyncLocalStore;

public final class RoomPersonalBandRatingHistoryRepository implements PersonalBandRatingSyncLocalStore {
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
