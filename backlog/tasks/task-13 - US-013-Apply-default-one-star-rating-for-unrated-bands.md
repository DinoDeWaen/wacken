---
id: task-13
title: 'US-013: Apply default one-star rating for unrated bands'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-15 06:33'
updated_date: '2026-05-15 11:53'
labels:
  - mvp1
  - rating
  - domain
dependencies:
  - task-4
  - task-5
  - task-8
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-013: Apply default one-star rating for unrated bands

**As an** attendee
**I want** unrated bands to count as 1 star by default
**So that** group decisions can treat missing ratings as OK / indifferent without blocking the rating workflow

### Notes
- Source: `backlog/docs/business-requirements.md` BR-020.
- Keep the default behavior in domain/application logic, not Android UI conditionals.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a group member has not rated a band When group decision logic reads that member rating Then the rating is treated as 1
- [x] #2 Given a band has no explicit rating from the current user When the band appears in the rating UI Then the user can see or set the 1-star default clearly
- [x] #3 Given a user changes the default rating When the rating is saved Then the explicit value replaces the default for future reads
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added focused application tests showing missing ratings read as a visible/default 1 while explicit ratings still round-trip.
2. Added `EffectiveRating` and `EffectiveRatingResolver` in the application layer to apply BR-020 using the existing domain `Rating` value object and repository.
3. Updated `ListBandsUseCase` so missing current-user ratings appear as a 1-star default without writing an explicit rating.
4. Verified `RateBandUseCase` explicit saves replace the default for future list reads.
5. Validated with application tests, full Gradle tests, and debug APK build.
6. README update not needed because setup, commands, architecture, and documented run behavior did not change.

Architecture impact: not architecture-significant; this applies BR-020 through existing application use cases and repositories without new modules, framework dependencies, or port changes. No ADR needed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Implemented effective default ratings for unrated bands. Missing ratings now resolve to 1 star in application logic, while explicit ratings are still stored and returned as explicit values. The Android list skeleton now renders default ratings as `1 (default)` when band data is present.

## Acceptance criteria validation

- AC1: Covered by `EffectiveRatingResolverTest.treatsMissingMemberRatingAsOneStarDefault`.
- AC2: Covered by `ListBandsUseCaseTest.returnsBandsWithStageAndTimeSortedByStartTime` and Android build validation for list rendering.
- AC3: Covered by `ListBandsUseCaseTest.savedRatingReplacesDefaultForFutureBandReads`.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :application:jacocoTestCoverageVerification`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug`

### Manual validation

Not run on device. The current Android screen is still a minimal skeleton; task-14 owns the richer interactive rating screen.

## TDD / BDD / approval-test evidence

Added failing application tests first for default effective ratings and default replacement, then implemented the resolver/list behavior until tests passed. No approval tests were needed because this was new behavior, not legacy refactoring.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: not needed; existing Clean Architecture boundaries and repository ports were reused.

## README impact

README not updated because setup, commands, architecture, troubleshooting, and documented public behavior did not change.

## Diagram impact

No diagram update needed because module boundaries and dependencies did not change.

## Commits / logical change list

- Added effective rating resolver and tests.
- Updated band list item/use case to expose rating value plus default marker.
- Updated minimal Android rendering to show default rating text.

## Risks and follow-up

Interactive star controls are still deferred to task-14.
<!-- SECTION:NOTES:END -->
