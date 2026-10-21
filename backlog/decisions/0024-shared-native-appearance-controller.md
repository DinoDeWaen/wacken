# ADR 0024: Shared native appearance controller

## Status

Accepted, 21 October2026. The user selected **Standard** in response to the
[concrete TASK-205 proposal](../docs/release-3.0-appearance-architecture-proposal.md).

## Context

The approved paired design needs System/Dark/Light, remembered local preference,
consistent dialogs and state retention. Existing static colors and inconsistent
Activity saved-state coverage make recreation-only theme changes unsafe.

## Decision

Use a composable Android presentation controller with pure preference/palette
policy, private SharedPreferences and explicit semantic bindings. Retain Android
Views and direct Activities, current JVM boundaries and existing operation stores.
No new framework, base Activity hierarchy, backend theme sync or schema.

The controller resolves preference/device mode at creation/resume/configuration
changes, applies view/window styling in place and disconnects lifecycle callbacks
on destruction. Bindings do not own domain operations or replay writes. Each
screen owns its context/drafts/scroll/expanded/review state and uses Bundle/stable
IDs or existing retained sessions for independent recreation. Workers must not
retain old Activities; durable pending writes remain in existing stores.

Existing WackenTheme helpers provide contextual semantic styles. Palette state
is not stored in mutable static Activity color constants. Header and Settings
share the same preference. Pure policy/contrast tests and native configuration,
restart, recreation and no-duplicate-write tests are required. Groma source
ownership and semantic changes need CLI curation and reviewed baseline updates;
ArchUnit and behavioral/native/database checks remain required.

## Consequences

- One appearance mechanism serves native screens without rebuilding live forms.
- New components must use semantic bindings and implement their own safe state
  restoration. A theme change cannot be treated as an operation command.
- Lifecycle listeners/bindings require explicit cleanup and retention tests.
- Device-local preferences are separate from member/festival data; no account
  synchronization is introduced.
- TASK-205 establishes the foundation; TASK-206–211 migrate screen-specific
  layouts/retention, and TASK-212 validates the integrated matrix before release.

## Alternatives considered

- Duplicate callbacks/preferences in each screen: drifts across eleven Activities
  and dialogs, with repeated lifecycle risks.
- Framework/navigation migration: unnecessary dependency and migration scope.
- Recreation-only changes: cannot safely preserve all in-flight forms/reviews.

## Links

- TASK-205
- [Approved design contract](../docs/release-3.0-design-contract.md)
- [Rating lifecycle](0016-band-catalog-and-archive-promoted-personal-ratings.md)
