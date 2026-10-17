package be.wacken.planner;

import java.util.List;
import java.util.OptionalInt;

import be.wacken.planner.application.BandListItem;

final class SelectedBandScrollTarget {
    private SelectedBandScrollTarget() {
    }

    static OptionalInt findIndex(String selectedBandName, List<BandListItem> bands) {
        if (selectedBandName == null || selectedBandName.isBlank()) {
            return OptionalInt.empty();
        }
        for (int index = 0; index < bands.size(); index++) {
            if (selectedBandName.equals(bands.get(index).bandName())) {
                return OptionalInt.of(index);
            }
        }
        return OptionalInt.empty();
    }
}
