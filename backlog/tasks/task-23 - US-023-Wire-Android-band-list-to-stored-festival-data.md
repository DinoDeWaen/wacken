---
id: task-23
title: 'US-023: Wire Android band list to stored festival data'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-15 15:22'
updated_date: '2026-05-15 16:15'
labels:
  - mvp1
  - android
  - listing
dependencies:
  - task-21
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-023: Wire Android band list to stored festival data

**As an** attendee
**I want** the app band list to show imported stored data
**So that** I can browse the lineup in the APK

### Notes
- Replace the current placeholder empty list wiring.
- Keep business logic in application/domain use cases.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given stored festival data exists When I open the app Then the band list shows imported bands with stage, time, and effective rating
- [x] #2 Given no festival data exists When I open the app Then the empty state remains clear
- [x] #3 Given a band has an explicit rating When I open the list Then the rating is shown instead of the default marker
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Verified MainActivity reads stored persisted festival data through ListBandsUseCase and file-backed repositories.
2. Verified the empty state remains clear when no performances exist.
3. Verified explicit ratings are resolved through ListBandsUseCase and rendered without the default marker.
4. Ran Gradle validation and closed with notes.

Architecture impact: not architecture-significant; this verifies existing UI wiring to approved use cases and repositories.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implementation notes:
- No additional production code was needed in this task; task-21 and task-22 already replaced the placeholder list with stored repository-backed data.
- MainActivity uses ListBandsUseCase with persisted performances and ratings and keeps the No bands imported yet empty state.

Validation package:
- Automated checks run: JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest; JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug.
- Manual validation: not run on device yet.
- README impact: no update needed; task-22 already documented the Android import path and existing README lists band listing behavior.
- Diagram impact: no diagram update needed; no architecture shape changed.
- ADR impact: no ADR needed.
- Approval status: no architecture approval required.
- Risks: visual polish and row click navigation are covered by task-24.
<!-- SECTION:NOTES:END -->
