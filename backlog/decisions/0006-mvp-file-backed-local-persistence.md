# 0006 - MVP File-Backed Local Persistence

## Status

Superseded by [`0007-room-local-cache-with-tsv-backend-source.md`](0007-room-local-cache-with-tsv-backend-source.md)

## Context

MVP 1 needs imported festival data and ratings to survive app restarts. The existing repository ports already isolate application use cases from storage details, and the current in-memory adapters are useful for tests but lose data when the process ends.

SQLite or Room would add more moving parts before the Android import and rating screens are finished. The MVP needs a small persistence mechanism that keeps architecture boundaries intact and can be replaced later.

## Decision

Use file-backed repository adapters in the `infrastructure` module for MVP 1 local persistence.

Each adapter receives an app-private storage directory as a `Path`, stores one data file per repository, and implements the existing domain repository ports for:

- bands
- stages
- performances
- stage distances
- food options
- ratings

The domain and application modules remain independent from Android and file APIs. The Android module will provide the app-private directory when wiring these adapters.

## Consequences

- Imported data and ratings can persist across repository recreation and app restarts once the Android module wires these adapters.
- The MVP avoids adding a database dependency before the data model stabilizes.
- File storage is acceptable for the small MVP datasets but is not optimized for concurrent writes, migrations, or large data volumes.
- A later move to Room or another database can replace the infrastructure adapters behind the same ports.
