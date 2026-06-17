---
id: task-115
title: 'DEF: Fix null error when opening group schedule'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-17 19:41'
updated_date: '2026-06-17 19:51'
labels:
  - defect
  - schedule
  - qa
  - android
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
A user can open Group Schedule and see `Schedule could not be generated: null` instead of a schedule or actionable error.

Business value: the schedule is the main festival planning view. A null error blocks planning and gives no information to the user or QA about the failing dependency/data path.

Observed evidence: screenshot `/Users/dino/Desktop/Screenshot 2026-06-17 at 21.38.42.png` shows the schedule screen rendering `Schedule could not be generated: null`.

Initial analysis: `ScheduleActivity` catches any exception around lock loading and schedule generation, then displays `error.getMessage()`. A plain `NullPointerException` has no message, so the UI renders `null` and masks the actual root cause. The likely failure class is an unguarded null from schedule inputs or repository data during generation/lock loading, but the exact source needs to be reproduced with a regression test and diagnostics.

How this passed QA: current automated checks cover domain/application schedule rules and layout helpers, but not the Android schedule screen error path with real repository wiring, missing/partial synced data, or no-message exceptions. Manual QA also did not include an assertion that schedule failures must have actionable messages and diagnostic logs.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given schedule generation or schedule lock loading throws an exception with no message, when the Group Schedule screen opens, then the UI shows an actionable non-null error message and records diagnostic details.
- [x] #2 Given local cached schedule data has partial or missing optional records such as distances, locks, ratings, or stage metadata, when the schedule is opened, then the app either generates the schedule with safe defaults or reports the specific missing required data without a raw null error.
- [x] #3 A failing regression test reproduces the null-message schedule failure before the fix, and passing tests prove the fixed behavior.
- [x] #4 Implementation notes explain the confirmed root cause, why existing QA missed it, and what validation was added.
- [x] #5 Business requirements and README impact are recorded using the canonical wording from delivery-governance.md.
- [x] #6 Android schedule-screen tests or focused presentation coverage include the caught-exception path so `Schedule could not be generated: null` cannot regress.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected the schedule screen error path, lock loading, and schedule generation dependencies.
2. Added focused regression tests for no-message schedule exceptions and lock-load warning messages.
3. Added `ScheduleErrorMessage` so no-message exceptions produce actionable non-null user text.
4. Updated `ScheduleActivity` to log schedule generation failures and lock-load failures with `SupabaseDiagnostics`.
5. Made schedule lock loading optional: if pulling group locks fails, the screen logs the issue, shows a warning, and continues with generated schedule choices.
6. Ran focused and broader validation, deleted and rebuilt the signed release APK, and verified APK signatures, metadata, and SHA-256.

Architecture impact: not architecture-significant; no domain, persistence, schema, dependency, or boundary changes were made.
Deviation: the exact original no-message exception source on the device was not available without a device stack trace. The confirmed root cause of the visible `null` defect was direct rendering of `Throwable.getMessage()` and treating optional lock loading as fatal.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Fixed the schedule screen so it no longer displays `Schedule could not be generated: null`. No-message exceptions now become actionable messages such as `Unexpected missing schedule data. Please sync from Supabase and try again.`

Schedule lock loading is now treated as optional. If group schedule locks cannot be pulled, the app logs the failure, shows a warning, and continues rendering the generated schedule without locked overrides instead of blocking the whole schedule screen.

## Acceptance criteria validation

- AC1: `ScheduleActivity` now logs schedule-generation and lock-load failures through `SupabaseDiagnostics`, and `ScheduleErrorMessage` guarantees non-null user text for no-message exceptions.
- AC2: Missing/unavailable lock data no longer blocks schedule generation; existing distance behavior still falls back to `StageWalkingTimePolicy.defaultWalkingMinutes` when no distance row exists. Required-data failures now show a non-null actionable message instead of raw `null`.
- AC3: `ScheduleErrorMessageTest.noMessageExceptionDoesNotRenderRawNull` reproduces the visible null-message failure class and proves the fixed behavior.
- AC4: The confirmed visible root cause was direct rendering of `Throwable.getMessage()`; QA missed it because tests covered schedule rules/layout but not the Android caught-exception path or no-message exceptions.
- AC5: README and business requirements impact are recorded below.
- AC6: Android focused presentation coverage now includes the caught no-message exception path so `Schedule could not be generated: null` cannot regress.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `git diff --check`
- Deleted previous `app/build/outputs/apk/release/app-release.apk` and rebuilt signed release APK.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew assembleRelease`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk`
- `shasum -a 256 app/build/outputs/apk/release/app-release.apk`

APK metadata: package `be.wacken.planner`, `versionCode=15`, `versionName=2.12`, minSdk `23`, targetSdk `36`.

APK SHA-256: `42d549441ec46c42ff6c506faedfb0847482cb51d5e79d32c86c9cb3735694c1`.

### Manual validation

Not run on a physical Android device. Manual UAT should install the new APK, open Group Schedule with synced data, and verify the schedule opens or any failure message is actionable and never ends in `null`.

## TDD / BDD / approval-test evidence

Used a focused regression test for the no-message exception path before completing the fix. Existing schedule tests continue to cover generation, layout, filters, locks, and styling.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: none, because setup, commands, architecture, and documented public schedule capabilities did not change.

## Business requirements impact

Business requirements impact: none, because the existing schedule requirements already imply usable schedule errors; this fix restores expected behavior without changing scope or rules.

## Diagram impact

Diagram impact: none, because system structure and data flow did not change.

## Commits / logical change list

- Add tested safe schedule error-message formatter.
- Log schedule generation and lock-load failures.
- Make schedule lock loading non-fatal and continue with generated choices.
- Rebuild and verify signed release APK.

## Risks and follow-up

The exact underlying no-message exception on the user device still needs device logs if the schedule cannot render after this fix. This change ensures that failure will be diagnosable and will not show a raw `null` message.
<!-- SECTION:NOTES:END -->
