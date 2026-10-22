---
id: TASK-206
title: 'US: Navigate Bands, Schedule and Settings with shared labeled destinations'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 09:21'
updated_date: '2026-10-22 04:44'
labels:
  - ux
  - release-3.0
milestone: m-2
dependencies:
  - TASK-205
references:
  - mainnavigation
modified_files:
  - app/src/androidTest/java/be/wacken/planner/CriticalFlowsAndroidTest.java
  - app/src/main/java/be/wacken/planner/MainNavigation.java
  - app/src/main/res/values/ids.xml
  - app/src/main/java/be/wacken/planner/MainActivity.java
  - app/src/main/java/be/wacken/planner/ScheduleActivity.java
  - app/src/main/java/be/wacken/planner/SettingsActivity.java
  - app/src/main/java/be/wacken/planner/BandDetailActivity.java
  - >-
    groma/systems/wacken/containers/android-application/components/mainnavigation.md
  - >-
    groma/systems/wacken/containers/android-application/components/mainactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/scheduleactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/settingsactivity.md
  - backlog/docs/groma-navigation-review-2026-10-21.md
  - tools/groma/architecture-baseline.json
  - README.md
  - backlog/docs/business-requirements.md
  - scripts/architecture/verify-groma.mjs
  - .github/workflows/ci.yml
priority: medium
type: enhancement
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As an attendee, I want consistent labeled destinations so I can move between rating and planning without losing context. Implement the approved three-destination pattern, detail/back behavior and accessible current-destination indication. Keep sync/close and festival management reachable, including return from external music apps. No changes to schedule rules or festival ownership.

Validated baseline: TASK-201, design/release-2.34/themes.html; user approved 2026-10-17. Keep the existing Android Views stack and current domain/application/infrastructure boundaries. Assess architecture impact before implementation; significant changes require explicit approval and ADR handling.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given any main destination, when Bands/Schedule/Settings is selected, then the named destination and selected state are exposed visually and to accessibility services.
- [x] #2 Given band detail or a review subflow, when Back is used, then the user returns to the correct band row or initiating Settings context without duplicate navigation.
- [x] #3 Given the same flow in dark/light or after external-app return, then destination order and action meaning remain consistent and state is retained.
- [x] #4 Native navigation tests cover Back, recreation, external-app return, offline use and reachable sync/close.
- [x] #5 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Characterized offline navigation and retained row/review context with red native destination scenario. 2. Composed stateless shared labeled navigation with existing Activity reuse/no-op current selection and stable hierarchy IDs; corrected detail Back wording. 3. Passed80 native cases on each API23/API36, JVM/QA/coverage/ArchUnit/lint/API/database and signed build gates. 4. Curated/rendered source-supported Groma component and7 collaborations, reviewed unchanged duplicate identities/pins/exceptions and explicit scanner-evidence growth; two scans pass. 5. Updated README/requirements and recorded signed development APK plus complete validation. Deviations: real package launcher fixture; input-focus and layout synchronization for native timing; corrected generic CI plugin restore to exact reviewed scanner. Architecture: no new significant decision, approved standard/ADR0024 retained. Remaining layouts/accessibility/publication are207–213.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
Shared labeled Bands/Schedule/Settings navigation now exposes fixed order and current state, reuses existing Activities, and leaves current selection unchanged. Detail Back and Settings review Back retain caller context. Stable content/list IDs support hierarchy restoration; sync/close and festival actions remain reachable.

## Acceptance criteria validation
AC1: native destination/selection/accessibility assertions in both appearances. AC2: original Activity identity and band row/review Settings scroll retained on Back. AC3: recreation and actual Home/package-launcher return preserve Settings; no duplicate destination. AC4: offline native tests plus reachable sync/close/festival actions; all80 native tests pass on API23 and API36. AC5: README, requirements and reviewed Groma map updated with development scope.

## How to test
### Automated tests
CI https://github.com/DinoDeWaen/wacken/actions/runs/39722244834 passed all4 jobs on d1bfcd315105be9cae75a6be038e96572c82b7ce:80 native tests each on API23/API36; JVM/unit/coverage, QA, lint/API compatibility, ArchUnit, signed-upgrade-runner8 and image-acquisition6 safety tests, real PostgreSQL/PostgREST creation and integrated lifecycle gates. Local app unit regressions and full80-case native suite passed; normal semantic Groma gate passed with two scans (96 components,297 sources,13 flows,88 duplicate identities), plus27 positive/negative fixtures. git diff --check passed.
Clean clone at6d5e9c8: JDK21 ./gradlew test qaTest :app:lintDebug assembleRelease --rerun-tasks passed. APK /private/tmp/wacken-206-build-checkout/app/build/outputs/apk/release/app-release.apk SHA256 c0c0e74eb0240b389407fedfd74b1222eb96040aa5c61c836096c144bd807eb8. apksigner verify passed stable certificate SHA256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. Later commits change test synchronization, gate error diagnostics, exact pinned scanner setup and docs only; production source is identical. APK is a development checkpoint, not published.
### Manual validation
Rendered Groma component/source/seven collaborations inspected in exported map. No claim of manual native TalkBack,200% or daylight validation; integrated appearance/accessibility matrix belongs TASK212. To inspect: move among all3 destinations, open/Back a band and Settings review, change appearance, send app Home and relaunch; verify retained context and sync/close.

## TDD / BDD / approval-test evidence
New native shared-destination scenario failed before implementation, passed afterward. Existing flows characterize ratings/offline behavior. Launcher fixture corrected to real package-launcher intent. API23 logcat exposed Back injection before review window input focus; awaitActivity now waits for focus and final native suites both pass without production workaround.

## Architecture impact
Architecture-significant change: no additional change beyond approved standard Views/controller composition. Approval received: user Standard; ADR0024 retained. Stateless navigation introduces no framework, module, persistent model or domain/data policy. Groma CLI authored supported collaborations, reviewed exact unchanged duplicate identities and pins/exceptions; reviewed source evidence growth only. Generic plugin restore unexpectedly selected a newer scanner in CI; exact @groma/scanner-java@0.2.2 restoration fixes setup without changing reviewed pins or weakening the gate.
ADR impact: none, because stateless presentation navigation uses the approved ADR0024 architecture without a new boundary or policy.

## README impact
README impact: updated development navigation behavior, map counts and exact scanner restoration command; published version remains2.35.1.

## Business requirements impact
Business requirements impact: updated planned BR061 development increment and state retention, preserving ADR0016 lifecycle and future3.0 publication distinction.

## Diagram impact
Diagram impact: updated Groma primary navigation component and seven source-supported collaborations via CLI; runtime boundaries and13 flows unchanged. Review: backlog/docs/groma-navigation-review-2026-10-21.md.

## Commits / logical change list
c532b50 shared navigation;6d5e9c8 docs/map review and bounded test layout wait;fab77eb gate diagnostics;c4770ca exact scanner restore;6187972 setup docs;d1bfcd3 native input-focus synchronization.

## Risks and follow-up
Remaining screen layout/search/detail/schedule/admin/support work207–211, integrated validation212 and final signed upgrade/publication213 remain. Two previously user-accepted deleted importer arrows remain exactly pinned; no new exception. No release published by this task.
<!-- SECTION:NOTES:END -->
