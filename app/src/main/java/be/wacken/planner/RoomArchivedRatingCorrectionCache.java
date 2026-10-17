package be.wacken.planner;

import be.wacken.planner.domain.*;
import be.wacken.planner.persistence.*;
import java.util.*;

/** Correction acknowledgement, explicit pending recovery and audit publish together. */
final class RoomArchivedRatingCorrectionCache implements SupabaseArchivedRatingCorrectionGateway.Cache {
    private final WackenDatabase database;
    private final String group,user;
    RoomArchivedRatingCorrectionCache(WackenDatabase database,String group,String user){
        this.database=database;this.group=group;this.user=user;
    }
    @Override public SupabaseArchivedRatingCorrectionGateway.Pending capture(ArchivedRatingReview expected){
        return database.runInTransaction(()->pending(expected));
    }
    private SupabaseArchivedRatingCorrectionGateway.Pending pending(ArchivedRatingReview target){
        var after=database.ratingStates().after(user,target.festivalId(),target.band().name());
        var planning=database.festivalPlanningRatings().findExact(group,user,target.festivalId(),target.band().name());
        return new SupabaseArchivedRatingCorrectionGateway.Pending(
                after!=null&&"PENDING".equals(after.syncStatus)?after.operationId:null,
                planning!=null&&"PENDING".equals(planning.syncStatus)?planning.value:null);
    }
    @Override public void apply(SupabaseArchivedRatingCorrectionGateway.Snapshot snapshot){
        ArchivedRatingReview current=snapshot.current();
        RatingCorrection audit=snapshot.receipt();
        if(!user.equals(current.userName())||!user.equals(audit.userName())
                ||!current.festivalId().equals(audit.festivalId())||!current.band().name().equals(audit.band().name())
                ||current.kind()!=audit.kind())throw new IllegalStateException("Correction snapshot belongs to another target.");
        database.runInTransaction(()->{
            var now=pending(current);
            if(current.kind()==ArchivedRatingKind.AFTER){
                if(!Objects.equals(now.afterOperation(),snapshot.pending().afterOperation()))
                    throw new IllegalStateException("A newer after-rating remains pending. Review it before retrying.");
                if(now.afterOperation()!=null){
                    database.ratingStates().acknowledge(user,current.festivalId(),current.band().name(),now.afterOperation());
                    // Keep the original offline fact/date; explicit correction resolves its upload.
                    database.personalBandRatingEvents().resolveReviewedPending(user,current.festivalId(),current.band().name(),now.afterOperation());
                }
            }else{
                if(!Objects.equals(now.planningValue(),snapshot.pending().planningValue()))
                    throw new IllegalStateException("A newer planning rating remains pending. Review it before retrying.");
                new RoomFestivalPlanningRatingRepository(database).saveSynced(new SavedFestivalPlanningRating(
                        group,user,current.festivalId(),current.band(),Rating.of(current.value())));
            }
            var ratings=new RoomFestivalAfterRatingRepository(database);
            ratings.mergeRemote(snapshot.after(),user);
            ratings.replaceCurrent(snapshot.established(),user);
            if(current.kind()==ArchivedRatingKind.AFTER){
                snapshot.after().stream().filter(a->a.userName().equals(user)
                        &&a.festivalId().equals(current.festivalId())&&a.band().name().equals(current.band().name()))
                    .filter(a->a.rating().value()>0).findFirst().ifPresent(a->
                        new RoomPersonalBandRatingHistoryRepository(database).saveSynced(new PersonalBandRatingEvent(
                            a.operationId(),user,a.band(),Optional.of(a.festivalId()),a.rating(),a.recordedAt(),Optional.of(audit.festivalName()))));
            }
            new RoomRatingCorrectionRepository(database).append(audit);
        });
    }
    @Override public void appendAudits(List<RatingCorrection> audits){
        if(audits.stream().anyMatch(a->!user.equals(a.userName())))throw new IllegalStateException("Audit belongs to another member.");
        database.runInTransaction(()->{
            var repository=new RoomRatingCorrectionRepository(database);
            audits.forEach(repository::append);
        });
    }
}
