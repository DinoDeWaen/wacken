package be.wacken.planner.application;

import java.util.List;
import be.wacken.planner.domain.CanonicalBand;
import be.wacken.planner.domain.ConfirmedBandAlias;

@FunctionalInterface
public interface FestivalBandCatalog {
    Snapshot load();
    record Snapshot(List<CanonicalBand> bands, List<ConfirmedBandAlias> aliases) {
        public Snapshot { bands = List.copyOf(bands); aliases = List.copyOf(aliases); }
    }
}
