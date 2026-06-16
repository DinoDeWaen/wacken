---
id: task-110
title: 'US: Persist manually selected schedule winners'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-16 20:00'
updated_date: '2026-06-16 20:25'
labels:
  - schedule
  - manual-selection
  - persistence
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user, I want a manually selected schedule winner to remain permanent, so that the schedule keeps the group decision instead of recalculating it back to the generated winner each time.

Business value: the schedule should reflect deliberate group choices during planning and at the festival. A manual winner must be visibly different from an automatically calculated winner.

In scope:
- Persist manual winner choices for schedule conflicts so they survive schedule refresh, regeneration, app restart, and sync/reload.
- Show a small lock icon on manually locked winners in the schedule block and decision detail.
- Keep generated decision evidence and lost alternatives visible so users can understand why the app originally chose another act.
- Define how a locked winner can be changed or cleared before implementation.

Out of scope:
- Changing personal band ratings when a winner is locked.
- Replacing the generated decision rules for conflicts without a manual lock.

Business rules impact: update business requirements because this changes BR-068, which currently says manual choices are local-only visible overrides.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a generated schedule conflict has an alternative, when I select that alternative as the winner and confirm the choice, then that selected act remains the winner after reopening or regenerating the schedule.
- [x] #2 Given a schedule winner was manually selected, when the schedule block and decision detail are shown, then a small lock icon identifies that winner as manually locked.
- [x] #3 Given a manually locked winner exists, when the schedule is recalculated, then the locked winner is not replaced by the calculated winner unless the lock is explicitly changed or cleared.
- [x] #4 Given a manually locked winner is shown, then the original generated winner, alternatives, ratings, and tie/lost-alternative evidence remain visible where applicable.
- [x] #5 Before implementation, the persistence, sync, ownership, and clear/change behavior for locked winners is documented; if this requires schema, sync contract, repository, or permission changes, architecture approval is requested before coding and ADR impact is recorded.
- [x] #6 Automated tests prove locked winners override recalculation without changing ratings, and Android compile validation passes.
- [x] #7 Business requirements and README impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected existing manual-selection, schedule-generation, Supabase, Room, and backend schema wiring.
2. Confirmed the previous behavior was in-memory only in ScheduleActivity.
3. Requested architecture approval and received approval for option 2: Supabase-backed group-wide locks.
4. Added TDD coverage for persisted lock application, recalculation override behavior, lock icon block content, and Supabase lock response mapping.
5. Implemented conflict-key based group locks, Supabase lock client, ScheduleActivity pull/save/unlock wiring, lock icon display, backend Flyway migration/RLS policies, and ADR 0009.
6. Updated business requirements and README.
7. Ran focused tests, full module validation, diff checks, rebuilt signed release APK, and verified APK signature/metadata.

Deviation: no Room table was added; the approved behavior is group-wide Supabase persistence and the schedule pulls locks from Supabase when rendering. Local offline lock caching remains out of scope. Architecture-significant change approved by user on 2026-06-16; ADR 0009 created.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Manual schedule winner selections are now group-wide locked choices stored in Supabase. The schedule pulls locks when rendering, applies the locked winner after generated conflict resolution, shows a lock icon/status, and lets a group member change the lock by selecting another act or clear it with an unlock action. Ratings and generated evidence are not changed.

## Acceptance criteria validation

- AC1: A selected alternative is upserted to `group_schedule_locks` and pulled on schedule render, so it remains the winner after schedule reload/regeneration when the same conflict is present.
- AC2: Locked winners show `🔒 LOCKED CHOICE` in details and a lock prefix in the schedule block band line.
- AC3: `ScheduleManualSelectionsTest.persistedLockStillAppliesWhenGeneratedWinnerChangesWithinSameConflict` proves a persisted lock overrides a changed generated winner for the same conflict.
- AC4: Detail candidates still include the generated choice and remaining alternatives with original ratings/status evidence.
- AC5: Persistence, sync, ownership, and clear/change behavior are documented here and in ADR 0009. User approved Supabase group-wide locks on 2026-06-16 before coding.
- AC6: Automated tests cover lock override behavior and Android compile validation passed.
- AC7: Business requirements and README impact are recorded below.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.ScheduleManualSelectionsTest --tests be.wacken.planner.ScheduleBlockContentTest --tests be.wacken.planner.SupabaseScheduleLockClientTest :app:compileDebugJavaWithJavac`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `git diff --check`
- Deleted `app/build/outputs/apk/release/app-release.apk` and rebuilt with signed `assembleRelease`.
- `apksigner verify --verbose app/build/outputs/apk/release/app-release.apk` verifies v1/v2 signatures with one signer.
- `aapt dump badging app/build/outputs/apk/release/app-release.apk` shows `versionCode=15`, `versionName=2.12`.
- SHA-256: `a0f7ac5c4d829af2715c5f58591eb180a4d2a8288727dcdc3d62b9e75b53aaf4`.

### Manual validation

Not run on an installed device in this task. Manual UAT should apply the Flyway migration, open a conflict detail, select an alternative, reopen the schedule on another signed-in group device, and verify the locked winner remains visible with the lock icon.

## TDD / BDD / approval-test evidence

Added focused failing tests first for durable lock application and recalculation override behavior, then implemented the helper/client/UI changes. No approval baseline was needed because this is intentional new behavior.

## Architecture impact

- Architecture-significant change: yes, Supabase schema/RLS and group sync contract added for schedule locks.
- Approval received: yes, user approved option 2 on 2026-06-16.
- ADR: created `backlog/decisions/0009-supabase-group-schedule-winner-locks.md`.

## README impact

README impact: updated basic functionality and ADR links to document Supabase-synced locked group winners.

## Business requirements impact

Business requirements impact: updated current capabilities, BR-068, the manual schedule choice glossary entry, and the related open question to reflect group-wide Supabase locks.

## Diagram impact

Diagram impact: none, because the existing Supabase backend relationship remains accurate and no new architectural container was introduced.

## Commits / logical change list

- Add Supabase `group_schedule_locks` migration and RLS policies.
- Add ADR 0009 for group schedule winner locks.
- Add lock store/client and app repository wiring.
- Apply conflict-key based locks in schedule rendering and details.
- Add lock icon/status and unlock action.
- Update tests, README, and business requirements.

## Risks and follow-up

- Locks are keyed from visible conflict candidates because the current domain model does not carry Supabase performance ids. If festival master data changes the candidate set, an old lock may no longer match and generated rules will apply.
- Supabase migration must be applied before installed clients can save or pull group locks.
- Offline lock caching is not included; schedule lock reads/writes use Supabase when the schedule is rendered or changed.
<!-- SECTION:NOTES:END -->
