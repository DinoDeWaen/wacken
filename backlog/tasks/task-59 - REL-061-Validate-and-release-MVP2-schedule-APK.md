---
id: task-59
title: 'REL-061: Validate and release MVP2 schedule APK'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-06-07 15:37'
updated_date: '2026-06-07 16:29'
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
- [ ] #3 Given the Android schedule screen is tested, when the APK is installed locally, then users can sync, rate, generate, and view the shared schedule.
- [x] #4 Release notes document MVP2 scope, validation, known non-goals, and accepted risks.
- [x] #5 The GitHub release is published with the debug APK attached and the tag/version metadata recorded.
- [x] #6 README, business requirements, diagram, ADR, and architecture impact are recorded using canonical delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirm all dependent MVP2 stories are Done and inspect current Android version/tag/release-note conventions.
2. Add an MVP2 UAT checklist covering group ratings, overlaps, optional decisions, veto-blocked decisions, conflict winners, lost alternatives, sync/rating flow, and invite flow.
3. Bump Android debug APK metadata to the MVP2 release version and add MVP2 release notes.
4. Run automated validation across domain/application/infrastructure/app plus backend verification where available.
5. Assemble the debug APK and record artifact path/version metadata.
6. Publish a GitHub release with the APK attached if the local GitHub tooling is authenticated.
7. Close the task with delivery-governance validation notes and canonical impact wording.

Architecture impact: not architecture-significant; release packaging/docs/version metadata only. No schema, API, architecture, dependency, or domain behavior change planned. ADR impact expected: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Prepared the MVP2 V2.0 debug APK release package: Android metadata is now `versionCode 3` / `versionName 2.0`, release notes are in `releases/v2.0.md`, and the MVP2 UAT checklist is in `backlog/docs/mvp2-android-uat-checklist.md`. Automated validation, backend validation, and debug APK assembly passed.

Installed-device UAT could not be executed from this environment because `adb devices` reported no connected devices or emulators. This is recorded as an accepted risk in the release notes and remains the only release validation gap.

## Acceptance criteria validation

- AC1: Automated tests and debug APK assembly passed.
- AC2: MVP2 UAT checklist covers representative group ratings, overlaps, optional decisions, veto-blocked cases, conflict winners, and lost alternatives. Domain/application tests also cover decision, conflict, and timeline behavior.
- AC3: Not completed in this environment. `adb devices` reported no connected devices or emulators, so installed local APK UAT could not be run.
- AC4: `releases/v2.0.md` documents MVP2 scope, validation, known non-goals, and accepted risks.
- AC5: Pending GitHub release publication after commit/tag/push.
- AC6: README, business requirements, diagram, ADR, and architecture impact are recorded below.

## How to test

### Automated tests

- `/bin/zsh -lc JAVA_HOME=java21 ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug`
- `backend/flyway/run-flyway.sh migrate`
- `backend/flyway/verify-auth-setup.sh`
- `git diff --check`

### Manual validation

- `adb devices` returned no connected devices or emulators, so installed-device UAT was not run. Use `backlog/docs/mvp2-android-uat-checklist.md` for device validation after installing `app/build/outputs/apk/debug/app-debug.apk`.

## TDD / BDD / approval-test evidence

This release task does not add product behavior. It packages the already completed MVP2 stories and provides a UAT checklist derived from the MVP2 acceptance scenarios. Existing story tests cover group decisions, conflict detection, conflict resolution, timeline generation, Android schedule wiring, rating clear behavior, return-to-row behavior, and invite text.

## Architecture impact

- Architecture-significant change: no. This is release packaging, version metadata, and documentation. No schema, API, dependency, domain, persistence, or module-boundary change was introduced.
- Approval received: not required.
- ADR: none required.

## README impact

README impact: updated release/UAT links with the MVP2 checklist and V2.0 release notes.

## Business requirements impact

Business requirements impact: none, because this task packages already documented MVP2 behavior and does not change product scope or business rules.

## Diagram impact

Diagram impact: none, because the system structure and data flow did not change.

## Commits / logical change list

- Bump Android debug APK metadata to V2.0.
- Add MVP2 Android UAT checklist.
- Add V2.0 release notes.
- Link MVP2 UAT/release docs from README.

## Risks and follow-up

- Installed-device UAT remains to be run on a connected Android device or emulator.
- V2.0 is a debug APK release for local installation, not Play Store distribution.

Release publication update:

- GitHub release published: https://github.com/DinoDeWaen/wacken/releases/tag/v2.0
- Attached APK: app/build/outputs/apk/debug/app-debug.apk
- Branch pushed: codex-task-2-testing-coverage
- Remaining validation gap: installed-device UAT still needs a connected Android device or emulator.
<!-- SECTION:NOTES:END -->
