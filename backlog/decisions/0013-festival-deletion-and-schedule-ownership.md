# ADR 0013: Festival Deletion and Schedule Ownership

## Status

Accepted. The user approved the standard design on 2026-10-16 for TASK-193.

## Context

Users need to remove an unwanted active or archived festival and its schedule
without deleting reusable bands. Existing Supabase cascade relationships cover
lineups and planning ratings, but performances and group schedule locks have no
festival owner. A hard deletion also clears the festival reference on personal
rating events, and the local empty-cache initializer recreates Wacken.

The product remains for one friend group. Festival archive and rename are
already group-wide member actions; deletion must be usable by those members.

## Decision

- Add explicit festival ownership to performances and schedule locks in Room and
  Supabase. Backfill legacy rows to `wacken-2026`. Scope schedule reads and lock
  writes to the active festival, and archived performance reads to the selected
  festival.
- Use `DeleteFestivalUseCase` with a deletion gateway. After user confirmation,
  delete the remote festival, verify it is absent, then clean its local records
  in one Room transaction. Remote failure preserves the local festival. A later
  successful festival sync also cleans records removed by another device.
- Allow authenticated members of the one friend group to delete festivals using
  an explicit RLS delete policy. Keep authorization in Postgres. No privileged
  API key or security-definer deletion endpoint is added.
- Let foreign keys cascade to festival-owned lineups, planning ratings,
  performances, and schedule locks. Never cascade from a festival to bands.
- Preserve personal rating events. Store a festival-name snapshot before the
  festival reference is cleared, using a security-invoker insert/update trigger
  and a backfill for existing events. Retain values and created dates.
- Persist local festival-cache initialization so an intentionally empty cache
  does not seed Wacken again. Do not backfill legacy Wacken events after its
  festival has been removed.

## Alternatives Considered

1. Admin-only deletion with the old global schedule tables cannot safely remove
   one festival's schedule and is unusable for ordinary group members.
2. Festival-scoped hard deletion satisfies the requested behavior with existing
   Postgres cascade relationships and explicit local cleanup. Selected.
3. Soft deletion would retain festival and audit records, but adds tombstone
   filtering and retention behavior beyond the requested removal.

## Consequences

Deletion requires a connection, explicit confirmation, and cannot be undone.
Planning ratings are removed with the festival; personal rating history and the
central band catalog remain. Other devices converge after successful sync.
Older app versions do not understand festival-owned schedules or the local
initialization marker, so users should update to V2.33.2.

Room version 9 and Flyway V011 must be deployed together with the updated
adapters. Verification covers migration, real Room cleanup, cross-festival
preservation, remote denial, and member/non-member authorization in a rolled
back database transaction.
