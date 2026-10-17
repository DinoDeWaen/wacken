---
id: task-22
title: 'US-022: Add Android CSV import screen'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-15 15:22'
updated_date: '2026-05-15 16:14'
labels:
  - mvp1
  - android
  - import
dependencies:
  - task-21
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-022: Add Android CSV import screen

**As an** admin
**I want** to import CSV files from the Android app
**So that** I can load festival data into the APK without developer test hooks

### Notes
- Use the CSV schemas in `backlog/docs/festival-data-csv-schemas.md`.
- A minimal first UI may accept pasted CSV text or picked files, but the chosen path must be usable on Android.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given I open the import screen When I provide valid CSV data Then the app imports bands, stages, performances, distances, and food
- [x] #2 Given the CSV contains validation errors When I import Then the app shows explicit row-level errors and does not mutate existing data
- [x] #3 Given import succeeds When I return to the band list Then imported bands are visible
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Reviewed the existing CSV import use case, Android UI style, and repository wiring from task-21.
2. Added a minimal Android import screen reachable from the band list that accepts pasted CSV text for bands, stages, performances, distances, and food.
3. Wired the import screen to file-backed repositories through AppRepositories so successful imports persist locally.
4. Presented row-level validation errors returned by ImportFestivalCsvUseCase without saving invalid imports.
5. Refreshed the band list in MainActivity.onResume so returning after a successful import shows imported performances.
6. Updated README to document the pasted-CSV Android import path.
7. Validated with Gradle tests, QA scenarios, and debug APK build.

Architecture impact: not architecture-significant; this wires Android UI to existing use cases and approved repository adapters.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implementation notes:
- Added ImportCsvActivity with paste fields for bands.csv, stages.csv, performances.csv, distances.csv, and food.csv.
- Added an Import CSV entry point from the band list.
- Extended AppRepositories to provide the file-backed repositories needed by the import use case.
- MainActivity refreshes from persisted performances/ratings on resume so imported bands are visible after returning.

Validation package:
- Automated checks run: JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug.
- Manual validation: not run on device yet. The APK build verifies Android wiring; application import behavior remains covered by existing use-case tests.
- README impact: README updated to mention the Android pasted-CSV import screen.
- Diagram impact: no diagram update needed; no container or dependency direction changed.
- ADR impact: no new ADR needed; task uses ADR 0006-approved persistence.
- Approval status: no architecture approval required for this UI wiring task.
- Risks: The MVP import UI uses pasted text, not Android file picker. File picking can be refined later if needed.
<!-- SECTION:NOTES:END -->
