package be.wacken.planner.application;

import be.wacken.planner.domain.*;
import java.util.*;
import java.util.stream.Collectors;

/** Separate audit CSV; the existing normal ratings CSV contract is unchanged. */
public final class ExportRatingCorrectionsCsvUseCase {
    private final RatingCorrectionRepository corrections;
    public ExportRatingCorrectionsCsvUseCase(RatingCorrectionRepository corrections){
        this.corrections=Objects.requireNonNull(corrections);
    }
    public String export(String user){
        if(user==null||user.isBlank())throw new DomainValidationException("Sign in before exporting your correction history.");
        StringBuilder csv=new StringBuilder("operation_id,festival_id,festival_name,band_name,rating_kind,previous_value,new_value,user,original_recorded_at,corrected_at\n");
        corrections.findByUser(user).stream().filter(a->a.userName().equals(user))
            .sorted(Comparator.comparing(RatingCorrection::correctedAt).thenComparing(RatingCorrection::operationId))
            .forEach(a->csv.append(List.of(a.operationId(),a.festivalId(),a.festivalName(),a.band().name(),a.kind().name(),
                String.valueOf(a.previousValue()),String.valueOf(a.newValue()),a.userName(),
                a.originalRecordedAt().map(Object::toString).orElse(""),a.correctedAt().toString())
                .stream().map(this::cell).collect(Collectors.joining(","))).append('\n'));
        return csv.toString();
    }
    private String cell(String value){
        if(value.contains(",")||value.contains("\"")||value.contains("\n")||value.contains("\r"))
            return "\""+value.replace("\"","\"\"")+"\"";
        return value;
    }
}
