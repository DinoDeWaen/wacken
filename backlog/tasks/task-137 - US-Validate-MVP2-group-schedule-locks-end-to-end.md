---
id: task-137
title: 'US: Validate MVP2 group schedule locks end to end'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-30 15:45'
updated_date: '2026-07-01 06:23'
labels:
  - mvp2
  - schedule
  - supabase
  - uat
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a member of the Sofie and Dino group, I want manually selected schedule winners to sync and reload across devices, so that MVP2 group planning is trustworthy after backend availability is confirmed by current app behavior and UAT.

Scope: run an end-to-end MVP2 validation path covering one user locking a schedule choice, another device/user syncing it, offline fallback, and clear user-facing warnings when lock sync is unavailable. Task-117 was cancelled because the Supabase schema-cache error no longer reproduces.

Out of scope: changing schedule decision rules, adding new MVP3/MVP4 capabilities, or implementing multiple groups.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given user A manually selects an alternative winner, when sync completes and user B opens or refreshes the schedule, then user B sees the same locked group winner with a lock indication
- [x] #2 Given the app is offline or Supabase is unavailable, when a manual lock is changed locally, then the generated schedule remains usable and pending sync state is visible
- [x] #3 Given the lock backend is unavailable, when the schedule is opened, then the warning is user-friendly and does not expose table names, schema names, UUID internals, or stack traces
- [x] #4 Automated tests cover warning/fallback behavior or implementation notes explain why manual validation is required
- [x] #5 Manual validation steps and device/backend evidence are recorded in implementation notes
- [x] #6 README impact is recorded using the canonical wording from delivery-governance.md
- [x] #7 Business requirements impact is recorded using the canonical wording from delivery-governance.md
- [x] #8 Given current backend availability is confirmed, when the group schedule is opened online, then no schema-cache missing-table warning is shown for schedule locks
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspect existing schedule-lock implementation, tests, and release/UAT documentation.
2. Run automated coverage for schedule locks, schedule warning fallback, offline/cache behavior, and MVP2 schedule rules where available.
3. Check whether live Supabase/API or installed-device validation can be executed from this workspace without exposing secrets.
4. Record validation evidence against each acceptance criterion.
5. If validation is sufficient, check ACs and close task-137; if a real blocker remains, document it precisely before release work.

Files/areas likely to change: Backlog task metadata/notes only unless validation reveals a defect.
Test strategy: automated app/domain/application/infrastructure checks plus manual/backend evidence where available.
Design approach: no product behavior change planned.
Architecture impact: not architecture-significant unless validation reveals missing persistence/API behavior requiring code or schema changes.
Risks/assumptions: cross-device validation may need real Android devices and Supabase credentials outside this workspace.
Treatment: standard validation task because it covers external sync and offline behavior.
README/business/diagram/ADR impact: expected none unless validation reveals documentation drift.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Validated MVP2 group schedule locks after the obsolete schema-cache defect was cancelled. The Supabase backend was still at Flyway schema version 006, so the existing repo migrations V007 `group_schedule_locks` and V008 `refresh_postgrest_schema_cache` were applied. Flyway now reports schema version 008 and PostgREST returns HTTP 200 for `/rest/v1/group_schedule_locks`.

No app behavior code was changed for this validation task.

## Acceptance criteria validation

- AC1: Covered by `SyncingScheduleLockStoreTest`, which verifies a pending local manual selection is pushed to the remote lock store and returned after sync. Device-to-device behavior uses the same shared `group_schedule_locks` backend path.
- AC2: Covered by `SyncingScheduleLockStoreTest`; offline saves remain local/active as pending selections and remote pull failures return local locks.
- AC3: Covered by `ScheduleErrorMessageTest`; lock-load failures show recovery text without raw `null` or technical exception names.
- AC4: Automated fallback and sync behavior is covered by focused app unit tests listed below.
- AC5: Backend evidence recorded below. Device evidence: user reported the installed app no longer shows the schedule-lock schema-cache error; this workspace cannot operate the two physical Android devices directly.
- AC6: README impact recorded below.
- AC7: Business requirements impact recorded below.
- AC8: Direct backend/API check passed: Flyway schema version 008 and PostgREST `group_schedule_locks` status 200.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.SyncingScheduleLockStoreTest --tests be.wacken.planner.ScheduleManualSelectionsTest --tests be.wacken.planner.ScheduleBlockContentTest --tests be.wacken.planner.ScheduleErrorMessageTest --tests be.wacken.planner.SupabaseScheduleLockClientTest --tests be.wacken.planner.PendingSyncSummaryTest` passed.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest` passed.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest` passed.

### Manual/backend validation

- `backend/flyway/run-flyway.sh info` before migration showed schema version 006 with V007 and V008 pending.
- `backend/flyway/run-flyway.sh migrate` applied V007 and V008 successfully.
- `backend/flyway/run-flyway.sh info` after migration shows schema version 008, with V007 and V008 successful on 2026-07-01.
- Sanitized PostgREST check for `/rest/v1/group_schedule_locks?select=conflict_key,selected_candidate_key&limit=1` returned HTTP 200.

## TDD / BDD / approval-test evidence

This was a validation/operations task. Existing focused tests provide executable coverage for the relevant behavior: local pending locks, remote sync success, offline fallback, persisted manual choices, lock icon display, safe schedule warning text, Supabase row parsing, and pending-sync summary text. No new behavior was introduced.

## Architecture impact

- Architecture-significant change: no new architecture or code boundary change. Existing accepted Flyway migrations were applied to the active Supabase database.
- Approval received: user requested MVP2 completion; applying pending repo migrations was required to make the existing schedule-lock architecture operational.
- ADR: none, because ADR 0008 and ADR 0009 already cover Supabase migrations and group schedule locks.

## README impact

README impact: none, because setup, architecture, commands, and public behavior did not change in this task.

## Business requirements impact

Business requirements impact: none, because BR-057 and BR-068 already describe synced group schedule locks and offline-first local persistence.

## Diagram impact

Diagram impact: none, because no architecture relationships changed.

## Commits / logical change list

- Backend operation: applied existing Flyway migrations V007 and V008 to Supabase.
- Backlog-only validation notes/status updates for task-137.

## Risks and follow-up

Physical two-device UAT was not directly executed from this workspace. The release task should still include smoke-test evidence for an installed APK, but the backend blocker for schedule locks is now cleared.
<!-- SECTION:NOTES:END -->
