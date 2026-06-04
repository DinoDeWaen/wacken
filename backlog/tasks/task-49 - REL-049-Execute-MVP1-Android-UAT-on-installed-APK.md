---
id: task-49
title: 'REL-049: Execute MVP1 Android UAT on installed APK'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-04 18:50'
updated_date: '2026-06-04 19:58'
labels:
  - mvp1
  - release
  - uat
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## Business value

MVP1 should be verified from the installable Android APK, not only from automated tests, so the release has evidence that the user-facing flow works on a device or emulator.

## User story

As the app owner, I want the MVP1 UAT checklist executed against an installed APK, so that V1.0 can be released with confidence in the import, listing, detail, rating, persistence, and invalid-import flows.

## Scope

In scope:
- Build and install the debug APK on an Android device or emulator.
- Execute the existing MVP1 Android UAT checklist with the sample CSV files.
- Record pass/fail evidence and any release-blocking defects.
- Confirm the previously closed Supabase sync issue is not part of this task.

Out of scope:
- Fixing newly discovered defects; create separate defect tasks if needed.
- Adding new MVP1 features.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the debug APK is built and installed, when the MVP1 UAT checklist is executed, then each checklist step has recorded pass/fail evidence.
- [x] #2 Given the valid sample CSV files are imported, when the band overview and detail screens are checked, then the expected bands, schedule data, links, and ratings are visible.
- [x] #3 Given a rating is changed and the app is restarted, when the band list is reopened, then the changed rating is still visible.
- [x] #4 Given invalid sample performance data is imported, when validation runs, then row-level validation feedback is shown and existing data remains intact.
- [x] #5 README impact, business requirements impact, diagram impact, and ADR impact are recorded using the canonical wording from delivery-governance.md.
- [x] #6 The task notes record whether any release-blocking defects were found and list follow-up task IDs for defects if needed.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Built the debug APK with JDK 21.
2. Launched the configured Medium_Phone_API_36.0 emulator headlessly and installed the APK.
3. Created/confirmed a disposable Supabase UAT user and seeded the valid app session for emulator UAT because the app now requires Supabase Auth and no UAT login was documented.
4. Executed MVP1 UAT for authenticated overview, Supabase-loaded lineup, valid CSV import, detail links/schedule, rating save, restart persistence, and invalid import feedback.
5. Found a release-blocking detail-rating visual defect, created task-52, fixed it, and re-ran the affected detail-rating UAT successfully.
6. Recorded final UAT evidence and closed the task.

Deviation: the old checklist empty-state expectation is superseded by the current authenticated Supabase sync flow; after clean sign-in the app syncs central master data instead of staying empty. The file-based CSV import path was still validated with the MVP1 sample files.
Architecture impact: not architecture-significant. No approval or ADR required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Executed MVP1 Android UAT on the Medium_Phone_API_36.0 emulator using the debug APK. The UAT found one release-blocking visual defect in the detail rating stars; task-52 was created, fixed, validated, and closed. After that fix, the MVP1 UAT gate passed.

## Acceptance criteria validation

- AC1: Passed. The APK was built, installed, and UAT evidence was recorded through emulator screenshots and task notes.
- AC2: Passed. The band overview and detail screens showed the expected sample bands, schedule data, music links, and visible ratings after task-52.
- AC3: Passed. A detail rating change persisted after force-stopping and reopening the app.
- AC4: Passed. Invalid sample performance import showed row-level unknown band/stage errors and preserved the previously imported valid data and rating.
- AC5: Impact notes recorded below.
- AC6: Passed. Release-blocking defect task-52 was found, fixed, and closed; no remaining UAT release blockers are known.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug
- task-52 follow-up checks also passed: JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:compileDebugJavaWithJavac and :app:testDebugUnitTest

### Manual validation

- Installed app/build/outputs/apk/debug/app-debug.apk on Medium_Phone_API_36.0 emulator.
- Confirmed authenticated band overview loads.
- Selected samples/mvp1/bands.csv, stages.csv, performances.csv, distances.csv, and food.csv through Android DocumentsUI and confirmed successful import.
- Confirmed imported list shows 5th Avenue and Midnight Skyline with stage/time data.
- Opened 5th Avenue detail and confirmed schedule data, music links, and visible rating stars after task-52.
- Changed a rating and confirmed overview refresh and restart persistence.
- Re-ran import with invalid-performances.csv and confirmed row-level unknown band/stage errors while existing data remained visible.

## TDD / BDD / approval-test evidence

This was release UAT rather than new behavior. The UAT found task-52, which was validated with focused emulator regression and app unit tests.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: none, because UAT used existing documented build/import flows and did not change setup, commands, or architecture.

## Business requirements impact

Business requirements impact: none, because UAT validated existing MVP1 behavior and did not change product scope or business rules.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- Executed emulator UAT for MVP1 release readiness.
- Created and closed task-52 for the detail rating visual blocker.

## Risks and follow-up

- The legacy UAT checklist still mentions an empty unauthenticated state, but current MVP1 behavior requires Supabase Auth and syncs central master data after sign-in. No release blocker remains from this mismatch, but the checklist should be refreshed in a future docs cleanup if desired.
- A disposable Supabase UAT user was created for this validation.
<!-- SECTION:NOTES:END -->
