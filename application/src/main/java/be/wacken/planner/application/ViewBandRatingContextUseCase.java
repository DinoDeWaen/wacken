package be.wacken.planner.application;

import be.wacken.planner.domain.*;
import java.util.*;

/** Own established opinion and exact selected-festival values; never a latest-event fallback. */
public final class ViewBandRatingContextUseCase {
    private final BandRepository bands;
    private final FestivalRepository festivals;
    private final FestivalLineupRepository lineups;
    private final FestivalPlanningRatingRepository planning;
    private final FestivalAfterRatingRepository after;
    private final PersonalBandRatingRepository current;
    public ViewBandRatingContextUseCase(BandRepository bands,FestivalRepository festivals,FestivalLineupRepository lineups,
            FestivalPlanningRatingRepository planning,FestivalAfterRatingRepository after,PersonalBandRatingRepository current) {
        this.bands=Objects.requireNonNull(bands);this.festivals=Objects.requireNonNull(festivals);this.lineups=Objects.requireNonNull(lineups);
        this.planning=Objects.requireNonNull(planning);this.after=Objects.requireNonNull(after);this.current=Objects.requireNonNull(current);
    }
    public Optional<BandRatingContext> show(String user,String festivalId,String name) {
        if(user==null||user.isBlank())throw new DomainValidationException("Sign in to view personal rating context.");
        if(name==null||name.isBlank())return Optional.empty();
        return bands.findByName(name).map(band->{
            List<Festival> all=festivals.findAll();
            Optional<Festival> selected=all.stream().filter(f->f.id().equals(festivalId)).findFirst();
            boolean linked=selected.isPresent()&&lineups.findByFestival(festivalId).stream().anyMatch(e->e.band().name().equals(band.name()));
            Optional<PersonalBandRating> established=current.findByUserAndBand(user,band)
                .filter(r->r.userName().equals(user)&&r.band().name().equals(band.name()));
            Optional<Rating> plan=linked?planning.findByUserFestivalAndBand(user,festivalId,band):Optional.empty();
            Optional<FestivalAfterRating> result=linked?after.findByUserFestivalAndBand(user,festivalId,band)
                .filter(r->r.userName().equals(user)&&r.festivalId().equals(festivalId)&&r.band().name().equals(band.name())):Optional.empty();
            boolean deleted=established.isPresent()&&all.stream().noneMatch(f->f.id().equals(established.orElseThrow().sourceFestivalId()));
            return new BandRatingContext(user,band,selected,established,plan,result,deleted);
        });
    }
}
