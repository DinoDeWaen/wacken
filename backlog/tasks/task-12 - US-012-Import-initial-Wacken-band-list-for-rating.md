---
id: task-12
title: 'US-012: Import initial Wacken band list for rating'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-15 06:32'
updated_date: '2026-05-15 10:03'
labels:
  - mvp1
  - rating
  - data
dependencies:
  - task-4
  - task-5
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-012: Import initial Wacken band list for rating

**As an** attendee
**I want** the current Wacken band list available before final stage times are known
**So that** my group can start rating bands as the first product priority

### Notes
- Source: `backlog/docs/business-requirements.md` BR-035 and rating-first business goal.
- This story may use a manually supplied file or a simple importer, but must not invent final performance/stage/time schemas.
- If website scraping is used, keep it isolated behind an adapter and preserve domain/application boundaries.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given an initial source that contains band names When the import runs Then the app stores bands that can be listed and rated
- [x] #2 Given final stage and performance times are not available When bands are imported Then the app still supports band-only records for rating
- [x] #3 Given the import source contains duplicate band names When the import runs Then duplicate bands are not created
- [x] #4 Given imported bands are available When I open the band list Then each imported band can be selected for rating
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added an application-level import use case that accepts an initial band-name source as plain band names and stores valid bands through `BandRepository`.
2. Added application tests first for importing band-only data, allowing missing performance/stage/time data, ignoring duplicate band names, and keeping imported bands available through the repository.
3. Kept source handling minimal and schema-free so this does not invent final CSV or Wacken scraping details.
4. Did not add website scraping in this story.
5. Validated with `./gradlew :application:test :application:jacocoTestCoverageVerification`, `./gradlew test`, and `./gradlew assembleDebug`.
6. README update was not needed because no user-facing command or setup step changed.

Deviation: AC4 mentions opening the band list, but band list UI is task-7; this task validates imported bands are available through the repository for the listing/rating flows.
Architecture impact: not architecture-significant; this uses existing repository ports and module boundaries from ADR 0001 and ADR 0004. No ADR added.
README impact: not needed.
Diagram impact: not needed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added `ImportInitialBandListUseCase` and `ImportInitialBandListResult` in the application module. The use case imports plain band names into `BandRepository`, supports band-only data before final performance schedules exist, and ignores duplicate band names.

## Acceptance criteria validation

- AC1: `ImportInitialBandListUseCaseTest.importsBandNamesWithoutPerformanceScheduleData` verifies initial band names are stored through `BandRepository`.
- AC2: The import use case accepts only band names and does not require stages, performances, or times.
- AC3: `ImportInitialBandListUseCaseTest.ignoresDuplicateBandNames` verifies duplicates are not stored and are counted.
- AC4: `ImportInitialBandListUseCaseTest.importedBandsAreAvailableForListingAndRatingFlows` verifies imported bands can be retrieved for listing/rating flows. Actual band list UI remains task-7.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :application:jacocoTestCoverageVerification`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug`

### Manual validation

- Ran `rg -n "csv|stage|performance|scrap|http|wacken.com"` against the import use case and tests; no schema/scraping assumptions were introduced.

## TDD / BDD / approval-test evidence

Used TDD in the application module. The initial red test run failed because the import use case/result did not exist. Then the minimal implementation was added and tests passed.

## Architecture impact

- Architecture-significant change: no; this uses existing repository ports and module boundaries.
- Approval received: not required.
- ADR: not needed.

## README impact

Not needed because setup and commands did not change.

## Diagram impact

Not needed because module boundaries did not change.

## Commits / logical change list

- `a34cd9a` Add initial band list import use case

## Risks and follow-up

- Website scraping and final CSV schemas remain in refinement tasks and task-6.
- Band list UI validation remains task-7.
<!-- SECTION:NOTES:END -->
