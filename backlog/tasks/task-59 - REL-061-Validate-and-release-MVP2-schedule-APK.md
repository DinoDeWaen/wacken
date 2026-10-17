---
id: task-59
title: 'REL-061: Validate and release MVP2 schedule APK'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-07 15:37'
updated_date: '2026-06-10 07:26'
labels:
  - mvp2
  - release
  - uat
dependencies:
  - task-57
  - task-58
  - task-60
  - task-61
  - task-62
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Release Wacken Planner 2026 MVP2 after the one-group decision engine, conflict resolution, timeline generation, Android schedule view, and invite story are complete enough for local installation.

In scope:
- Create an MVP2 UAT checklist with representative group ratings, overlaps, optional decisions, vetoes, and lost alternatives.
- Run automated validation across domain/application/infrastructure/app as relevant.
- Run Android debug APK build and focused manual schedule UAT.
- Add release notes and bump version metadata for the MVP2 release version.
- Publish the GitHub release with the APK attached.

Out of scope:
- Play Store distribution, MVP3 travel/lunch/food behavior, and MVP4 PDF export.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given MVP2 stories are complete, when release validation runs, then automated tests and debug APK assembly pass.
- [x] #2 Given representative group ratings and overlapping performances, when MVP2 UAT is run, then the generated schedule demonstrates GO, OPTIONAL, veto-blocked, conflict-winner, and lost-alternative cases.
- [x] #3 Given the Android schedule screen is tested, when the APK is installed locally, then users can sync, rate, generate, and view the shared schedule.
- [x] #4 Release notes document MVP2 scope, validation, known non-goals, and accepted risks.
- [x] #5 The GitHub release is published with the debug APK attached and the tag/version metadata recorded.
- [x] #6 README, business requirements, diagram, ADR, and architecture impact are recorded using canonical delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirmed dependent MVP2 stories were completed, including follow-up crash fix task-63 and schedule-star task-65.
2. Kept the MVP2 UAT checklist and V2.0 release notes as the release evidence.
3. Rebuilt and validated the current debug APK after follow-up MVP2 fixes.
4. Replaced the GitHub v2.0 release APK asset with the current debug APK and updated the release notes body.
5. Re-ran device discovery; no Android device or emulator was attached, so installed-device UAT remains an accepted release risk.
6. Closed the task with delivery-governance validation notes and canonical impact wording.

Deviation: original installed-device UAT could not be run from this environment because adb reported no attached devices. Per user request to finalise task-59, the remaining device-UAT gap is recorded as an accepted risk for this debug APK release rather than left as an open release blocker. Architecture impact: not architecture-significant; release documentation and release asset refresh only. ADR impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Finalised the MVP2 V2.0 debug APK release package. The GitHub `v2.0` release is published at https://github.com/DinoDeWaen/wacken/releases/tag/v2.0 with `app-debug.apk` attached. The release asset was refreshed after the Android group-schedule runtime crash fix and after adding winner/lost-alternative stars to the schedule. Release notes in `releases/v2.0.md` now document those package updates and the accepted installed-device UAT risk.

Android metadata remains `versionCode 3` / `versionName 2.0`, with the debug APK at `app/build/outputs/apk/debug/app-debug.apk`.

## Acceptance criteria validation

- AC1: Automated tests and debug APK assembly passed on the final package.
- AC2: MVP2 UAT checklist covers representative group ratings, overlaps, optional decisions, veto-blocked cases, conflict winners, and lost alternatives. Domain/application tests cover decision, conflict, and timeline behavior.
- AC3: No Android device or emulator is attached in this Codex environment, so installed-device UAT could not be executed here. `adb devices` returned only `List of devices attached`. Per user request to finalise task-59, this is accepted as a remaining release risk for the debug APK, and the UAT checklist remains the device-validation path.
- AC4: `releases/v2.0.md` documents MVP2 scope, validation, known non-goals, accepted risks, and post-release APK refreshes.
- AC5: GitHub release `v2.0` is published with the current debug APK attached and version metadata recorded.
- AC6: README, business requirements, diagram, ADR, and architecture impact are recorded below using delivery-governance wording.

## How to test

### Automated tests

- `/bin/zsh -lc 'JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug'`
- `git diff --check`
- `gh release upload v2.0 app/build/outputs/apk/debug/app-debug.apk --repo DinoDeWaen/wacken --clobber`
- `gh release edit v2.0 --repo DinoDeWaen/wacken --notes-file releases/v2.0.md`

### Manual validation

- `adb devices` returned no connected devices or emulators, so installed-device UAT was not run from this environment. Use `backlog/docs/mvp2-android-uat-checklist.md` after installing the GitHub release APK to complete physical-device validation.

## TDD / BDD / approval-test evidence

This release task does not add product behavior. It packages completed MVP2 behavior and release fixes. Existing story tests cover group decisions, conflict detection, conflict resolution, timeline generation, Android schedule wiring, rating clear behavior, return-to-row behavior, invite text, Android runtime compatibility for the schedule path, and schedule winner/lost-alternative stars.

## Architecture impact

- Architecture-significant change: no. This is release documentation, release evidence, and release asset refresh only. No schema, API, dependency, domain, persistence, or module-boundary change was introduced.
- Approval received: not required.
- ADR impact: none, because no architecture-significant decision was made.

## README impact

README impact: none, because README already links the MVP2 UAT checklist and V2.0 release notes; this finalisation only refreshes the release notes and GitHub release asset.

## Business requirements impact

Business requirements impact: none, because this task packages already documented MVP2 behavior and does not change product scope or business rules.

## Diagram impact

Diagram impact: none, because the system structure and data flow did not change.

## Commits / logical change list

- Updated `releases/v2.0.md` with post-release package updates for task-63 and task-65.
- Rebuilt and validated the current debug APK.
- Replaced the GitHub `v2.0` APK asset with the current APK.
- Updated the GitHub `v2.0` release body from `releases/v2.0.md`.
- Finalised task-59 with the device-UAT gap recorded as an accepted release risk.

## Risks and follow-up

- Installed-device UAT remains to be run on a connected Android device or emulator using `backlog/docs/mvp2-android-uat-checklist.md`.
- V2.0 is a debug APK release for local installation, not Play Store distribution.
<!-- SECTION:NOTES:END -->
