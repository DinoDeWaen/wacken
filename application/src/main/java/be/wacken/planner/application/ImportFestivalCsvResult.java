package be.wacken.planner.application;

import java.util.List;

public record ImportFestivalCsvResult(boolean success, List<String> errors) {
    public ImportFestivalCsvResult {
        errors = List.copyOf(errors);
    }

    public static ImportFestivalCsvResult imported() {
        return new ImportFestivalCsvResult(true, List.of());
    }

    public static ImportFestivalCsvResult failure(List<String> errors) {
        return new ImportFestivalCsvResult(false, errors);
    }
}
