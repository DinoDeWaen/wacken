---
id: task-62
title: 'US-064: Return from detail to the selected overview row'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-07 16:02'
updated_date: '2026-06-07 16:20'
labels:
  - mvp2
  - android
  - ux
  - rating
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user browsing bands, I want returning from a band detail screen to bring me back to the same band row in the overview, so I can continue rating without losing my place.

In scope:
- Remember the band row opened from the overview.
- When returning from detail, restore the overview scroll position so that band row is visible.
- Refresh the row rating/state after any detail changes without jumping to the top.
- Keep normal app reactivation sync behavior working.

Out of scope:
- New sorting/filtering controls, schedule timeline navigation, and manual row pinning.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given I open a band detail from the overview, when I go back, then the overview returns to the row for that band instead of the top of the list.
- [x] #2 Given I change a rating in detail, when I go back, then the same row is visible and shows the updated rating.
- [x] #3 Given the app syncs on reactivation while returning from detail, when the overview refreshes, then it still scrolls to the selected band row after data loads.
- [x] #4 Given the selected band is no longer present after refresh, when I go back, then the overview falls back gracefully without crashing.
- [x] #5 Focused Android validation covers returning from detail after no change, after rating change, and after refresh.
- [x] #6 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
- [x] #7 Architecture impact is assessed; ADR impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected MainActivity detail navigation, reload/sync behavior, and existing Android test style.
2. Added a focused app-module helper and tests for resolving the selected band row after no-change refresh, rating-change refresh, and missing-band refresh.
3. Implemented selected-band tracking in MainActivity before opening detail and restored ListView selection after sync/reload data loads.
4. Updated the sync failure branch so returning from detail still reloads cached local changes and restores the row when Supabase sync fails.
5. Updated README and business requirements to document the return-to-row behavior.
6. Ran focused Android validation and diff checks.

Deviation: no UI integration framework was introduced; focused validation uses a pure app-module helper because the project has no existing Robolectric/UI test setup. Architecture impact: not architecture-significant; Android presentation state only. ADR impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Returning from a band detail screen now brings the overview back to the selected band row after the refreshed list is loaded. MainActivity remembers the opened band name, reloads the overview after normal reactivation sync or cached fallback, and scrolls to that band if it still exists. If the band disappeared after refresh, the restore is skipped without crashing.

## Acceptance criteria validation

- AC1: MainActivity records the opened band and restores the ListView selection after `loadBandList()`.
- AC2: The list reloads after returning from detail, so rating changes are read from Room and the same row is restored.
- AC3: Lifecycle sync still runs first; selection restore happens after refreshed data is bound.
- AC4: Missing selected bands return an empty target and are skipped gracefully.
- AC5: `SelectedBandScrollTargetTest` covers unchanged refresh, rating-change refresh, and missing-band refresh. App compile validates Android wiring.
- AC6: README and business requirements impacts are recorded below.
- AC7: Architecture impact assessed below.

## How to test

### Automated tests

- `/bin/zsh -lc 'JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac'`\n- `git diff --check`\n\n### Manual validation\n\n- Not run on a physical device in this task. The focused helper tests cover row-target behavior and app Java compilation covers MainActivity wiring.\n\n## TDD / BDD / approval-test evidence\n\nAdded focused tests before implementation for the three Backlog.md scenarios: unchanged refresh, rating-changed refresh, and selected band missing after refresh.\n\n## Architecture impact\n\n- Architecture-significant change: no. The change is Android presentation state only. No domain, persistence, backend, schema, API, dependency, or module-boundary change was introduced.\n- Approval received: not required.\n- ADR: none required.\n\n## README impact\n\nREADME impact: updated basic functionality to document returning from band detail to the same overview row after refresh.\n\n## Business requirements impact\n\nBusiness requirements impact: added BR-054a for keeping the selected overview row visible when returning from band detail.\n\n## Diagram impact\n\nDiagram impact: none, because the system structure and data flow did not change.\n\n## Commits / logical change list\n\n- Add selected-band row target helper and app-module tests.\n- Remember the opened detail band in MainActivity.\n- Restore ListView selection after refreshed overview data loads.\n- Reload cached data after failed reactivation sync when returning from detail.\n- Document return-to-row behavior.\n\n## Risks and follow-up\n\n- The project still lacks full device/UI automation; this task uses focused helper tests plus Android compilation as the feasible validation path.
<!-- SECTION:NOTES:END -->
