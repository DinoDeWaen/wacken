# ADR 0005: Food and stage repository ports for CSV import

## Status

Accepted

## Context

Task-6 requires CSV import for bands, stages, performances, distances, and food to populate domain repositories. The existing domain already had `Stage` and `StageDistance`, but only distance data had a repository port. Food had no domain concept or repository.

Adding these repositories affects the domain model and hexagonal ports, so explicit approval was required.

## Decision

Use the minimal approved option:

- Add `StageRepository` as a domain port.
- Add `FoodOption` as a minimal domain object.
- Add `FoodOptionRepository` as a domain port.
- Add in-memory infrastructure adapters for both ports.

Keep food details intentionally small for MVP 1. Proximity, categories, coordinates, and lunch-specific behavior remain future refinements.

## Consequences

Positive:

- CSV import can populate all required master-data repositories.
- The domain/application layers stay independent of infrastructure.
- Future food/lunch behavior has an explicit domain entry point.

Trade-offs:

- Food is currently represented only by name.
- Stage ids remain an import concern; the domain `Stage` still uses the existing name-based value object.

## Alternatives considered

- Minimal option: add only the missing ports/domain object. Accepted.
- Standard option: add richer food location/proximity modeling now. Rejected because lunch planning is a later MVP area.
- Maximum option: design full food, lunch, map, and travel model now. Rejected as premature.

## Links

- Related task: task-6
- Related schema: `backlog/docs/festival-data-csv-schemas.md`
