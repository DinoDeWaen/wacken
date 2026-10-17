package be.wacken.planner.persistence;

import be.wacken.planner.RatingStateSyncStore;
import be.wacken.planner.domain.*;
import java.time.Instant;
import java.util.*;

public final class RoomFestivalAfterRatingRepository implements RatingStateSyncStore {
    private final WackenDatabase database;
    public RoomFestivalAfterRatingRepository(WackenDatabase database){this.database=database;}
    @Override public void save(FestivalAfterRating rating){
        database.runInTransaction(()->{
            database.ratingStates().saveAfter(row(rating,"PENDING"));
            if(rating.rating().value()>0){
                RoomFestival festival=database.festivals().findAll().stream()
                        .filter(f->f.id.equals(rating.festivalId())).findFirst().orElse(null);
                database.personalBandRatingEvents().save(new RoomPersonalBandRatingEvent(
                        rating.operationId(),rating.userName(),rating.band().name(),rating.festivalId(),
                        festival==null?null:festival.name,rating.rating().value(),rating.recordedAt().toString(),"PENDING"));
            }
        });
    }
    @Override public Optional<FestivalAfterRating> findByUserFestivalAndBand(String user,String festival,Band band){
        return Optional.ofNullable(database.ratingStates().after(user,festival,band.name())).map(this::domain);
    }
    @Override public List<FestivalAfterRating> findByUser(String user){
        return database.ratingStates().afterByUser(user).stream().map(this::domain).collect(java.util.stream.Collectors.toList());
    }
    @Override public List<FestivalAfterRating> findByFestival(String festival){
        return database.ratingStates().afterByFestival(festival).stream().map(this::domain).collect(java.util.stream.Collectors.toList());
    }
    @Override public List<FestivalAfterRating> findPending(String user){
        return database.ratingStates().pending(user).stream().map(this::domain).collect(java.util.stream.Collectors.toList());
    }
    @Override public void acknowledge(FestivalAfterRating expected){
        database.ratingStates().acknowledge(expected.userName(),expected.festivalId(),expected.band().name(),expected.operationId());
    }
    @Override public void mergeRemote(List<FestivalAfterRating> ratings,String user){
        if(ratings.stream().anyMatch(r->!r.userName().equals(user)))throw new IllegalArgumentException("Another member's after-rating cannot enter this pull.");
        database.runInTransaction(()->{
            database.ratingStates().clearSyncedAfter(user);
            for(FestivalAfterRating rating:ratings){
                RoomFestivalAfterRating local=database.ratingStates().after(user,rating.festivalId(),rating.band().name());
                if(local==null||!local.syncStatus.equals("PENDING"))database.ratingStates().saveAfter(row(rating,"SYNCED"));
            }
        });
    }
    @Override public void replaceCurrent(List<PersonalBandRating> ratings,String user){
        if(ratings.stream().anyMatch(r->!r.userName().equals(user)))throw new IllegalArgumentException("Another member's band rating cannot enter this pull.");
        database.runInTransaction(()->{
            database.ratingStates().clearCurrent(user);
            for(PersonalBandRating r:ratings)database.ratingStates().saveCurrent(new RoomPersonalBandRating(
                    user,r.band().name(),r.rating().value(),r.sourceFestivalId(),r.sourceFestivalName(),r.sourceEventId(),r.establishedAt().toString()));
        });
    }
    private RoomFestivalAfterRating row(FestivalAfterRating r,String status){
        return new RoomFestivalAfterRating(r.userName(),r.festivalId(),r.band().name(),r.operationId(),
                r.rating().value(),r.recordedAt().toString(),status,r.requiresReview());
    }
    private FestivalAfterRating domain(RoomFestivalAfterRating r){
        return new FestivalAfterRating(r.operationId,r.userName,r.festivalId,new Band(r.bandName),
                Rating.of(r.value),Instant.parse(r.recordedAt),r.requiresReview);
    }
}
