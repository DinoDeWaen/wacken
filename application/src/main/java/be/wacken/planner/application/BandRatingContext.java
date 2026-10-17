package be.wacken.planner.application;

import be.wacken.planner.domain.*;
import java.time.Instant;
import java.util.*;

public record BandRatingContext(String userName,Band band,Optional<Festival> festival,
                                Optional<PersonalBandRating> current,Optional<Rating> planning,
                                Optional<FestivalAfterRating> after,boolean sourceDeleted) {
    public List<String> lines() {
        List<String> lines=new ArrayList<>();lines.add("Member: "+userName);
        lines.add("Established band rating: "+current.map(r->Integer.toString(r.rating().value())).orElse("Unrated"));
        current.ifPresent(r->lines.add("Established source: "+(r.sourceFestivalName().isBlank()?r.sourceFestivalId():r.sourceFestivalName())
            +" ["+r.sourceFestivalId()+"]"+(sourceDeleted?" (deleted festival)":"")+" · established "+date(r.establishedAt())));
        lines.add("Festival: "+festival.map(f->f.name()+" ["+f.id()+"]").orElse("No selected festival"));
        lines.add("Festival planning rating: "+score(planning));
        lines.add("Festival after-rating: "+score(after.map(FestivalAfterRating::rating)));
        after.ifPresent(r->lines.add("After-rating state recorded: "+date(r.recordedAt())+(r.requiresReview()?" · review required":"")));
        return List.copyOf(lines);
    }
    private static String score(Optional<Rating> rating){return rating.filter(r->r.value()>0).map(r->Integer.toString(r.value())).orElse("Unrated");}
    private static String date(Instant value){return Instant.EPOCH.equals(value)?"unknown":value.toString();}
}
