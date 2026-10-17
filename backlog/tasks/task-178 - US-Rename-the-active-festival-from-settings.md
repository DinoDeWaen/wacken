---
id: task-178
title: 'US: Rename the active festival from settings'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-23 09:33'
updated_date: '2026-08-23 13:21'
labels:
  - user-story
  - festival
  - settings
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: The active festival name can be corrected after creation without losing ratings, lineup links, archive continuity, or sync context.

As a festival admin, I want to rename the currently active festival from Settings, so that typos or placeholder names can be fixed while preserving the same festival identity.

Scope: Settings/Admin workflow for the single active festival. Users can open a rename action, enter a non-blank new display name, save it, and see the updated name on active festival screens. The festival id and all existing lineup, planning rating, personal rating, and schedule references remain unchanged.

Out of scope: editing archived festival names, changing festival ids, managing multiple upcoming festivals, and renaming remote Supabase rows unless the active festival persistence/sync contract already supports it.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a festival is active, when the user opens Settings/Admin, then a rename-active-festival action is available.
- [x] #2 Given the user saves a non-blank new festival name, then the active festival display name is updated without changing the festival id or linked lineup and ratings.
- [x] #3 Given the new festival name is blank or whitespace, when the user saves, then the app blocks the rename and shows a clear required-name message.
- [x] #4 Given no festival is active, then the rename-active-festival action is not shown or is disabled with clear feedback.
- [x] #5 Automated tests cover successful rename, blank-name validation, and identity preservation.
- [x] #6 Architecture impact is assessed before implementation; if persistence or sync contracts must change, explicit approval is requested before coding and ADR impact is recorded.
- [x] #7 Business requirements and README impact are recorded using delivery-governance wording when implemented.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added application tests for active-festival rename success, blank-name rejection, no-active-festival handling, and identity preservation.
2. Added RenameActiveFestivalUseCase and result type using the existing FestivalRepository save-by-id contract.
3. Added Festival.rename to preserve id/status while changing only the display name.
4. Added a Settings/Admin rename action when an active festival exists, using a dialog with required-name feedback.
5. Added SettingsActivityRegressionTest coverage for the Settings rename action.
6. Ran focused application/app validation and closed task-178.

Design approach: standard application-use-case slice with Android UI at the edge; no persistence schema or Supabase contract change.
Test strategy: TDD with application use-case tests plus app source regression.
Architecture impact: not architecture-significant because existing repository and Room upsert behavior support renaming by id. Approval/ADR not required.
Deviation: remote Supabase festival rename sync remains out of scope per task wording.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Implemented active-festival rename from Settings. Settings now shows a `Rename active festival` Admin action only when an active festival exists. Saving a valid name updates the same festival record by id; blank names are rejected with visible feedback.

## Acceptance criteria validation

- AC1: Settings/Admin exposes `Rename active festival` when an active festival exists; covered by `SettingsActivityRegressionTest.exposesRenameActiveFestivalActionInAdminSettings`.
- AC2: Renaming saves the active festival with the same id/status and a trimmed new display name; covered by `RenameActiveFestivalUseCaseTest.renamesActiveFestivalAndPreservesIdentity`.
- AC3: Blank or whitespace names are rejected with `Festival name must not be blank.`; covered by `RenameActiveFestivalUseCaseTest.rejectsBlankActiveFestivalName` and the Settings source regression.
- AC4: No-active-festival handling returns `No active festival is available to rename.`; covered by `RenameActiveFestivalUseCaseTest.failsWhenNoFestivalIsActive`. The Settings action is only added from `activeFestival().ifPresent(...)`.
- AC5: Automated tests cover successful rename, blank-name validation, no-active handling, and identity preservation.
- AC6: Architecture impact was assessed; no persistence or sync contract change was required.
- AC7: Business requirements impact and README impact are recorded below.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest --tests be.wacken.planner.SettingsActivityRegressionTest`
- `git diff --check -- domain/src/main/java/be/wacken/planner/domain/Festival.java application/src/main/java/be/wacken/planner/application/RenameActiveFestivalResult.java application/src/main/java/be/wacken/planner/application/RenameActiveFestivalUseCase.java application/src/test/java/be/wacken/planner/application/RenameActiveFestivalUseCaseTest.java app/src/main/java/be/wacken/planner/SettingsActivity.java app/src/test/java/be/wacken/planner/SettingsActivityRegressionTest.java`

### Manual validation

Not run on device; covered by application tests and app source-regression tests.

## TDD / BDD / approval-test evidence

TDD was used with failing tests for the missing use case and missing Settings rename action before implementation. No approval or characterization test was needed.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none required.

## README impact

README impact: none, because this adds a small Settings action to an existing festival administration workflow and does not change setup, commands, architecture, or troubleshooting.

## Business requirements impact

Business requirements impact: already updated before implementation with BR-104 and BR-105 for active-festival rename and identity preservation.

## Diagram impact

Diagram impact: none, because this did not change architecture, module structure, dependencies, or runtime data flow.

## ADR impact

ADR impact: none, because this did not introduce or change an architecture decision.

## Commits / logical change list

- Add `Festival.rename`.
- Add `RenameActiveFestivalUseCase` and result.
- Add Settings/Admin rename action.
- Add application and app regression tests.

## Risks and follow-up

- Remote Supabase festival-name sync remains out of scope unless an existing persistence/sync contract is extended in a future task.
- Existing unrelated `.idea/workspace.xml` local change remains untouched.
- Device-level UI smoke was not run from this workspace.
<!-- SECTION:NOTES:END -->
