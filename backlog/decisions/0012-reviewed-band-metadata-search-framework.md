# ADR 0012: Reviewed Band Metadata Search Framework

## Status

Accepted

## Context

Imported festival CSVs can create or expose bands with incomplete picture,
biography, Spotify, or YouTube metadata. The own band database remains the
golden source, and existing metadata must not be overwritten automatically.
External providers such as MusicBrainz, Wikidata, Wikipedia, Spotify, and
YouTube introduce API contracts, configuration, availability, and ambiguity
concerns that must not leak into Android UI business rules.

The user approved starting with the metadata search framework and the first
important integration after the architecture approval gate was raised for
task-179.

## Decision

Add a standard-depth application boundary for reviewed band metadata search:

- Application use cases produce metadata proposals for missing fields only.
- Own-catalog proposals are generated before external provider proposals.
- External metadata sources implement an application-level provider interface.
- Android hosts the review UI and provider adapters.
- Saving is a separate approval step that applies only selected proposals and
  still refuses to overwrite existing non-empty metadata.

Provider-specific HTTP clients, credentials, and source mapping are implemented
in separate stories so each source can be validated independently.

## Consequences

Positive:
- Metadata enrichment is reviewable and testable without coupling provider
  logic to Activities.
- The own band database stays the golden source.
- Provider failures or missing configuration can be reported without blocking
  other metadata work.
- Future providers can be added behind the same application boundary.

Negative / trade-offs:
- The first framework slice adds review flow structure before all providers are
  available.
- The Android review screen remains source-regression tested rather than fully
  instrumented until UI automation is added.

## Alternatives considered

- Minimal option: keep the existing one-click own-catalog enrichment and defer
  the review framework. Rejected because external provider stories need an
  approval boundary first.
- Standard option: introduce proposal and provider use cases now, with
  provider-specific adapters in follow-up stories. Accepted.
- Maximum option: add provider framework, all provider adapters, provider
  credentials, and persistence changes in one task. Rejected because it is too
  broad and increases review risk.

## Links

- Related task: task-179.1
- Parent defect: task-179
- Related docs: backlog/docs/business-requirements.md
