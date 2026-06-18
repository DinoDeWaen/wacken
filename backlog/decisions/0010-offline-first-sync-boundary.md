# ADR 0010: Offline-First Sync Boundary

## Status

Accepted

## Context

The app is intended for use at Wacken, where mobile data can be slow or absent.
The previous implementation had grown from simple lifecycle sync calls in
Activities into several direct Supabase interactions. That made the Room cache
feel like an implementation detail instead of the primary way the app remains
usable offline.

Ratings already had a pending local state, but group schedule locks still used
Supabase directly. If the backend was unreachable or the schema was not visible
to PostgREST, manual schedule choices could not be saved for later sync.

The approved approach is the standard architecture option: keep the existing
Clean Architecture modules and Room database, but make sync ownership explicit.

## Decision

Use Room as the offline-first read model for app screens. Supabase remains the
shared backend, but UI workflows read and write local state first.

Mutable shared data must use a sync boundary:

- User edits are stored locally as pending operations.
- Background/manual sync pushes pending operations to Supabase when available.
- Successful remote writes mark local operations as synced.
- Remote pulls refresh synced local rows without overwriting pending local
  operations for the same business key.
- If Supabase is unavailable, screens continue from Room and pending operations
  stay queued.

For group schedule locks, the app stores lock selections and clears in
`schedule_locks` Room rows with `PENDING` or `SYNCED` status and an `UPSERT` or
`DELETE` operation. `SyncingScheduleLockStore` coordinates local-first behavior
with the existing Supabase `group_schedule_locks` REST adapter.

Conflict policy for the current MVP:

- Local pending schedule-lock operations win over remote pulls on the same
  conflict key until they sync.
- Remote upsert/delete semantics remain last-write-wins at Supabase level for
  group-wide locks.
- Rich conflict review, audit history, and member attribution UI are deferred.

## Consequences

Positive:
- The schedule remains useful without network when cached data exists.
- Manual schedule choices can be made offline and synced later.
- Activities no longer need to own the persistence details of whether a write
  is remote or queued.
- The same pattern can be extended to other mutable shared data.

Negative / trade-offs:
- The Room schema now carries sync metadata for schedule locks.
- Last-write-wins can still surprise users if two devices edit the same lock
  while offline.
- A first install still needs one successful sync or import before offline use.
- Background sync orchestration remains simple thread-based Android code for
  now; a richer worker/scheduler can be introduced later if needed.

## Alternatives considered

- Minimal option: keep direct Activity/Supabase calls and sanitize errors.
  Rejected because it does not solve offline edits or reduce sync coupling.
- Maximum option: introduce event sourcing, audit history, multi-group conflict
  resolution, and a dedicated background job framework now. Rejected because it
  is broader than the current product needs.

## Links

- Related task: task-118
- Related task: task-117
- Related ADR: `0009-supabase-group-schedule-winner-locks.md`
- Related docs: `backlog/docs/business-requirements.md`
