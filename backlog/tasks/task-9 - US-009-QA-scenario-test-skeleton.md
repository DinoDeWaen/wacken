---
id: task-9
title: 'US-009: QA scenario test skeleton'
status: Done
assignee:
  - '@codex'
created_date: '2026-01-06 16:57'
updated_date: '2026-05-15 14:16'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-009: QA scenario test skeleton

**As a** QA engineer
**I want** a runnable scenario test for listing and rating
**So that** regression checks cover the MVP1 flow end-to-end
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the QA test suite When I run a dedicated QA task Then a scenario test executes for listing bands and setting ratings
- [x] #2 Given the decision rules document When writing QA scenarios Then tests include at least one happy path and one invalid input path for MVP1 features
- [x] #3 Given CI configuration When the QA suite fails Then CI reports the failure and stops the build
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added a dedicated application `qaTest` source set and Gradle task for MVP scenario tests.
2. Added a QA scenario test covering listing bands, default rating display, setting a valid rating, seeing it on a reopened list, and rejecting invalid rating input.
3. Wired GitHub Actions CI to run `./gradlew qaTest` so QA failures stop the build.
4. Updated README with the QA task command and CI behavior.
5. Validated with `./gradlew qaTest`, `./gradlew test`, and `./gradlew assembleDebug`.
6. ADR not needed because this uses the planned testing setup and does not change production architecture.

Architecture impact: not architecture-significant; this adds a planned QA test task/source set only and does not change production module boundaries or business behavior.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added a dedicated QA scenario suite under `application/src/qaTest/java` with a `qaTest` Gradle task. The scenario covers the MVP listing/rating flow, including default 1-star display, saving a valid rating, reopening the list, and rejecting invalid rating input. CI now runs `./gradlew qaTest` as a real failing step instead of a placeholder.

## Acceptance criteria validation

- AC1: `./gradlew qaTest` runs `ListingAndRatingQaScenarioTest`.
- AC2: The QA suite includes a happy path and invalid input path for MVP listing/rating behavior.
- AC3: GitHub Actions now runs `./gradlew qaTest`, so QA failure stops the workflow.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug`

### Manual validation

No manual UI validation was needed; this task adds executable QA scenario coverage and CI wiring.

## TDD / BDD / approval-test evidence

Added BDD-style QA scenarios around user-visible listing/rating behavior. No approval tests were needed.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: not needed.

## README impact

README updated with `./gradlew qaTest` and the CI QA-suite behavior.

## Diagram impact

No diagram update needed because architecture diagrams did not change.

## Commits / logical change list

- Added `qaTest` source set and Gradle task.
- Added listing/rating QA scenario test.
- Replaced CI QA placeholder with `./gradlew qaTest`.
- Updated README test instructions.

## Risks and follow-up

The QA scenario is application-level, not Android instrumentation. That keeps MVP regression checks fast; UI instrumentation can be added later when Android UI workflows are richer.
<!-- SECTION:NOTES:END -->
