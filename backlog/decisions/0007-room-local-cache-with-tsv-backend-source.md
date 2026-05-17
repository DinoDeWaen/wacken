# ADR 0007: Room Local Cache With TSV Backend Source

## Status

Accepted

## Context

The MVP originally used file-backed repository adapters as local persistence.
That kept the domain clean, but every repository read parsed app-private TSV
files and every write rewrote a full file. The band list and future schedule
flows need fast local reads, and the product direction expects the current file
source to be replaceable by a backend API later.

The domain and application layers already depend on repository ports, so the
persistence model can change at the adapter edge without changing business
use cases.

## Decision

Use Room as the Android app's local cache. Treat the existing app-private TSV
files as the current MVP backend-like source.

The runtime persistence shape is:

```text
TSV backend-like source -> synced repository adapters -> Room local cache -> domain repository ports -> application use cases
```

Repository reads used by the app are served from Room. Imports and rating writes
write through to both the TSV source and Room cache. On first use, when a Room
table is empty, the app seeds that cache table from the TSV source so data from
older MVP installs is migrated into the local cache.

Room entities, DAOs, and database wiring live in the Android app edge. Domain
and application modules do not depend on Android, Room, SQL, files, or future
API details.

## Consequences

Positive:
- Band list and detail reads use indexed local Room storage instead of reparsing
  TSV files on each read.
- TSV remains a replaceable backend-like adapter for the MVP.
- A future backend API can replace the TSV source behind the same sync/write-
  through adapter shape.
- Domain and application code stay clean and persistence-technology agnostic.

Negative / trade-offs:
- The app now has an AndroidX Room dependency and annotation processing step.
- The MVP uses `allowMainThreadQueries()` to match the existing synchronous use
  cases; a later production hardening task should move sync and database work
  off the UI thread.
- The current sync model is simple write-through, not a full conflict-aware
  offline sync engine.

## Alternatives Considered

- Keep TSV as the only persistence and add in-memory caching: rejected because it
  keeps file parsing as the persistence model and does not prepare the API swap.
- Build a full sync engine with dirty flags, versioning, background workers, and
  conflict handling: rejected as too large for the MVP and not yet required.

## Links

- Related task: task-38
- Supersedes: [`0006-mvp-file-backed-local-persistence.md`](0006-mvp-file-backed-local-persistence.md)
- Related docs: [`architecture-guidelines.md`](../docs/architecture-guidelines.md)
