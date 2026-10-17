---
id: task-174
title: 'TEST: Add Android instrumentation smoke tests for critical flows'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-19 11:02'
updated_date: '2026-10-17 10:00'
labels:
  - future
  - testing
  - android
  - qa
milestone: m-0
dependencies: []
modified_files:
  - app/src/androidTest/java/be/wacken/planner/CriticalFlowsAndroidTest.java
  - scripts/verify-android-smoke.sh
  - README.md
  - .github/workflows/ci.yml
priority: low
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: The highest-risk Android journeys can be verified on device or emulator before release instead of relying only on JVM tests.

As a maintainer, I want a small instrumentation smoke suite so that releases catch broken Android wiring, navigation, and offline startup behavior.

Scope: critical smoke tests only: startup with cache, band detail rating screen, schedule open, settings export entry point, and network-free launch behavior.

Out of scope: full visual regression coverage, exhaustive UI tests, and flaky tests that depend on live Supabase.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the instrumentation suite runs on an emulator or device, then it verifies the app can start with cached data and no network.
- [x] #2 Given the band detail screen is opened, then the smoke suite verifies rating controls render and can be interacted with without crashing.
- [x] #3 Given the group schedule is opened, then the smoke suite verifies the screen renders without the previous null schedule failure.
- [x] #4 The test command is documented and either added to CI or explicitly documented as manual release validation.
- [x] #5 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording when implemented.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Final: reused existing instrumentation/Room/CI architecture, added four real-Activity offline characterization scenarios and a fixture-restoring emulator-only runner, wired the existing CI job to all19 native tests, documented focused/all commands. Focused4/4 and full19/19 pass with no skips on isolated API36 ARM64 data images; 184 JVM tests passed with 10 existing isolated-database scenarios deferred to their separate mandatory release gate. Fresh signed baseline APK rebuilt with all tasks rerun and v1/v2 verified. Deviation: debug/release certificate mismatch required isolated emulator data rather than uninstalling existing release data; corrected one schedule assertion to account for rating stars. No production behavior, dependency, schema or architecture change; no ADR required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
Added native characterization of cached offline startup, the real detail planning-rating touch/reset control, a populated cached schedule and the enabled Settings CSV export entry. Added an emulator-only offline runner and made the existing CI instrumentation job use it for all native tests. Existing production Activities, repositories, schemas and framework choices are unchanged.

## Acceptance criteria validation
AC1: startup renders fixture lineup while Wi-Fi/mobile data are off and retains pending cache data.
AC2: actual RatingStarsView touch saves planning 5 as PENDING; Reset returns it to zero.
AC3: schedule renders the fixture band with rating stars and its 18:00–19:00 time range.
AC4: README documents focused/all commands; CI calls the all-suite offline runner.
AC5: impacts and evidence below.

## How to test
### Automated tests
sh scripts/verify-android-smoke.sh emulator-5554: PASS, 4 tests, zero failures/skips.
sh scripts/verify-android-smoke.sh emulator-5554 all: PASS, 19 native tests, zero failures/skips on API36 ARM64 emulator.
JDK21 ./gradlew :app:testDebugUnitTest :app:assembleRelease --rerun-tasks with existing keychain signing: PASS. JVM report: 194 tests, 184 executed successfully, 10 existing database-integration tests skipped by their explicit isolated-API prerequisite; these still require scripts/verify-festival-creation.sh at the final release gate.
sh -n scripts/verify-android-smoke.sh and git diff --check: PASS.
apksigner verify --verbose app/build/outputs/apk/release/app-release.apk: verified v1/v2. Fresh baseline validation APK is be.wacken.planner, versionName 2.33.5, code41; this is not a published 2.34 release.
SHA256: d607a69c805a3c20f41787854e48fc7b31c2bed6142c804c635f4fb9fa016910.

### Manual validation
Used a separate writable emulator data directory /private/tmp/wacken-smoke-174, preserving the existing signed release installation. The runner rejects physical devices and multiple connected targets, verifies offline prerequisites and restores emulator network state with a trap. Tests restore fixture/session state. Export coverage checks its enabled wired entry point, not an actual share/send action.

## TDD / BDD / approval-test evidence
This is native characterization of existing behavior before further feature changes. Four Given/When/Then journeys are executable in CriticalFlowsAndroidTest. Initial execution was blocked by the normal debug/release certificate mismatch and executed zero tests; isolated writable images resolved that without uninstalling existing data. First actual run had three passes and one overly exact schedule-label assertion (the production label includes stars); corrected the assertion to verify the real band label plus time range, then 4/4 and full19/19 passed. No production regression was invented or hidden.

## Architecture impact
Architecture-significant change: no; incremental coverage uses the existing runner, Room, fixture session and CI job introduced by prior approved work. No new dependency, runtime seam, provider or persistence contract.
Approval received: user requested finishing 2.34; emulator/build commands used scoped tool approvals.
ADR impact: none, because this extends tests within the existing architecture and test strategy.

## README impact
README impact: updated focused/full offline native commands, CI usage, isolation and certificate-mismatch guidance.

## Business requirements impact
Business requirements impact: none, because no product behavior changes and the requirements already permit meaningful instrumentation in a future task.

## Diagram impact
Diagram impact: none, because no production component or relationship changes.

## Commits / logical change list
1. CriticalFlowsAndroidTest with fixture restoration and four real-Activity journeys.
2. scripts/verify-android-smoke.sh with emulator/network safeguards and focused/all selection.
3. Existing CI native job and README validation commands.

## Risks and follow-up
Local native evidence is API36 ARM64; CI x86_64 execution is still pending on push. Full device/accessibility/appearance checks belong to 3.0 TASK-212. The ten database API scenarios remain a separate mandatory final-release gate. 2.34 features and distribution tasks remain open; no release was published by this task.
<!-- SECTION:NOTES:END -->
