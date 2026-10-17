package be.wacken.planner.application;

import be.wacken.planner.domain.*;
import java.util.*;

/** A detail screen keeps its selected festival when the group's active festival changes. */
public final class FestivalPlanningRatingView implements RatingRepository {
    private final FestivalRepository festivals;
    private final FestivalLineupRepository lineups;
    private final FestivalPlanningRatingRepository planning;
    private final String group,festival;
    public FestivalPlanningRatingView(FestivalRepository festivals,FestivalLineupRepository lineups,
                                     FestivalPlanningRatingRepository planning,String group,String festival) {
        this.festivals=festivals;this.lineups=lineups;this.planning=planning;this.group=group;this.festival=festival;
    }
    @Override public void save(String user,Band band,Rating value) {
        if(festivals.findAll().stream().noneMatch(f->f.id().equals(festival)&&f.isActive()))
            throw new DomainValidationException("This festival is no longer active. Reopen the selected festival before rating.");
        if(lineups.findByFestival(festival).stream().noneMatch(e->e.band().name().equals(band.name())))
            throw new DomainValidationException("This band is no longer in the selected festival lineup.");
        planning.save(group,user,festival,band,value);
    }
    @Override public Optional<Rating> findByUserAndBand(String user,Band band) {
        return planning.findByUserFestivalAndBand(user,festival,band);
    }
    @Override public List<SavedRating> findAll() {
        return planning.findByFestival(festival).stream().map(r->new SavedRating(r.userName(),r.band(),r.rating())).toList();
    }
}
