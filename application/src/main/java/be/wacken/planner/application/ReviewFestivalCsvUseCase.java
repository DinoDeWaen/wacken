package be.wacken.planner.application;

import be.wacken.planner.domain.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/** Reviews a local master-data import. No rating repository or remote write port is involved. */
public final class ReviewFestivalCsvUseCase {
    private enum Kind { BAND, STAGE, PERFORMANCE, DISTANCE, FOOD }
    public record Change(String key,String kind,String identity,Map<String,String> before,
                         Map<String,String> after,List<String> errors) {
        public Change {
            before=Collections.unmodifiableMap(new LinkedHashMap<>(before));
            after=Collections.unmodifiableMap(new LinkedHashMap<>(after));errors=List.copyOf(errors);
        }
        public boolean valid(){return errors.isEmpty();}
        public String operation(){return before.isEmpty()?"ADD":after.isEmpty()?"REMOVE":"CHANGE";}
    }
    public static final class Review {
        private final Snapshot baseline;
        private final List<Change> changes;
        private Review(Snapshot baseline,List<Change> changes){this.baseline=baseline;this.changes=List.copyOf(changes);}
        public List<Change> changes(){return changes;}
        public String festivalId(){return baseline.festival.id();}
        public String festivalName(){return baseline.festival.name();}
    }
    private record Snapshot(Festival festival,List<Band> catalog,List<Stage> stages,List<Performance> performances,
                            List<StageDistance> distances,List<FoodOption> food,List<FestivalLineupEntry> lineup) {}
    private final BandRepository bands;
    private final StageRepository stages;
    private final PerformanceRepository performances;
    private final StageDistanceRepository distances;
    private final FoodOptionRepository food;
    private final FestivalRepository festivals;
    private final FestivalLineupRepository lineups;
    private final Consumer<Runnable> transaction;
    public ReviewFestivalCsvUseCase(BandRepository bands,StageRepository stages,PerformanceRepository performances,
            StageDistanceRepository distances,FoodOptionRepository food,FestivalRepository festivals,
            FestivalLineupRepository lineups,Consumer<Runnable> transaction) {
        this.bands=Objects.requireNonNull(bands);this.stages=Objects.requireNonNull(stages);
        this.performances=Objects.requireNonNull(performances);this.distances=Objects.requireNonNull(distances);
        this.food=Objects.requireNonNull(food);this.festivals=Objects.requireNonNull(festivals);
        this.lineups=Objects.requireNonNull(lineups);this.transaction=Objects.requireNonNull(transaction);
    }
    public Review review(FestivalCsvFiles files) {
        Snapshot baseline=snapshot();List<Change> changes=new ArrayList<>();
        Map<String,String> bandNames=names("bands.csv",files.bandsCsv(),"band_id");
        Map<String,String> stageNames=names("stages.csv",files.stagesCsv(),"stage_id");
        baseline.catalog.forEach(b->bandNames.putIfAbsent(b.name(),b.name()));
        baseline.stages.forEach(s->stageNames.putIfAbsent(s.name(),s.name()));
        Map<Kind,Map<String,Map<String,String>>> old=rows(baseline);
        preview(Kind.BAND,"bands.csv",files.bandsCsv(),baseline,old,bandNames,stageNames,changes);
        preview(Kind.STAGE,"stages.csv",files.stagesCsv(),baseline,old,bandNames,stageNames,changes);
        preview(Kind.PERFORMANCE,"performances.csv",files.performancesCsv(),baseline,old,bandNames,stageNames,changes);
        preview(Kind.DISTANCE,"distances.csv",files.distancesCsv(),baseline,old,bandNames,stageNames,changes);
        preview(Kind.FOOD,"food.csv",files.foodCsv(),baseline,old,bandNames,stageNames,changes);
        return new Review(baseline,changes);
    }
    public ImportFestivalCsvResult apply(Review review,Set<String> accepted) {
        Objects.requireNonNull(review);Set<String> choices=Set.copyOf(accepted);
        if(choices.isEmpty())return ImportFestivalCsvResult.imported();
        Map<String,Change> known=review.changes.stream().collect(Collectors.toMap(Change::key,c->c));
        if(choices.stream().anyMatch(key->!known.containsKey(key)||!known.get(key).valid()))
            return ImportFestivalCsvResult.failure(List.of("Only valid reviewed rows may be accepted."));
        Map<Kind,Map<String,Map<String,String>>> combined=rows(review.baseline);
        for(Change change:review.changes)if(choices.contains(change.key())) {
            Kind kind=Kind.valueOf(change.kind());
            if(change.after().isEmpty())combined.get(kind).remove(change.identity());
            else combined.get(kind).put(change.identity(),change.after());
        }
        List<String> errors=validate(combined);
        if(!errors.isEmpty())return ImportFestivalCsvResult.failure(errors);
        try {
            transaction.accept(()->{
                if(!review.baseline.equals(snapshot()))
                    throw new IllegalStateException("Festival data changed since review. Reload the files and review again.");
                publish(review.baseline,combined);
            });
            return ImportFestivalCsvResult.imported();
        } catch(RuntimeException failure) {
            return ImportFestivalCsvResult.failure(List.of(failure.getMessage()==null?"Import could not be saved. Existing data was retained.":failure.getMessage()));
        }
    }
    private Snapshot snapshot() {
        Festival festival=FestivalLifecycle.activeFestival(festivals.findAll())
            .orElseThrow(()->new DomainValidationException("Create an active festival before reviewing a local import."));
        return new Snapshot(festival,sorted(bands.findAll(),Band::name),sorted(stages.findAll(),Stage::name),
            sorted(performances.findAll(),p->p.festivalId()+"\n"+p.band().name()+"\n"+p.start()),
            sorted(distances.findAll(),d->d.from().name()+"\n"+d.to().name()),
            sorted(food.findAll(),FoodOption::name),sorted(lineups.findByFestival(festival.id()),e->e.band().name()));
    }
    private static <T> List<T> sorted(List<T> values,java.util.function.Function<T,String> key) {
        return values.stream().sorted(Comparator.comparing(key)).toList();
    }
    private Map<Kind,Map<String,Map<String,String>>> rows(Snapshot state) {
        Map<Kind,Map<String,Map<String,String>>> result=new EnumMap<>(Kind.class);
        for(Kind kind:Kind.values())result.put(kind,new LinkedHashMap<>());
        Map<String,Band> catalog=state.catalog.stream().collect(Collectors.toMap(Band::name,b->b));
        state.lineup.forEach(e->put(result,Kind.BAND,bandFields(catalog.getOrDefault(e.band().name(),e.band()))));
        state.stages.forEach(s->put(result,Kind.STAGE,Map.of("name",s.name())));
        state.performances.stream().filter(p->p.festivalId().equals(state.festival.id())).forEach(p->put(result,Kind.PERFORMANCE,
            fields("band",p.band().name(),"stage",p.stage().name(),"start",p.start().toString(),"end",p.end().toString())));
        state.distances.forEach(d->put(result,Kind.DISTANCE,fields("from",d.from().name(),"to",d.to().name(),"minutes",Integer.toString(d.walkingMinutes()))));
        state.food.forEach(f->put(result,Kind.FOOD,fields("name",f.name(),"near_stage","")));
        return result;
    }
    private void preview(Kind kind,String file,String csv,Snapshot baseline,
            Map<Kind,Map<String,Map<String,String>>> old,Map<String,String> bandNames,Map<String,String> stageNames,List<Change> changes) {
        if(csv.isBlank())return; // An unselected companion file is not a request to remove its data.
        List<FestivalCsvReader.Row> input;
        try {
            List<String> headers=FestivalCsvReader.headers(file,csv);
            if(!headers.containsAll(requiredHeaders(kind))||new HashSet<>(headers).size()!=headers.size())
                throw new IllegalArgumentException("Missing or duplicate required headers in "+file+".");
            input=FestivalCsvReader.reviewRows(file,csv);
        } catch(IllegalArgumentException error) {
            changes.add(invalid(file,0,Map.of("file",file),error.getMessage()));return;
        }
        Map<String,Map<String,String>> proposed=new LinkedHashMap<>();
        Map<String,Integer> identities=new HashMap<>();
        Map<Integer,Map<String,String>> parsed=new LinkedHashMap<>();
        Map<Integer,String> failures=new HashMap<>();
        Map<String,Long> csvIds=input.stream().filter(r->!idColumn(kind).isEmpty())
            .collect(Collectors.groupingBy(r->r.value(idColumn(kind)),Collectors.counting()));
        for(var row:input)try {
            if(!idColumn(kind).isEmpty() && csvIds.getOrDefault(need(row,idColumn(kind)),0L)>1)
                throw new IllegalArgumentException("Duplicate "+idColumn(kind)+" "+row.value(idColumn(kind))+".");
            Map<String,String> value=parse(kind,row,baseline,bandNames,stageNames);
            parsed.put(row.rowNumber(),value);identities.merge(identity(kind,value),1,Integer::sum);
        } catch(IllegalArgumentException|java.time.DateTimeException error) {failures.put(row.rowNumber(),error.getMessage());}
        boolean complete=failures.isEmpty();
        for(var row:input) {
            Map<String,String> value=parsed.get(row.rowNumber());
            if(value==null) {changes.add(invalid(file,row.rowNumber(),row.valuesByHeader(),failures.get(row.rowNumber())));continue;}
            String identity=identity(kind,value);
            if(identities.get(identity)>1) {
                changes.add(invalid(file,row.rowNumber(),row.valuesByHeader(),"Duplicate persisted identity "+identity+"."));complete=false;continue;
            }
            proposed.put(identity,value);
            Map<String,String> before=old.get(kind).getOrDefault(identity,Map.of());
            if(!before.equals(value))changes.add(new Change(key(kind,identity),kind.name(),identity,before,value,List.of()));
        }
        // A malformed/duplicate row cannot make a missing baseline row look like a deletion.
        if(complete)old.get(kind).forEach((id,before)->{
            if(!proposed.containsKey(id))changes.add(new Change(key(kind,id),kind.name(),id,before,Map.of(),List.of()));
        });
    }
    private Map<String,String> parse(Kind kind,FestivalCsvReader.Row row,Snapshot baseline,
                                    Map<String,String> bandNames,Map<String,String> stageNames) {
        return switch(kind) {
            case BAND -> {
                need(row,"band_id");String name=need(row,"name");
                Optional<String> bio=BiographyText.readable(optional(first(row.value("biography"),row.value("biography_html"))));
                String spotify=row.value("spotify_artist_id");
                Band candidate=new Band(name,bio,optional(first(row.value("image_url"),row.value("thumbnail_url"))),
                    optional(row.value("youtube_url")),optional(spotify).map(id->"https://open.spotify.com/artist/"+id));
                Band existing=baseline.catalog.stream().filter(b->b.name().equals(name)).findFirst().orElse(null);
                if(existing!=null)candidate=new Band(name,keep(existing.biography(),candidate.biography()),
                    keep(existing.imageUrl(),candidate.imageUrl()),keep(existing.youtubeUrl(),candidate.youtubeUrl()),keep(existing.spotifyUrl(),candidate.spotifyUrl()));
                yield bandFields(candidate);
            }
            case STAGE -> {need(row,"stage_id");yield Map.of("name",new Stage(need(row,"name")).name());}
            case PERFORMANCE -> {
                need(row,"performance_id");
                String band=resolve(bandNames,need(row,"band_id"),"band"),stage=resolve(stageNames,need(row,"stage_id"),"stage");
                LocalDateTime start=LocalDateTime.parse(need(row,"start_at")),end=LocalDateTime.parse(need(row,"end_at"));
                new Performance(new Band(band),new Stage(stage),start,end,baseline.festival.id());
                yield fields("band",band,"stage",stage,"start",start.toString(),"end",end.toString());
            }
            case DISTANCE -> {
                String from=resolve(stageNames,need(row,"from_stage_id"),"stage"),to=resolve(stageNames,need(row,"to_stage_id"),"stage");
                int minutes=Integer.parseInt(need(row,"walking_minutes"));
                StageDistance distance=StageDistance.between(new Stage(from),new Stage(to),minutes);
                yield fields("from",from,"to",to,"minutes",Integer.toString(distance.walkingMinutes()));
            }
            case FOOD -> {
                need(row,"food_id");String near=row.value("near_stage_id");
                yield fields("name",new FoodOption(need(row,"name")).name(),"near_stage",near.isBlank()?"":resolve(stageNames,near,"stage"));
            }
        };
    }
    private List<String> validate(Map<Kind,Map<String,Map<String,String>>> data) {
        List<String> errors=new ArrayList<>();var lineup=data.get(Kind.BAND);var stage=data.get(Kind.STAGE);
        List<Map<String,String>> shows=new ArrayList<>(data.get(Kind.PERFORMANCE).values());
        for(var show:shows) {
            if(!lineup.containsKey(show.get("band")))errors.add("Performance references a band absent from the accepted lineup: "+show.get("band"));
            if(!stage.containsKey(show.get("stage")))errors.add("Performance references a stage absent from accepted data: "+show.get("stage"));
        }
        for(int a=0;a<shows.size();a++)for(int b=a+1;b<shows.size();b++) {
            var first=shows.get(a);var second=shows.get(b);
            if(first.get("stage").equals(second.get("stage"))&&LocalDateTime.parse(first.get("start")).isBefore(LocalDateTime.parse(second.get("end")))
                    &&LocalDateTime.parse(second.get("start")).isBefore(LocalDateTime.parse(first.get("end"))))
                errors.add("Accepted performances overlap on "+first.get("stage")+".");
        }
        for(var distance:data.get(Kind.DISTANCE).values())
            if(!stage.containsKey(distance.get("from"))||!stage.containsKey(distance.get("to")))errors.add("Accepted walking distance references a removed stage.");
        for(var option:data.get(Kind.FOOD).values())
            if(!option.get("near_stage").isBlank()&&!stage.containsKey(option.get("near_stage")))errors.add("Accepted food row references a removed stage.");
        return errors;
    }
    private void publish(Snapshot baseline,Map<Kind,Map<String,Map<String,String>>> data) {
        Map<String,Band> catalog=new LinkedHashMap<>();baseline.catalog.forEach(b->catalog.put(b.name(),b));
        data.get(Kind.BAND).values().forEach(row->catalog.put(row.get("name"),band(row)));
        bands.replaceAll(new ArrayList<>(catalog.values())); // Never delete canonical bands for a lineup removal.
        stages.replaceAll(data.get(Kind.STAGE).values().stream().map(r->new Stage(r.get("name"))).toList());
        List<Performance> all=baseline.performances.stream().filter(p->!p.festivalId().equals(baseline.festival.id())).collect(Collectors.toCollection(ArrayList::new));
        data.get(Kind.PERFORMANCE).values().forEach(r->all.add(new Performance(new Band(r.get("band")),new Stage(r.get("stage")),
            LocalDateTime.parse(r.get("start")),LocalDateTime.parse(r.get("end")),baseline.festival.id())));
        performances.replaceAll(all);
        distances.replaceAll(data.get(Kind.DISTANCE).values().stream().map(r->StageDistance.between(new Stage(r.get("from")),new Stage(r.get("to")),Integer.parseInt(r.get("minutes")))).toList());
        food.replaceAll(data.get(Kind.FOOD).values().stream().map(r->new FoodOption(r.get("name"))).toList());
        Map<String,FestivalLineupEntry> previous=baseline.lineup.stream().collect(Collectors.toMap(e->e.band().name(),e->e));
        lineups.saveAllForFestival(baseline.festival.id(),data.get(Kind.BAND).keySet().stream()
            .map(name->previous.getOrDefault(name,new FestivalLineupEntry(baseline.festival.id(),catalog.get(name),name))).toList());
    }
    private static Band band(Map<String,String> r){return new Band(r.get("name"),optional(r.get("biography")),optional(r.get("image")),optional(r.get("youtube")),optional(r.get("spotify")));}
    private static Map<String,String> bandFields(Band b){return fields("name",b.name(),"biography",b.biography().orElse(""),"image",b.imageUrl().orElse(""),"youtube",b.youtubeUrl().orElse(""),"spotify",b.spotifyUrl().orElse(""));}
    private static Optional<String> optional(String value){return value==null||value.isBlank()?Optional.empty():Optional.of(value);}
    private static Optional<String> keep(Optional<String> existing,Optional<String> proposed){return existing.isPresent()?existing:proposed;}
    private static String first(String a,String b){return a.isBlank()?b:a;}
    private static String need(FestivalCsvReader.Row row,String key){String value=row.value(key);if(value.isBlank())throw new IllegalArgumentException(key+" must not be blank.");return value;}
    private static String resolve(Map<String,String> names,String id,String type){String name=names.get(id);if(name==null||name.isBlank())throw new IllegalArgumentException("Unknown or ambiguous "+type+" ID "+id+". Select a valid companion CSV to resolve its name.");return name;}
    private static String key(Kind kind,String id){return kind.name()+":"+id;}
    private static String identity(Kind kind,Map<String,String> row){return switch(kind){case BAND,STAGE,FOOD->row.get("name");case PERFORMANCE->row.get("band")+" @ "+row.get("start");case DISTANCE->quoted(row.get("from"))+" → "+quoted(row.get("to"));};}
    private static String quoted(String value){return "\""+value.replace("\\","\\\\").replace("\"","\\\"")+"\"";}
    private static void put(Map<Kind,Map<String,Map<String,String>>> rows,Kind kind,Map<String,String> value){rows.get(kind).put(identity(kind,value),value);}
    private static Map<String,String> fields(String... values){Map<String,String> row=new LinkedHashMap<>();for(int i=0;i<values.length;i+=2)row.put(values[i],values[i+1]);return row;}
    private static Change invalid(String file,int line,Map<String,String> values,String message){String id=file+" row "+line;return new Change("INVALID:"+id,"INVALID",id,Map.of(),values,List.of(message==null?"Invalid row.":message));}
    private static String idColumn(Kind kind){return switch(kind){case BAND->"band_id";case STAGE->"stage_id";case PERFORMANCE->"performance_id";case FOOD->"food_id";case DISTANCE->"";};}
    private static List<String> requiredHeaders(Kind kind){return switch(kind){
        case BAND->List.of("band_id","name");case STAGE->List.of("stage_id","name");
        case PERFORMANCE->List.of("performance_id","band_id","stage_id","start_at","end_at");
        case DISTANCE->List.of("from_stage_id","to_stage_id","walking_minutes");case FOOD->List.of("food_id","name");};}
    private static Map<String,String> names(String file,String csv,String id) {
        Map<String,String> result=new HashMap<>();Set<String> ambiguous=new HashSet<>();
        try {for(var row:FestivalCsvReader.reviewRows(file,csv)) {
            String key=row.value(id),name=row.value("name");
            if(!key.isBlank()&&!name.isBlank()) {if(result.putIfAbsent(key,name)!=null)ambiguous.add(key);}
        }}catch(IllegalArgumentException malformed){return result;}
        ambiguous.forEach(idValue->result.put(idValue,""));return result;
    }
}
