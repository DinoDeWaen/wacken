---
id: TASK-195
title: 'DEF: Add festival CSV fails on device and for group members'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-16 09:10'
updated_date: '2026-10-16 11:24'
labels:
  - defect
  - android
  - supabase
  - festival
dependencies: []
references:
  - importcsvactivity
  - supabasemasterdataclient
modified_files:
  - app/src/test/java/be/wacken/planner/AddFestivalActionTest.java
  - app/src/main/java/be/wacken/planner/AddFestivalAction.java
  - app/src/main/java/be/wacken/planner/ImportCsvActivity.java
  - app/src/test/java/be/wacken/planner/SupabaseMasterDataClientTest.java
  - app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java
  - backend/flyway/sql/V012__group_member_festival_creation.sql
  - backend/flyway/sql/V011__festival_deletion_and_schedule_scope.sql
  - backend/flyway/tests/group_member_festival_creation.sql
  - README.md
  - backlog/decisions/0011-post-wacken-festival-rating-model.md
priority: high
type: bug
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a signed-in member of the shared planning group, I can add the next festival from a valid bands CSV after archiving the previous festival, so the lineup appears group-wide. The current Android action runs network work on the UI thread and existing RLS rejects non-admin festival and lineup writes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a signed-in group member and no active festival, when they add a valid band CSV, then the festival and lineup are saved group-wide and the UI stays responsive.
- [x] #2 Given a non-member, when they attempt festival or lineup creation through the database API, then the write is rejected.
- [x] #3 Automated regression tests cover the Android worker/UI behavior and database authorization; manual verification steps are documented.
- [x] #4 Architecture approval, ADR, README, business requirements, and diagram impact are recorded with delivery-governance wording.
- [x] #5 Given Supabase rejects the festival creation request, when a member adds a festival, then the existing local festival cache is retained and a useful error is shown.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Reproduced the Android failure path and added failing worker/UI regression tests.
2. Moved add-festival persistence to a background worker with progress, restored button state, and a useful fallback error.
3. Added insert-only band and lineup creation plus V012 group-member RLS policies; verified non-member denial and member creation in a rollback-only database test.
4. Restored the exact V011 migration already applied remotely so Flyway validates this branch, applied and validated V012, updated ADR 0011 and README, and ran the Android/application test and build gates.

Final test strategy: Android unit tests for worker callbacks and Supabase request methods; application QA tests; live RLS transaction test; Flyway validate; debug APK build. A device run remains a release smoke test because no device was connected.

Architecture impact: approved by the user on 2026-10-16. ADR 0011 updated.

Deviation: the production database already had V011 while this checkout lacked that file, so the exact existing migration was restored before V012. Review found cached band metadata could force a forbidden PATCH; the new-festival lineup path now reuses the existing band ID without that update.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Fixed Add Festival CSV creation for signed-in members of the shared group. Remote persistence runs on a worker; the UI shows progress and a useful error.
- V012 allows group members to insert an active festival, new active bands, and lineup entries. New-band and lineup requests use plain inserts; existing bands are reused without metadata updates.
- Restored V011 from existing repository history to match the migration already installed in the database.

## Acceptance criteria validation

- Member insert and non-member rejection passed in a rollback-only live database test, including the Android lineup DELETE then INSERT sequence.
- Android worker/UI callback and request-method regressions passed. Existing repository tests cover retaining the local cache when festival POST fails.
- No Android device was connected, so final on-device CSV smoke testing remains for the next APK distribution.

## How to test

### Automated tests

- JAVA_HOME with Java 21: ./gradlew test qaTest assembleDebug — passed.
- Focused SupabaseMasterDataClientTest passed after a red test exposed an existing-band metadata PATCH.
- Flyway migrate and validate — passed with 12 validated migrations.
- Live rollback-only backend/flyway/tests/group_member_festival_creation.sql — passed; no test rows remained.
- git diff --check — passed.
- app:lintDebug remains blocked by 14 pre-existing NewApi errors outside these changes.

### Manual validation

1. Install the new debug APK on an authenticated group-member phone.
2. Archive any active festival, open Add Festival, name it, and choose a valid bands CSV containing a known and a new band.
3. Confirm the button disables and progress appears while the screen stays responsive; then confirm the festival and lineup on a second signed-in group-member device after sync.
4. With Supabase unavailable or a rejected festival POST, retry and confirm a readable error and the previous local cache still displays.
5. Check a non-member token cannot insert a festival or lineup entry.

## TDD / BDD / approval-test evidence

- The new worker test failed before AddFestivalAction existed, then passed after implementation.
- The cached-metadata lineup test failed on an unexpected PATCH, then passed after reusing the remote band ID.
- The RLS policy test failed before V012 and passed after it.

## Architecture impact

- Architecture-significant change: yes, group-member RLS insertion policies.
- Approval received: user approved on 2026-10-16.
- ADR: updated 0011-post-wacken-festival-rating-model.md.

## README impact

README impact: updated the Add Festival authorization, background work, and migration description.

## Business requirements impact

Business requirements impact: none, because group-wide festival creation was already the specified behavior; this task restores it.

## Diagram impact

Diagram impact: none, because no module or data-flow boundary changed; Android UI and Supabase remain the same containers.

## Commits / logical change list

- Android worker and failure feedback.
- Insert-only Supabase adapter and V012 authorization.
- Regression tests, migration-chain repair, README and ADR.

## Risks and follow-up

- The fix reaches the phone only after installing a build containing it. No device was connected for a CSV smoke test.
- A failure after the initial festival insert can leave a partial remote festival. Multi-request creation is not atomic and should be handled in a separate task.
- The working branch is behind main; integrate carefully to avoid overwriting newer work.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Fixed Add Festival CSV creation for shared-group members with background Android writes, insert-only Supabase requests, and V012 authorization. Automated tests and live RLS verification passed; device smoke testing remains for APK distribution.
<!-- SECTION:FINAL_SUMMARY:END -->
