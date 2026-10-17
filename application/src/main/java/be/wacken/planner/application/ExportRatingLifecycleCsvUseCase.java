package be.wacken.planner.application;

import be.wacken.planner.domain.*;
import java.time.Instant;
import java.util.*;

/** Version2 own cached rating state and history; existing CSV contracts remain separate. */
public final class ExportRatingLifecycleCsvUseCase {
    public static final String HEADER="schema_version,record_type,user_id,band_name,festival_id,festival_name,festival_status,festival_name_snapshot,rating,source_festival_id,source_festival_name,source_event_id,recorded_at,established_at,corrected_at,original_recorded_at,previous_rating,new_rating,operation_id,requires_review,youtube_url,spotify_url";
    private final BandRepository bands;
    private final FestivalRepository festivals;
    private final FestivalLineupRepository lineups;
    private final FestivalPlanningRatingRepository planning;
    private final FestivalAfterRatingRepository after;
    private final PersonalBandRatingRepository current;
    private final PersonalBandRatingHistoryRepository history;
    private final RatingCorrectionRepository corrections;

    public ExportRatingLifecycleCsvUseCase(BandRepository bands,FestivalRepository festivals,FestivalLineupRepository lineups,
            FestivalPlanningRatingRepository planning,FestivalAfterRatingRepository after,PersonalBandRatingRepository current,
            PersonalBandRatingHistoryRepository history,RatingCorrectionRepository corrections) {
        this.bands=Objects.requireNonNull(bands);this.festivals=Objects.requireNonNull(festivals);
        this.lineups=Objects.requireNonNull(lineups);this.planning=Objects.requireNonNull(planning);
        this.after=Objects.requireNonNull(after);this.current=Objects.requireNonNull(current);
        this.history=Objects.requireNonNull(history);this.corrections=Objects.requireNonNull(corrections);
    }

    public String export(String user) {
        if(user==null||user.isBlank())throw new DomainValidationException("Sign in to export personal rating context.");
        Map<String,Band> catalog=new TreeMap<>();
        bands.findAll().forEach(b->catalog.put(b.name(),b));
        Map<String,Festival> retained=new TreeMap<>();
        festivals.findAll().forEach(f->retained.put(f.id(),f));
        List<String[]> rows=new ArrayList<>();
        for(var r:current.findByUser(user)) {
            if(!user.equals(r.userName()))continue;
            var row=row("ESTABLISHED_BAND",user,r.band(),catalog);
            row[8]=Integer.toString(r.rating().value());row[9]=r.sourceFestivalId();
            row[10]=r.sourceFestivalName();row[11]=r.sourceEventId();row[13]=date(r.establishedAt());rows.add(row);
        }
        for(var f:retained.values()) {
            Set<String> names=new TreeSet<>();
            lineups.findByFestival(f.id()).forEach(entry->names.add(entry.band().name()));
            for(String name:names) {
                Band band=catalog.getOrDefault(name,new Band(name));
                var plan=row("FESTIVAL_PLANNING",user,band,catalog);festival(plan,f.id(),retained);
                plan[8]=planning.findByUserFestivalAndBand(user,f.id(),band).map(r->Integer.toString(r.value())).orElse("");
                rows.add(plan);
                var result=row("FESTIVAL_AFTER",user,band,catalog);festival(result,f.id(),retained);
                after.findByUserFestivalAndBand(user,f.id(),band)
                    .filter(r->r.userName().equals(user)&&r.festivalId().equals(f.id())&&r.band().name().equals(name))
                    .ifPresent(r->{result[8]=Integer.toString(r.rating().value());result[12]=date(r.recordedAt());
                        result[18]=r.operationId();result[19]=Boolean.toString(r.requiresReview());});
                rows.add(result);
            }
        }
        for(var event:history.findByUser(user)) {
            if(!user.equals(event.userName()))continue;
            var row=row("HISTORY_AFTER",user,event.band(),catalog);
            festival(row,event.festivalId().orElse(""),retained);row[7]=event.festivalNameSnapshot().orElse("");
            row[8]=Integer.toString(event.rating().value());row[12]=date(event.createdAt());row[18]=event.id();rows.add(row);
        }
        for(var correction:corrections.findByUser(user)) {
            if(!user.equals(correction.userName()))continue;
            var row=row("CORRECTION_"+correction.kind().name(),user,correction.band(),catalog);
            festival(row,correction.festivalId(),retained);row[7]=correction.festivalName();
            row[14]=date(correction.correctedAt());row[15]=correction.originalRecordedAt().map(ExportRatingLifecycleCsvUseCase::date).orElse("");
            row[16]=Integer.toString(correction.previousValue());row[17]=Integer.toString(correction.newValue());
            row[18]=correction.operationId();rows.add(row);
        }
        List<String> encoded=rows.stream().map(ExportRatingLifecycleCsvUseCase::encode).sorted().toList();
        return HEADER+"\n"+String.join("\n",encoded)+(encoded.isEmpty()?"":"\n");
    }
    private static String[] row(String kind,String user,Band band,Map<String,Band> catalog) {
        String[] row=new String[22];Arrays.fill(row,"");row[0]="2";row[1]=kind;row[2]=user;row[3]=band.name();
        Band canonical=catalog.get(band.name());
        if(canonical!=null){row[20]=canonical.youtubeUrl().orElse("");row[21]=canonical.spotifyUrl().orElse("");}
        return row;
    }
    private static void festival(String[] row,String id,Map<String,Festival> retained) {
        row[4]=id;Festival festival=retained.get(id);
        row[5]=festival==null?"":festival.name();
        row[6]=festival==null?(id.isEmpty()?"UNKNOWN":"DELETED"):(festival.isActive()?"ACTIVE":"ARCHIVED");
    }
    private static String date(Instant value){return Instant.EPOCH.equals(value)?"":value.toString();}
    private static String encode(String[] row) {
        return Arrays.stream(row).map(value->value.contains(",")||value.contains("\"")||value.contains("\n")||value.contains("\r")
                ?"\""+value.replace("\"","\"\"")+"\"":value).collect(java.util.stream.Collectors.joining(","));
    }
}
