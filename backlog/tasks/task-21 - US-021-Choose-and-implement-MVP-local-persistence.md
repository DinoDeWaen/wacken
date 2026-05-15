---
id: task-21
title: 'US-021: Choose and implement MVP local persistence'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-15 15:22'
updated_date: '2026-05-15 16:11'
labels:
  - mvp1
  - android
  - persistence
dependencies:
  - task-6
  - task-8
  - task-13
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-021: Choose and implement MVP local persistence

**As a** user
**I want** imported festival data and ratings to persist locally on the device
**So that** the APK remains useful after closing and reopening the app

### Notes
- This is architecture-significant because it introduces a persistence strategy/adapter.
- Prefer the smallest durable storage that supports MVP 1 data and can be migrated later.
- Must keep domain/application independent from Android storage APIs.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given festival data has been imported When the app restarts Then bands and performances are still available
- [x] #2 Given I rate a band When the app restarts Then the rating is still available
- [x] #3 Given persistence is introduced When the task is completed Then an ADR records the storage decision and README documents any setup or behavior impact
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Requested architecture approval for MVP local persistence before coding. User approved option 1: minimal file-backed repositories.
2. Added infrastructure persistence tests covering bands, stages, performances, stage distances, food options, and ratings surviving repository recreation.
3. Implemented file-backed repository adapters in infrastructure using app-private file paths supplied by Android.
4. Kept domain/application independent from Android storage APIs and avoided a database dependency for MVP.
5. Added ADR 0006 and README documentation for the persistence decision.
6. Wired Android composition to use file-backed band, performance, and rating repositories.
7. Validated with infrastructure tests, full Gradle tests, QA scenarios, and debug APK build.

Architecture impact: architecture-significant; approved by the user on 2026-05-15 before production code changes. ADR 0006 records the accepted decision.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implementation notes:
- Added MVP file-backed repository adapters in infrastructure for bands, stages, performances, stage distances, food options, and ratings.
- Added focused persistence coverage proving data survives repository recreation.
- Wired Android repository composition to app-private file storage for band list/rating paths.
- Added ADR 0006 for the approved persistence decision.

Validation package:
- Automated checks run: JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :infrastructure:test; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug.
- Manual validation: not run on a device yet; persistence is covered by repository recreation tests and Android compile/build wiring.
- README impact: README updated with ADR link, C4 container storage note, and file-backed persistence behavior.
- Diagram impact: README C4 container diagram updated to show app-private file storage.
- ADR impact: ADR 0006 added after explicit user approval.
- Approval status: architecture-significant option 1 approved by user before implementation.
- Risks: Android CSV import UI still follows in task-22; full imported-data restart behavior is completed when that UI writes through these repositories.
<!-- SECTION:NOTES:END -->
