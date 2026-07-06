package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;
import be.wacken.planner.domain.RealRatingRepository;
import be.wacken.planner.domain.SavedRating;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ExportRatingsCsvUseCase {
    private static final String HEADER = "band_id,band_name,planning_rating,real_rating,group_ratings,stage,date,time,schedule_status";

    private final BandRepository bands;
    private final PerformanceRepository performances;
    private final RatingRepository planningRatings;
    private final RealRatingRepository realRatings;

    public ExportRatingsCsvUseCase(
            BandRepository bands,
            PerformanceRepository performances,
            RatingRepository planningRatings,
            RealRatingRepository realRatings
    ) {
        this.bands = Objects.requireNonNull(bands, "bands must not be null");
        this.performances = Objects.requireNonNull(performances, "performances must not be null");
        this.planningRatings = Objects.requireNonNull(planningRatings, "planningRatings must not be null");
        this.realRatings = Objects.requireNonNull(realRatings, "realRatings must not be null");
    }

    public String export(String userName) {
        List<Performance> allPerformances = performances.findAll();
        Map<String, List<Performance>> performancesByBand = allPerformances.stream()
                .collect(Collectors.groupingBy(performance -> performance.band().name()));
        Map<String, Band> exportBands = exportBands(allPerformances);

        StringBuilder csv = new StringBuilder(HEADER).append('\n');
        for (Band band : exportBands.values()) {
            List<Performance> bandPerformances = performancesByBand.getOrDefault(band.name(), List.of())
                    .stream()
                    .sorted(Comparator.comparing(Performance::start))
                    .toList();
            csv.append(row(userName, band, bandPerformances)).append('\n');
        }
        return csv.toString();
    }

    private Map<String, Band> exportBands(List<Performance> allPerformances) {
        Map<String, Band> exportBands = new LinkedHashMap<>();
        List<Band> localBands = new ArrayList<>(bands.findAll());
        localBands.sort(Comparator.comparing(Band::name, String.CASE_INSENSITIVE_ORDER));
        for (Band band : localBands) {
            exportBands.put(band.name(), band);
        }
        allPerformances.stream()
                .map(Performance::band)
                .sorted(Comparator.comparing(Band::name, String.CASE_INSENSITIVE_ORDER))
                .forEach(band -> exportBands.putIfAbsent(band.name(), band));
        return exportBands;
    }

    private String row(String userName, Band band, List<Performance> bandPerformances) {
        return String.join(",",
                csv(band.name()),
                csv(band.name()),
                csv(ratingValue(planningRatings.findByUserAndBand(userName, band))),
                csv(ratingValue(realRatings.findByUserAndBand(userName, band))),
                csv(groupRatings(band)),
                csv(join(bandPerformances.stream().map(performance -> performance.stage().name()).distinct().toList())),
                csv(join(bandPerformances.stream().map(Performance::start).map(LocalDateTime::toLocalDate).distinct().map(LocalDate::toString).toList())),
                csv(join(bandPerformances.stream().map(this::timeRange).toList())),
                csv(bandPerformances.isEmpty() ? "UNSCHEDULED" : "SCHEDULED")
        );
    }

    private String groupRatings(Band band) {
        return planningRatings.findAll()
                .stream()
                .filter(rating -> rating.band().equals(band))
                .filter(rating -> rating.rating().value() > 0)
                .sorted(Comparator.comparing(SavedRating::userName, String.CASE_INSENSITIVE_ORDER))
                .map(rating -> rating.userName() + "=" + rating.rating().value())
                .collect(Collectors.joining(";"));
    }

    private String ratingValue(Optional<Rating> rating) {
        return rating
                .map(Rating::value)
                .filter(value -> value > 0)
                .map(String::valueOf)
                .orElse("");
    }

    private String join(List<String> values) {
        return String.join(";", values);
    }

    private String timeRange(Performance performance) {
        return clock(performance.start().toLocalTime()) + "-" + clock(performance.end().toLocalTime());
    }

    private String clock(LocalTime time) {
        return time.toString().substring(0, 5);
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }
        boolean quoted = value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r");
        String escaped = value.replace("\"", "\"\"");
        return quoted ? "\"" + escaped + "\"" : escaped;
    }
}
