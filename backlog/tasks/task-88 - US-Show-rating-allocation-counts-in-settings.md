---
id: task-88
title: 'US: Show rating allocation counts in settings'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-11 15:18'
updated_date: '2026-06-11 18:25'
labels:
  - android
  - settings
  - ratings
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Add a rating distribution summary to the Settings screen.

As a festival attendee, I want to see how many bands I have rated with 5, 4, 3, 2, and 1 stars so that I can quickly understand and balance my rating allocation before generating or reviewing the group schedule.

Scope: show counts for the signed-in user ratings available in the app. Out of scope: changing the rating scale, changing schedule rules, or adding charts beyond the count summary.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given I am signed in and have rated bands, when I open Settings, then I see separate counts for 5-star, 4-star, 3-star, 2-star, and 1-star ratings.
- [x] #2 Given I have no bands for a rating value, when I open Settings, then that rating value is still shown with a count of 0.
- [x] #3 Given ratings change and Settings is opened again after refresh/sync, then the displayed counts reflect the current local ratings.
- [x] #4 Automated tests or focused characterization coverage prove the rating-count calculation and Settings wiring.
- [x] #5 README, business requirements, ADR, and diagram impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added RatingAllocationSummary for current-user rating allocation counts across 5, 4, 3, 2, and 1 stars.
2. Covered the helper with focused unit tests, including zero-count buckets and ignoring unrated/other-user ratings.
3. Wired SettingsActivity to render the count summary from the local ratings repository for the signed-in user and refresh after Settings sync completes.
4. Ran focused app tests, Android compile, and git diff checks.
5. Closed task-88 with validation and canonical impact notes.
Architecture impact: not architecture-significant; this stays in Android/app presentation logic and existing rating repository APIs.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Settings now shows a rating allocation summary for the signed-in user with visible buckets for 5, 4, 3, 2, and 1 stars. Counts ignore unrated `0` values and other users, and the summary refreshes after Settings sync completes.

## Acceptance criteria validation

- AC1: Settings renders separate 5-star through 1-star counts for the signed-in user.
- AC2: `RatingAllocationSummaryTest.keepsZeroBucketsVisibleWhenNoRatingsExist` covers zero-count buckets.
- AC3: Settings calls `refreshRatingAllocation()` when opened and after sync completes.
- AC4: `RatingAllocationSummaryTest` covers count calculation, zero buckets, and display formatting; Android compile verifies Settings wiring.
- AC5: README, business requirements, ADR, and diagram impact are recorded below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.RatingAllocationSummaryTest :app:compileDebugJavaWithJavac
- git diff --check

### Manual validation

- Open Settings after signing in and verify the rating allocation summary lists 5 stars through 1 star.
- Change ratings, sync or reopen Settings, and verify the counts reflect current local ratings.

## TDD / BDD / approval-test evidence

- Added focused unit coverage for rating allocation behavior before wiring the Settings screen.

## Architecture impact

- Architecture-significant change: no
- Approval received: not required
- ADR: none

## README impact

README impact: none, because this task adds a Settings screen summary inside existing app behavior and does not change setup, commands, architecture, or troubleshooting.

## Business requirements impact

Business requirements impact: none, because this implements a requested Settings presentation enhancement without changing rating scale, sync rules, or schedule business rules.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- Added `RatingAllocationSummary` and unit coverage.
- Rendered the rating allocation summary in Settings and refreshed it after sync.

## Risks and follow-up

- Installed-device visual validation remains recommended because local Android UI screenshot automation is not configured for this native Activity.
<!-- SECTION:NOTES:END -->
