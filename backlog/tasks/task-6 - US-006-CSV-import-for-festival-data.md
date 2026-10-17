---
id: task-6
title: 'US-006: CSV import for festival data'
status: Done
assignee:
  - '@codex'
created_date: '2026-01-06 16:57'
updated_date: '2026-05-15 14:09'
labels: []
dependencies:
  - task-16
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-006: CSV import for festival data

**As an** admin
**I want** to import bands, stages, performances, distances, and food from CSV
**So that** the system has validated master data for scheduling
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given valid CSV files for bands, stages, performances, distances, and food When I run the import use case Then domain repositories are populated with parsed objects
- [x] #2 Given CSV data with missing references When the import runs Then it fails with explicit errors listing the missing ids
- [x] #3 Given overlapping performances on the same stage When the import runs Then it flags the overlap and fails the import
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added application-level tests for CSV import using the task-16 schemas: valid import, missing band/stage references, unknown stages, and same-stage overlaps.
2. Implemented a minimal CSV parser/import use case that parses `bands.csv`, `stages.csv`, `performances.csv`, `distances.csv`, and `food.csv` from provided text inputs.
3. Populated existing repositories for bands, performances, and distances.
4. Added approved minimal domain concepts/ports/adapters: `StageRepository`, `FoodOption`, `FoodOptionRepository`, `InMemoryStageRepository`, and `InMemoryFoodOptionRepository`.
5. Kept validation errors explicit and row-oriented, matching `backlog/docs/festival-data-csv-schemas.md` for missing references and overlaps.
6. Validated with targeted application/domain/infrastructure tests, full Gradle tests, and debug APK build.
7. Added ADR 0005 and updated README with the new import repositories and CSV schema link.

Architecture impact: architecture-significant and explicitly approved by the user. ADR 0005 records the accepted minimal option.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Implemented MVP CSV import for festival master data. `ImportFestivalCsvUseCase` now parses `bands.csv`, `stages.csv`, `performances.csv`, `distances.csv`, and `food.csv`, validates missing references and same-stage overlaps, and populates the domain repositories only when validation succeeds.

Added the approved minimal stage and food repository support: `StageRepository`, `FoodOption`, `FoodOptionRepository`, `InMemoryStageRepository`, and `InMemoryFoodOptionRepository`.

## Acceptance criteria validation

- AC1: Covered by `ImportFestivalCsvUseCaseTest.importsValidFestivalCsvFilesIntoRepositories`.
- AC2: Covered by `ImportFestivalCsvUseCaseTest.failsWhenPerformanceReferencesMissingBandAndUnknownStage`.
- AC3: Covered by `ImportFestivalCsvUseCaseTest.failsWhenPerformancesOverlapOnSameStage`.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :infrastructure:test`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :application:jacocoTestCoverageVerification`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug`

### Manual validation

No device/manual UI validation was needed; task-6 is application/domain/infrastructure import behavior.

## TDD / BDD / approval-test evidence

Added failing domain, infrastructure, and application tests first, then implemented the approved minimal domain ports/adapters and CSV import behavior until tests passed. No approval tests were needed because this was new behavior.

## Architecture impact

- Architecture-significant change: yes, because task-6 required new domain concepts/ports for stage and food repositories.
- Approval received: yes. User approved minimal food support with `1`, then approved the additional minimal stage repository change with `approved`.
- ADR: added `backlog/decisions/0005-food-and-stage-repository-ports-for-csv-import.md`.

## README impact

README updated with ADR 0005, the CSV schema link, and the current import repository coverage.

## Diagram impact

No diagram update needed because module boundaries and dependency direction did not change.

## Commits / logical change list

- Added CSV import input/result/use case.
- Added minimal food and stage domain ports/adapters.
- Added domain, infrastructure, and application tests.
- Added ADR 0005 and README links.

## Risks and follow-up

The CSV parser is intentionally minimal for MVP and supports simple RFC-style quoting on a single line; multiline quoted fields are not yet supported. Rich food metadata, durable persistence, and reviewed data-grid updates remain separate follow-up work.
<!-- SECTION:NOTES:END -->
