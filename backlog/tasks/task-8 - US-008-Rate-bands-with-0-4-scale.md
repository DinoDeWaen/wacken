---
id: task-8
title: 'US-008: Rate bands with 0-4 scale'
status: Done
assignee:
  - '@codex'
created_date: '2026-01-06 16:57'
updated_date: '2026-05-15 11:51'
labels:
  - rating
  - mvp1
dependencies:
  - task-4
  - task-5
  - task-7
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-008: Rate bands with 0-4 scale

**As an** attendee
**I want** to set my rating per band on the list
**So that** the app can compute group decisions later
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a band in the list When I set a rating between 0 and 4 Then the rating is stored via the application use case
- [x] #2 Given a stored rating When I reopen the band list Then the selected rating is displayed
- [x] #3 Given an invalid rating input When I attempt to save Then the app prevents it and shows a validation message
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added application tests for saving ratings through a use case, reading stored ratings for the band list, and rejecting invalid rating values with a clear validation result.
2. Implemented `RateBandUseCase`, `RateBandResult`, and rating-aware `BandListItem` behavior through `ListBandsUseCase`.
3. Kept rating validation in the existing domain `Rating` value object and returned application-level validation messages without Android-specific conditionals.
4. Updated the Android list skeleton to display selected rating text when present; richer star UI remains in task-14.
5. Validated with targeted application tests, full Gradle tests, and debug APK build.
6. README update not needed because setup, commands, architecture, and public run behavior did not change.

Architecture impact: not architecture-significant; reused existing `Rating`, `RatingRepository`, and module boundaries. No ADR needed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implementation notes:
- Added `RateBandUseCase` to store 0-4 ratings through `RatingRepository`.
- Added `RateBandResult` so invalid ratings return a validation message and do not persist.
- Extended `ListBandsUseCase` and `BandListItem` so reopening/listing bands includes the current user rating when stored.
- Updated the minimal Android list rendering to include rating text when list data is present.

Validation package:
- Automated checks run: `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :application:jacocoTestCoverageVerification`; `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test`; `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug`.
- Manual validation: not run on device; current Android UI is still a minimal skeleton and richer star interaction is tracked separately in task-14.
- README impact: not updated because no setup, command, architecture, troubleshooting, or documented public behavior changed.
- Diagram impact: not updated because module architecture and dependencies did not change.
- ADR impact: none; no architecture-significant decision.
- Approval status: no human architecture approval required.
- Risks: list-level interactive rating UI is not complete in the Android screen yet; this task delivers the application behavior and display state, while task-14 covers the richer rating screen.
<!-- SECTION:NOTES:END -->
