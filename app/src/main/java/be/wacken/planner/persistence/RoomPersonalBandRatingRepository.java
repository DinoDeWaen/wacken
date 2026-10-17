package be.wacken.planner.persistence;

import be.wacken.planner.domain.*;
import java.time.Instant;
import java.util.*;

public final class RoomPersonalBandRatingRepository implements PersonalBandRatingRepository {
    private final RoomRatingStateDao states;
    public RoomPersonalBandRatingRepository(WackenDatabase database){states=database.ratingStates();}
    @Override public Optional<PersonalBandRating> findByUserAndBand(String user,Band band){
        return Optional.ofNullable(states.current(user,band.name())).map(this::domain);
    }
    @Override public List<PersonalBandRating> findByUser(String user){
        return states.currentByUser(user).stream().map(this::domain).collect(java.util.stream.Collectors.toList());
    }
    private PersonalBandRating domain(RoomPersonalBandRating r){
        return new PersonalBandRating(r.userName,new Band(r.bandName),Rating.of(r.value),r.sourceFestivalId,
                r.sourceFestivalName,r.sourceEventId,Instant.parse(r.establishedAt));
    }
}
