package be.wacken.planner.domain;

import java.util.Objects;
import java.util.Optional;

/** Empty selection means an explicitly reviewed choice to create a separate band. */
public record ReviewedFestivalBand(String uploadedName, Optional<CanonicalBand> selected, boolean rememberAlias) {
    public ReviewedFestivalBand {
        uploadedName = new Band(uploadedName).name();
        Objects.requireNonNull(selected, "selected");
        if (rememberAlias && selected.isEmpty()) throw new DomainValidationException("Select a catalog band before remembering an alias.");
    }
}
