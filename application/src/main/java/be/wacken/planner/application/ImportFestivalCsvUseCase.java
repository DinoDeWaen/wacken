package be.wacken.planner.application;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.FoodOption;
import be.wacken.planner.domain.FoodOptionRepository;
import be.wacken.planner.domain.Performance;
import be.wacken.planner.domain.PerformanceRepository;
import be.wacken.planner.domain.Stage;
import be.wacken.planner.domain.StageDistance;
import be.wacken.planner.domain.StageDistanceRepository;
import be.wacken.planner.domain.StageRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.function.Consumer;

public final class ImportFestivalCsvUseCase {
    private final BandRepository bands;
    private final StageRepository stages;
    private final PerformanceRepository performances;
    private final StageDistanceRepository distances;
    private final FoodOptionRepository foodOptions;
    private final Consumer<Runnable> transaction;

    public ImportFestivalCsvUseCase(
            BandRepository bands,
            StageRepository stages,
            PerformanceRepository performances,
            StageDistanceRepository distances,
            FoodOptionRepository foodOptions,
            Consumer<Runnable> transaction
    ) {
        this.bands = Objects.requireNonNull(bands, "bands must not be null");
        this.stages = Objects.requireNonNull(stages, "stages must not be null");
        this.performances = Objects.requireNonNull(performances, "performances must not be null");
        this.distances = Objects.requireNonNull(distances, "distances must not be null");
        this.foodOptions = Objects.requireNonNull(foodOptions, "foodOptions must not be null");
        this.transaction = Objects.requireNonNull(transaction, "transaction must not be null");
    }

    public ImportFestivalCsvResult importCsv(FestivalCsvFiles files) {
        Objects.requireNonNull(files, "files must not be null");

        ParsedData parsedData;
        try {
            parsedData = parse(files);
        } catch (FestivalCsvReader.FormatException error) {
            return ImportFestivalCsvResult.failure(List.of(error.getMessage()));
        }
        List<String> errors = validate(parsedData);
        if (!errors.isEmpty()) {
            return ImportFestivalCsvResult.failure(errors);
        }

        try {
            transaction.accept(() -> {
                bands.replaceAll(new ArrayList<>(parsedData.bandsById().values()));
                stages.replaceAll(new ArrayList<>(parsedData.stagesById().values()));
                performances.replaceAll(parsedData.performances().stream()
                        .map(row -> new Performance(
                                parsedData.bandsById().get(row.bandId()),
                                parsedData.stagesById().get(row.stageId()),
                                LocalDateTime.parse(row.startAt()),
                                LocalDateTime.parse(row.endAt())
                        ))
                        .collect(Collectors.toList()));
                distances.replaceAll(parsedData.distances().stream()
                        .map(row -> StageDistance.between(
                                parsedData.stagesById().get(row.fromStageId()),
                                parsedData.stagesById().get(row.toStageId()),
                                Integer.parseInt(row.walkingMinutes())
                        ))
                        .collect(Collectors.toList()));
                foodOptions.replaceAll(parsedData.foodOptions().stream()
                        .map(row -> new FoodOption(row.name()))
                        .collect(Collectors.toList()));
            });
        } catch (RuntimeException error) {
            return ImportFestivalCsvResult.failure(List.of(
                    error.getMessage() == null ? "Festival data could not be saved." : error.getMessage()
            ));
        }

        return ImportFestivalCsvResult.imported();
    }

    private ParsedData parse(FestivalCsvFiles files) {
        Map<String, Band> bandsById = new LinkedHashMap<>();
        for (FestivalCsvReader.Row row : FestivalCsvReader.rows("bands.csv", files.bandsCsv())) {
            bandsById.put(row.required("band_id"), new Band(
                    row.required("name"),
                    optional(plainText(preferredBiography(row))),
                    optional(firstPresent(row.value("image_url"), row.value("thumbnail_url"))),
                    optional(row.value("youtube_url")),
                    spotifyUrl(row.value("spotify_artist_id"))
            ));
        }

        Map<String, Stage> stagesById = new LinkedHashMap<>();
        for (FestivalCsvReader.Row row : FestivalCsvReader.rows("stages.csv", files.stagesCsv())) {
            stagesById.put(row.required("stage_id"), new Stage(row.required("name")));
        }

        List<PerformanceRow> performanceRows = FestivalCsvReader.rows("performances.csv", files.performancesCsv())
                .stream()
                .map(row -> new PerformanceRow(
                        row.rowNumber(),
                        row.required("performance_id"),
                        row.required("band_id"),
                        row.required("stage_id"),
                        row.required("start_at"),
                        row.required("end_at")
                ))
                .collect(Collectors.toList());

        List<DistanceRow> distanceRows = FestivalCsvReader.rows("distances.csv", files.distancesCsv())
                .stream()
                .map(row -> new DistanceRow(
                        row.rowNumber(),
                        row.required("from_stage_id"),
                        row.required("to_stage_id"),
                        row.required("walking_minutes")
                ))
                .collect(Collectors.toList());

        List<FoodRow> foodRows = FestivalCsvReader.rows("food.csv", files.foodCsv())
                .stream()
                .map(row -> new FoodRow(
                        row.rowNumber(),
                        row.required("food_id"),
                        row.required("name"),
                        row.value("near_stage_id")
                ))
                .collect(Collectors.toList());

        return new ParsedData(bandsById, stagesById, performanceRows, distanceRows, foodRows);
    }

    private List<String> validate(ParsedData data) {
        List<String> errors = new ArrayList<>();

        for (PerformanceRow row : data.performances()) {
            if (!data.bandsById().containsKey(row.bandId())) {
                errors.add("performances.csv row " + row.rowNumber() + " references unknown band_id " + row.bandId());
            }
            if (!data.stagesById().containsKey(row.stageId())) {
                errors.add("performances.csv row " + row.rowNumber() + " references unknown stage_id " + row.stageId());
            }
            if (!LocalDateTime.parse(row.endAt()).isAfter(LocalDateTime.parse(row.startAt()))) {
                errors.add("performances.csv row " + row.rowNumber() + " end_at must be after start_at");
            }
        }

        for (DistanceRow row : data.distances()) {
            if (!data.stagesById().containsKey(row.fromStageId())) {
                errors.add("distances.csv row " + row.rowNumber() + " references unknown stage_id " + row.fromStageId());
            }
            if (!data.stagesById().containsKey(row.toStageId())) {
                errors.add("distances.csv row " + row.rowNumber() + " references unknown stage_id " + row.toStageId());
            }
            if (Integer.parseInt(row.walkingMinutes()) < 0) {
                errors.add("distances.csv row " + row.rowNumber() + " walking_minutes must be 0 or greater");
            }
        }

        for (FoodRow row : data.foodOptions()) {
            if (!row.nearStageId().isBlank() && !data.stagesById().containsKey(row.nearStageId())) {
                errors.add("food.csv row " + row.rowNumber() + " references unknown stage_id " + row.nearStageId());
            }
        }

        errors.addAll(overlapErrors(data.performances()));
        return errors;
    }

    private List<String> overlapErrors(List<PerformanceRow> performanceRows) {
        List<String> errors = new ArrayList<>();
        Map<String, List<PerformanceRow>> byStage = new HashMap<>();
        for (PerformanceRow row : performanceRows) {
            byStage.computeIfAbsent(row.stageId(), ignored -> new ArrayList<>()).add(row);
        }

        for (Map.Entry<String, List<PerformanceRow>> entry : byStage.entrySet()) {
            List<PerformanceRow> rows = entry.getValue();
            for (int left = 0; left < rows.size(); left++) {
                for (int right = left + 1; right < rows.size(); right++) {
                    PerformanceRow first = rows.get(left);
                    PerformanceRow second = rows.get(right);
                    if (overlaps(first, second)) {
                        errors.add("performances.csv rows " + first.rowNumber()
                                + " and " + second.rowNumber()
                                + " overlap on stage_id " + entry.getKey());
                    }
                }
            }
        }
        return errors;
    }

    private boolean overlaps(PerformanceRow first, PerformanceRow second) {
        LocalDateTime firstStart = LocalDateTime.parse(first.startAt());
        LocalDateTime firstEnd = LocalDateTime.parse(first.endAt());
        LocalDateTime secondStart = LocalDateTime.parse(second.startAt());
        LocalDateTime secondEnd = LocalDateTime.parse(second.endAt());
        return firstStart.isBefore(secondEnd) && secondStart.isBefore(firstEnd);
    }

    private Optional<String> optional(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

    private Optional<String> spotifyUrl(String spotifyArtistId) {
        return optional(spotifyArtistId).map(id -> "https://open.spotify.com/artist/" + id);
    }

    private String plainText(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }
        return html
                .replaceAll("<[^>]+>", " ")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String preferredBiography(FestivalCsvReader.Row row) {
        return firstPresent(row.value("biography"), row.value("biography_html"));
    }

    private String firstPresent(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return second == null ? "" : second;
    }

    private record ParsedData(
            Map<String, Band> bandsById,
            Map<String, Stage> stagesById,
            List<PerformanceRow> performances,
            List<DistanceRow> distances,
            List<FoodRow> foodOptions
    ) {
    }

    private record PerformanceRow(int rowNumber, String performanceId, String bandId, String stageId, String startAt, String endAt) {
    }

    private record DistanceRow(int rowNumber, String fromStageId, String toStageId, String walkingMinutes) {
    }

    private record FoodRow(int rowNumber, String foodId, String name, String nearStageId) {
    }

}
