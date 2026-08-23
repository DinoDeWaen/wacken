---
id: task-177
title: 'US: Require explicit festival name when adding a festival'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-23 09:33'
updated_date: '2026-08-23 13:18'
labels:
  - user-story
  - festival
  - import
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: The app should not accidentally create a future festival with a placeholder name, because festival names become visible context for planning ratings, archives, and future history.

As a festival admin, I want the add-festival name field to start empty and be mandatory, so that the active festival is created with the real festival name I intended.

Scope: add-festival mode only. The festival name field starts blank, validates before adding the festival, keeps the selected bands CSV intact after validation errors, and shows clear feedback when the name is missing.

Out of scope: renaming an already active festival, fuzzy band linking, metadata enrichment, and multiple upcoming festivals.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given no festival is active, when the add-festival screen opens, then the festival name field is empty and no placeholder value is submitted.
- [x] #2 Given the festival name is blank or whitespace, when the user tries to add the festival from bands.csv, then the app blocks creation and shows a clear required-name message.
- [x] #3 Given a valid festival name and valid bands.csv are selected, when the user adds the festival, then the festival is created using the entered trimmed name.
- [x] #4 Automated tests cover blank-name validation and successful add-festival creation with an explicit name.
- [x] #5 Business requirements and README impact are recorded using delivery-governance wording when implemented.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected add-festival UI and AddFestivalUseCase validation behavior.
2. Added focused AddFestivalUseCase tests for blank festival-name rejection and explicit trimmed name creation.
3. Added ImportCsvActivityRegressionTest to guard against reintroducing a hard-coded add-festival default name.
4. Removed the hard-coded `Rock im Park` default and made AddFestivalUseCase reject blank festival names before generated id validation can mask the error.
5. Ran focused application/app validation and closed the task.

Design approach: minimal change using existing Festival domain validation and existing ImportCsvActivity add-festival flow.
Test strategy: TDD with AddFestivalUseCase unit tests plus app source regression for the UI default.
Architecture impact: not architecture-significant; no persistence, schema, dependency, API, or module-boundary change. Approval/ADR not required.
Deviation: focused single-test application run tripped JaCoCo because too few tests were selected; reran full :application:test with the focused app regression successfully.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Implemented explicit add-festival naming. The add-festival screen no longer pre-fills `Rock im Park`, and the application use case now rejects blank festival names before generated festival-id validation can produce the wrong error. Valid names continue to be trimmed by the Festival domain model.

## Acceptance criteria validation

- AC1: Add-festival mode no longer calls `festivalName.setText("Rock im Park")`; covered by `ImportCsvActivityRegressionTest`.
- AC2: Blank or whitespace festival names are rejected with `Festival name must not be blank.`; covered by `AddFestivalUseCaseTest.rejectsBlankFestivalNameBeforeCreatingFestival`.
- AC3: Explicit names are trimmed and used to create the active festival; covered by `AddFestivalUseCaseTest.trimsExplicitFestivalNameWhenAddingFestival`.
- AC4: Automated tests were added for blank-name validation, successful explicit-name creation, and the UI default regression.
- AC5: Business requirements impact and README impact are recorded below.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest --tests be.wacken.planner.ImportCsvActivityRegressionTest`
- `git diff --check -- application/src/main/java/be/wacken/planner/application/AddFestivalUseCase.java application/src/test/java/be/wacken/planner/application/AddFestivalUseCaseTest.java app/src/main/java/be/wacken/planner/ImportCsvActivity.java app/src/test/java/be/wacken/planner/ImportCsvActivityRegressionTest.java`

### Manual validation

Not run on device; the change is covered by application and app unit/source-regression tests.

## TDD / BDD / approval-test evidence

TDD was used: the app regression failed while `festivalName.setText("Rock im Park")` was still present, then passed after implementation. Use-case tests cover the business validation path. No approval or characterization test was needed.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none required.

## README impact

README impact: none, because this is a small behavior correction in an existing add-festival workflow and does not change setup, commands, architecture, or troubleshooting.

## Business requirements impact

Business requirements impact: already updated before implementation with BR-103 for explicit non-blank festival names.

## Diagram impact

Diagram impact: none, because this did not change architecture, module structure, dependencies, or runtime data flow.

## ADR impact

ADR impact: none, because this did not introduce or change an architecture decision.

## Commits / logical change list

- Remove hard-coded add-festival default name.
- Reject blank festival names before generated festival-id validation.
- Add application and app regression tests.

## Risks and follow-up

- Existing unrelated `.idea/workspace.xml` local change remains untouched.
- Device-level UI smoke was not run from this workspace.
<!-- SECTION:NOTES:END -->
