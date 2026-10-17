package be.wacken.planner.application;

import java.util.*;
import be.wacken.planner.domain.*;

/** Proposes identities; it never creates or silently chooses a fuzzy/alias match. */
public final class ReviewFestivalBandsUseCase {
    private final FestivalBandCatalog catalog;
    public ReviewFestivalBandsUseCase(FestivalBandCatalog catalog) { this.catalog = Objects.requireNonNull(catalog); }

    public List<Proposal> review(List<Band> uploaded) {
        FestivalBandCatalog.Snapshot snapshot = catalog.load();
        return uploaded.stream().map(Band::name).distinct().map(name -> propose(name, snapshot)).toList();
    }

    private Proposal propose(String name, FestivalBandCatalog.Snapshot snapshot) {
        List<CanonicalBand> exact = snapshot.bands().stream().filter(b -> b.band().name().equals(name)).toList();
        if (exact.size() > 1) throw new DomainValidationException("Catalog contains duplicate exact names. Resolve them before creation.");
        Set<String> aliasIds = new HashSet<>();
        snapshot.aliases().stream().filter(a -> ConfirmedBandAlias.key(a.name()).equals(ConfirmedBandAlias.key(name)))
                .map(a -> a.canonical().id()).forEach(aliasIds::add);
        List<CanonicalBand> suggestions = snapshot.bands().stream()
                .filter(b -> aliasIds.contains(b.id()) || BandNameMatcher.likelySameBand(name,b.band().name()))
                .sorted(Comparator.comparing((CanonicalBand b) -> !aliasIds.contains(b.id()))
                        .thenComparing(b -> b.band().name(),String.CASE_INSENSITIVE_ORDER)).toList();
        return new Proposal(name, exact.stream().findFirst(), suggestions);
    }

    public record Proposal(String uploadedName, Optional<CanonicalBand> exactMatch, List<CanonicalBand> suggestions) {
        public Proposal { suggestions = List.copyOf(suggestions); }
    }
}
