---
id: task-28
title: Show band-only imports in Android list
status: Done
assignee:
  - '@codex'
created_date: '2026-05-15 19:57'
updated_date: '2026-05-15 19:58'
labels:
  - bug
  - android
  - mvp1
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## Bug: Show band-only imports in Android list

**As a** user
**I want** bands imported from bands.csv to appear even before performances are available
**So that** I can start rating the early lineup list

### Notes
- User imported bands.csv and no bands appeared.
- Current list is performance-backed only, so bands without stage/time are hidden.
- Keep business logic in application/domain rather than Android UI.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given only bands.csv has imported bands When I return to the band list Then those bands are visible
- [x] #2 Given a band has no performance yet Then the list marks stage/time as not scheduled
- [x] #3 Given performances exist Then performance-backed stage/time display still works
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added application test coverage for bands imported without performances appearing with unscheduled stage/time labels.
2. Extended ListBandsUseCase to read BandRepository as a fallback while preserving performance-backed ordering/display when performances exist.
3. Wired MainActivity to pass the persisted BandRepository into the use case.
4. Verified no remaining Android runtime compatibility issue was introduced in this path.
5. Ran test, qaTest, and assembleDebug with JDK 21.
6. Closed task and committed.

Architecture impact: not architecture-significant; this extends existing listing use-case behavior within current ports.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implementation notes:
- ListBandsUseCase now lists imported bands when no performances exist.
- Bands without performances render as stage `Not scheduled yet` and time `TBA - TBA`.
- Existing performance-backed list behavior remains unchanged when performances are imported.
- MainActivity now passes the file-backed band repository into ListBandsUseCase.

Validation package:
- Automated checks run: JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug.
- Manual validation: not run on device in this shell.
- README impact: no update needed.
- Diagram impact: no diagram update needed.
- ADR impact: no ADR needed.
- Approval status: no architecture approval required.
- Risks: if the already-installed app has previous empty imports, reinstalling the rebuilt APK or importing bands.csv again should show the stored bands.
<!-- SECTION:NOTES:END -->
