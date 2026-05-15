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

public final class ImportFestivalCsvUseCase {
    private final BandRepository bands;
    private final StageRepository stages;
    private final PerformanceRepository performances;
    private final StageDistanceRepository distances;
    private final FoodOptionRepository foodOptions;

    public ImportFestivalCsvUseCase(
            BandRepository bands,
            StageRepository stages,
            PerformanceRepository performances,
            StageDistanceRepository distances,
            FoodOptionRepository foodOptions
    ) {
        this.bands = Objects.requireNonNull(bands, "bands must not be null");
        this.stages = Objects.requireNonNull(stages, "stages must not be null");
        this.performances = Objects.requireNonNull(performances, "performances must not be null");
        this.distances = Objects.requireNonNull(distances, "distances must not be null");
        this.foodOptions = Objects.requireNonNull(foodOptions, "foodOptions must not be null");
    }

    public ImportFestivalCsvResult importCsv(FestivalCsvFiles files) {
        Objects.requireNonNull(files, "files must not be null");

        ParsedData parsedData = parse(files);
        List<String> errors = validate(parsedData);
        if (!errors.isEmpty()) {
            return ImportFestivalCsvResult.failure(errors);
        }

        parsedData.bandsById().values().forEach(bands::save);
        parsedData.stagesById().values().forEach(stages::save);
        parsedData.performances().forEach(row -> performances.save(new Performance(
                parsedData.bandsById().get(row.bandId()),
                parsedData.stagesById().get(row.stageId()),
                LocalDateTime.parse(row.startAt()),
                LocalDateTime.parse(row.endAt())
        )));
        parsedData.distances().forEach(row -> distances.save(StageDistance.between(
                parsedData.stagesById().get(row.fromStageId()),
                parsedData.stagesById().get(row.toStageId()),
                Integer.parseInt(row.walkingMinutes())
        )));
        parsedData.foodOptions().forEach(row -> foodOptions.save(new FoodOption(row.name())));

        return ImportFestivalCsvResult.imported();
    }

    private ParsedData parse(FestivalCsvFiles files) {
        Map<String, Band> bandsById = new LinkedHashMap<>();
        for (CsvRow row : parseCsv(files.bandsCsv())) {
            bandsById.put(row.required("band_id"), new Band(
                    row.required("name"),
                    optional(row.value("youtube_url")),
                    spotifyUrl(row.value("spotify_artist_id"))
            ));
        }

        Map<String, Stage> stagesById = new LinkedHashMap<>();
        for (CsvRow row : parseCsv(files.stagesCsv())) {
            stagesById.put(row.required("stage_id"), new Stage(row.required("name")));
        }

        List<PerformanceRow> performanceRows = parseCsv(files.performancesCsv())
                .stream()
                .map(row -> new PerformanceRow(
                        row.rowNumber(),
                        row.required("performance_id"),
                        row.required("band_id"),
                        row.required("stage_id"),
                        row.required("start_at"),
                        row.required("end_at")
                ))
                .toList();

        List<DistanceRow> distanceRows = parseCsv(files.distancesCsv())
                .stream()
                .map(row -> new DistanceRow(
                        row.rowNumber(),
                        row.required("from_stage_id"),
                        row.required("to_stage_id"),
                        row.required("walking_minutes")
                ))
                .toList();

        List<FoodRow> foodRows = parseCsv(files.foodCsv())
                .stream()
                .map(row -> new FoodRow(
                        row.rowNumber(),
                        row.required("food_id"),
                        row.required("name"),
                        row.value("near_stage_id")
                ))
                .toList();

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

    private List<CsvRow> parseCsv(String csv) {
        List<String> lines = csv.lines()
                .filter(line -> !line.isBlank())
                .toList();
        if (lines.isEmpty()) {
            return List.of();
        }

        List<String> headers = splitLine(lines.get(0));
        List<CsvRow> rows = new ArrayList<>();
        for (int index = 1; index < lines.size(); index++) {
            List<String> values = splitLine(lines.get(index));
            Map<String, String> valuesByHeader = new HashMap<>();
            for (int column = 0; column < headers.size(); column++) {
                String value = column < values.size() ? values.get(column) : "";
                valuesByHeader.put(headers.get(column), value.trim());
            }
            rows.add(new CsvRow(index + 1, valuesByHeader));
        }
        return rows;
    }

    private List<String> splitLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (character == '"') {
                quoted = !quoted;
            } else if (character == ',' && !quoted) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }
        values.add(current.toString());
        return values;
    }

    private java.util.Optional<String> optional(String value) {
        if (value == null || value.isBlank()) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(value);
    }

    private java.util.Optional<String> spotifyUrl(String spotifyArtistId) {
        return optional(spotifyArtistId).map(id -> "https://open.spotify.com/artist/" + id);
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

    private record CsvRow(int rowNumber, Map<String, String> valuesByHeader) {
        private String required(String header) {
            return value(header);
        }

        private String value(String header) {
            return valuesByHeader.getOrDefault(header, "");
        }
    }
}
