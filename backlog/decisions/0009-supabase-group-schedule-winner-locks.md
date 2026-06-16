# ADR 0009: Supabase Group Schedule Winner Locks

## Status

Accepted

## Context

Manual schedule choices were previously held only in `ScheduleActivity` memory.
That meant selecting an alternative act changed the current screen but was lost
after schedule regeneration, app restart, or using another device.

The requested behavior is that a manually selected winner is permanent for the
group and identified with a lock icon. Because the lock belongs to the shared
`Sofie and Dino` group, local-only persistence is not sufficient.

## Decision

Store manual schedule winner locks in Supabase in `group_schedule_locks`.

Each lock is scoped by `group_id` and a stable `conflict_key` derived from the
visible conflict candidate set. The selected winner is stored as a
`selected_candidate_key` derived from band, stage, start, and end. The app pulls
locks when rendering the schedule and upserts a lock when a group member selects
an alternative as the act to attend.

Any authenticated member of the group can view, insert, update, or delete locks
for that group. Ratings remain unchanged; locks override only the visible group
schedule winner for the matching conflict.

## Consequences

Positive:
- Manual choices survive schedule reloads and app restarts.
- Manual choices are shared across devices in the same group.
- The generated decision evidence and alternatives can remain visible because
  locks apply after schedule generation.

Negative / trade-offs:
- A lock can become stale if festival master data changes enough that the
  candidate set no longer matches the stored conflict key.
- The app now has another Supabase-backed group data contract to migrate and
  protect with RLS.
- Conflict identity is based on visible candidate data because the current
  domain model does not carry Supabase performance ids.

## Alternatives considered

- Local-only persistence: rejected because the user explicitly approved group
  locks shared through Supabase.
- Full collaborative decision model with audit/history/permissions: rejected as
  too broad for the current story.
- Refactor domain `Performance` to include backend ids: rejected for this task
  because it would expand the blast radius across imports, Room cache, domain,
  tests, and schedule generation.

## Links

- Related task: task-110
- Related docs: `backlog/docs/business-requirements.md`
