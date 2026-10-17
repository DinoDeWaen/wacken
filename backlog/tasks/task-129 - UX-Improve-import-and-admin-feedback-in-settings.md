---
id: task-129
title: 'UX: Improve import and admin feedback in settings'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-19 06:04'
updated_date: '2026-06-19 18:57'
labels:
  - ux
  - settings
  - import
dependencies: []
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Settings contains group, import, and sync/admin actions. Make import/sync outcomes easier to understand with explicit success/failure summaries, last sync time, and next recommended action.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a sync or import completes, when the user remains on settings, then a concise result summary is shown.
- [x] #2 Given a sync or import fails, when the user sees the error, then the message includes what can still be used offline and what action to try next.
- [x] #3 Settings actions remain visually grouped by normal user actions versus admin/import actions.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added BDD-style feedback-message tests through TDD for successful sync time and offline recovery guidance.
2. Added explicit sync completion time, pending summary, and next action to Settings.
3. Returned import outcomes to Settings and added success/failure recovery language while keeping user and admin actions in separate panels.
4. Ran focused and full Android validation, deleted the prior release APK, then built and verified a fresh signed APK.
5. Recorded validation evidence and closed the task.
Architecture impact: not architecture-significant; feedback is held for the active UI visit and passed through Android activity results. No new persistent model, schema, API, dependency, or sync semantics were introduced. No ADR required.
Documentation impact: README, business requirements, and diagrams unchanged because no setup, workflow, architecture, or business rule changed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Settings now reports successful sync time, pending summary, and a next action.
- Failed sync and import outcomes state that cached data remains usable offline and tell the user what to retry. Import returns its result to Settings.
- Admin/import remains a distinct Settings section with an explanation that imports change shared master data while preserving ratings.

## Acceptance criteria validation

- AC1: Settings shows concise sync and returned import result summaries.
- AC2: Failure messages retain offline usability and the next retry action.
- AC3: Group/rating/sync actions remain separate from the Admin import section.

## How to test

### Automated tests
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.SettingsFeedbackTest :app:compileDebugJavaWithJavac`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease`
- APK signature verification passed for v1 and v2.

### Manual validation
- Complete or fail a sync from Settings, then import valid or invalid CSV data and return to Settings to inspect the concise feedback.

## TDD / BDD / approval-test evidence
- Added `SettingsFeedbackTest` first for success and recovery scenarios.

## Architecture impact
- Architecture-significant change: no.
- Approval received: not required.
- ADR impact: none, because no architectural boundary, persistence schema, external API, or dependency changed.

## README impact
- README impact: none, because setup, public workflow, architecture, commands, and troubleshooting instructions did not change.

## Business requirements impact
- Business requirements impact: none, because this improves feedback for existing workflows without changing product rules.

## Diagram impact
- Diagram impact: none, because the existing system structure is unchanged.

## Commits / logical change list
- Added tested feedback summaries and Settings/import activity-result wiring.
- Rebuilt signed release APK: `app/build/outputs/apk/release/app-release.apk`, SHA-256 `24288375d69930c7450850011dc895bd577b71ed85d2e0cf519dd1f3681fbe67`.

## Risks and follow-up
- Sync completion time is scoped to the active Settings visit; persisting it across restarts would require a separate approved persistence task.
<!-- SECTION:NOTES:END -->
