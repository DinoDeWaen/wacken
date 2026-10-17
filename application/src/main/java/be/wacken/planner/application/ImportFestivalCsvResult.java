package be.wacken.planner.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record ImportFestivalCsvResult(boolean success, List<String> errors) {
    public ImportFestivalCsvResult {
        errors = Collections.unmodifiableList(new ArrayList<>(errors));
    }

    public static ImportFestivalCsvResult imported() {
        return new ImportFestivalCsvResult(true, Collections.emptyList());
    }

    public static ImportFestivalCsvResult failure(List<String> errors) {
        return new ImportFestivalCsvResult(false, errors);
    }
}
